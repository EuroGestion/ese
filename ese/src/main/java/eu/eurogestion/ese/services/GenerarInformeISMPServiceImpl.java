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
import eu.eurogestion.ese.domain.Ismp;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISMPJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoInspeccionDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.IsDAO;
import eu.eurogestion.ese.repository.IsmpDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResultadoInspeccionDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class GenerarInformeISMPServiceImpl implements GenerarInformeISMPService {

	/** Repositories & Services **/

	@Autowired
	public ResultadoInspeccionDAO resultadoInspeccionDAO;

	@Autowired
	public IsmpDAO ismpDAO;

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

	public void firmaISMPInspectorSeguridad(GenerarInformeISMPJSP generarInformeISMPJSP, HttpSession sesion,
			Model model) throws EseException {
		Ismp ismp = ismpDAO.getOne(Integer.parseInt(generarInformeISMPJSP.getIdISMP()));
		Is is = ismp.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISMP si no está cerrado su informe de anomalías.");
			throw new EseException("No se puede cerrar la inspección ISMP si no está cerrado su informe de anomalías.");
		}
		ismp.setFirmaInspector(personalDAO.getOne(generarInformeISMPJSP.getIdFirmaInspectorSeguridad()));
		ismp.setFechahoraFirmaInspector(new Date());
		ismpDAO.save(ismp);

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (ismp.getFirmaResponsable() != null) {
			finalizarInformeISMP(ismp, sesion, model, mensajeInfo);
			generarInformeISMPJSP.setIdEstadoISMP(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}

	}

	public void firmaISMPResponsableSeguridad(GenerarInformeISMPJSP generarInformeISMPJSP, HttpSession sesion,
			Model model) throws EseException {
		Ismp ismp = ismpDAO.getOne(Integer.parseInt(generarInformeISMPJSP.getIdISMP()));
		Is is = ismp.getIs();
		if (is.getInformeAnomalias() != null && is.getInformeAnomalias().getEvidencia20() == null) {
			model.addAttribute("error",
					"No se puede cerrar la inspección ISMP si no está cerrado su informe de anomalías.");
			throw new EseException("No se puede cerrar la inspección ISMP si no está cerrado su informe de anomalías.");
		}
		ismp.setFirmaResponsable(personalDAO.getOne(generarInformeISMPJSP.getIdFirmaResponsableSeguridad()));
		ismp.setFechahoraFirmaResponsable(new Date());
		ismpDAO.save(ismp);

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (ismp.getFirmaInspector() != null) {
			finalizarInformeISMP(ismp, sesion, model, mensajeInfo);
			generarInformeISMPJSP.setIdEstadoISMP(Constantes.ESTADO_INSPECCION_FINALIZADA);
		} else {
			model.addAttribute("info", mensajeInfo);
		}

	}

	private void finalizarInformeISMP(Ismp ismp, HttpSession sesion, Model model, List<String> mensajeInfo)
			throws EseException {

		ismp.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_FINALIZADA));
		isDAO.save(ismp.getIs());

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicISMPMerPel(ismp);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);

		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_FICHA_ISMP);

		Evidencia evidencia = utilesPDFService.crearEvidencia(ismp.getIs().getNumReferencia(), fichero, md5,
				tipoEvidencia, Utiles.sysdate(), Constantes.TYPE_PDF);

		ismp.setEvidencia80(evidencia);
		ismpDAO.save(ismp);

		cerrarTareaPendiente(ismp);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	private void cerrarTareaPendiente(Ismp ismp) {

		TareaPendiente tareaPendiente = tareaPendienteDAO.findTareaPendienteByReference(ismp.getIdIsmp(),
				Constantes.TAREAPTE_GENERAR_ISMP);
		if (tareaPendiente != null) {
			tareaPendiente.setLeido(Boolean.TRUE);
			tareaPendienteDAO.save(tareaPendiente);
		}

	}

	@Override
	public void guardarInformeISMP(GenerarInformeISMPJSP generarInformeISMPJSP) throws EseException {

		Ismp ismp = convertGenerarInformeISMPJSPToISMP(generarInformeISMPJSP);

		if (Constantes.ESTADO_INSPECCION_PLANIFICADA
				.equals(ismp.getIs().getEstadoInspeccion().getIdEstadoInspeccion())) {
			ismp.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_REALIZADA));
		}

		isDAO.save(ismp.getIs());
		ismpDAO.save(ismp);

	}

	@Override
	public GenerarInformeISMPJSP createGenerarInformeISMPJSPToIdISMP(Integer idISMP) {
		GenerarInformeISMPJSP generarInformeISMPJSP = new GenerarInformeISMPJSP();
		Ismp ismp = ismpDAO.getOne(idISMP);

		// GLOBALES
		generarInformeISMPJSP.setIdISMP(ismp.getIdIsmp().toString());
		generarInformeISMPJSP.setIsLectura(Constantes.ESTADO_INSPECCION_FINALIZADA
				.equals(ismp.getIs().getEstadoInspeccion().getIdEstadoInspeccion()));
		generarInformeISMPJSP.setIsFirmado(ismp.getFirmaInspector() != null || ismp.getFirmaResponsable() != null);
		generarInformeISMPJSP.setIdEstadoISMP(ismp.getIs().getEstadoInspeccion().getIdEstadoInspeccion());

		// FRAGMENTO ISMP 1

		generarInformeISMPJSP.setNumeroReferencia(ismp.getIs().getNumReferencia());
		generarInformeISMPJSP.setFecha(
				Utiles.convertDateToString(ismp.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));

		if (ismp.getIs().getTren() != null) {
			generarInformeISMPJSP.setIdTren(ismp.getIs().getTren().getIdTren().toString());
		}

		generarInformeISMPJSP.setLugarInspeccion(ismp.getIs().getLugar());
		generarInformeISMPJSP.setTipoMercancias(ismp.getTiposMercancias());
		generarInformeISMPJSP.setNumeroVagonesComposicion(ismp.getNumeroVagones());
		generarInformeISMPJSP
				.setNumeroContenedoresEtiquetadoBuenEstado(ismp.getEtiquetasVagonesBuenEstado().toString());
		generarInformeISMPJSP.setNumeroContenedoresEtiquetadoMalEstado(ismp.getEtiquetasVagonesMal().toString());
		generarInformeISMPJSP
				.setNumeroContenedoresEtiquetadoIncorrecto(ismp.getEtiquetasVagonesIncorrecto().toString());
		generarInformeISMPJSP
				.setNumeroContenedoresEtiquetadoInspeccionado(ismp.getEtiquetasVagonesInspeccionados().toString());
		generarInformeISMPJSP.setFaltaTotalEtiquetas(ismp.getFaltaTotalEtiquetas().toString());
		generarInformeISMPJSP.setFaltaAlgunaEtiqueta(ismp.getFaltaAlgunaEtiqueta().toString());
		generarInformeISMPJSP.setEtiquetasInadecuadas(ismp.getEtiquetaInadecuada().toString());
		generarInformeISMPJSP.setEtiquetasTamanoInadecuado(ismp.getTamanoInadecuado().toString());
		generarInformeISMPJSP.setEtiquetasOtras(ismp.getOtras().toString());
		generarInformeISMPJSP.setEtiquetasObservaciones(ismp.getEtiquetasObservaciones());

		// FRAGMENTO ISMP 2

		generarInformeISMPJSP.setNumeroContenedoresPanelesBuenEstado(ismp.getPanelesVagonesBuenEstado().toString());
		generarInformeISMPJSP.setNumeroContenedoresPanelesMalEstado(ismp.getPanelesVagonesMalEstado().toString());
		generarInformeISMPJSP.setNumeroContenedoresPanelesIncorrecto(ismp.getPanelesVagonesIncorrecto().toString());
		generarInformeISMPJSP
				.setNumeroContenedoresPanelesInspeccionado(ismp.getPanelesVagonesInspeccionados().toString());
		generarInformeISMPJSP.setNumeroONUIncorrecto(ismp.getNumeroOnuIncorrecto().toString());
		generarInformeISMPJSP.setFaltaPanelAlgunLateral(ismp.getFaltaPanelLateral().toString());
		generarInformeISMPJSP.setFaltaPanel2Laterales(ismp.getFaltaPanelDosLaterales().toString());
		generarInformeISMPJSP.setPanelesTamanoInadecuado(ismp.getTamanoInadecuadaPanel().toString());
		generarInformeISMPJSP.setPanelesOtras(ismp.getPanelesOtras().toString());
		generarInformeISMPJSP.setPanelesObservaciones(ismp.getPanelesObservaciones());
		generarInformeISMPJSP.setVacioCorrecta(ismp.getCartaVacioCorrecta().toString());
		generarInformeISMPJSP.setVacioIncorrecta(ismp.getCartaVacioIncorrecta().toString());
		generarInformeISMPJSP
				.setVacioTotal(String.valueOf(ismp.getCartaVacioCorrecta() + ismp.getCartaVacioIncorrecta()));
		generarInformeISMPJSP.setCargadoCorrecta(ismp.getCartaCargadoCorrecta().toString());
		generarInformeISMPJSP.setCargadoIncorrecta(ismp.getCartaCargadoIncorrecta().toString());
		generarInformeISMPJSP
				.setCargadoTotal(String.valueOf(ismp.getCartaCargadoCorrecta() + ismp.getCartaCargadoIncorrecta()));
		generarInformeISMPJSP.setNumeroDocumentosCorrecta(ismp.getDocumentosInspeccionadosCorrecta().toString());
		generarInformeISMPJSP.setNumeroDocumentosIncorrecta(ismp.getDocumentosInspeccionadosIncorrecta().toString());
		generarInformeISMPJSP.setNumeroDocumentosTotal(String
				.valueOf(ismp.getDocumentosInspeccionadosCorrecta() + ismp.getDocumentosInspeccionadosIncorrecta()));

		// FRAGMENTO ISMP 3

		generarInformeISMPJSP.setDatosCorrectoOrdenIncorrecto(ismp.getCartaOrdenInadecuado().toString());
		generarInformeISMPJSP.setDatosCorrectoExcesoLexico(ismp.getCartaExcesoLexico().toString());
		generarInformeISMPJSP.setDenominacionIncorrecta(ismp.getCartaDenominacionIncorrecta().toString());
		generarInformeISMPJSP.setNumeroPeligroIncorrecto(ismp.getCartaNumeroIncorrecto().toString());
		generarInformeISMPJSP.setFaltaFraseTransporteVacio(ismp.getCartaFaltaFrase().toString());
		generarInformeISMPJSP.setFaltaVariosDatos(ismp.getCartaFaltaDatos().toString());
		generarInformeISMPJSP.setFaltaMencionRelativa(ismp.getCartaFaltaMencion().toString());
		generarInformeISMPJSP.setCartaOtras(ismp.getCartaOtras().toString());
		generarInformeISMPJSP.setCartaObservaciones(ismp.getCartaObservaciones());
		generarInformeISMPJSP.setRecepcionPersonalConduccion(ismp.getRecepcionBoi());
		generarInformeISMPJSP.setRecepcionPersonalConduccionObservaciones(ismp.getRecepcionBoiObservaciones());
		generarInformeISMPJSP.setRecepcionCarta(ismp.getRecepcionFicha());
		generarInformeISMPJSP.setRecepcionCartaObservaciones(ismp.getRecepcionFichaObservaciones());

		// FRAGMENTO ISMP 4

		generarInformeISMPJSP.setReconocimientoRealizaTerminalCiclo(ismp.getReconocimientoMaterialTerminal());
		generarInformeISMPJSP.setReconocimientoRealizaTerminalCicloObservaciones(
				ismp.getReconocimientoMaterialTerminalObservaciones());
		generarInformeISMPJSP.setReconocimientoArchivaModeloCiclo(ismp.getReconocimientoMaterialArchiva());
		generarInformeISMPJSP
				.setReconocimientoArchivaModeloCicloObservaciones(ismp.getReconocimientoMaterialArchivaObservaciones());
		generarInformeISMPJSP.setReconocimientoTerminalMMPP(ismp.getReconocimientoMaterialOrigen());
		generarInformeISMPJSP
				.setReconocimientoTerminalMMPPObservaciones(ismp.getReconocimientoMaterialOrigenObservaciones());
		generarInformeISMPJSP.setReconocimientoRealizaTerminalPosteriorCarga(ismp.getReconocimientoPosteriorTerminal());
		generarInformeISMPJSP.setReconocimientoRealizaTerminalPosteriorCargaObservaciones(
				ismp.getReconocimientoPosteriorTerminalObservaciones());
		generarInformeISMPJSP.setReconocimientoArchivaModeloPosteriorCarga(ismp.getReconocimientoPosteriorModelo());
		generarInformeISMPJSP.setReconocimientoArchivaModeloPosteriorCargaObservaciones(
				ismp.getReconocimientoPosteriorModeloObservaciones());
		generarInformeISMPJSP.setReconocimientoDepositoNoEstanco(ismp.getReconocimientoVisualDeposito());
		generarInformeISMPJSP
				.setReconocimientoDepositoNoEstancoObservaciones(ismp.getReconocimientoVisualDepositoObservaciones());
		generarInformeISMPJSP.setReconocimientoDefectosManifiestos(ismp.getReconocimientoVisualDefectos());
		generarInformeISMPJSP
				.setReconocimientoDefectosManifiestosObservaciones(ismp.getReconocimientoVisualDefectosObservaciones());
		generarInformeISMPJSP.setReconocimientoSobrepasaNivel(ismp.getReconocimientoVisualCapacidad());
		generarInformeISMPJSP
				.setReconocimientoSobrepasaNivelObservaciones(ismp.getReconocimientoVisualCapacidadObservaciones());
		generarInformeISMPJSP.setReconocimientoObservaciones(ismp.getReconocimientoObservaciones());

		// FRAGMENTO ISMP 5

		generarInformeISMPJSP.setPrescripcionNotificaPersonal(ismp.getPrescripcionSeNotifica());
		generarInformeISMPJSP
				.setPrescripcionNotificaPersonalObservaciones(ismp.getPrescripcionSeNotificaObservaciones());
		generarInformeISMPJSP.setPrescripcionCorrespondeTexto(ismp.getPrescripcionCorresponde());
		generarInformeISMPJSP
				.setPrescripcionCorrespondeTextoObservaciones(ismp.getPrescripcionCorrespondeObservaciones());
		generarInformeISMPJSP.setMedidasCautelares(ismp.getMedidasCautelares());
		generarInformeISMPJSP.setListaAnexos(ismp.getListaDocumentos());

		// FRAGMENTO ISMP 6

		if (ismp.getFirmaInspector() != null) {
			generarInformeISMPJSP.setIdFirmaInspectorSeguridad(ismp.getFirmaInspector().getIdPersonal());
		}

		if (ismp.getFirmaResponsable() != null) {
			generarInformeISMPJSP.setIdFirmaResponsableSeguridad(ismp.getFirmaResponsable().getIdPersonal());
		}

		return generarInformeISMPJSP;
	}

	private Ismp convertGenerarInformeISMPJSPToISMP(GenerarInformeISMPJSP generarInformeISMPJSP) {

		Ismp ismp = ismpDAO.getOne(Integer.parseInt(generarInformeISMPJSP.getIdISMP()));
		Is is = isDAO.getOne(ismp.getIs().getIdIs());

		// FRAGMENTO IS
		is.setFechaInspeccion(Utiles.parseDatePantalla(generarInformeISMPJSP.getFecha()));
		is.setLugar(generarInformeISMPJSP.getLugarInspeccion());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getIdTren())) {
			is.setTren(trenDAO.getOne(Integer.parseInt(generarInformeISMPJSP.getIdTren())));
		}

		// FRAGMENTO ISMP 1

		ismp.setTiposMercancias(generarInformeISMPJSP.getTipoMercancias());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getNumeroVagonesComposicion())) {
			ismp.setNumeroVagones(generarInformeISMPJSP.getNumeroVagonesComposicion());
		}

		ismp.setEtiquetasVagonesBuenEstado(
				Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresEtiquetadoBuenEstado()));
		ismp.setEtiquetasVagonesMal(Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresEtiquetadoMalEstado()));
		ismp.setEtiquetasVagonesIncorrecto(
				Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresEtiquetadoIncorrecto()));
		ismp.setEtiquetasVagonesInspeccionados(
				Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresEtiquetadoInspeccionado()));
		ismp.setFaltaTotalEtiquetas(Integer.parseInt(generarInformeISMPJSP.getFaltaTotalEtiquetas()));
		ismp.setFaltaAlgunaEtiqueta(Integer.parseInt(generarInformeISMPJSP.getFaltaAlgunaEtiqueta()));
		ismp.setEtiquetaInadecuada(Integer.parseInt(generarInformeISMPJSP.getEtiquetasInadecuadas()));
		ismp.setTamanoInadecuado(Integer.parseInt(generarInformeISMPJSP.getEtiquetasTamanoInadecuado()));
		ismp.setOtras(Integer.parseInt(generarInformeISMPJSP.getEtiquetasOtras()));

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getEtiquetasObservaciones())) {
			ismp.setEtiquetasObservaciones(generarInformeISMPJSP.getEtiquetasObservaciones());
		}

		// FRAGMENTO ISMP 2

		ismp.setPanelesVagonesBuenEstado(
				Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresPanelesBuenEstado()));
		ismp.setPanelesVagonesMalEstado(
				Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresPanelesMalEstado()));
		ismp.setPanelesVagonesIncorrecto(
				Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresPanelesIncorrecto()));
		ismp.setPanelesVagonesInspeccionados(
				Integer.parseInt(generarInformeISMPJSP.getNumeroContenedoresPanelesInspeccionado()));
		ismp.setNumeroOnuIncorrecto(Integer.parseInt(generarInformeISMPJSP.getNumeroONUIncorrecto()));
		ismp.setFaltaPanelLateral(Integer.parseInt(generarInformeISMPJSP.getFaltaPanelAlgunLateral()));
		ismp.setFaltaPanelDosLaterales(Integer.parseInt(generarInformeISMPJSP.getFaltaPanel2Laterales()));
		ismp.setTamanoInadecuadaPanel(Integer.parseInt(generarInformeISMPJSP.getPanelesTamanoInadecuado()));
		ismp.setPanelesOtras(Integer.parseInt(generarInformeISMPJSP.getPanelesOtras()));

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getPanelesObservaciones())) {
			ismp.setPanelesObservaciones(generarInformeISMPJSP.getPanelesObservaciones());
		}

		ismp.setCartaVacioCorrecta(Integer.parseInt(generarInformeISMPJSP.getVacioCorrecta()));
		ismp.setCartaVacioIncorrecta(Integer.parseInt(generarInformeISMPJSP.getVacioIncorrecta()));
		ismp.setCartaCargadoCorrecta(Integer.parseInt(generarInformeISMPJSP.getCargadoCorrecta()));
		ismp.setCartaCargadoIncorrecta(Integer.parseInt(generarInformeISMPJSP.getCargadoIncorrecta()));
		ismp.setDocumentosInspeccionadosCorrecta(Integer.parseInt(generarInformeISMPJSP.getNumeroDocumentosCorrecta()));
		ismp.setDocumentosInspeccionadosIncorrecta(
				Integer.parseInt(generarInformeISMPJSP.getNumeroDocumentosIncorrecta()));

		// FRAGMENTO ISMP 3

		ismp.setCartaOrdenInadecuado(Integer.parseInt(generarInformeISMPJSP.getDatosCorrectoOrdenIncorrecto()));
		ismp.setCartaExcesoLexico(Integer.parseInt(generarInformeISMPJSP.getDatosCorrectoExcesoLexico()));
		ismp.setCartaNumeroIncorrecto(Integer.parseInt(generarInformeISMPJSP.getDenominacionIncorrecta()));
		ismp.setCartaFaltaFrase(Integer.parseInt(generarInformeISMPJSP.getFaltaFraseTransporteVacio()));
		ismp.setCartaFaltaDatos(Integer.parseInt(generarInformeISMPJSP.getFaltaVariosDatos()));
		ismp.setCartaFaltaMencion(Integer.parseInt(generarInformeISMPJSP.getFaltaMencionRelativa()));
		ismp.setCartaOtras(Integer.parseInt(generarInformeISMPJSP.getCartaOtras()));
		ismp.setRecepcionBoi(generarInformeISMPJSP.isRecepcionPersonalConduccion());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getRecepcionPersonalConduccionObservaciones())) {
			ismp.setRecepcionBoiObservaciones(generarInformeISMPJSP.getRecepcionPersonalConduccionObservaciones());
		}

		ismp.setRecepcionFicha(generarInformeISMPJSP.isRecepcionCarta());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getRecepcionCartaObservaciones())) {
			ismp.setRecepcionFichaObservaciones(generarInformeISMPJSP.getRecepcionCartaObservaciones());
		}

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getCartaObservaciones())) {
			ismp.setCartaObservaciones(generarInformeISMPJSP.getCartaObservaciones());
		}

		// FRAGMENTO ISMP 4

		ismp.setReconocimientoMaterialTerminal(generarInformeISMPJSP.isReconocimientoRealizaTerminalCiclo());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoRealizaTerminalCicloObservaciones())) {
			ismp.setReconocimientoMaterialTerminalObservaciones(
					generarInformeISMPJSP.getReconocimientoRealizaTerminalCicloObservaciones());
		}

		ismp.setReconocimientoMaterialArchiva(generarInformeISMPJSP.isReconocimientoArchivaModeloCiclo());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoArchivaModeloCicloObservaciones())) {
			ismp.setReconocimientoMaterialArchivaObservaciones(
					generarInformeISMPJSP.getReconocimientoArchivaModeloCicloObservaciones());
		}

		ismp.setReconocimientoMaterialOrigen(generarInformeISMPJSP.isReconocimientoTerminalMMPP());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoTerminalMMPPObservaciones())) {
			ismp.setReconocimientoMaterialOrigenObservaciones(
					generarInformeISMPJSP.getReconocimientoTerminalMMPPObservaciones());
		}

		ismp.setReconocimientoPosteriorTerminal(generarInformeISMPJSP.isReconocimientoRealizaTerminalPosteriorCarga());

		if (StringUtils
				.isNotBlank(generarInformeISMPJSP.getReconocimientoRealizaTerminalPosteriorCargaObservaciones())) {
			ismp.setReconocimientoPosteriorTerminalObservaciones(
					generarInformeISMPJSP.getReconocimientoRealizaTerminalPosteriorCargaObservaciones());
		}

		ismp.setReconocimientoPosteriorModelo(generarInformeISMPJSP.isReconocimientoArchivaModeloPosteriorCarga());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoArchivaModeloPosteriorCargaObservaciones())) {
			ismp.setReconocimientoPosteriorModeloObservaciones(
					generarInformeISMPJSP.getReconocimientoArchivaModeloPosteriorCargaObservaciones());
		}

		ismp.setReconocimientoVisualDeposito(generarInformeISMPJSP.isReconocimientoDepositoNoEstanco());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoDepositoNoEstancoObservaciones())) {
			ismp.setReconocimientoVisualDepositoObservaciones(
					generarInformeISMPJSP.getReconocimientoDepositoNoEstancoObservaciones());
		}

		ismp.setReconocimientoVisualDefectos(generarInformeISMPJSP.isReconocimientoDefectosManifiestos());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoDefectosManifiestosObservaciones())) {
			ismp.setReconocimientoVisualDefectosObservaciones(
					generarInformeISMPJSP.getReconocimientoDefectosManifiestosObservaciones());
		}

		ismp.setReconocimientoVisualCapacidad(generarInformeISMPJSP.isReconocimientoSobrepasaNivel());

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoSobrepasaNivelObservaciones())) {
			ismp.setReconocimientoVisualCapacidadObservaciones(
					generarInformeISMPJSP.getReconocimientoSobrepasaNivelObservaciones());
		}

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getReconocimientoObservaciones())) {
			ismp.setReconocimientoObservaciones(generarInformeISMPJSP.getReconocimientoObservaciones());
		}

		// FRAGMENTO ISMP 5

		ismp.setPrescripcionSeNotifica(generarInformeISMPJSP.isPrescripcionNotificaPersonal());
		if (StringUtils.isNotBlank(generarInformeISMPJSP.getPrescripcionNotificaPersonalObservaciones())) {
			ismp.setPrescripcionSeNotificaObservaciones(
					generarInformeISMPJSP.getPrescripcionNotificaPersonalObservaciones());
		}
		ismp.setPrescripcionCorresponde(generarInformeISMPJSP.isPrescripcionCorrespondeTexto());
		if (StringUtils.isNotBlank(generarInformeISMPJSP.getPrescripcionCorrespondeTextoObservaciones())) {
			ismp.setPrescripcionCorrespondeObservaciones(
					generarInformeISMPJSP.getPrescripcionCorrespondeTextoObservaciones());
		}

		if (StringUtils.isNotBlank(generarInformeISMPJSP.getMedidasCautelares())) {
			ismp.setMedidasCautelares(generarInformeISMPJSP.getMedidasCautelares());
		}
		if (StringUtils.isNotBlank(generarInformeISMPJSP.getListaAnexos())) {
			ismp.setListaDocumentos(generarInformeISMPJSP.getListaAnexos());
		}

		ismp.setIs(is);
		return ismp;
	}

}
