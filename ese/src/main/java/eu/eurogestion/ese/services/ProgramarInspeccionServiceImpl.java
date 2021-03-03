package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Cad;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Is;
import eu.eurogestion.ese.domain.Iscc;
import eu.eurogestion.ese.domain.Iseet;
import eu.eurogestion.ese.domain.Iseet2;
import eu.eurogestion.ese.domain.Iseet3;
import eu.eurogestion.ese.domain.Ismp;
import eu.eurogestion.ese.domain.Iso;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.TipoInspeccion;
import eu.eurogestion.ese.domain.TipoTarea;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ProgramarInspeccionJSP;
import eu.eurogestion.ese.repository.CadDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoInspeccionDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.IsDAO;
import eu.eurogestion.ese.repository.IsccDAO;
import eu.eurogestion.ese.repository.Iseet2DAO;
import eu.eurogestion.ese.repository.Iseet3DAO;
import eu.eurogestion.ese.repository.IseetDAO;
import eu.eurogestion.ese.repository.IsmpDAO;
import eu.eurogestion.ese.repository.IsoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TipoInspeccionDAO;
import eu.eurogestion.ese.repository.TipoTareaDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ProgramarInspeccionServiceImpl implements ProgramarInspeccionService {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public TipoInspeccionDAO tipoInspeccionDAO;

	@Autowired
	public EstadoInspeccionDAO estadoInspeccionDAO;

	@Autowired
	public TrenDAO trenDAO;

	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	@Autowired
	public IsoDAO isoDAO;

	@Autowired
	public IseetDAO iseetDAO;

	@Autowired
	public Iseet2DAO iseet2DAO;

	@Autowired
	public Iseet3DAO iseet3DAO;

	@Autowired
	public IsmpDAO ismpDAO;

	@Autowired
	public IsccDAO isccDAO;

	@Autowired
	public IsDAO isDAO;

	@Autowired
	public TareaPendienteDAO tareaPendienteDAO;

	@Autowired
	public TipoTareaDAO tipoTareaDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public CadDAO cadDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void registroInspeccionYTarea(ProgramarInspeccionJSP formProgramarInspeccion, HttpSession sesion)
			throws EseException {
		switch (Integer.parseInt(formProgramarInspeccion.getIdTipoInspeccion())) {
		case Constantes.TIPO_INSPECCION_ISO:
			programarISO(formProgramarInspeccion, sesion);
			break;
		case Constantes.TIPO_INSPECCION_ISCC:
			programarISCC(formProgramarInspeccion, sesion);
			break;
		case Constantes.TIPO_INSPECCION_ISMP:
			programarISMP(formProgramarInspeccion, sesion);
			break;
		case Constantes.TIPO_INSPECCION_ISEER:
			// TODO falta hacer el metodo
			break;
		case Constantes.TIPO_INSPECCION_ISEET:
			programarISEET(formProgramarInspeccion, sesion);
			break;
		case Constantes.TIPO_INSPECCION_ISRC:
			// TODO falta hacer el metodo
			break;
		case Constantes.TIPO_INSPECCION_CAD:
			programarCAD(formProgramarInspeccion, sesion);
			break;
		default:
			break;
		}
	}

	private void programarISCC(ProgramarInspeccionJSP formProgramarInspeccion, HttpSession sesion) throws EseException {

		Iscc iscc = new Iscc();
		iscc.setIs(crearIsByForm(formProgramarInspeccion));

		isDAO.save(iscc.getIs());
		iscc = isccDAO.save(iscc);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicInsSegEstCarComFer(iscc);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		Evidencia evidencia = crearEvidencia(iscc.getIs().getNumReferencia(), fichero,
				Constantes.TIPO_EVIDENCIA_ISCC_PLANIFICADA);

		iscc.setEvidencia30(evidencia);
		iscc = inicializarFormularioISCC(iscc);
		Iscc isccBBDD = isccDAO.save(iscc);

		TareaPendiente tarea = crearTareaByForm(formProgramarInspeccion, isccBBDD.getIdIscc(),
				Constantes.TAREAPTE_GENERAR_ISCC);
		tareaPendienteDAO.save(tarea);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private void programarISMP(ProgramarInspeccionJSP formProgramarInspeccion, HttpSession sesion) throws EseException {

		Ismp ismp = new Ismp();
		ismp.setIs(crearIsByForm(formProgramarInspeccion));

		isDAO.save(ismp.getIs());
		ismp = ismpDAO.save(ismp);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicISMPMerPel(ismp);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		Evidencia evidencia = crearEvidencia(ismp.getIs().getNumReferencia(), fichero,
				Constantes.TIPO_EVIDENCIA_ISMP_PLANIFICADA);

		ismp.setEvidencia78(evidencia);
		ismp = inicializarFormularioISMP(ismp);
		Ismp ismpBBDD = ismpDAO.save(ismp);

		TareaPendiente tarea = crearTareaByForm(formProgramarInspeccion, ismpBBDD.getIdIsmp(),
				Constantes.TAREAPTE_GENERAR_ISMP);
		tareaPendienteDAO.save(tarea);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private void programarISEET(ProgramarInspeccionJSP formProgramarInspeccion, HttpSession sesion)
			throws EseException {

		Iseet iseet = new Iseet();
		iseet.setIs(crearIsByForm(formProgramarInspeccion));
		isDAO.save(iseet.getIs());
		Iseet2 iseet2 = new Iseet2();
		Iseet2 iseet2DDBB = iseet2DAO.save(iseet2);
		Iseet3 iseet3 = new Iseet3();
		Iseet3 iseet3DDBB = iseet3DAO.save(iseet3);
		iseet.setIseet2(iseet2DDBB);
		iseet.setIseet3(iseet3DDBB);
		iseet = iseetDAO.save(iseet);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicInsSegEquEleSegMatRodTra(iseet);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		Evidencia evidencia = crearEvidencia(iseet.getIs().getNumReferencia(), fichero,
				Constantes.TIPO_EVIDENCIA_ISEET_PLANIFICADA);

		iseet.setEvidencia15(evidencia);
		Iseet iseetBBDD = iseetDAO.save(iseet);

		TareaPendiente tarea = crearTareaByForm(formProgramarInspeccion, iseetBBDD.getIdIseet(),
				Constantes.TAREAPTE_GENERAR_ISEET);
		tareaPendienteDAO.save(tarea);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private void programarCAD(ProgramarInspeccionJSP formProgramarInspeccion, HttpSession sesion) throws EseException {

		Cad cad = new Cad();
		cad.setIs(crearIsByForm(formProgramarInspeccion));

		isDAO.save(cad.getIs());

		cad = cadDAO.save(cad);

		TareaPendiente tarea = crearTareaByForm(formProgramarInspeccion, cad.getIdCad(),
				Constantes.TAREAPTE_REALIZAR_CAD);
		tareaPendienteDAO.save(tarea);
	}

	private void programarISO(ProgramarInspeccionJSP formProgramarInspeccion, HttpSession sesion) throws EseException {

		Iso iso = new Iso();
		iso.setIs(crearIsByForm(formProgramarInspeccion));
		isDAO.save(iso.getIs());
		iso = isoDAO.save(iso);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFicInsSegOpe(iso);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		Evidencia evidencia = crearEvidencia(iso.getIs().getNumReferencia(), fichero,
				Constantes.TIPO_EVIDENCIA_ISO_PLANIFICADA);

		iso.setEvidencia7(evidencia);
		Iso isoBBDD = isoDAO.save(iso);

		TareaPendiente tarea = crearTareaByForm(formProgramarInspeccion, isoBBDD.getIdIso(),
				Constantes.TAREAPTE_GENERAR_ISO);
		tareaPendienteDAO.save(tarea);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private Evidencia crearEvidencia(String titulo, byte[] fichero, Integer tipoEvidenciaId) throws EseException {

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(tipoEvidenciaId);

		Evidencia evidencia = utilesPDFService.crearEvidencia(titulo, fichero, md5, tipoEvidencia, Utiles.sysdate(),
				Constantes.TYPE_PDF);

		return evidencia;
	}

	private TareaPendiente crearTareaByForm(ProgramarInspeccionJSP inspeccionForm, Integer idInspeccion,
			String tablaTareaPte) {

		TareaPendiente tarea = new TareaPendiente();

		tarea.setIdTareaPte(idInspeccion);
		tarea.setTablaTareaPte(tablaTareaPte);
		tarea.setFecha(Utiles.parseDatePantalla(inspeccionForm.getFechaInspeccion()));

		Personal origen = personalDAO.getOne(Integer.parseInt(inspeccionForm.getIdCreador()));
		tarea.setOrigen(origen);

		Personal destinatario = personalDAO.getOne(Integer.parseInt(inspeccionForm.getIdInspectorAsignado()));
		tarea.setDestinatario(destinatario);

		TipoTarea tipoTarea = tipoTareaDAO.getOne(Constantes.TIPO_TAREA_INSPECCION_ASIGNADA);
		tarea.setTipoTarea(tipoTarea);

		tarea.setLeido(Boolean.FALSE);

		return tarea;
	}

	private Ismp inicializarFormularioISMP(Ismp ismp) {
		ismp.setEtiquetasVagonesBuenEstado(0);
		ismp.setEtiquetasVagonesMal(0);
		ismp.setEtiquetasVagonesIncorrecto(0);
		ismp.setEtiquetasVagonesInspeccionados(0);
		ismp.setFaltaTotalEtiquetas(0);
		ismp.setFaltaAlgunaEtiqueta(0);
		ismp.setEtiquetaInadecuada(0);
		ismp.setTamanoInadecuado(0);
		ismp.setOtras(0);
		ismp.setPanelesVagonesBuenEstado(0);
		ismp.setPanelesVagonesMalEstado(0);
		ismp.setPanelesVagonesIncorrecto(0);
		ismp.setPanelesVagonesInspeccionados(0);
		ismp.setNumeroOnuIncorrecto(0);
		ismp.setFaltaPanelLateral(0);
		ismp.setFaltaPanelDosLaterales(0);
		ismp.setTamanoInadecuadaPanel(0);
		ismp.setPanelesOtras(0);
		ismp.setCartaVacioCorrecta(0);
		ismp.setCartaVacioIncorrecta(0);
		ismp.setCartaCargadoCorrecta(0);
		ismp.setCartaCargadoIncorrecta(0);
		ismp.setDocumentosInspeccionadosCorrecta(0);
		ismp.setDocumentosInspeccionadosIncorrecta(0);
		ismp.setCartaOrdenInadecuado(0);
		ismp.setCartaExcesoLexico(0);
		ismp.setCartaDenominacionIncorrecta(0);
		ismp.setCartaNumeroIncorrecto(0);
		ismp.setCartaFaltaFrase(0);
		ismp.setCartaFaltaDatos(0);
		ismp.setCartaFaltaMencion(0);
		ismp.setCartaOtras(0);
		ismp.setRecepcionBoi(Boolean.FALSE);
		ismp.setRecepcionFicha(Boolean.FALSE);
		ismp.setReconocimientoMaterialTerminal(Boolean.FALSE);
		ismp.setReconocimientoMaterialArchiva(Boolean.FALSE);
		ismp.setReconocimientoMaterialOrigen(Boolean.FALSE);
		ismp.setReconocimientoPosteriorTerminal(Boolean.FALSE);
		ismp.setReconocimientoPosteriorModelo(Boolean.FALSE);
		ismp.setReconocimientoVisualDeposito(Boolean.FALSE);
		ismp.setReconocimientoVisualDefectos(Boolean.FALSE);
		ismp.setReconocimientoVisualCapacidad(Boolean.FALSE);
		ismp.setPrescripcionSeNotifica(Boolean.FALSE);
		ismp.setPrescripcionCorresponde(Boolean.FALSE);
		return ismp;
	}

	private Iscc inicializarFormularioISCC(Iscc iscc) {

		iscc.setCargaDistribucion(0);
		iscc.setCargaCargasConcentradas(0);
		iscc.setCargaDistancia(0);
		iscc.setCargaAltura(0);
		iscc.setCargaRebasa(0);
		iscc.setCargaCargamento(0);
		iscc.setVagonesConservan(0);
		iscc.setVagonesCerradosTechos(0);
		iscc.setVagonesCerradasPuertas(0);
		iscc.setVagonesCerradasTapas(0);
		iscc.setVagonesCerradasCompuertas(0);
		iscc.setVagonesCerradasValvulas(0);
		iscc.setVagonesBordesLevantados(0);
		iscc.setVagonesBordesBajadosInmovil(0);
		iscc.setVagonesBordesBajados(0);
		iscc.setVagonesVisiblesInscripciones(0);
		iscc.setVagonesTelerosAmovibles(0);
		iscc.setVagonesTelerosPivotantes(0);
		iscc.setVagonesTelerosPivotantesHorizontal(0);
		iscc.setVagonesCadenasTeleros(0);
		iscc.setVagonesDispositivosTension(0);
		iscc.setVagonesPiso(0);
		iscc.setVagonesMercancias(0);
		iscc.setVagonesParedes(0);
		iscc.setVagonesPuertas(0);
		iscc.setVagonesAnillas(0);
		iscc.setVagonesEquipamientos(0);
		iscc.setAcondicionamientoReparto(0);
		iscc.setAcondicionamientoMercancias(0);
		iscc.setAcondicionamientoPesadas(0);
		iscc.setAcondicionamientoSusceptibles(0);
		iscc.setAcondicionamientoSolidamenteSujeta(0);
		iscc.setAcondicionamientoLonaProxima(0);
		iscc.setAcondicionamientoLonaSeparacion(0);
		iscc.setAcondicionamientoVerificarParedes(0);
		iscc.setAcondicionamientoVerificarCalces(0);
		iscc.setAcondicionamientoVerificarAmarre(0);
		iscc.setAcondicionamientoVerificarDosAmarras(0);
		iscc.setAcondicionamientoVerificarCalces7(0);
		iscc.setAcondicionamientoVerificarBastidores(0);
		iscc.setAcondicionamientoVerificarCunas(0);
		iscc.setAcondicionamientoVerificarTransversal(0);
		iscc.setAcondicionamientoVerificarLongitudinal(0);
		iscc.setAcondicionamientoVerificarBobinasMenos10(0);
		iscc.setAcondicionamientoVerificarBobinasMas10(0);
		iscc.setAcondicionamientoBascularAgrupamiento(0);
		iscc.setAcondicionamientoBascularPuntales(0);
		iscc.setAcondicionamientoBascularCaballetes(0);
		iscc.setAcondicionamientoApiladaRepartidas(0);
		iscc.setAcondicionamientoApiladaAnchura(0);
		iscc.setAcondicionamientoApiladaCapas(0);
		iscc.setAcondicionamientoApiladaRozamiento(0);
		iscc.setAcondicionamientoApiladaBastidores(0);
		iscc.setAcondicionamientoApiladaUnidades(0);
		iscc.setAcondicionamientoApiladaCintas(0);
		iscc.setAcondicionamientoApiladaParteInferior(0);
		iscc.setAcondicionamientoApiladaIntercalares(0);
		iscc.setAcondicionamientoApiladaImbricadas(0);
		iscc.setAcondicionamientoApiladaOscilar(0);
		iscc.setAcondicionamientoApiladaTubos(0);
		iscc.setAcondicionamientoApiladaCapasCalces(0);
		iscc.setAcondicionamientoRigidas(0);
		iscc.setAcondicionamientoFlexibles(0);
		iscc.setMediosCalcesRetencion(0);
		iscc.setMediosCalcesRodadura(0);
		iscc.setMediosIntercalares(0);
		iscc.setMediosAmarres(0);

		return iscc;
	}

	private Is crearIsByForm(ProgramarInspeccionJSP inspeccionForm) throws EseException {

		Is is = new Is();

		is.setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_PLANIFICADA));

		TipoInspeccion tipoInspeccion = tipoInspeccionDAO
				.getOne(Integer.parseInt(inspeccionForm.getIdTipoInspeccion()));
		is.setTipoInspeccion(tipoInspeccion);

		is.setNumReferencia(inspeccionForm.getReferencia());
		is.setFechaInspeccion(Utiles.parseDatePantalla(inspeccionForm.getFechaInspeccion()));

		if (StringUtils.isNotBlank(inspeccionForm.getIdtren())) {
			Tren tren = trenDAO.getOne(Integer.parseInt(inspeccionForm.getIdtren()));
			is.setTren(tren);

		}

		Personal inspector = personalDAO.getOne(Integer.parseInt(inspeccionForm.getIdInspectorAsignado()));
		is.setInspector(inspector);

		if (StringUtils.isNotBlank(inspeccionForm.getLugar())) {
			is.setLugar(inspeccionForm.getLugar());
		}

		Personal creador = personalDAO.getOne(Integer.parseInt(inspeccionForm.getIdCreador()));
		is.setCreador(creador);

		return is;
	}
}
