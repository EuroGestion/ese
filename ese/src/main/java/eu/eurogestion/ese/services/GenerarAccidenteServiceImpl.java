package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eu.eurogestion.ese.blockchain.DocumentoAtril;
import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.TipoAccidente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarAccidenteJSP;
import eu.eurogestion.ese.repository.AccidenteDAO;
import eu.eurogestion.ese.repository.CausaAccidenteDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoAccidenteDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoAccidenteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class GenerarAccidenteServiceImpl implements GenerarAccidenteService {

	/** Repositories & Services **/

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public AccidenteDAO accidenteDAO;

	@Autowired
	public EstadoAccidenteDAO estadoAccidenteDAO;

	@Autowired
	public TipoAccidenteDAO tipoAccidenteDAO;

	@Autowired
	public CausaAccidenteDAO causaAccidenteDAO;

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public Accidente guardarAccidente(GenerarAccidenteJSP generarAccidenteJSP) throws EseException {

		Accidente accidente;

		if (generarAccidenteJSP.getIdAccidente() != null) {
			accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		} else {
			accidente = new Accidente();
			accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_CREADO));
		}

		accidente.setNumeroSuceso(generarAccidenteJSP.getNumeroIdentificacion());
		accidente.setCompania(companiaDAO.getOne(Integer.parseInt(generarAccidenteJSP.getIdEmpresa())));

		accidente.setTipoAccidente(obtenerTipoAccidente(generarAccidenteJSP));
		accidente.setCausaAccidente(causaAccidenteDAO.getOne(Integer.parseInt(generarAccidenteJSP.getIdCausa())));
		accidente.setFechaAccidente(Utiles.parseDatePantalla(generarAccidenteJSP.getFecha()));
		accidente.setHoraAccidente(Utiles.parseTimeHorasPantalla(generarAccidenteJSP.getHora()));
		accidente.setLugarAccidente(generarAccidenteJSP.getLugar());

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDescripcionSucesoInvestigacion())) {
			accidente.setDescripcion(generarAccidenteJSP.getDescripcionSucesoInvestigacion());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getCondicionesAtmosfericasInvestigacion())) {
			accidente.setCondicionesAtmosfericas(generarAccidenteJSP.getCondicionesAtmosfericasInvestigacion());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getCirculacionesImplicadasInvestigacion())) {
			accidente.setCirculacionesImplicadas(generarAccidenteJSP.getCirculacionesImplicadasInvestigacion());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getComposicionTrenesInvestigacion())) {
			accidente.setComposicionTrenes(generarAccidenteJSP.getComposicionTrenesInvestigacion());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDanhiosMaterialRodanteInvestigacion())) {
			accidente.setDanosMaterialesMaterial(generarAccidenteJSP.getDanhiosMaterialRodanteInvestigacion());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDanhiosInfraestructuraInvestigacion())) {
			accidente.setDanosMaterialesInfraestructura(generarAccidenteJSP.getDanhiosInfraestructuraInvestigacion());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getPerturbacionesServicio())) {
			accidente.setPerturbacionesServicio(generarAccidenteJSP.getPerturbacionesServicio());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getPrevisionesRestablecimiento())) {
			accidente.setPrevisionesRestablecimiento(generarAccidenteJSP.getPrevisionesRestablecimiento());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getPrimerasMedidasAdoptadas())) {
			accidente.setPrimerasMedidas(generarAccidenteJSP.getPrimerasMedidasAdoptadas());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getNotificacionesEfectuadas())) {
			accidente.setNotificacionesEfectuadas(generarAccidenteJSP.getNotificacionesEfectuadas());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getObservacionesFichaAccidente())) {
			accidente.setObservacionesFichaAccidente(generarAccidenteJSP.getObservacionesFichaAccidente());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getHeridosVictimaTren())) {
			accidente.setHeridosTren(Integer.parseInt(generarAccidenteJSP.getHeridosVictimaTren()));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getMuertesVictimaTren())) {
			accidente.setFallecidosTren(Integer.parseInt(generarAccidenteJSP.getMuertesVictimaTren()));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getHeridosVictimaAjenaTren())) {
			accidente.setHeridosAjenos(Integer.parseInt(generarAccidenteJSP.getHeridosVictimaAjenaTren()));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getMuertesVictimaAjenaTren())) {
			accidente.setFallecidosAjenos(Integer.parseInt(generarAccidenteJSP.getMuertesVictimaAjenaTren()));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getIdResponsableSeguridad())) {
			accidente.setResponsableSeguridad(
					personalDAO.getOne(Integer.parseInt(generarAccidenteJSP.getIdResponsableSeguridad())));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getNombreResponsableCIAF())) {
			accidente.setInvestigadorCiafNombre(generarAccidenteJSP.getNombreResponsableCIAF());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getTelefonoResponsableCIAF())) {
			accidente.setInvestigadorCiafTelefono(generarAccidenteJSP.getTelefonoResponsableCIAF());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getCorreoResponsableCIAF())) {
			accidente.setInvestigadorCiafEmail(generarAccidenteJSP.getCorreoResponsableCIAF());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getObservacionesIntervieneCIAF())) {
			accidente.setObservacionesIntervieneCiaf(generarAccidenteJSP.getObservacionesIntervieneCIAF());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getIdDelegadoInvestigacion())) {
			accidente.setDelegado(
					personalDAO.getOne(Integer.parseInt(generarAccidenteJSP.getIdDelegadoInvestigacion())));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getNumeroInformeFinal())) {
			accidente.setNumeroReferenciaFinal(generarAccidenteJSP.getNumeroInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getFechaInformeFinal())) {
			accidente.setFechaInformeFinal(Utiles.parseDatePantalla(generarAccidenteJSP.getFechaInformeFinal()));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getSucesoInformeFinal())) {
			accidente.setHechosSuceso(generarAccidenteJSP.getSucesoInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getCircunstanciasSucesoInformeFinal())) {
			accidente.setHechosCircunstancias(generarAccidenteJSP.getCircunstanciasSucesoInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDanosInformeFinal())) {
			accidente.setHechosDanos(generarAccidenteJSP.getDanosInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getCircunstanciasExternasInformeFinal())) {
			accidente.setHechosCircunstanciasExternas(generarAccidenteJSP.getCircunstanciasExternasInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getResumenDeclaracionesTestigosInformeFinal())) {
			accidente.setResumenDeclaraciones(generarAccidenteJSP.getResumenDeclaracionesTestigosInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getSistemaGestionSeguridadInformeFinal())) {
			accidente.setResumenSgs(generarAccidenteJSP.getSistemaGestionSeguridadInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getNormativaInformeFinal())) {
			accidente.setResumenNormativa(generarAccidenteJSP.getNormativaInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getFuncionamientoMaterialRodanteInformeFinal())) {
			accidente.setResumenFuncionamiento(generarAccidenteJSP.getFuncionamientoMaterialRodanteInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDocumentacionGestionCirculacionInformeFinal())) {
			accidente.setResumenDocumentacion(generarAccidenteJSP.getDocumentacionGestionCirculacionInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getInterfazHombreMaquinaInformeFinal())) {
			accidente.setResumenInterfaz(generarAccidenteJSP.getInterfazHombreMaquinaInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getOtrosSucesosAnterioresInformeFinal())) {
			accidente.setResumenOtros(generarAccidenteJSP.getOtrosSucesosAnterioresInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDescripcionDefinitivaInformeFinal())) {
			accidente.setAnalisisDescripcion(generarAccidenteJSP.getDescripcionDefinitivaInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDeliberacionInformeFinal())) {
			accidente.setAnalisisDeliberacion(generarAccidenteJSP.getDeliberacionInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getConclusionesInformeFinal())) {
			accidente.setAnalisisConclusiones(generarAccidenteJSP.getConclusionesInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getObservacionesAdicionalesInformeFinal())) {
			accidente.setAnalisisObservaciones(generarAccidenteJSP.getObservacionesAdicionalesInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getMedidasAdoptadasInformeFinal())) {
			accidente.setMedidasAdoptadas(generarAccidenteJSP.getMedidasAdoptadasInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getRecomendacionesInformeFinal())) {
			accidente.setRecomendaciones(generarAccidenteJSP.getRecomendacionesInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getDatosComplementariosInformeFinal())) {
			accidente.setDatosComplementarios(generarAccidenteJSP.getDatosComplementariosInformeFinal());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getNumIdentificacion())) {
			accidente.setNumIdInfCiaf(generarAccidenteJSP.getNumIdentificacion());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getFechaCIAF())) {
			accidente.setFechaInfCiaf(Utiles.parseDatePantalla(generarAccidenteJSP.getFechaCIAF()));
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getComentariosInformeCIAF())) {
			accidente.setComentariosAlInfCiaf(generarAccidenteJSP.getComentariosInformeCIAF());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getObservacionesIntervencionCIAF())) {
			accidente.setObservacionesAlInfCiaf(generarAccidenteJSP.getObservacionesIntervencionCIAF());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getAnnoReferencia())) {
			accidente.setFichaMedidasAno(generarAccidenteJSP.getAnnoReferencia());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getNumRefInformeFinalCIAF())) {
			accidente.setFichaMedidasRefInforme(generarAccidenteJSP.getNumRefInformeFinalCIAF());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getMedidasAdoptadas())) {
			accidente.setFichaMedidasAdoptadas(generarAccidenteJSP.getMedidasAdoptadas());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getMedidasProyectadas())) {
			accidente.setFichaMedidasProyectadas(generarAccidenteJSP.getMedidasProyectadas());
		}

		if (StringUtils.isNotBlank(generarAccidenteJSP.getObservaciones())) {
			accidente.setFichaMedidasObservaciones(generarAccidenteJSP.getObservaciones());
		}

		Accidente accidenteDDBB = accidenteDAO.save(accidente);

		return accidenteDDBB;
	}

	@Override
	public void confirmarIntervieneCIAF(GenerarAccidenteJSP generarAccidenteJSP) throws EseException {
		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		accidente.setIntervieneCiaf(generarAccidenteJSP.getIntervieneCIAF());
		if (!generarAccidenteJSP.getIntervieneCIAF()) {
			accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_RECOGIENDO_DATOS));
		}
		accidenteDAO.save(accidente);
		generarAccidenteJSP.setIdEstadoAccidente(accidente.getEstadoAccidente().getIdEstadoAccidente());

	}

	@Override
	public void confirmarDelegaInvestigacion(GenerarAccidenteJSP generarAccidenteJSP) throws EseException {
		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		accidente.setSeDelegaInvestigacion(generarAccidenteJSP.getDelegaInvestigacion());
		if (!generarAccidenteJSP.getDelegaInvestigacion()) {
			accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_RECOGIENDO_DATOS));
		}
		accidenteDAO.save(accidente);
		generarAccidenteJSP.setIdEstadoAccidente(accidente.getEstadoAccidente().getIdEstadoAccidente());
	}

	public void firmaAccidenteFichaAccidente(GenerarAccidenteJSP generarAccidenteJSP, Model model) throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		DocumentoAtril documentoAtril = new DocumentoAtril();

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaFichaAccidente(personalDAO.getOne(generarAccidenteJSP.getIdFirmaFichaAccidente()));
		accidente.setFechahoraFirmaFichaAccidente(new Date());

		try {
			documentoAtril.setFile(utilesPDFService.generarDocumentoFicAccIncFer(accidente));
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}
		documentoAtril.setType(Constantes.TYPE_PDF);

		Evidencia evidencia = crearEvidencia(accidente, documentoAtril,
				Constantes.TIPO_EVIDENCIA_SF_FICHA_SUCESOS_FERROVIARIOS);

		accidente.setEvidenciaTipo60(evidencia);

		accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_ESPERANDO_CIAF));
		accidenteDAO.save(accidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_ESPERANDO_CIAF);

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	public void firmaAccidenteFichaEstructura(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		DocumentoAtril documentoAtril = new DocumentoAtril();

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaFichaEstructura(personalDAO.getOne(generarAccidenteJSP.getIdFirmaFichaEstructura()));
		accidente.setFechahoraFirmaFichaEstructura(new Date());

		accidente.setInvestigadorCiafNombre(generarAccidenteJSP.getNombreResponsableCIAF());
		accidente.setInvestigadorCiafTelefono(generarAccidenteJSP.getTelefonoResponsableCIAF());
		accidente.setInvestigadorCiafEmail(generarAccidenteJSP.getCorreoResponsableCIAF());
		accidente.setResponsableSeguridad(
				personalDAO.getOne(Integer.parseInt(generarAccidenteJSP.getIdResponsableSeguridad())));

		try {
			documentoAtril.setFile(utilesPDFService.generarDocumentoFicEstInvRecDatCooCasAccIncFerr(accidente));
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}
		documentoAtril.setType(Constantes.TYPE_PDF);

		Evidencia evidencia = crearEvidencia(accidente, documentoAtril, Constantes.TIPO_EVIDENCIA_SF_ENVIO_CIAF);

		accidente.setEvidenciaTipo50(evidencia);

		accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_ENVIADA_ESTRUCTURA));

		accidenteDAO.save(accidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_ENVIADA_ESTRUCTURA);
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	public void firmaAccidenteFichaNotificacionResponsableSeguridad(GenerarAccidenteJSP generarAccidenteJSP,
			Model model) throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaFichaDelegacionResponsable(
				personalDAO.getOne(generarAccidenteJSP.getIdFirmaFichaNotificacionResponsableSeguridad()));
		accidente.setFechahoraFirmaFichaDelegacionResponsable(new Date());

		accidenteDAO.save(accidente);
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (accidente.getFirmaFichaDelegacionDelegado() != null) {
			finalizarFichaNotificacion(generarAccidenteJSP, model, mensajeInfo);
			generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_RECOGIENDO_DATOS);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	public void firmaAccidenteFichaNotificacionDelegadoSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaFichaDelegacionDelegado(
				personalDAO.getOne(generarAccidenteJSP.getIdFirmaFichaNotificacionDelegadoSeguridad()));
		accidente.setFechahoraFirmaFichaDelegacionDelegado(new Date());

		accidenteDAO.save(accidente);
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		if (accidente.getFirmaFichaDelegacionResponsable() != null) {
			finalizarFichaNotificacion(generarAccidenteJSP, model, mensajeInfo);
			generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_RECOGIENDO_DATOS);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	private void finalizarFichaNotificacion(GenerarAccidenteJSP generarAccidenteJSP, Model model,
			List<String> mensajeInfo) throws EseException {
		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		DocumentoAtril documentoAtril = new DocumentoAtril();
		try {
			documentoAtril.setFile(utilesPDFService.generarDocumentoFicNotDelIntAccIncFer(accidente));
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}
		documentoAtril.setType(Constantes.TYPE_PDF);

		Evidencia evidencia = crearEvidencia(accidente, documentoAtril,
				Constantes.TIPO_EVIDENCIA_SF_DESIGNACION_DELEGADO_SEGURIDAD);

		accidente.setEvidenciaTipo51(evidencia);

		accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_RECOGIENDO_DATOS));
		accidenteDAO.save(accidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);

	}

	public void firmaAccidenteInformeFinalResponsableSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaInformeFinalResponsable(
				personalDAO.getOne(generarAccidenteJSP.getIdFirmaInformeFinalResponsableSeguridad()));
		accidente.setFechahoraFirmaInformeFinalResponsable(new Date());

		accidenteDAO.save(accidente);
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		if (accidente.getFirmaInformeFinalDelegado() != null) {
			finalizarInformeFinal(generarAccidenteJSP, model, mensajeInfo);
			if (accidente.getIntervieneCiaf()) {
				generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_RECIBIENDO_CIAF);
			} else {
				generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_APLICANDO_MEDIDAS);
			}
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	public void firmaAccidenteInformeFinalDelegadoSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaInformeFinalDelegado(
				personalDAO.getOne(generarAccidenteJSP.getIdFirmaInformeFinalDelegadoSeguridad()));
		accidente.setFechahoraFirmaInformeFinalDelegado(new Date());

		accidenteDAO.save(accidente);
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		if (accidente.getFirmaInformeFinalResponsable() != null) {
			finalizarInformeFinal(generarAccidenteJSP, model, mensajeInfo);
			if (accidente.getIntervieneCiaf()) {
				generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_RECIBIENDO_CIAF);
			} else {
				generarAccidenteJSP.setIdEstadoAccidente(Constantes.ESTADO_INVESTIGACION_APLICANDO_MEDIDAS);
			}
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	private void finalizarInformeFinal(GenerarAccidenteJSP generarAccidenteJSP, Model model, List<String> mensajeInfo)
			throws EseException {
		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		DocumentoAtril documentoAtril = new DocumentoAtril();

		try {
			documentoAtril.setFile(utilesPDFService.generarDocumentoInfInvAccIncFerr(accidente));
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}
		documentoAtril.setType(Constantes.TYPE_PDF);

		Evidencia evidencia = crearEvidencia(accidente, documentoAtril,
				Constantes.TIPO_EVIDENCIA_SF_INFORME_INVESTIGACION);

		accidente.setEvidenciaTipo61(evidencia);

		if (accidente.getIntervieneCiaf()) {
			accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_RECIBIENDO_CIAF));
		} else {
			accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_APLICANDO_MEDIDAS));
		}

		accidenteDAO.save(accidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);

	}

	public void anadirDocumentoInformeProvisionalCIAF(GenerarAccidenteJSP generarAccidenteJSP) throws EseException {
		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());

		DocumentoAtril documentoAtril = new DocumentoAtril();
		try {
			documentoAtril.setFile(generarAccidenteJSP.getEvidencia().getBytes());
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}
		documentoAtril.setType(generarAccidenteJSP.getEvidencia().getContentType());

		Evidencia evidencia = crearEvidencia(accidente, documentoAtril, Constantes.TIPO_EVIDENCIA_SF_INFORME_CIAF);

		accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_ENVIANDO_CIAF));
		accidente.setEvidenciaTipo63(evidencia);
		accidenteDAO.save(accidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		generarAccidenteJSP.setIdEstadoAccidente(accidente.getEstadoAccidente().getIdEstadoAccidente());
	}

	public void firmaAccidenteFichaComentarios(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		DocumentoAtril documentoAtril = new DocumentoAtril();

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaFichaComentariosCiaf(personalDAO.getOne(generarAccidenteJSP.getIdFirmaFichaComentarios()));
		accidente.setFechahoraFirmaFichaComentariosCiaf(new Date());

		try {
			documentoAtril.setFile(utilesPDFService.generarDocumentoFicComSugInfProAccIncFerrCIAF(accidente));
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}
		documentoAtril.setType(Constantes.TYPE_PDF);

		Evidencia evidencia = crearEvidencia(accidente, documentoAtril,
				Constantes.TIPO_EVIDENCIA_SF_FICHA_COMENTARIOS_INFORME_CIAF);

		accidente.setEvidenciaTipo64(evidencia);

		accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_APLICANDO_MEDIDAS));

		accidenteDAO.save(accidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		generarAccidenteJSP.setIdEstadoAccidente(accidente.getEstadoAccidente().getIdEstadoAccidente());
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	public void firmaAccidenteFichaMedidas(GenerarAccidenteJSP generarAccidenteJSP, Model model) throws EseException {

		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		DocumentoAtril documentoAtril = new DocumentoAtril();

		accidente = guardarAccidente(generarAccidenteJSP);

		accidente.setFirmaFichaMedidas(personalDAO.getOne(generarAccidenteJSP.getIdFirmaFichaMedidas()));
		accidente.setFechahoraFirmaFichaMedidas(new Date());

		try {
			documentoAtril.setFile(utilesPDFService.generarDocumentoFicMedAdop(accidente));
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}
		documentoAtril.setType(Constantes.TYPE_PDF);

		Evidencia evidencia = crearEvidencia(accidente, documentoAtril,
				Constantes.TIPO_EVIDENCIA_SF_FICHA_MEDIDAS_ADOPTADAS);

		accidente.setEvidenciaTipo65(evidencia);

		accidente.setEstadoAccidente(estadoAccidenteDAO.getOne(Constantes.ESTADO_INVESTIGACION_FINALIZADO));

		accidenteDAO.save(accidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		generarAccidenteJSP.setIdEstadoAccidente(accidente.getEstadoAccidente().getIdEstadoAccidente());
		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");
		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	private Evidencia crearEvidencia(Accidente investigacion, DocumentoAtril documentoAtril, Integer idTipoEvidencia)
			throws EseException {

		String md5 = Utiles.calculateHashMD5(documentoAtril.getFile());
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(idTipoEvidencia);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), documentoAtril.getFile(), md5,
				tipoEvidencia, Utiles.sysdate(), documentoAtril.getType());

		return evidencia;
	}

	private TipoAccidente obtenerTipoAccidente(GenerarAccidenteJSP generarAccidenteJSP) {

		Integer idTipoAccidente = Constantes.TIPO_ACCIDENTE_INCIDENTE;
		Integer sumaMuertos = Integer.parseInt(generarAccidenteJSP.getMuertesVictimaAjenaTren())
				+ Integer.parseInt(generarAccidenteJSP.getMuertesVictimaTren());
		Integer sumaHeridos = Integer.parseInt(generarAccidenteJSP.getHeridosVictimaAjenaTren())
				+ Integer.parseInt(generarAccidenteJSP.getHeridosVictimaTren());

		if (sumaHeridos == 0 && sumaMuertos == 0) {
			idTipoAccidente = Constantes.TIPO_ACCIDENTE_ACCIDENTE;
		}

		if ((sumaHeridos > 0 && sumaHeridos < 6) || sumaMuertos == 1) {
			idTipoAccidente = Constantes.TIPO_ACCIDENTE_ACCIDENTE_LEVE;
		}
		if (sumaHeridos > 5 || sumaMuertos > 1) {
			idTipoAccidente = Constantes.TIPO_ACCIDENTE_ACCIDENTE_GRAVE;
		}
		return tipoAccidenteDAO.getOne(idTipoAccidente);
	}

}
