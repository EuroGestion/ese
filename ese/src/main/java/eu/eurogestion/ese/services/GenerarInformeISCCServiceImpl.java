package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Is;
import eu.eurogestion.ese.domain.Iscc;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISCCJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoInspeccionDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.IsDAO;
import eu.eurogestion.ese.repository.IsccDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResultadoInspeccionDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class GenerarInformeISCCServiceImpl implements GenerarInformeISCCService {

	/** Repositories & Services **/

	@Autowired
	public ResultadoInspeccionDAO resultadoInspeccionDAO;

	@Autowired
	public IsccDAO isccDAO;

	@Autowired
	public IsDAO isDAO;

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

	public void firmaISCCInspectorSeguridad(GenerarInformeISCCJSP generarInformeISCCJSP, HttpSession sesion,
			Model model) throws EseException {
		Iscc iscc = isccDAO.getOne(Integer.parseInt(generarInformeISCCJSP.getIdISCC()));
		Is is = iscc.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISCC si no está cerrado su informe de anomalías.");
			throw new EseException("No se puede cerrar la inspección ISCC si no está cerrado su informe de anomalías.");
		}

		iscc.setFirmaInspector(personalDAO.getOne(generarInformeISCCJSP.getIdFirmaInspectorSeguridad()));
		iscc.setFechahoraFirmaInspector(new Date());
		isccDAO.save(iscc);
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		if (iscc.getFirmaResponsable() != null) {
			finalizarInformeISCC(iscc, sesion, model, mensajeInfo);
			generarInformeISCCJSP.setIdEstadoISCC(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}

	}

	public void firmaISCCResponsableSeguridad(GenerarInformeISCCJSP generarInformeISCCJSP, HttpSession sesion,
			Model model) throws EseException {
		Iscc iscc = isccDAO.getOne(Integer.parseInt(generarInformeISCCJSP.getIdISCC()));
		Is is = iscc.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISCC si no está cerrado su informe de anomalías.");
			throw new EseException("No se puede cerrar la inspección ISCC si no está cerrado su informe de anomalías.");
		}
		iscc.setFirmaResponsable(personalDAO.getOne(generarInformeISCCJSP.getIdFirmaResponsableSeguridad()));
		iscc.setFechahoraFirmaResponsable(new Date());
		isccDAO.save(iscc);
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		if (iscc.getFirmaInspector() != null) {
			finalizarInformeISCC(iscc, sesion, model, mensajeInfo);
			generarInformeISCCJSP.setIdEstadoISCC(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}

	}

	private void finalizarInformeISCC(Iscc iscc, HttpSession sesion, Model model, List<String> mensajeInfo)
			throws EseException {

		iscc.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_FINALIZADA));
		isDAO.save(iscc.getIs());

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicInsSegEstCarComFer(iscc);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_FICHA_ISCC);

		Evidencia evidencia = utilesPDFService.crearEvidencia(iscc.getIs().getNumReferencia(), fichero, md5,
				tipoEvidencia, Utiles.sysdate(), Constantes.TYPE_PDF);

		iscc.setEvidencia31(evidencia);
		isccDAO.save(iscc);

		cerrarTareaPendiente(iscc);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	private void cerrarTareaPendiente(Iscc iscc) {

		TareaPendiente tareaPendiente = tareaPendienteDAO.findTareaPendienteByReference(iscc.getIdIscc(),
				Constantes.TAREAPTE_GENERAR_ISCC);
		if (tareaPendiente != null) {
			tareaPendiente.setLeido(Boolean.TRUE);
			tareaPendienteDAO.save(tareaPendiente);
		}

	}

	@Override
	public void guardarInformeISCC(GenerarInformeISCCJSP generarInformeISCCJSP) throws EseException {

		Iscc iscc = convertGenerarInformeISCCJSPToISCC(generarInformeISCCJSP);

		if (Constantes.ESTADO_INSPECCION_PLANIFICADA
				.equals(iscc.getIs().getEstadoInspeccion().getIdEstadoInspeccion())) {
			iscc.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_REALIZADA));
		}

		isDAO.save(iscc.getIs());
		isccDAO.save(iscc);

	}

	@Override
	public GenerarInformeISCCJSP createGenerarInformeISCCJSPToIdISCC(Integer idISCC) {
		GenerarInformeISCCJSP generarInformeISCCJSP = new GenerarInformeISCCJSP();
		Iscc iscc = isccDAO.getOne(idISCC);

		// GLOBALES
		generarInformeISCCJSP.setIdISCC(iscc.getIdIscc().toString());
		generarInformeISCCJSP.setIsLectura(Constantes.ESTADO_INSPECCION_FINALIZADA
				.equals(iscc.getIs().getEstadoInspeccion().getIdEstadoInspeccion()));
		generarInformeISCCJSP.setIsFirmado(iscc.getFirmaInspector() != null || iscc.getFirmaResponsable() != null);
		generarInformeISCCJSP.setIdEstadoISCC(iscc.getIs().getEstadoInspeccion().getIdEstadoInspeccion());

		// FRAGMENTO ISCC 1

		generarInformeISCCJSP.setNumeroReferencia(iscc.getIs().getNumReferencia());
		generarInformeISCCJSP.setFecha(
				Utiles.convertDateToString(iscc.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));

		if (iscc.getIs().getTren() != null) {
			generarInformeISCCJSP.setIdTren(iscc.getIs().getTren().getIdTren().toString());
		}

		generarInformeISCCJSP.setLugarInspeccion(iscc.getIs().getLugar());
		generarInformeISCCJSP.setTiposMercancias(iscc.getTiposMercancias());
		generarInformeISCCJSP.setVagonesInspeccionadosLugar(iscc.getTraficoVagonesInspeccionado());
		generarInformeISCCJSP.setNumeroVagonesComposicion(iscc.getNumeroVagones());
		generarInformeISCCJSP.setObservacionesTrafico(iscc.getTraficoObservaciones());
		// FRAGMENTO ISCC 2

		generarInformeISCCJSP.setTransporteSeRealiza(iscc.getCargaTransporteRealizaObservaciones());
		generarInformeISCCJSP.setCohesionUnidades(iscc.getCargaCohesionObservaciones());
		generarInformeISCCJSP.setMercanciasCubiertas(iscc.getCargaMercanciasObservaciones());
		generarInformeISCCJSP.setCargaDistribucionCarga(iscc.getCargaDistribucion().toString());
		generarInformeISCCJSP.setCargaDistribucionCargaObservaciones(iscc.getCargaDistribucionObservaciones());
		generarInformeISCCJSP.setCargaCargasConcentradas(iscc.getCargaCargasConcentradas().toString());
		generarInformeISCCJSP.setCargaCargasConcentradasObservaciones(iscc.getCargaCargasConcentradasObservaciones());
		generarInformeISCCJSP.setCargaDistanciaBrida(iscc.getCargaDistancia().toString());
		generarInformeISCCJSP.setCargaDistanciaBridaObservaciones(iscc.getCargaDistanciaObservaciones());
		generarInformeISCCJSP.setCargaAlturaCentro(iscc.getCargaAltura().toString());
		generarInformeISCCJSP.setCargaAlturaCentroObservaciones(iscc.getCargaAlturaObservaciones());
		generarInformeISCCJSP.setCargaRebasaGalibo(iscc.getCargaRebasa().toString());
		generarInformeISCCJSP.setCargaRebasaGaliboObservaciones(iscc.getCargaRebasaObservaciones());
		generarInformeISCCJSP.setCargaRebasaLimites(iscc.getCargaCargamento().toString());
		generarInformeISCCJSP.setCargaRebasaLimitesObservaciones(iscc.getCargaCargamentoObservaciones());
		generarInformeISCCJSP.setObservacionesCarga(iscc.getCargaObservaciones());

		// FRAGMENTO ISCC 3

		generarInformeISCCJSP.setVagonesSeConservan(iscc.getVagonesConservan().toString());
		generarInformeISCCJSP.setVagonesSeConservanObservaciones(iscc.getVagonesConservanObservaciones());
		generarInformeISCCJSP.setVagonesCerradosTechos(iscc.getVagonesCerradosTechos().toString());
		generarInformeISCCJSP.setVagonesCerradosTechosObservaciones(iscc.getVagonesCerradosTechosObservaciones());
		generarInformeISCCJSP.setVagonesCerradasPuertas(iscc.getVagonesCerradasPuertas().toString());
		generarInformeISCCJSP.setVagonesCerradasPuertasObservaciones(iscc.getVagonesCerradasPuertasObservaciones());
		generarInformeISCCJSP.setVagonesCerradasTapas(iscc.getVagonesCerradasTapas().toString());
		generarInformeISCCJSP.setVagonesCerradasTapasObservaciones(iscc.getVagonesCerradasTapasObservaciones());
		generarInformeISCCJSP.setVagonesCerradasCompuertas(iscc.getVagonesCerradasCompuertas().toString());
		generarInformeISCCJSP
				.setVagonesCerradasCompuertasObservaciones(iscc.getVagonesCerradasCompuertasObservaciones());
		generarInformeISCCJSP.setVagonesCerradasValvulas(iscc.getVagonesCerradasValvulas().toString());
		generarInformeISCCJSP.setVagonesCerradasValvulasObservaciones(iscc.getVagonesCerradasValvulasObservaciones());
		generarInformeISCCJSP.setVagonesBordesLevantados(iscc.getVagonesBordesLevantados().toString());
		generarInformeISCCJSP.setVagonesBordesLevantadosObservaciones(iscc.getVagonesBordesLevantadosObservaciones());
		generarInformeISCCJSP.setVagonesBordesBajados(iscc.getVagonesBordesBajados().toString());
		generarInformeISCCJSP.setVagonesBordesBajadosObservaciones(iscc.getVagonesBordesBajadosObservaciones());
		generarInformeISCCJSP.setVagonesBordesBajadosInmoviles(iscc.getVagonesBordesBajadosInmovil().toString());
		generarInformeISCCJSP
				.setVagonesBordesBajadosInmovilesObservaciones(iscc.getVagonesBordesBajadosInmovilObservaciones());
		generarInformeISCCJSP.setVagonesVisiblesInscripciones(iscc.getVagonesVisiblesInscripciones().toString());
		generarInformeISCCJSP
				.setVagonesVisiblesInscripcionesObservaciones(iscc.getVagonesVisiblesInscripcionesObservaciones());
		generarInformeISCCJSP.setVagonesTelerosAmovibles(iscc.getVagonesTelerosAmovibles().toString());
		generarInformeISCCJSP.setVagonesTelerosAmoviblesObservaciones(iscc.getVagonesTelerosAmoviblesObservaciones());
		generarInformeISCCJSP.setVagonesTelerosPivotantes(iscc.getVagonesTelerosPivotantes().toString());
		generarInformeISCCJSP.setVagonesTelerosPivotantesObservaciones(iscc.getVagonesTelerosPivotantesObservaciones());
		generarInformeISCCJSP
				.setVagonesTelerosPivotantesHorizontal(iscc.getVagonesTelerosPivotantesHorizontal().toString());
		generarInformeISCCJSP.setVagonesTelerosPivotantesHorizontalObservaciones(
				iscc.getVagonesTelerosPivotantesHorizontalObservaciones());
		generarInformeISCCJSP.setVagonesCadenasTeleros(iscc.getVagonesCadenasTeleros().toString());
		generarInformeISCCJSP.setVagonesCadenasTelerosObservaciones(iscc.getVagonesCadenasTelerosObservaciones());
		generarInformeISCCJSP.setVagonesDispositivosTension(iscc.getVagonesDispositivosTension().toString());
		generarInformeISCCJSP
				.setVagonesDispositivosTensionObservaciones(iscc.getVagonesDispositivosTensionObservaciones());

		// FRAGMENTO ISCC 4
		generarInformeISCCJSP.setVagonesPiso(iscc.getVagonesPiso().toString());
		generarInformeISCCJSP.setVagonesPisoObservaciones(iscc.getVagonesPisoObservaciones());
		generarInformeISCCJSP.setVagonesMercancias(iscc.getVagonesMercancias().toString());
		generarInformeISCCJSP.setVagonesMercanciasObservaciones(iscc.getVagonesMercanciasObservaciones());
		generarInformeISCCJSP.setVagonesParedes(iscc.getVagonesParedes().toString());
		generarInformeISCCJSP.setVagonesParedesObservaciones(iscc.getVagonesParedesObservaciones());
		generarInformeISCCJSP.setVagonesPuertas(iscc.getVagonesPuertas().toString());
		generarInformeISCCJSP.setVagonesPuertasObservaciones(iscc.getVagonesPuertasObservaciones());
		generarInformeISCCJSP.setVagonesAnillas(iscc.getVagonesAnillas().toString());
		generarInformeISCCJSP.setVagonesAnillasObservaciones(iscc.getVagonesAnillasObservaciones());
		generarInformeISCCJSP.setVagonesEquipamiento(iscc.getVagonesEquipamientos().toString());
		generarInformeISCCJSP.setVagonesEquipamientoObservaciones(iscc.getVagonesEquipamientosObservaciones());
		generarInformeISCCJSP.setObservacionesVagones(iscc.getVagonesObservaciones());

		// FRAGMENTO ISCC 5

		generarInformeISCCJSP.setAcondicionamientoReparto(iscc.getAcondicionamientoReparto().toString());
		generarInformeISCCJSP.setAcondicionamientoRepartoObservaciones(iscc.getAcondicionamientoRepartoObservaciones());
		generarInformeISCCJSP.setAcondicionamientoMercancias(iscc.getAcondicionamientoMercancias().toString());
		generarInformeISCCJSP
				.setAcondicionamientoMercanciasObservaciones(iscc.getAcondicionamientoMercanciasObservaciones());
		generarInformeISCCJSP.setAcondicionamientoPesadas(iscc.getAcondicionamientoPesadas().toString());
		generarInformeISCCJSP.setAcondicionamientoPesadasObservaciones(iscc.getAcondicionamientoPesadasObservaciones());
		generarInformeISCCJSP.setAcondicionamientoSusceptibles(iscc.getAcondicionamientoSusceptibles().toString());
		generarInformeISCCJSP
				.setAcondicionamientoSusceptiblesObservaciones(iscc.getAcondicionamientoSusceptiblesObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoSolidamenteSujeta(iscc.getAcondicionamientoSolidamenteSujeta().toString());
		generarInformeISCCJSP.setAcondicionamientoSolidamenteSujetaObservaciones(
				iscc.getAcondicionamientoSolidamenteSujetaObservaciones());
		generarInformeISCCJSP.setAcondicionamientoLonaProxima(iscc.getAcondicionamientoLonaProxima().toString());
		generarInformeISCCJSP
				.setAcondicionamientoLonaProximaObservaciones(iscc.getAcondicionamientoLonaProximaObservaciones());
		generarInformeISCCJSP.setAcondicionamientoLonaSeparacion(iscc.getAcondicionamientoLonaSeparacion().toString());
		generarInformeISCCJSP.setAcondicionamientoLonaSeparacionObservaciones(
				iscc.getAcondicionamientoLonaSeparacionObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarParedes(iscc.getAcondicionamientoVerificarParedes().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarParedesObservaciones(
				iscc.getAcondicionamientoVerificarParedesObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarCalces(iscc.getAcondicionamientoVerificarCalces().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarCalcesObservaciones(
				iscc.getAcondicionamientoVerificarCalcesObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarAmarre(iscc.getAcondicionamientoVerificarAmarre().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarAmarreObservaciones(
				iscc.getAcondicionamientoVerificarAmarreObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarDosAmarras(iscc.getAcondicionamientoVerificarDosAmarras().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarDosAmarrasObservaciones(
				iscc.getAcondicionamientoVerificarDosAmarrasObservaciones());

		// FRAGMENTO ISCC 6
		generarInformeISCCJSP
				.setAcondicionamientoVerificarCalces7(iscc.getAcondicionamientoVerificarCalces7().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarCalces7Observaciones(
				iscc.getAcondicionamientoVerificarCalces7Observaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarBastidores(iscc.getAcondicionamientoVerificarBastidores().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarBastidoresObservaciones(
				iscc.getAcondicionamientoVerificarBastidoresObservaciones());
		generarInformeISCCJSP.setAcondicionamientoVerificarCunas(iscc.getAcondicionamientoVerificarCunas().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarCunasObservaciones(
				iscc.getAcondicionamientoVerificarCunasObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarTransversal(iscc.getAcondicionamientoVerificarTransversal().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarTransversalObservaciones(
				iscc.getAcondicionamientoVerificarTransversalObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarLongitudinal(iscc.getAcondicionamientoVerificarLongitudinal().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarLongitudinalObservaciones(
				iscc.getAcondicionamientoVerificarLongitudinalObservaciones());
		generarInformeISCCJSP.setAcondicionamientoVerificarBobinasMenos10(
				iscc.getAcondicionamientoVerificarBobinasMenos10().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarBobinasMenos10Observaciones(
				iscc.getAcondicionamientoVerificarBobinasMenos10Observaciones());
		generarInformeISCCJSP
				.setAcondicionamientoVerificarBobinasMas10(iscc.getAcondicionamientoVerificarBobinasMas10().toString());
		generarInformeISCCJSP.setAcondicionamientoVerificarBobinasMas10Observaciones(
				iscc.getAcondicionamientoVerificarBobinasMas10Observaciones());
		generarInformeISCCJSP
				.setAcondicionamientoBascularAgrupamiento(iscc.getAcondicionamientoBascularAgrupamiento().toString());
		generarInformeISCCJSP.setAcondicionamientoBascularAgrupamientoObservaciones(
				iscc.getAcondicionamientoBascularAgrupamientoObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoBascularPuntales(iscc.getAcondicionamientoBascularPuntales().toString());
		generarInformeISCCJSP.setAcondicionamientoBascularPuntalesObservaciones(
				iscc.getAcondicionamientoBascularPuntalesObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoBascularCaballetes(iscc.getAcondicionamientoBascularCaballetes().toString());
		generarInformeISCCJSP.setAcondicionamientoBascularCaballetesObservaciones(
				iscc.getAcondicionamientoBascularCaballetesObservaciones());

		// FRAGMENTO ISCC 7
		generarInformeISCCJSP
				.setAcondicionamientoApiladaRepartidas(iscc.getAcondicionamientoApiladaRepartidas().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaRepartidasObservaciones(
				iscc.getAcondicionamientoApiladaRepartidasObservaciones());
		generarInformeISCCJSP.setAcondicionamientoApiladaAnchura(iscc.getAcondicionamientoApiladaAnchura().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaAnchuraObservaciones(
				iscc.getAcondicionamientoApiladaAnchuraObservaciones());
		generarInformeISCCJSP.setAcondicionamientoApiladaCapas(iscc.getAcondicionamientoApiladaCapas().toString());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaCapasObservaciones(iscc.getAcondicionamientoApiladaCapasObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaRozamiento(iscc.getAcondicionamientoApiladaRozamiento().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaRozamientoObservaciones(
				iscc.getAcondicionamientoApiladaRozamientoObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaBastidores(iscc.getAcondicionamientoApiladaBastidores().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaBastidoresObservaciones(
				iscc.getAcondicionamientoApiladaBastidoresObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaUnidades(iscc.getAcondicionamientoApiladaUnidades().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaUnidadesObservaciones(
				iscc.getAcondicionamientoApiladaUnidadesObservaciones());
		generarInformeISCCJSP.setAcondicionamientoApiladaCintas(iscc.getAcondicionamientoApiladaCintas().toString());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaCintasObservaciones(iscc.getAcondicionamientoApiladaCintasObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaParteInferior(iscc.getAcondicionamientoApiladaParteInferior().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaParteInferiorObservaciones(
				iscc.getAcondicionamientoApiladaParteInferiorObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaIntercalares(iscc.getAcondicionamientoApiladaIntercalares().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaIntercalaresObservaciones(
				iscc.getAcondicionamientoApiladaIntercalaresObservaciones());

		// FRAGMENTO ISCC 8
		generarInformeISCCJSP
				.setAcondicionamientoApiladaImbricadas(iscc.getAcondicionamientoApiladaImbricadas().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaImbricadasObservaciones(
				iscc.getAcondicionamientoApiladaImbricadasObservaciones());
		generarInformeISCCJSP.setAcondicionamientoApiladaOscilar(iscc.getAcondicionamientoApiladaOscilar().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaOscilarObservaciones(
				iscc.getAcondicionamientoApiladaOscilarObservaciones());
		generarInformeISCCJSP.setAcondicionamientoApiladaTubos(iscc.getAcondicionamientoApiladaTubos().toString());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaTubosObservaciones(iscc.getAcondicionamientoApiladaTubosObservaciones());
		generarInformeISCCJSP
				.setAcondicionamientoApiladaCapasCalces(iscc.getAcondicionamientoApiladaCapasCalces().toString());
		generarInformeISCCJSP.setAcondicionamientoApiladaCapasCalcesObservaciones(
				iscc.getAcondicionamientoApiladaCapasCalcesObservaciones());
		generarInformeISCCJSP.setAcondicionamientoRigidas(iscc.getAcondicionamientoRigidas().toString());
		generarInformeISCCJSP.setAcondicionamientoRigidasObservaciones(iscc.getAcondicionamientoRigidasObservaciones());
		generarInformeISCCJSP.setAcondicionamientoFlexibles(iscc.getAcondicionamientoFlexibles().toString());
		generarInformeISCCJSP
				.setAcondicionamientoFlexiblesObservaciones(iscc.getAcondicionamientoFlexiblesObservaciones());
		generarInformeISCCJSP.setObservacionesAcondicionamiento(iscc.getAcondicionamientoObservaciones());

		// FRAGMENTO ISCC 9
		generarInformeISCCJSP.setMediosCalcesRetencion(iscc.getMediosCalcesRetencion().toString());
		generarInformeISCCJSP.setMediosCalcesRetencionObservaciones(iscc.getMediosCalcesRetencionObservaciones());
		generarInformeISCCJSP.setMediosCalcesRodadura(iscc.getMediosCalcesRodadura().toString());
		generarInformeISCCJSP.setMediosCalcesRodaduraObservaciones(iscc.getMediosCalcesRodaduraObservaciones());
		generarInformeISCCJSP.setMediosIntercalares(iscc.getMediosIntercalares().toString());
		generarInformeISCCJSP.setMediosIntercalaresObservaciones(iscc.getMediosIntercalaresObservaciones());
		generarInformeISCCJSP.setMediosAmarres(iscc.getMediosAmarres().toString());
		generarInformeISCCJSP.setMediosAmarresObservaciones(iscc.getMediosAmarresObservaciones());
		generarInformeISCCJSP.setObservacionesMedios(iscc.getMediosObservaciones());
		generarInformeISCCJSP.setMedidasCautelares(iscc.getMedidasCautelares());
		generarInformeISCCJSP.setListaAnexos(iscc.getListaAnexos());

		// FRAGMENTO ISCC 10

		if (iscc.getFirmaInspector() != null) {
			generarInformeISCCJSP.setIdFirmaInspectorSeguridad(iscc.getFirmaInspector().getIdPersonal());
		}

		if (iscc.getFirmaResponsable() != null) {
			generarInformeISCCJSP.setIdFirmaResponsableSeguridad(iscc.getFirmaResponsable().getIdPersonal());
		}

		return generarInformeISCCJSP;
	}

	private Iscc convertGenerarInformeISCCJSPToISCC(GenerarInformeISCCJSP generarInformeISCCJSP) {

		Iscc iscc = isccDAO.getOne(Integer.parseInt(generarInformeISCCJSP.getIdISCC()));
		Is is = isDAO.getOne(iscc.getIs().getIdIs());

		// FRAGMENTO IS
		is.setFechaInspeccion(Utiles.parseDatePantalla(generarInformeISCCJSP.getFecha()));
		is.setLugar(generarInformeISCCJSP.getLugarInspeccion());

		if (StringUtils.isNotBlank(generarInformeISCCJSP.getIdTren())) {
			is.setTren(trenDAO.getOne(Integer.parseInt(generarInformeISCCJSP.getIdTren())));
		}

		// FRAGMENTO ISCC 1

		if (StringUtils.isNotBlank(generarInformeISCCJSP.getTiposMercancias())) {
			iscc.setTiposMercancias(generarInformeISCCJSP.getTiposMercancias());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesInspeccionadosLugar())) {
			iscc.setTraficoVagonesInspeccionado(generarInformeISCCJSP.getVagonesInspeccionadosLugar());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getNumeroVagonesComposicion())) {
			iscc.setNumeroVagones(generarInformeISCCJSP.getNumeroVagonesComposicion());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getObservacionesTrafico())) {
			iscc.setTraficoObservaciones(generarInformeISCCJSP.getObservacionesTrafico());
		}
		// FRAGMENTO ISCC 2

		if (StringUtils.isNotBlank(generarInformeISCCJSP.getTransporteSeRealiza())) {
			iscc.setCargaTransporteRealizaObservaciones(generarInformeISCCJSP.getTransporteSeRealiza());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getCohesionUnidades())) {
			iscc.setCargaCohesionObservaciones(generarInformeISCCJSP.getCohesionUnidades());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getMercanciasCubiertas())) {
			iscc.setCargaMercanciasObservaciones(generarInformeISCCJSP.getMercanciasCubiertas());
		}
		iscc.setCargaDistribucion(Integer.parseInt(generarInformeISCCJSP.getCargaDistribucionCarga()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getCargaDistribucionCargaObservaciones())) {
			iscc.setCargaDistribucionObservaciones(generarInformeISCCJSP.getCargaDistribucionCargaObservaciones());
		}
		iscc.setCargaCargasConcentradas(Integer.parseInt(generarInformeISCCJSP.getCargaCargasConcentradas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getCargaCargasConcentradasObservaciones())) {
			iscc.setCargaCargasConcentradasObservaciones(
					generarInformeISCCJSP.getCargaCargasConcentradasObservaciones());
		}
		iscc.setCargaDistancia(Integer.parseInt(generarInformeISCCJSP.getCargaDistanciaBrida()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getCargaDistanciaBridaObservaciones())) {
			iscc.setCargaDistanciaObservaciones(generarInformeISCCJSP.getCargaDistanciaBridaObservaciones());
		}
		iscc.setCargaAltura(Integer.parseInt(generarInformeISCCJSP.getCargaAlturaCentro()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getCargaAlturaCentroObservaciones())) {
			iscc.setCargaAlturaObservaciones(generarInformeISCCJSP.getCargaAlturaCentroObservaciones());
		}
		iscc.setCargaRebasa(Integer.parseInt(generarInformeISCCJSP.getCargaRebasaGalibo()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getCargaRebasaGaliboObservaciones())) {
			iscc.setCargaRebasaObservaciones(generarInformeISCCJSP.getCargaRebasaGaliboObservaciones());
		}
		iscc.setCargaCargamento(Integer.parseInt(generarInformeISCCJSP.getCargaRebasaLimites()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getCargaRebasaLimitesObservaciones())) {
			iscc.setCargaCargamentoObservaciones(generarInformeISCCJSP.getCargaRebasaLimitesObservaciones());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getObservacionesCarga())) {
			iscc.setCargaObservaciones(generarInformeISCCJSP.getObservacionesCarga());
		}

		// FRAGMENTO ISCC 3

		iscc.setVagonesConservan(Integer.parseInt(generarInformeISCCJSP.getVagonesSeConservan()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesSeConservanObservaciones())) {
			iscc.setVagonesConservanObservaciones(generarInformeISCCJSP.getVagonesSeConservanObservaciones());
		}
		iscc.setVagonesCerradosTechos(Integer.parseInt(generarInformeISCCJSP.getVagonesCerradosTechos()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesCerradosTechosObservaciones())) {
			iscc.setVagonesCerradosTechosObservaciones(generarInformeISCCJSP.getVagonesCerradosTechosObservaciones());
		}
		iscc.setVagonesCerradasPuertas(Integer.parseInt(generarInformeISCCJSP.getVagonesCerradasPuertas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesCerradasPuertasObservaciones())) {
			iscc.setVagonesCerradasPuertasObservaciones(generarInformeISCCJSP.getVagonesCerradasPuertasObservaciones());
		}
		iscc.setVagonesCerradasTapas(Integer.parseInt(generarInformeISCCJSP.getVagonesCerradasTapas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesCerradasTapasObservaciones())) {
			iscc.setVagonesCerradasTapasObservaciones(generarInformeISCCJSP.getVagonesCerradasTapasObservaciones());
		}
		iscc.setVagonesCerradasCompuertas(Integer.parseInt(generarInformeISCCJSP.getVagonesCerradasCompuertas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesCerradasCompuertasObservaciones())) {
			iscc.setVagonesCerradasCompuertasObservaciones(
					generarInformeISCCJSP.getVagonesCerradasCompuertasObservaciones());
		}
		iscc.setVagonesCerradasValvulas(Integer.parseInt(generarInformeISCCJSP.getVagonesCerradasValvulas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesCerradasValvulasObservaciones())) {
			iscc.setVagonesCerradasValvulasObservaciones(
					generarInformeISCCJSP.getVagonesCerradasValvulasObservaciones());
		}
		iscc.setVagonesBordesLevantados(Integer.parseInt(generarInformeISCCJSP.getVagonesBordesLevantados()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesBordesLevantadosObservaciones())) {
			iscc.setVagonesBordesLevantadosObservaciones(
					generarInformeISCCJSP.getVagonesBordesLevantadosObservaciones());
		}
		iscc.setVagonesBordesBajados(Integer.parseInt(generarInformeISCCJSP.getVagonesBordesBajados()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesBordesBajadosObservaciones())) {
			iscc.setVagonesBordesBajadosObservaciones(generarInformeISCCJSP.getVagonesBordesBajadosObservaciones());
		}
		iscc.setVagonesBordesBajadosInmovil(Integer.parseInt(generarInformeISCCJSP.getVagonesBordesBajadosInmoviles()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesBordesBajadosInmovilesObservaciones())) {
			iscc.setVagonesBordesBajadosInmovilObservaciones(
					generarInformeISCCJSP.getVagonesBordesBajadosInmovilesObservaciones());
		}
		iscc.setVagonesVisiblesInscripciones(Integer.parseInt(generarInformeISCCJSP.getVagonesVisiblesInscripciones()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesVisiblesInscripcionesObservaciones())) {
			iscc.setVagonesVisiblesInscripcionesObservaciones(
					generarInformeISCCJSP.getVagonesVisiblesInscripcionesObservaciones());
		}
		iscc.setVagonesTelerosAmovibles(Integer.parseInt(generarInformeISCCJSP.getVagonesTelerosAmovibles()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesTelerosAmoviblesObservaciones())) {
			iscc.setVagonesTelerosAmoviblesObservaciones(
					generarInformeISCCJSP.getVagonesTelerosAmoviblesObservaciones());
		}
		iscc.setVagonesTelerosPivotantes(Integer.parseInt(generarInformeISCCJSP.getVagonesTelerosPivotantes()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesTelerosPivotantesObservaciones())) {
			iscc.setVagonesTelerosPivotantesObservaciones(
					generarInformeISCCJSP.getVagonesTelerosPivotantesObservaciones());
		}
		iscc.setVagonesTelerosPivotantesHorizontal(
				Integer.parseInt(generarInformeISCCJSP.getVagonesTelerosPivotantesHorizontal()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesTelerosPivotantesHorizontalObservaciones())) {
			iscc.setVagonesTelerosPivotantesHorizontalObservaciones(
					generarInformeISCCJSP.getVagonesTelerosPivotantesHorizontalObservaciones());
		}
		iscc.setVagonesCadenasTeleros(Integer.parseInt(generarInformeISCCJSP.getVagonesCadenasTeleros()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesCadenasTelerosObservaciones())) {
			iscc.setVagonesCadenasTelerosObservaciones(generarInformeISCCJSP.getVagonesCadenasTelerosObservaciones());
		}
		iscc.setVagonesDispositivosTension(Integer.parseInt(generarInformeISCCJSP.getVagonesDispositivosTension()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesDispositivosTensionObservaciones())) {
			iscc.setVagonesDispositivosTensionObservaciones(
					generarInformeISCCJSP.getVagonesDispositivosTensionObservaciones());
		}

		// FRAGMENTO ISCC 4
		iscc.setVagonesPiso(Integer.parseInt(generarInformeISCCJSP.getVagonesPiso()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesPisoObservaciones())) {
			iscc.setVagonesPisoObservaciones(generarInformeISCCJSP.getVagonesPisoObservaciones());
		}
		iscc.setVagonesMercancias(Integer.parseInt(generarInformeISCCJSP.getVagonesMercancias()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesMercanciasObservaciones())) {
			iscc.setVagonesMercanciasObservaciones(generarInformeISCCJSP.getVagonesMercanciasObservaciones());
		}
		iscc.setVagonesParedes(Integer.parseInt(generarInformeISCCJSP.getVagonesParedes()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesParedesObservaciones())) {
			iscc.setVagonesParedesObservaciones(generarInformeISCCJSP.getVagonesParedesObservaciones());
		}
		iscc.setVagonesPuertas(Integer.parseInt(generarInformeISCCJSP.getVagonesPuertas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesPuertasObservaciones())) {
			iscc.setVagonesPuertasObservaciones(generarInformeISCCJSP.getVagonesPuertasObservaciones());
		}
		iscc.setVagonesAnillas(Integer.parseInt(generarInformeISCCJSP.getVagonesAnillas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesAnillasObservaciones())) {
			iscc.setVagonesAnillasObservaciones(generarInformeISCCJSP.getVagonesAnillasObservaciones());
		}
		iscc.setVagonesEquipamientos(Integer.parseInt(generarInformeISCCJSP.getVagonesEquipamiento()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getVagonesEquipamientoObservaciones())) {
			iscc.setVagonesEquipamientosObservaciones(generarInformeISCCJSP.getVagonesEquipamientoObservaciones());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getObservacionesVagones())) {
			iscc.setVagonesObservaciones(generarInformeISCCJSP.getObservacionesVagones());
		}

		// FRAGMENTO ISCC 5

		iscc.setAcondicionamientoReparto(Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoReparto()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoRepartoObservaciones())) {
			iscc.setAcondicionamientoRepartoObservaciones(
					generarInformeISCCJSP.getAcondicionamientoRepartoObservaciones());
		}
		iscc.setAcondicionamientoMercancias(Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoMercancias()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoMercanciasObservaciones())) {
			iscc.setAcondicionamientoMercanciasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoMercanciasObservaciones());
		}
		iscc.setAcondicionamientoPesadas(Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoPesadas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoPesadasObservaciones())) {
			iscc.setAcondicionamientoPesadasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoPesadasObservaciones());
		}
		iscc.setAcondicionamientoSusceptibles(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoSusceptibles()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoSusceptiblesObservaciones())) {
			iscc.setAcondicionamientoSusceptiblesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoSusceptiblesObservaciones());
		}
		iscc.setAcondicionamientoSolidamenteSujeta(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoSolidamenteSujeta()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoSolidamenteSujetaObservaciones())) {
			iscc.setAcondicionamientoSolidamenteSujetaObservaciones(
					generarInformeISCCJSP.getAcondicionamientoSolidamenteSujetaObservaciones());
		}
		iscc.setAcondicionamientoLonaProxima(Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoLonaProxima()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoLonaProximaObservaciones())) {
			iscc.setAcondicionamientoLonaProximaObservaciones(
					generarInformeISCCJSP.getAcondicionamientoLonaProximaObservaciones());
		}
		iscc.setAcondicionamientoLonaSeparacion(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoLonaSeparacion()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoLonaSeparacionObservaciones())) {
			iscc.setAcondicionamientoLonaSeparacionObservaciones(
					generarInformeISCCJSP.getAcondicionamientoLonaSeparacionObservaciones());
		}
		iscc.setAcondicionamientoVerificarParedes(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarParedes()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarParedesObservaciones())) {
			iscc.setAcondicionamientoVerificarParedesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarParedesObservaciones());
		}
		iscc.setAcondicionamientoVerificarCalces(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarCalces()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarCalcesObservaciones())) {
			iscc.setAcondicionamientoVerificarCalcesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarCalcesObservaciones());
		}
		iscc.setAcondicionamientoVerificarAmarre(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarAmarre()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarAmarreObservaciones())) {
			iscc.setAcondicionamientoVerificarAmarreObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarAmarreObservaciones());
		}
		iscc.setAcondicionamientoVerificarDosAmarras(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarDosAmarras()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarDosAmarrasObservaciones())) {
			iscc.setAcondicionamientoVerificarDosAmarrasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarDosAmarrasObservaciones());
		}

		// FRAGMENTO ISCC 6
		iscc.setAcondicionamientoVerificarCalces7(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarCalces7()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarCalces7Observaciones())) {
			iscc.setAcondicionamientoVerificarCalces7Observaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarCalces7Observaciones());
		}
		iscc.setAcondicionamientoVerificarBastidores(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarBastidores()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarBastidoresObservaciones())) {
			iscc.setAcondicionamientoVerificarBastidoresObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarBastidoresObservaciones());
		}
		iscc.setAcondicionamientoVerificarCunas(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarCunas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarCunasObservaciones())) {
			iscc.setAcondicionamientoVerificarCunasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarCunasObservaciones());
		}
		iscc.setAcondicionamientoVerificarTransversal(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarTransversal()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarTransversalObservaciones())) {
			iscc.setAcondicionamientoVerificarTransversalObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarTransversalObservaciones());
		}
		iscc.setAcondicionamientoVerificarLongitudinal(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarLongitudinal()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarLongitudinalObservaciones())) {
			iscc.setAcondicionamientoVerificarLongitudinalObservaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarLongitudinalObservaciones());
		}
		iscc.setAcondicionamientoVerificarBobinasMenos10(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarBobinasMenos10()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarBobinasMenos10Observaciones())) {
			iscc.setAcondicionamientoVerificarBobinasMenos10Observaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarBobinasMenos10Observaciones());
		}
		iscc.setAcondicionamientoVerificarBobinasMas10(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoVerificarBobinasMas10()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoVerificarBobinasMas10Observaciones())) {
			iscc.setAcondicionamientoVerificarBobinasMas10Observaciones(
					generarInformeISCCJSP.getAcondicionamientoVerificarBobinasMas10Observaciones());
		}
		iscc.setAcondicionamientoBascularAgrupamiento(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoBascularAgrupamiento()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoBascularAgrupamientoObservaciones())) {
			iscc.setAcondicionamientoBascularAgrupamientoObservaciones(
					generarInformeISCCJSP.getAcondicionamientoBascularAgrupamientoObservaciones());
		}
		iscc.setAcondicionamientoBascularPuntales(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoBascularPuntales()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoBascularPuntalesObservaciones())) {
			iscc.setAcondicionamientoBascularPuntalesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoBascularPuntalesObservaciones());
		}
		iscc.setAcondicionamientoBascularCaballetes(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoBascularCaballetes()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoBascularCaballetesObservaciones())) {
			iscc.setAcondicionamientoBascularCaballetesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoBascularCaballetesObservaciones());
		}

		// FRAGMENTO ISCC 7

		iscc.setAcondicionamientoApiladaRepartidas(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaRepartidas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaRepartidasObservaciones())) {
			iscc.setAcondicionamientoApiladaRepartidasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaRepartidasObservaciones());
		}
		iscc.setAcondicionamientoApiladaAnchura(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaAnchura()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaAnchuraObservaciones())) {
			iscc.setAcondicionamientoApiladaAnchuraObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaAnchuraObservaciones());
		}
		iscc.setAcondicionamientoApiladaCapas(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaCapas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaCapasObservaciones())) {
			iscc.setAcondicionamientoApiladaCapasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaCapasObservaciones());
		}
		iscc.setAcondicionamientoApiladaRozamiento(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaRozamiento()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaRozamientoObservaciones())) {
			iscc.setAcondicionamientoApiladaRozamientoObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaRozamientoObservaciones());
		}
		iscc.setAcondicionamientoApiladaBastidores(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaBastidores()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaBastidoresObservaciones())) {
			iscc.setAcondicionamientoApiladaBastidoresObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaBastidoresObservaciones());
		}
		iscc.setAcondicionamientoApiladaUnidades(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaUnidades()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaUnidadesObservaciones())) {
			iscc.setAcondicionamientoApiladaUnidadesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaUnidadesObservaciones());
		}
		iscc.setAcondicionamientoApiladaCintas(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaCintas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaCintasObservaciones())) {
			iscc.setAcondicionamientoApiladaCintasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaCintasObservaciones());
		}
		iscc.setAcondicionamientoApiladaParteInferior(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaParteInferior()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaParteInferiorObservaciones())) {
			iscc.setAcondicionamientoApiladaParteInferiorObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaParteInferiorObservaciones());
		}
		iscc.setAcondicionamientoApiladaIntercalares(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaIntercalares()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaIntercalaresObservaciones())) {
			iscc.setAcondicionamientoApiladaIntercalaresObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaIntercalaresObservaciones());
		}

		// FRAGMENTO ISCC 8
		iscc.setAcondicionamientoApiladaImbricadas(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaImbricadas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaImbricadasObservaciones())) {
			iscc.setAcondicionamientoApiladaImbricadasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaImbricadasObservaciones());
		}
		iscc.setAcondicionamientoApiladaOscilar(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaOscilar()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaOscilarObservaciones())) {
			iscc.setAcondicionamientoApiladaOscilarObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaOscilarObservaciones());
		}
		iscc.setAcondicionamientoApiladaTubos(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaTubos()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaTubosObservaciones())) {
			iscc.setAcondicionamientoApiladaTubosObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaTubosObservaciones());
		}
		iscc.setAcondicionamientoApiladaCapasCalces(
				Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoApiladaCapasCalces()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoApiladaCapasCalcesObservaciones())) {
			iscc.setAcondicionamientoApiladaCapasCalcesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoApiladaCapasCalcesObservaciones());
		}
		iscc.setAcondicionamientoRigidas(Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoRigidas()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoRigidasObservaciones())) {
			iscc.setAcondicionamientoRigidasObservaciones(
					generarInformeISCCJSP.getAcondicionamientoRigidasObservaciones());
		}
		iscc.setAcondicionamientoFlexibles(Integer.parseInt(generarInformeISCCJSP.getAcondicionamientoFlexibles()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getAcondicionamientoFlexiblesObservaciones())) {
			iscc.setAcondicionamientoFlexiblesObservaciones(
					generarInformeISCCJSP.getAcondicionamientoFlexiblesObservaciones());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getObservacionesAcondicionamiento())) {
			iscc.setAcondicionamientoObservaciones(generarInformeISCCJSP.getObservacionesAcondicionamiento());
		}

		// FRAGMENTO ISCC 9
		iscc.setMediosCalcesRetencion(Integer.parseInt(generarInformeISCCJSP.getMediosCalcesRetencion()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getMediosCalcesRetencionObservaciones())) {
			iscc.setMediosCalcesRetencionObservaciones(generarInformeISCCJSP.getMediosCalcesRetencionObservaciones());
		}
		iscc.setMediosCalcesRodadura(Integer.parseInt(generarInformeISCCJSP.getMediosCalcesRodadura()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getMediosCalcesRodaduraObservaciones())) {
			iscc.setMediosCalcesRodaduraObservaciones(generarInformeISCCJSP.getMediosCalcesRodaduraObservaciones());
		}
		iscc.setMediosIntercalares(Integer.parseInt(generarInformeISCCJSP.getMediosIntercalares()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getMediosIntercalaresObservaciones())) {
			iscc.setMediosIntercalaresObservaciones(generarInformeISCCJSP.getMediosIntercalaresObservaciones());
		}
		iscc.setMediosAmarres(Integer.parseInt(generarInformeISCCJSP.getMediosAmarres()));
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getMediosAmarresObservaciones())) {
			iscc.setMediosAmarresObservaciones(generarInformeISCCJSP.getMediosAmarresObservaciones());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getObservacionesMedios())) {
			iscc.setMediosObservaciones(generarInformeISCCJSP.getObservacionesMedios());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getMedidasCautelares())) {
			iscc.setMedidasCautelares(generarInformeISCCJSP.getMedidasCautelares());
		}
		if (StringUtils.isNotBlank(generarInformeISCCJSP.getListaAnexos())) {
			iscc.setListaAnexos(generarInformeISCCJSP.getListaAnexos());
		}

		iscc.setIs(is);
		return iscc;
	}
}
