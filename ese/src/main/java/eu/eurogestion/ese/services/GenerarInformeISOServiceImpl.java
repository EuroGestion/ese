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

import eu.eurogestion.ese.domain.EstadoVerificacionInforme;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Is;
import eu.eurogestion.ese.domain.Iso;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISOJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoInspeccionDAO;
import eu.eurogestion.ese.repository.EstadoVerificacionInformeDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.IsoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class GenerarInformeISOServiceImpl implements GenerarInformeISOService {

	/** Repositories & Services **/

	@Autowired
	public EstadoVerificacionInformeDAO estadoVerificacionInformeDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public IsoDAO isoDAO;

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

	public void firmaISOInspectorSeguridad(GenerarInformeISOJSP generarInformeISOJSP, HttpSession sesion, Model model)
			throws EseException {

		Iso iso = isoDAO.getOne(Integer.parseInt(generarInformeISOJSP.getIdISO()));
		Is is = iso.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISO si no está cerrado su informe de anomalías.");
			throw new EseException("No se puede cerrar la inspección ISO si no está cerrado su informe de anomalías.");
		}
		iso.setFirmaInspector(personalDAO.getOne(generarInformeISOJSP.getIdFirmaInspectorSeguridad()));
		iso.setFechahoraFirmaInspector(new Date());
		isoDAO.save(iso);

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (iso.getFirmaResponsable() != null) {
			finalizarInformeISO(iso, sesion, model, mensajeInfo);
			generarInformeISOJSP.setIdEstadoISO(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	public void firmaISOResponsableSeguridad(GenerarInformeISOJSP generarInformeISOJSP, HttpSession sesion, Model model)
			throws EseException {

		Iso iso = isoDAO.getOne(Integer.parseInt(generarInformeISOJSP.getIdISO()));
		Is is = iso.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISO si no está cerrado su informe de anomalías.");
			throw new EseException("No se puede cerrar la inspección ISO si no está cerrado su informe de anomalías.");
		}
		iso.setFirmaResponsable(personalDAO.getOne(generarInformeISOJSP.getIdFirmaResponsableSeguridad()));
		iso.setFechahoraFirmaResponsable(new Date());
		isoDAO.save(iso);

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (iso.getFirmaInspector() != null) {
			finalizarInformeISO(iso, sesion, model, mensajeInfo);
			generarInformeISOJSP.setIdEstadoISO(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	private void finalizarInformeISO(Iso iso, HttpSession sesion, Model model, List<String> mensajeInfo)
			throws EseException {

		iso.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_FINALIZADA));
		isoDAO.save(iso);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicInsSegOpe(iso);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_FICHA_ISO);

		Evidencia evidencia = utilesPDFService.crearEvidencia(iso.getIs().getNumReferencia(), fichero, md5,
				tipoEvidencia, Utiles.sysdate(), Constantes.TYPE_PDF);

		iso.setEvidencia9(evidencia);
		isoDAO.save(iso);

		cerrarTareaPendiente(iso);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	private void cerrarTareaPendiente(Iso iso) {

		TareaPendiente tareaPendiente = tareaPendienteDAO.findTareaPendienteByReference(iso.getIdIso(),
				Constantes.TAREAPTE_GENERAR_ISO);
		if (tareaPendiente != null) {
			tareaPendiente.setLeido(Boolean.TRUE);
			tareaPendienteDAO.save(tareaPendiente);
		}
	}

	@Override
	public void guardarInformeISO(GenerarInformeISOJSP generarInformeISOJSP) throws EseException {

		Iso iso = convertGenerarInformeISOJSPToISO(generarInformeISOJSP);

		if (Constantes.ESTADO_INSPECCION_PLANIFICADA
				.equals(iso.getIs().getEstadoInspeccion().getIdEstadoInspeccion())) {
			iso.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_REALIZADA));
		}

		isoDAO.save(iso);
	}

	@Override
	public GenerarInformeISOJSP createGenerarInformeISOJSPToIdISO(Integer idISO) {

		GenerarInformeISOJSP generarInformeISOJSP = new GenerarInformeISOJSP();
		Iso iso = isoDAO.getOne(idISO);
		generarInformeISOJSP.setIdISO(iso.getIdIso().toString());
		generarInformeISOJSP.setIsLectura(Constantes.ESTADO_INSPECCION_FINALIZADA
				.equals(iso.getIs().getEstadoInspeccion().getIdEstadoInspeccion()));
		generarInformeISOJSP.setIsFirmado(iso.getFirmaInspector() != null || iso.getFirmaResponsable() != null);
		generarInformeISOJSP.setIdEstadoISO(iso.getIs().getEstadoInspeccion().getIdEstadoInspeccion());

		// pagina 1
		generarInformeISOJSP.setPersonalConduccion(
				iso.getMaquinista() != null ? iso.getMaquinista().getIdPersonal().toString() : null);
		generarInformeISOJSP.setNveLocomotora(iso.getNveLocomotora());
		generarInformeISOJSP.setFecha(
				Utiles.convertDateToString(iso.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		generarInformeISOJSP.setHoraInicioServicio(
				iso.getHoraInicioServicio() != null ? Utiles.formatStringHorasPantalla(iso.getHoraInicioServicio())
						: null);
		generarInformeISOJSP.setHoraFinServicio(
				iso.getHoraFinServicio() != null ? Utiles.formatStringHorasPantalla(iso.getHoraFinServicio()) : null);
		generarInformeISOJSP.setTiemposDescanso(iso.getTiempoDescanso());
		generarInformeISOJSP.setObservacionesCirculacionFerroviaria(iso.getObservacionesCirculacionFerroviaria());
		generarInformeISOJSP.setVerificadoLNM(iso.getLibroNormasMaquinistaLNMVerificacion() != null
				? iso.getLibroNormasMaquinistaLNMVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesLNM(iso.getLibroNormasMaquinistaLNMObservaciones());

		generarInformeISOJSP.setVerificadoLIM(iso.getLibroItinerarioMaquinistaLIMVerificacion() != null
				? iso.getLibroItinerarioMaquinistaLIMVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesLIM(iso.getLibroItinerarioMaquinistaLIMObservaciones());

		generarInformeISOJSP.setVerificadoLlaves333(iso.getLlave333Verificacion() != null
				? iso.getLlave333Verificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesLlaves333(iso.getLlave333Observaciones());

		generarInformeISOJSP.setVerificadoLlaveCuadradillo(iso.getLlaveCuadradilloVerificacion() != null
				? iso.getLlaveCuadradilloVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesLlaveCuadradillo(iso.getLlaveCuadradilloObservaciones());

		generarInformeISOJSP.setVerificadoTituloConduccion(iso.getLicenciaOTituloConducccionVerificacion() != null
				? iso.getLicenciaOTituloConducccionVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesTituloConduccion(iso.getLicenciaOTituloConducccionObservaciones());

		generarInformeISOJSP.setVerificadoDocReglamentaria(iso.getDocReglamentariaVerificacion() != null
				? iso.getDocReglamentariaVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesDocReglamentaria(iso.getDocReglamentariaObservaciones());

		generarInformeISOJSP.setVerificadoTelefonoMovil(iso.getTelefonoMovilVerificacion() != null
				? iso.getTelefonoMovilVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesTelefonoMovil(iso.getTelefonoMovilObservaciones());

		generarInformeISOJSP.setVerificadoTabletaPortatil(iso.getTabletaUOrdenadorPortatilVerificacion() != null
				? iso.getTabletaUOrdenadorPortatilVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesTabletaPortatil(iso.getTabletaUOrdenadorPortatilObservaciones());

		generarInformeISOJSP.setObservacionesDotacionPersonal(iso.getObservacionesDotacionPersonal());

		// pagina 2
		generarInformeISOJSP.setVerificadoChapa(iso.getSenalesColaOChapasReflectantesVerificacion() != null
				? iso.getSenalesColaOChapasReflectantesVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setNumeroChapa(iso.getSenalesColaOChapasReflectantesNumero() != null
				? iso.getSenalesColaOChapasReflectantesNumero().toString()
				: null);
		generarInformeISOJSP.setObservacionesChapa(iso.getSenalesColaOChapasReflectantesObservaciones());

		generarInformeISOJSP.setVerificadoLinternas(iso.getLinternasLucesBlancoRojoVerificacion() != null
				? iso.getLinternasLucesBlancoRojoVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setNumeroLinternas(
				iso.getLinternasLucesBlancoRojoNumero() != null ? iso.getLinternasLucesBlancoRojoNumero().toString()
						: null);
		generarInformeISOJSP.setObservacionesLinternas(iso.getLinternasLucesBlancoRojoObservaciones());

		generarInformeISOJSP.setVerificadoBanderines(iso.getBanderinesRojosVerificacion() != null
				? iso.getBanderinesRojosVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setNumeroBanderines(
				iso.getBanderinesRojosNumero() != null ? iso.getBanderinesRojosNumero().toString() : null);
		generarInformeISOJSP.setObservacionesBanderines(iso.getBanderinesRojosObservaciones());

		generarInformeISOJSP.setVerificadoCalces(iso.getCalcesAntiderivaVerificacion() != null
				? iso.getCalcesAntiderivaVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setNumeroCalces(
				iso.getCalcesAntiderivaNumero() != null ? iso.getCalcesAntiderivaNumero().toString() : null);
		generarInformeISOJSP.setObservacionesCalces(iso.getCalcesAntiderivaObservaciones());

		generarInformeISOJSP.setVerificadoBarras(iso.getBarrasOUtilesCortocircuitoVerificacion() != null
				? iso.getCalcesAntiderivaVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setNumeroBarras(
				iso.getBarrasOUtilesCortocircuitoNumero() != null ? iso.getBarrasOUtilesCortocircuitoNumero().toString()
						: null);
		generarInformeISOJSP.setObservacionesBarras(iso.getBarrasOUtilesCortocircuitoObservaciones());

		generarInformeISOJSP.setVerificadoJuegoLlaves(iso.getJuegoLlavesCerraduraVehiculosVerificacion() != null
				? iso.getJuegoLlavesCerraduraVehiculosVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setNumeroJuegoLlaves(iso.getJuegoLlavesCerraduraVehiculosNumero() != null
				? iso.getJuegoLlavesCerraduraVehiculosNumero().toString()
				: null);
		generarInformeISOJSP.setObservacionesJuegoLlaves(iso.getJuegoLlavesCerraduraVehiculosObservaciones());

		generarInformeISOJSP.setVerificadoManual(iso.getManualConduccionMaterialTraccionVerificacion() != null
				? iso.getManualConduccionMaterialTraccionVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setNumeroManual(iso.getManualConduccionMaterialTraccionNumero() != null
				? iso.getManualConduccionMaterialTraccionNumero().toString()
				: null);
		generarInformeISOJSP.setObservacionesManual(iso.getManualConduccionMaterialTraccionObservaciones());

		generarInformeISOJSP.setVerificadoLibro(iso.getLibroAveriasVerificacion() != null
				? iso.getLibroAveriasVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP
				.setNumeroLibro(iso.getLibroAveriasNumero() != null ? iso.getLibroAveriasNumero().toString() : null);
		generarInformeISOJSP.setObservacionesLibro(iso.getLibroAveriasObservaciones());

		generarInformeISOJSP.setObservacionesLocomotora(iso.getObservacionesDotacionLocomotora());

		generarInformeISOJSP.setVerificadoDestreza(iso.getDestrezaConduccionVerificacion() != null
				? iso.getDestrezaConduccionVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesDestreza(iso.getDestrezaConduccionObservaciones());

		generarInformeISOJSP.setVerificadoDestreza(iso.getDestrezaConduccionVerificacion() != null
				? iso.getDestrezaConduccionVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesDestreza(iso.getDestrezaConduccionObservaciones());

		generarInformeISOJSP.setVerificadoCumple(iso.getCumpleDocumentacionYNormativaVerificacion() != null
				? iso.getCumpleDocumentacionYNormativaVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesCumple(iso.getCumpleDocumentacionYNormativaObservaciones());

		generarInformeISOJSP.setVerificadoCumpleTiempo(iso.getCumpleTiemposMaximosConduccionVerificacion() != null
				? iso.getCumpleTiemposMaximosConduccionVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesCumpleTiempo(iso.getCumpleTiemposMaximosConduccionObservaciones());

		generarInformeISOJSP.setVerificadoDispositivos(iso.getCumpleUsoAdecuadoDispositivosVerificacion() != null
				? iso.getCumpleUsoAdecuadoDispositivosVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesDispositivos(iso.getCumpleUsoAdecuadoDispositivosObservaciones());

		generarInformeISOJSP.setVerificadoPoseeFormacion(
				iso.getSuficienteYCorrectaFormacionOperacionesVerificacion() != null
						? iso.getSuficienteYCorrectaFormacionOperacionesVerificacion().getIdEstadoVerificacion()
								.toString()
						: null);
		generarInformeISOJSP
				.setObservacionesPoseeFormacion(iso.getSuficienteYCorrectaFormacionOperacionesObservaciones());

		// pagina 3
		generarInformeISOJSP.setVerificadoAntesServicio(iso.getAntesServicioVerificacion() != null
				? iso.getAntesServicioVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesAntesServicio(iso.getAntesServicioObservaciones());

		generarInformeISOJSP.setVerificadoTrasServicio(iso.getTrasServicioVerificacion() != null
				? iso.getTrasServicioVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesTrasServicio(iso.getTrasServicioObservaciones());

		generarInformeISOJSP.setVerificadoRelevo(iso.getEnRelevoVerificacion() != null
				? iso.getEnRelevoVerificacion().getIdEstadoVerificacion().toString()
				: null);
		generarInformeISOJSP.setObservacionesRelevo(iso.getEnRelevoObservaciones());

		generarInformeISOJSP.setObservacionesIdoneidad(iso.getObservacionesAcompa());
		generarInformeISOJSP.setObservacionesMedidasCautelares(iso.getMedidasCautelaresAdoptadas());
		generarInformeISOJSP.setObservacionesDocumentoAnexo(iso.getDocumentosAnexos());

		if (iso.getFirmaInspector() != null) {
			generarInformeISOJSP.setIdFirmaInspectorSeguridad(iso.getFirmaInspector().getIdPersonal());
		}
		if (iso.getFirmaResponsable() != null) {
			generarInformeISOJSP.setIdFirmaResponsableSeguridad(iso.getFirmaResponsable().getIdPersonal());
		}

		return generarInformeISOJSP;
	}

	private Iso convertGenerarInformeISOJSPToISO(GenerarInformeISOJSP generarInformeISOJSP) {

		Map<String, EstadoVerificacionInforme> mapaEstados = new HashMap<>();
		mapaEstados.put(Constantes.ESTADO_VERIFICACION_INFORME_CORRECTO.toString(),
				estadoVerificacionInformeDAO.getOne(Constantes.ESTADO_VERIFICACION_INFORME_CORRECTO));
		mapaEstados.put(Constantes.ESTADO_VERIFICACION_INFORME_INCORRECTO.toString(),
				estadoVerificacionInformeDAO.getOne(Constantes.ESTADO_VERIFICACION_INFORME_INCORRECTO));
		mapaEstados.put(Constantes.ESTADO_VERIFICACION_INFORME_NO_APLICA.toString(),
				estadoVerificacionInformeDAO.getOne(Constantes.ESTADO_VERIFICACION_INFORME_NO_APLICA));
		Iso iso = isoDAO.getOne(Integer.parseInt(generarInformeISOJSP.getIdISO()));

		// pagina 1
		iso.setMaquinista(personalDAO.getOne(Integer.parseInt(generarInformeISOJSP.getPersonalConduccion())));
		iso.setNveLocomotora(generarInformeISOJSP.getNveLocomotora());
		iso.getIs().setFechaInspeccion(Utiles.parseDatePantalla(generarInformeISOJSP.getFecha()));
		iso.setHoraInicioServicio(Utiles.parseTimeHorasPantalla(generarInformeISOJSP.getHoraInicioServicio()));
		iso.setHoraFinServicio(Utiles.parseTimeHorasPantalla(generarInformeISOJSP.getHoraFinServicio()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getTiemposDescanso())) {
			iso.setTiempoDescanso(generarInformeISOJSP.getTiemposDescanso());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesCirculacionFerroviaria())) {
			iso.setObservacionesCirculacionFerroviaria(generarInformeISOJSP.getObservacionesCirculacionFerroviaria());
		}

		iso.setLibroNormasMaquinistaLNMVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoLNM()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesLNM())) {
			iso.setLibroNormasMaquinistaLNMObservaciones(generarInformeISOJSP.getObservacionesLNM());
		}

		iso.setLibroItinerarioMaquinistaLIMVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoLIM()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesLIM())) {
			iso.setLibroItinerarioMaquinistaLIMObservaciones(generarInformeISOJSP.getObservacionesLIM());
		}

		iso.setLlave333Verificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoLlaves333()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesLlaves333())) {
			iso.setLlave333Observaciones(generarInformeISOJSP.getObservacionesLlaves333());
		}

		iso.setLlaveCuadradilloVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoLlaveCuadradillo()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesLlaveCuadradillo())) {
			iso.setLlaveCuadradilloObservaciones(generarInformeISOJSP.getObservacionesLlaveCuadradillo());
		}

		iso.setLicenciaOTituloConducccionVerificacion(
				mapaEstados.get(generarInformeISOJSP.getVerificadoTituloConduccion()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesTituloConduccion())) {
			iso.setLicenciaOTituloConducccionObservaciones(generarInformeISOJSP.getObservacionesTituloConduccion());
		}

		iso.setDocReglamentariaVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoDocReglamentaria()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesDocReglamentaria())) {
			iso.setDocReglamentariaObservaciones(generarInformeISOJSP.getObservacionesDocReglamentaria());
		}

		iso.setTelefonoMovilVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoTelefonoMovil()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesTelefonoMovil())) {
			iso.setTelefonoMovilObservaciones(generarInformeISOJSP.getObservacionesTelefonoMovil());
		}

		iso.setTabletaUOrdenadorPortatilVerificacion(
				mapaEstados.get(generarInformeISOJSP.getVerificadoTabletaPortatil()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesTabletaPortatil())) {
			iso.setTabletaUOrdenadorPortatilObservaciones(generarInformeISOJSP.getObservacionesTabletaPortatil());
		}

		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesDotacionPersonal())) {
			iso.setObservacionesDotacionPersonal(generarInformeISOJSP.getObservacionesDotacionPersonal());
		}

		// pagina 2
		iso.setSenalesColaOChapasReflectantesVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoChapa()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesChapa())) {
			iso.setSenalesColaOChapasReflectantesObservaciones(generarInformeISOJSP.getObservacionesChapa());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroChapa())) {
			iso.setSenalesColaOChapasReflectantesNumero(Integer.parseInt(generarInformeISOJSP.getNumeroChapa()));
		}

		iso.setLinternasLucesBlancoRojoVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoLinternas()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesLinternas())) {
			iso.setLinternasLucesBlancoRojoObservaciones(generarInformeISOJSP.getObservacionesLinternas());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroLinternas())) {
			iso.setLinternasLucesBlancoRojoNumero(Integer.parseInt(generarInformeISOJSP.getNumeroLinternas()));
		}

		iso.setBanderinesRojosVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoBanderines()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesBanderines())) {
			iso.setBanderinesRojosObservaciones(generarInformeISOJSP.getObservacionesBanderines());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroBanderines())) {
			iso.setBanderinesRojosNumero(Integer.parseInt(generarInformeISOJSP.getNumeroBanderines()));
		}

		iso.setCalcesAntiderivaVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoCalces()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesCalces())) {
			iso.setCalcesAntiderivaObservaciones(generarInformeISOJSP.getObservacionesCalces());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroCalces())) {
			iso.setCalcesAntiderivaNumero(Integer.parseInt(generarInformeISOJSP.getNumeroCalces()));
		}

		iso.setBarrasOUtilesCortocircuitoVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoBarras()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesBarras())) {
			iso.setBarrasOUtilesCortocircuitoObservaciones(generarInformeISOJSP.getObservacionesBarras());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroBarras())) {
			iso.setBarrasOUtilesCortocircuitoNumero(Integer.parseInt(generarInformeISOJSP.getNumeroBarras()));
		}

		iso.setJuegoLlavesCerraduraVehiculosVerificacion(
				mapaEstados.get(generarInformeISOJSP.getVerificadoJuegoLlaves()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesJuegoLlaves())) {
			iso.setJuegoLlavesCerraduraVehiculosObservaciones(generarInformeISOJSP.getObservacionesJuegoLlaves());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroJuegoLlaves())) {
			iso.setJuegoLlavesCerraduraVehiculosNumero(Integer.parseInt(generarInformeISOJSP.getNumeroJuegoLlaves()));
		}

		iso.setManualConduccionMaterialTraccionVerificacion(
				mapaEstados.get(generarInformeISOJSP.getVerificadoManual()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesManual())) {
			iso.setManualConduccionMaterialTraccionObservaciones(generarInformeISOJSP.getObservacionesManual());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroManual())) {
			iso.setManualConduccionMaterialTraccionNumero(Integer.parseInt(generarInformeISOJSP.getNumeroManual()));
		}

		iso.setLibroAveriasVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoLibro()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesLibro())) {
			iso.setLibroAveriasObservaciones(generarInformeISOJSP.getObservacionesLibro());
		}
		if (StringUtils.isNotBlank(generarInformeISOJSP.getNumeroLibro())) {
			iso.setLibroAveriasNumero(Integer.parseInt(generarInformeISOJSP.getNumeroLibro()));
		}

		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesLocomotora())) {
			iso.setObservacionesDotacionLocomotora(generarInformeISOJSP.getObservacionesLocomotora());
		}

		iso.setDestrezaConduccionVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoDestreza()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesDestreza())) {
			iso.setDestrezaConduccionObservaciones(generarInformeISOJSP.getObservacionesDestreza());
		}

		iso.setCumpleDocumentacionYNormativaVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoCumple()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesCumple())) {
			iso.setCumpleDocumentacionYNormativaObservaciones(generarInformeISOJSP.getObservacionesCumple());
		}

		iso.setCumpleTiemposMaximosConduccionVerificacion(
				mapaEstados.get(generarInformeISOJSP.getVerificadoCumpleTiempo()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesCumpleTiempo())) {
			iso.setCumpleTiemposMaximosConduccionObservaciones(generarInformeISOJSP.getObservacionesCumpleTiempo());
		}

		iso.setCumpleUsoAdecuadoDispositivosVerificacion(
				mapaEstados.get(generarInformeISOJSP.getVerificadoDispositivos()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesDispositivos())) {
			iso.setCumpleUsoAdecuadoDispositivosObservaciones(generarInformeISOJSP.getObservacionesDispositivos());
		}

		iso.setSuficienteYCorrectaFormacionOperacionesVerificacion(
				mapaEstados.get(generarInformeISOJSP.getVerificadoPoseeFormacion()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesPoseeFormacion())) {
			iso.setSuficienteYCorrectaFormacionOperacionesObservaciones(
					generarInformeISOJSP.getObservacionesPoseeFormacion());
		}

		// pagina 3
		iso.setAntesServicioVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoAntesServicio()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesAntesServicio())) {
			iso.setAntesServicioObservaciones(generarInformeISOJSP.getObservacionesAntesServicio());
		}

		iso.setTrasServicioVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoTrasServicio()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesTrasServicio())) {
			iso.setTrasServicioObservaciones(generarInformeISOJSP.getObservacionesTrasServicio());
		}

		iso.setEnRelevoVerificacion(mapaEstados.get(generarInformeISOJSP.getVerificadoRelevo()));
		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesRelevo())) {
			iso.setEnRelevoObservaciones(generarInformeISOJSP.getObservacionesRelevo());
		}

		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesIdoneidad())) {
			iso.setObservacionesAcompa(generarInformeISOJSP.getObservacionesIdoneidad());
		}

		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesMedidasCautelares())) {
			iso.setMedidasCautelaresAdoptadas(generarInformeISOJSP.getObservacionesMedidasCautelares());
		}

		if (StringUtils.isNotBlank(generarInformeISOJSP.getObservacionesDocumentoAnexo())) {
			iso.setDocumentosAnexos(generarInformeISOJSP.getObservacionesDocumentoAnexo());
		}

		return iso;
	}

}
