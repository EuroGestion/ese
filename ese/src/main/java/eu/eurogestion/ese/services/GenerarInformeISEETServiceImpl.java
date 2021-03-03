package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Is;
import eu.eurogestion.ese.domain.Iseet;
import eu.eurogestion.ese.domain.Iseet2;
import eu.eurogestion.ese.domain.Iseet3;
import eu.eurogestion.ese.domain.ResultadoInspeccion;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISEETJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoInspeccionDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.Iseet2DAO;
import eu.eurogestion.ese.repository.Iseet3DAO;
import eu.eurogestion.ese.repository.IseetDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResultadoInspeccionDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class GenerarInformeISEETServiceImpl implements GenerarInformeISEETService {

	/** Repositories & Services **/

	@Autowired
	public ResultadoInspeccionDAO resultadoInspeccionDAO;

	@Autowired
	public IseetDAO iseetDAO;

	@Autowired
	public Iseet2DAO iseet2DAO;

	@Autowired
	public Iseet3DAO iseet3DAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public TrenDAO trenDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EstadoInspeccionDAO estadoInspeccionDAO;

	@Autowired
	public TareaPendienteDAO tareaPendienteDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	public void firmaISEETInspectorSeguridad(GenerarInformeISEETJSP generarInformeISEETJSP, HttpSession sesion,
			Model model) throws EseException {

		Iseet iseet = iseetDAO.getOne(Integer.parseInt(generarInformeISEETJSP.getIdISEET()));
		Is is = iseet.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISEET si no está cerrado su informe de anomalías.");
			throw new EseException(
					"No se puede cerrar la inspección ISEET si no está cerrado su informe de anomalías.");
		}
		iseet.getIseet3().setFirmaInspector(personalDAO.getOne(generarInformeISEETJSP.getIdFirmaInspectorSeguridad()));
		iseet.getIseet3().setFechahoraFirmaInspector(new Date());
		iseet3DAO.save(iseet.getIseet3());

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (iseet.getIseet3().getFirmaResponsable() != null) {
			finalizarInformeISEET(iseet, sesion, model, mensajeInfo);
			generarInformeISEETJSP.setIdEstadoISEET(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	public void firmaISEETResponsableSeguridad(GenerarInformeISEETJSP generarInformeISEETJSP, HttpSession sesion,
			Model model) throws EseException {

		Iseet iseet = iseetDAO.getOne(Integer.parseInt(generarInformeISEETJSP.getIdISEET()));
		Is is = iseet.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISEET si no está cerrado su informe de anomalías.");
			throw new EseException(
					"No se puede cerrar la inspección ISEET si no está cerrado su informe de anomalías.");
		}
		iseet.getIseet3()
				.setFirmaResponsable(personalDAO.getOne(generarInformeISEETJSP.getIdFirmaResponsableSeguridad()));
		iseet.getIseet3().setFechahoraFirmaResponsable(new Date());
		iseet3DAO.save(iseet.getIseet3());

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (iseet.getIseet3().getFirmaInspector() != null) {
			finalizarInformeISEET(iseet, sesion, model, mensajeInfo);
			generarInformeISEETJSP.setIdEstadoISEET(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	private void finalizarInformeISEET(Iseet iseet, HttpSession sesion, Model model, List<String> mensajeInfo)
			throws EseException {

		iseet.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_FINALIZADA));
		iseetDAO.save(iseet);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicInsSegEquEleSegMatRodTra(iseet);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_FICHA_ISEET);

		Evidencia evidencia = utilesPDFService.crearEvidencia(iseet.getIs().getNumReferencia(), fichero, md5,
				tipoEvidencia, Utiles.sysdate(), Constantes.TYPE_PDF);

		iseet.setEvidencia17(evidencia);
		iseetDAO.save(iseet);

		cerrarTareaPendiente(iseet);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	private void cerrarTareaPendiente(Iseet iseet) {

		TareaPendiente tareaPendiente = tareaPendienteDAO.findTareaPendienteByReference(iseet.getIdIseet(),
				Constantes.TAREAPTE_GENERAR_ISEET);
		if (tareaPendiente != null) {
			tareaPendiente.setLeido(Boolean.TRUE);
			tareaPendienteDAO.save(tareaPendiente);
		}
	}

	@Override
	public void guardarInformeISEET(GenerarInformeISEETJSP generarInformeISEETJSP) throws EseException {

		Iseet iseet = convertGenerarInformeISEETJSPToISEET(generarInformeISEETJSP);

		if (Constantes.ESTADO_INSPECCION_PLANIFICADA
				.equals(iseet.getIs().getEstadoInspeccion().getIdEstadoInspeccion())) {
			iseet.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_REALIZADA));
		}

		iseet3DAO.save(iseet.getIseet3());
		iseet2DAO.save(iseet.getIseet2());
		iseetDAO.save(iseet);
	}

	@Override
	public GenerarInformeISEETJSP createGenerarInformeISEETJSPToIdISEET(Integer idISEET) {

		GenerarInformeISEETJSP generarInformeISEETJSP = new GenerarInformeISEETJSP();
		Iseet iseet = iseetDAO.getOne(idISEET);
		Iseet2 iseet2 = iseet.getIseet2();
		Iseet3 iseet3 = iseet.getIseet3();

		// GLOBALES
		generarInformeISEETJSP.setIdISEET(iseet.getIdIseet().toString());
		generarInformeISEETJSP.setIsLectura(Constantes.ESTADO_INSPECCION_FINALIZADA
				.equals(iseet.getIs().getEstadoInspeccion().getIdEstadoInspeccion()));
		generarInformeISEETJSP.setIsFirmado(
				iseet.getIseet3().getFirmaInspector() != null || iseet.getIseet3().getFirmaResponsable() != null);
		generarInformeISEETJSP.setIdEstadoISEET(iseet.getIs().getEstadoInspeccion().getIdEstadoInspeccion());

		// FRAGMENTO ISEET 1
		generarInformeISEETJSP.setNumeroReferencia(iseet.getIs().getNumReferencia());
		generarInformeISEETJSP.setFecha(
				Utiles.convertDateToString(iseet.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		generarInformeISEETJSP.setIdPersonalConduccion(
				iseet.getMaquinista() != null ? iseet.getMaquinista().getIdPersonal().toString() : null);
		generarInformeISEETJSP.setNveLocomotora(iseet.getNve());
		if (iseet.getIs().getTren() != null) {
			generarInformeISEETJSP.setIdTren(iseet.getIs().getTren().getIdTren().toString());
		}
		generarInformeISEETJSP.setLugarInspeccion(iseet.getIs().getLugar());
		generarInformeISEETJSP.setObservacionesCirculacionFerroviaria(iseet.getObservacionesCirculacion());

		// FRAGMENTO ISEET 2
		generarInformeISEETJSP.setIdResultadoCaja1(
				iseet.getCaja1() != null ? iseet.getCaja1().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoCaja2(
				iseet.getCaja2() != null ? iseet.getCaja2().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoCaja3(
				iseet.getCaja3() != null ? iseet.getCaja3().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoCaja4(
				iseet.getCaja4() != null ? iseet.getCaja4().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoCaja5(
				iseet.getCaja5() != null ? iseet.getCaja5().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoCaja6(
				iseet.getCaja6() != null ? iseet.getCaja6().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoCaja7(
				iseet.getCaja7() != null ? iseet.getCaja7().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setObservacionesCaja(iseet.getObservacionesCaja());

		// FRAGMENTO ISEET 3
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion8(
				iseet.getElementosChoque8() != null ? iseet.getElementosChoque8().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion9(
				iseet.getElementosChoque9() != null ? iseet.getElementosChoque9().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion10(iseet.getElementosChoque10() != null
				? iseet.getElementosChoque10().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion11(iseet.getElementosChoque11() != null
				? iseet.getElementosChoque11().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion12(iseet.getElementosChoque12() != null
				? iseet.getElementosChoque12().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion13(iseet.getElementosChoque13() != null
				? iseet.getElementosChoque13().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion14(iseet.getElementosChoque14() != null
				? iseet.getElementosChoque14().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion15(iseet.getElementosChoque15() != null
				? iseet.getElementosChoque15().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion16(iseet.getElementosChoque16() != null
				? iseet.getElementosChoque16().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion17(iseet.getElementosChoque17() != null
				? iseet.getElementosChoque17().getIdResultadoInspeccion().toString()
				: null);

		// FRAGMENTO ISEET 4
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion18(iseet.getElementosChoque18() != null
				? iseet.getElementosChoque18().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion19(iseet.getElementosChoque19() != null
				? iseet.getElementosChoque19().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion20(iseet.getElementosChoque20() != null
				? iseet.getElementosChoque20().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion21(iseet.getElementosChoque21() != null
				? iseet.getElementosChoque21().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion22(iseet.getElementosChoque22() != null
				? iseet.getElementosChoque22().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion23(iseet.getElementosChoque23() != null
				? iseet.getElementosChoque23().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoElementosChoqueTraccion24(iseet.getElementosChoque24() != null
				? iseet.getElementosChoque24().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setObservacionesElementosChoqueTraccion(iseet.getObservacionesElementosChoque());

		// FRAGMENTO ISEET 5
		generarInformeISEETJSP.setIdResultadoBogies25(
				iseet.getBogies25() != null ? iseet.getBogies25().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoBogies26(
				iseet.getBogies26() != null ? iseet.getBogies26().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoBogies27(
				iseet.getBogies27() != null ? iseet.getBogies27().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoBogies28(
				iseet.getBogies28() != null ? iseet.getBogies28().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setObservacionesBogies(iseet.getObservacionesBogies());

		// FRAGMENTO ISEET 6
		generarInformeISEETJSP.setIdResultadoSuspension29(
				iseet.getSuspension29() != null ? iseet.getSuspension29().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoSuspension30(
				iseet.getSuspension30() != null ? iseet.getSuspension30().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoSuspension31(
				iseet.getSuspension31() != null ? iseet.getSuspension31().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoSuspension32(
				iseet.getSuspension32() != null ? iseet.getSuspension32().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoSuspension33(
				iseet.getSuspension33() != null ? iseet.getSuspension33().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setObservacionesSuspension(iseet.getObservacionesSuspension());

		// FRAGMENTO ISEET 7
		generarInformeISEETJSP.setIdResultadoRodaje34(
				iseet.getRodaje34() != null ? iseet.getRodaje34().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje35(
				iseet.getRodaje35() != null ? iseet.getRodaje35().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje36(
				iseet.getRodaje36() != null ? iseet.getRodaje36().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje37(
				iseet.getRodaje37() != null ? iseet.getRodaje37().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje38(
				iseet.getRodaje38() != null ? iseet.getRodaje38().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje39(
				iseet.getRodaje39() != null ? iseet.getRodaje39().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje40(
				iseet.getRodaje40() != null ? iseet.getRodaje40().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje41(
				iseet.getRodaje41() != null ? iseet.getRodaje41().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje42(
				iseet.getRodaje42() != null ? iseet.getRodaje42().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje43(
				iseet.getRodaje43() != null ? iseet.getRodaje43().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje44(
				iseet.getRodaje44() != null ? iseet.getRodaje44().getIdResultadoInspeccion().toString() : null);

		// FRAGMENTO ISEET 8
		generarInformeISEETJSP.setIdResultadoRodaje45(
				iseet.getRodaje45() != null ? iseet.getRodaje45().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje46(
				iseet.getRodaje46() != null ? iseet.getRodaje46().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje47(
				iseet.getRodaje47() != null ? iseet.getRodaje47().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje48(
				iseet.getRodaje48() != null ? iseet.getRodaje48().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje49(
				iseet.getRodaje49() != null ? iseet.getRodaje49().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje50(
				iseet.getRodaje50() != null ? iseet.getRodaje50().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoRodaje51(
				iseet.getRodaje51() != null ? iseet.getRodaje51().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setObservacionesRodaje(iseet.getObservacionesRodaje());

		// FRAGMENTO ISEET 9
		generarInformeISEETJSP.setIdResultadoFreno52(
				iseet2.getFreno52() != null ? iseet2.getFreno52().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno53(
				iseet2.getFreno53() != null ? iseet2.getFreno53().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno54(
				iseet2.getFreno54() != null ? iseet2.getFreno54().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno55(
				iseet2.getFreno55() != null ? iseet2.getFreno55().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno56(
				iseet2.getFreno56() != null ? iseet2.getFreno56().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno57(
				iseet2.getFreno57() != null ? iseet2.getFreno57().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno58(
				iseet2.getFreno58() != null ? iseet2.getFreno58().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno59(
				iseet2.getFreno59() != null ? iseet2.getFreno59().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno60(
				iseet2.getFreno60() != null ? iseet2.getFreno60().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno61(
				iseet2.getFreno61() != null ? iseet2.getFreno61().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno62(
				iseet2.getFreno62() != null ? iseet2.getFreno62().getIdResultadoInspeccion().toString() : null);

		// FRAGMENTO ISEET 10
		generarInformeISEETJSP.setIdResultadoFreno63(
				iseet2.getFreno63() != null ? iseet2.getFreno63().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno64(
				iseet2.getFreno64() != null ? iseet2.getFreno64().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno65(
				iseet2.getFreno65() != null ? iseet2.getFreno65().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno66(
				iseet2.getFreno66() != null ? iseet2.getFreno66().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno67(
				iseet2.getFreno67() != null ? iseet2.getFreno67().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno68(
				iseet2.getFreno68() != null ? iseet2.getFreno68().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno69(
				iseet2.getFreno69() != null ? iseet2.getFreno69().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno70(
				iseet2.getFreno70() != null ? iseet2.getFreno70().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno71(
				iseet2.getFreno71() != null ? iseet2.getFreno71().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno72(
				iseet2.getFreno72() != null ? iseet2.getFreno72().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno73(
				iseet2.getFreno73() != null ? iseet2.getFreno73().getIdResultadoInspeccion().toString() : null);

		// FRAGMENTO ISEET 11
		generarInformeISEETJSP.setIdResultadoFreno74(
				iseet2.getFreno74() != null ? iseet2.getFreno74().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno75(
				iseet2.getFreno75() != null ? iseet2.getFreno75().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno76(
				iseet2.getFreno76() != null ? iseet2.getFreno76().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno77(
				iseet2.getFreno77() != null ? iseet2.getFreno77().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno78(
				iseet2.getFreno78() != null ? iseet2.getFreno78().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno79(
				iseet2.getFreno79() != null ? iseet2.getFreno79().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno80(
				iseet2.getFreno80() != null ? iseet2.getFreno80().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno81(
				iseet2.getFreno81() != null ? iseet2.getFreno81().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno82(
				iseet2.getFreno82() != null ? iseet2.getFreno82().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno83(
				iseet2.getFreno83() != null ? iseet2.getFreno83().getIdResultadoInspeccion().toString() : null);

		// FRAGMENTO ISEET 12
		generarInformeISEETJSP.setIdResultadoFreno84(
				iseet2.getFreno84() != null ? iseet2.getFreno84().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno85(
				iseet2.getFreno85() != null ? iseet2.getFreno85().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno86(
				iseet2.getFreno86() != null ? iseet2.getFreno86().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno87(
				iseet2.getFreno87() != null ? iseet2.getFreno87().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno88(
				iseet2.getFreno88() != null ? iseet2.getFreno88().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno89(
				iseet2.getFreno89() != null ? iseet2.getFreno89().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno90(
				iseet2.getFreno90() != null ? iseet2.getFreno90().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno91(
				iseet2.getFreno91() != null ? iseet2.getFreno91().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setIdResultadoFreno92(
				iseet2.getFreno92() != null ? iseet2.getFreno92().getIdResultadoInspeccion().toString() : null);
		generarInformeISEETJSP.setObservacionesFreno(iseet2.getObservacionesFreno());

		// FRAGMENTO ISEET 13
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl93(
				iseet2.getVigilancia93() != null ? iseet2.getVigilancia93().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl94(
				iseet2.getVigilancia94() != null ? iseet2.getVigilancia94().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl95(
				iseet2.getVigilancia95() != null ? iseet2.getVigilancia95().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl96(
				iseet2.getVigilancia96() != null ? iseet2.getVigilancia96().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl97(
				iseet2.getVigilancia97() != null ? iseet2.getVigilancia97().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl98(
				iseet2.getVigilancia98() != null ? iseet2.getVigilancia98().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl99(
				iseet2.getVigilancia99() != null ? iseet2.getVigilancia99().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl100(
				iseet2.getVigilancia100() != null ? iseet2.getVigilancia100().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl101(
				iseet2.getVigilancia101() != null ? iseet2.getVigilancia101().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl102(
				iseet2.getVigilancia102() != null ? iseet2.getVigilancia102().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoEquipoVigilanciaControl103(
				iseet2.getVigilancia103() != null ? iseet2.getVigilancia103().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setObservacionesEquipoVigilanciaControl(iseet2.getObservacionesVigilancia());

		// FRAGMENTO ISEET 14
		generarInformeISEETJSP.setIdResultadoAlumbrado104(
				iseet2.getAlumbrado104() != null ? iseet2.getAlumbrado104().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoAlumbrado105(
				iseet2.getAlumbrado105() != null ? iseet2.getAlumbrado105().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoAlumbrado106(
				iseet2.getAlumbrado106() != null ? iseet2.getAlumbrado106().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoAlumbrado107(
				iseet2.getAlumbrado107() != null ? iseet2.getAlumbrado107().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoAlumbrado108(
				iseet2.getAlumbrado108() != null ? iseet2.getAlumbrado108().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoAlumbrado109(
				iseet2.getAlumbrado109() != null ? iseet2.getAlumbrado109().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setObservacionesAlumbrado(iseet2.getObservacionesAlumbrado());

		generarInformeISEETJSP.setIdResultadoPantografo110(
				iseet3.getPantografo110() != null ? iseet3.getPantografo110().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoPantografo111(
				iseet3.getPantografo111() != null ? iseet3.getPantografo111().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoPantografo112(
				iseet3.getPantografo112() != null ? iseet3.getPantografo112().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setIdResultadoPantografo113(
				iseet3.getPantografo113() != null ? iseet3.getPantografo113().getIdResultadoInspeccion().toString()
						: null);
		generarInformeISEETJSP.setObservacionesPantografo(iseet3.getObservacionesPantografo());

		// FRAGMENTO ISEET 15
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor114(iseet3.getCompartimento114() != null
				? iseet3.getCompartimento114().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor115(iseet3.getCompartimento115() != null
				? iseet3.getCompartimento115().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor116(iseet3.getCompartimento116() != null
				? iseet3.getCompartimento116().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor117(iseet3.getCompartimento117() != null
				? iseet3.getCompartimento117().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor118(iseet3.getCompartimento118() != null
				? iseet3.getCompartimento118().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor119(iseet3.getCompartimento119() != null
				? iseet3.getCompartimento119().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor120(iseet3.getCompartimento120() != null
				? iseet3.getCompartimento120().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoCompartimientoMotor121(iseet3.getCompartimento121() != null
				? iseet3.getCompartimento121().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setObservacionesCompartimientoMotor(iseet3.getObservacionesCompartimento());

		// FRAGMENTO ISEET 16
		generarInformeISEETJSP.setIdResultadoFuncionalidad122(iseet3.getFuncionalidad122() != null
				? iseet3.getFuncionalidad122().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoFuncionalidad123(iseet3.getFuncionalidad123() != null
				? iseet3.getFuncionalidad123().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoFuncionalidad124(iseet3.getFuncionalidad124() != null
				? iseet3.getFuncionalidad124().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoFuncionalidad125(iseet3.getFuncionalidad125() != null
				? iseet3.getFuncionalidad125().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setIdResultadoFuncionalidad126(iseet3.getFuncionalidad126() != null
				? iseet3.getFuncionalidad126().getIdResultadoInspeccion().toString()
				: null);
		generarInformeISEETJSP.setObservacionesFuncionalidad(iseet3.getObservacionesFuncionalidad());

		generarInformeISEETJSP.setMedidasCautelaresAdoptadas(iseet3.getMedidasCautelares());
		generarInformeISEETJSP.setListaDocumentosAnexos(iseet3.getDocumentosAnexos());

		// FRAGMENTO ISEET 17
		if (iseet3.getFirmaInspector() != null) {
			generarInformeISEETJSP.setIdFirmaInspectorSeguridad(iseet3.getFirmaInspector().getIdPersonal());
		}
		if (iseet3.getFirmaResponsable() != null) {
			generarInformeISEETJSP.setIdFirmaResponsableSeguridad(iseet3.getFirmaResponsable().getIdPersonal());
		}

		return generarInformeISEETJSP;
	}

	private Iseet convertGenerarInformeISEETJSPToISEET(GenerarInformeISEETJSP generarInformeISEETJSP) {

		Map<String, ResultadoInspeccion> mapaEstados = new HashMap<>();
		mapaEstados.put(Constantes.RESULTADO_INSPECCION_CRITICO.toString(),
				resultadoInspeccionDAO.getOne(Constantes.RESULTADO_INSPECCION_CRITICO));
		mapaEstados.put(Constantes.RESULTADO_INSPECCION_IMPORTANTE.toString(),
				resultadoInspeccionDAO.getOne(Constantes.RESULTADO_INSPECCION_IMPORTANTE));
		mapaEstados.put(Constantes.RESULTADO_INSPECCION_LEVE.toString(),
				resultadoInspeccionDAO.getOne(Constantes.RESULTADO_INSPECCION_LEVE));
		mapaEstados.put(Constantes.RESULTADO_INSPECCION_SATISFACTORIO.toString(),
				resultadoInspeccionDAO.getOne(Constantes.RESULTADO_INSPECCION_SATISFACTORIO));
		mapaEstados.put(Constantes.RESULTADO_INSPECCION_NO_APLICA.toString(),
				resultadoInspeccionDAO.getOne(Constantes.RESULTADO_INSPECCION_NO_APLICA));

		Iseet iseet = iseetDAO.getOne(Integer.parseInt(generarInformeISEETJSP.getIdISEET()));

		// FRAGMENTO ISEET 1
		iseet.setMaquinista(personalDAO.getOne(Integer.parseInt(generarInformeISEETJSP.getIdPersonalConduccion())));
		iseet.setNve(generarInformeISEETJSP.getNveLocomotora());
		iseet.getIs().setFechaInspeccion(Utiles.parseDatePantalla(generarInformeISEETJSP.getFecha()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getIdTren())) {
			iseet.getIs().setTren(trenDAO.getOne(Integer.parseInt(generarInformeISEETJSP.getIdTren())));

		}
		iseet.getIs().setLugar(generarInformeISEETJSP.getLugarInspeccion());
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesCirculacionFerroviaria())) {
			iseet.setObservacionesCirculacion(generarInformeISEETJSP.getObservacionesCirculacionFerroviaria());
		}

		// FRAGMENTO ISEET 2
		iseet.setCaja1(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCaja1()));
		iseet.setCaja2(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCaja1()));
		iseet.setCaja3(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCaja1()));
		iseet.setCaja4(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCaja1()));
		iseet.setCaja5(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCaja1()));
		iseet.setCaja6(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCaja1()));
		iseet.setCaja7(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCaja1()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesCaja())) {
			iseet.setObservacionesCaja(generarInformeISEETJSP.getObservacionesCaja());
		}

		// FRAGMENTO ISEET 3
		iseet.setElementosChoque8(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion8()));
		iseet.setElementosChoque9(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion9()));
		iseet.setElementosChoque10(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion10()));
		iseet.setElementosChoque11(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion11()));
		iseet.setElementosChoque12(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion12()));
		iseet.setElementosChoque13(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion13()));
		iseet.setElementosChoque14(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion14()));
		iseet.setElementosChoque15(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion15()));
		iseet.setElementosChoque16(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion16()));
		iseet.setElementosChoque17(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion17()));

		// FRAGMENTO ISEET 4
		iseet.setElementosChoque18(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion18()));
		iseet.setElementosChoque19(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion19()));
		iseet.setElementosChoque20(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion20()));
		iseet.setElementosChoque21(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion21()));
		iseet.setElementosChoque22(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion22()));
		iseet.setElementosChoque23(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion23()));
		iseet.setElementosChoque24(mapaEstados.get(generarInformeISEETJSP.getIdResultadoElementosChoqueTraccion24()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesElementosChoqueTraccion())) {
			iseet.setObservacionesElementosChoque(generarInformeISEETJSP.getObservacionesElementosChoqueTraccion());
		}

		// FRAGMENTO ISEET 5

		iseet.setBogies25(mapaEstados.get(generarInformeISEETJSP.getIdResultadoBogies25()));
		iseet.setBogies26(mapaEstados.get(generarInformeISEETJSP.getIdResultadoBogies26()));
		iseet.setBogies27(mapaEstados.get(generarInformeISEETJSP.getIdResultadoBogies27()));
		iseet.setBogies28(mapaEstados.get(generarInformeISEETJSP.getIdResultadoBogies28()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesBogies())) {
			iseet.setObservacionesBogies(generarInformeISEETJSP.getObservacionesBogies());
		}

		// FRAGMENTO ISEET 6
		iseet.setSuspension29(mapaEstados.get(generarInformeISEETJSP.getIdResultadoSuspension29()));
		iseet.setSuspension30(mapaEstados.get(generarInformeISEETJSP.getIdResultadoSuspension30()));
		iseet.setSuspension31(mapaEstados.get(generarInformeISEETJSP.getIdResultadoSuspension31()));
		iseet.setSuspension32(mapaEstados.get(generarInformeISEETJSP.getIdResultadoSuspension32()));
		iseet.setSuspension33(mapaEstados.get(generarInformeISEETJSP.getIdResultadoSuspension33()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesSuspension())) {
			iseet.setObservacionesSuspension(generarInformeISEETJSP.getObservacionesSuspension());
		}

		// FRAGMENTO ISEET 7
		iseet.setRodaje34(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje34()));
		iseet.setRodaje35(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje35()));
		iseet.setRodaje36(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje36()));
		iseet.setRodaje37(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje37()));
		iseet.setRodaje38(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje38()));
		iseet.setRodaje39(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje39()));
		iseet.setRodaje40(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje40()));
		iseet.setRodaje41(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje41()));
		iseet.setRodaje42(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje42()));
		iseet.setRodaje43(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje43()));
		iseet.setRodaje44(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje44()));

		// FRAGMENTO ISEET 8
		iseet.setRodaje45(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje45()));
		iseet.setRodaje46(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje46()));
		iseet.setRodaje47(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje47()));
		iseet.setRodaje48(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje48()));
		iseet.setRodaje49(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje49()));
		iseet.setRodaje50(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje50()));
		iseet.setRodaje51(mapaEstados.get(generarInformeISEETJSP.getIdResultadoRodaje51()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesRodaje())) {
			iseet.setObservacionesRodaje(generarInformeISEETJSP.getObservacionesRodaje());
		}

		// FRAGMENTO ISEET 9
		Iseet2 iseet2 = iseet.getIseet2();
		iseet2.setFreno52(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno52()));
		iseet2.setFreno53(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno53()));
		iseet2.setFreno54(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno54()));
		iseet2.setFreno55(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno55()));
		iseet2.setFreno56(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno56()));
		iseet2.setFreno57(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno57()));
		iseet2.setFreno58(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno58()));
		iseet2.setFreno59(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno59()));
		iseet2.setFreno60(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno60()));
		iseet2.setFreno61(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno61()));
		iseet2.setFreno62(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno62()));

		// FRAGMENTO ISEET 10
		iseet2.setFreno63(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno63()));
		iseet2.setFreno64(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno64()));
		iseet2.setFreno65(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno65()));
		iseet2.setFreno66(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno66()));
		iseet2.setFreno67(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno67()));
		iseet2.setFreno68(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno68()));
		iseet2.setFreno69(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno69()));
		iseet2.setFreno70(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno70()));
		iseet2.setFreno71(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno71()));
		iseet2.setFreno72(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno72()));
		iseet2.setFreno73(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno73()));

		// FRAGMENTO ISEET 11
		iseet2.setFreno74(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno74()));
		iseet2.setFreno75(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno75()));
		iseet2.setFreno76(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno76()));
		iseet2.setFreno77(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno77()));
		iseet2.setFreno78(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno78()));
		iseet2.setFreno79(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno79()));
		iseet2.setFreno80(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno80()));
		iseet2.setFreno81(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno81()));
		iseet2.setFreno82(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno82()));
		iseet2.setFreno83(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno83()));

		// FRAGMENTO ISEET 12
		iseet2.setFreno84(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno84()));
		iseet2.setFreno85(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno85()));
		iseet2.setFreno86(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno86()));
		iseet2.setFreno87(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno87()));
		iseet2.setFreno88(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno88()));
		iseet2.setFreno89(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno89()));
		iseet2.setFreno90(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno90()));
		iseet2.setFreno91(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno91()));
		iseet2.setFreno92(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFreno92()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesFreno())) {
			iseet2.setObservacionesFreno(generarInformeISEETJSP.getObservacionesFreno());
		}

		// FRAGMENTO ISEET 13
		iseet2.setVigilancia93(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl93()));
		iseet2.setVigilancia94(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl94()));
		iseet2.setVigilancia95(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl95()));
		iseet2.setVigilancia96(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl96()));
		iseet2.setVigilancia97(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl97()));
		iseet2.setVigilancia98(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl98()));
		iseet2.setVigilancia99(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl99()));
		iseet2.setVigilancia100(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl100()));
		iseet2.setVigilancia101(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl101()));
		iseet2.setVigilancia102(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl102()));
		iseet2.setVigilancia103(mapaEstados.get(generarInformeISEETJSP.getIdResultadoEquipoVigilanciaControl103()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesEquipoVigilanciaControl())) {
			iseet2.setObservacionesVigilancia(generarInformeISEETJSP.getObservacionesEquipoVigilanciaControl());
		}

		// FRAGMENTO ISEET 14
		iseet2.setAlumbrado104(mapaEstados.get(generarInformeISEETJSP.getIdResultadoAlumbrado104()));
		iseet2.setAlumbrado105(mapaEstados.get(generarInformeISEETJSP.getIdResultadoAlumbrado105()));
		iseet2.setAlumbrado106(mapaEstados.get(generarInformeISEETJSP.getIdResultadoAlumbrado106()));
		iseet2.setAlumbrado107(mapaEstados.get(generarInformeISEETJSP.getIdResultadoAlumbrado107()));
		iseet2.setAlumbrado108(mapaEstados.get(generarInformeISEETJSP.getIdResultadoAlumbrado108()));
		iseet2.setAlumbrado109(mapaEstados.get(generarInformeISEETJSP.getIdResultadoAlumbrado109()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesAlumbrado())) {
			iseet2.setObservacionesAlumbrado(generarInformeISEETJSP.getObservacionesAlumbrado());
		}

		Iseet3 iseet3 = iseet.getIseet3();
		iseet3.setPantografo110(mapaEstados.get(generarInformeISEETJSP.getIdResultadoPantografo110()));
		iseet3.setPantografo111(mapaEstados.get(generarInformeISEETJSP.getIdResultadoPantografo111()));
		iseet3.setPantografo112(mapaEstados.get(generarInformeISEETJSP.getIdResultadoPantografo112()));
		iseet3.setPantografo113(mapaEstados.get(generarInformeISEETJSP.getIdResultadoPantografo113()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesPantografo())) {
			iseet3.setObservacionesPantografo(generarInformeISEETJSP.getObservacionesPantografo());
		}

		// FRAGMENTO ISEET 15
		iseet3.setCompartimento114(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor114()));
		iseet3.setCompartimento115(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor115()));
		iseet3.setCompartimento116(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor116()));
		iseet3.setCompartimento117(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor117()));
		iseet3.setCompartimento118(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor118()));
		iseet3.setCompartimento119(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor119()));
		iseet3.setCompartimento120(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor120()));
		iseet3.setCompartimento121(mapaEstados.get(generarInformeISEETJSP.getIdResultadoCompartimientoMotor121()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesCompartimientoMotor())) {
			iseet3.setObservacionesCompartimento(generarInformeISEETJSP.getObservacionesCompartimientoMotor());
		}

		// FRAGMENTO ISEET 16
		iseet3.setFuncionalidad122(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFuncionalidad122()));
		iseet3.setFuncionalidad123(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFuncionalidad123()));
		iseet3.setFuncionalidad124(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFuncionalidad124()));
		iseet3.setFuncionalidad125(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFuncionalidad125()));
		iseet3.setFuncionalidad126(mapaEstados.get(generarInformeISEETJSP.getIdResultadoFuncionalidad126()));
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getObservacionesFuncionalidad())) {
			iseet3.setObservacionesFuncionalidad(generarInformeISEETJSP.getObservacionesFuncionalidad());
		}

		if (StringUtils.isNotBlank(generarInformeISEETJSP.getMedidasCautelaresAdoptadas())) {
			iseet3.setMedidasCautelares(generarInformeISEETJSP.getMedidasCautelaresAdoptadas());
		}
		if (StringUtils.isNotBlank(generarInformeISEETJSP.getListaDocumentosAnexos())) {
			iseet3.setDocumentosAnexos(generarInformeISEETJSP.getListaDocumentosAnexos());
		}

		iseet.setIseet2(iseet2);
		iseet.setIseet3(iseet3);
		return iseet;
	}

}
