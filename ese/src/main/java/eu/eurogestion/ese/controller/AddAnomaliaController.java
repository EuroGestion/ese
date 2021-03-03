package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.AnexoAnomalia;
import eu.eurogestion.ese.domain.Anomalia;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.EstadoAnomalia;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoAnomalia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AddAnomaliaJSP;
import eu.eurogestion.ese.pojo.CrearInformeAnomaliasJSP;
import eu.eurogestion.ese.repository.AnexoAnomaliaDAO;
import eu.eurogestion.ese.repository.AnomaliaDAO;
import eu.eurogestion.ese.repository.EstadoAnomaliaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoAnomaliaDAO;
import eu.eurogestion.ese.services.AddAnomaliaService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro, rmerino
 *
 */
@Slf4j
@Controller
public class AddAnomaliaController {

	/** Repositories & Services **/

	@Autowired
	public TipoAnomaliaDAO tipoAnomaliaDAO;

	@Autowired
	public EstadoAnomaliaDAO estadoAnomaliaDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public AnomaliaDAO anomaliaDAO;

	@Autowired
	public AnexoAnomaliaDAO anexoAnomaliaDAO;

	@Autowired
	public AddAnomaliaService addAnomaliaService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/** Functions **/

	@ModelAttribute("tiposAnomalia")
	public List<TipoAnomalia> listTipoAnomaliaAll() {
		List<TipoAnomalia> lista = new ArrayList<>();
		lista.addAll(tipoAnomaliaDAO.findAll());
		return lista;
	}

	@ModelAttribute("estadosAnomalia")
	public List<EstadoAnomalia> listEstadoAnomaliaAll() {
		List<EstadoAnomalia> lista = new ArrayList<>();
		lista.addAll(estadoAnomaliaDAO.findAll());
		return lista;
	}

	@ModelAttribute("responsables")
	public List<Personal> listResponsableAll() {
		List<Personal> lista = new ArrayList<>();
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de addAnomalia
	 * 
	 * @param model                   objeto model de la pantalla
	 * @param crearInformeAnomaliaJSP jsp de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/addAnomalia", method = RequestMethod.POST)
	public String addAnomalia(CrearInformeAnomaliasJSP crearInformeAnomaliaJSP, Model model) {

		AddAnomaliaJSP addAnomaliaJSP = rellenarJSP(crearInformeAnomaliaJSP);
		addAnomaliaJSP.setLectura(Boolean.FALSE);
		model.addAttribute("addAnomalia", addAnomaliaJSP);

		return "addAnomalia";
	}

	private AddAnomaliaJSP rellenarJSP(CrearInformeAnomaliasJSP crearInformeAnomaliaJSP) {

		AddAnomaliaJSP addAnomaliaJSP = new AddAnomaliaJSP();
		addAnomaliaJSP.setNumReferencia(crearInformeAnomaliaJSP.getNumeroInspeccion());
		addAnomaliaJSP.setNombreInspector(crearInformeAnomaliaJSP.getNombreInspector());
		addAnomaliaJSP.setFechaInspeccion(crearInformeAnomaliaJSP.getFechaInspeccion());
		addAnomaliaJSP.setIdInformeAnomalia(crearInformeAnomaliaJSP.getIdInformeAnomalias());

		addAnomaliaJSP.setCodigoInforme(crearInformeAnomaliaJSP.getCodigoInforme());
		addAnomaliaJSP.setDescripcion(crearInformeAnomaliaJSP.getDescripcionDatosInspeccion());
		addAnomaliaJSP.setObservaciones(crearInformeAnomaliaJSP.getObservacionesDatosInspeccion());
		addAnomaliaJSP.setMedidasCautelares(crearInformeAnomaliaJSP.getMedidasCautelares());
		addAnomaliaJSP.setLecturaInformeAnomalia(crearInformeAnomaliaJSP.isLectura());

		addAnomaliaJSP.setIdTipoInspeccion(crearInformeAnomaliaJSP.getIdTipoInspeccion());

		addAnomaliaJSP.setPaginaVuelta(crearInformeAnomaliaJSP.getPaginaVuelta());
		addAnomaliaJSP.setIdPersonal(crearInformeAnomaliaJSP.getIdPersonal());

		return addAnomaliaJSP;
	}

	/**
	 * Guarda una anomalia en la BBDD.
	 * 
	 * @param addAnomaliaJSP objeto JSP con los datos de la pantalla
	 * @param model          objeto model de la pantalla
	 * @param session        sesion de la pantalla
	 * @return string con el nombre de la pantalla
	 */
	@RequestMapping(value = "/aceptarAddAnomalia", method = RequestMethod.POST)
	public String aceptarAddAnomalia(AddAnomaliaJSP addAnomaliaJSP, Model model, HttpSession session) {

		Anomalia anomalia;
		try {
			anomalia = addAnomaliaService.guardarAnomalia(addAnomaliaJSP);
		} catch (EseException e) {
			model.addAttribute("addAnomalia", addAnomaliaJSP);
			log.error(e.getMessage());
			return "addAnomalia";
		}

		addAnomaliaJSP.setIdAnomalia(anomalia.getIdAnomalia().toString());

		if (Constantes.ESTADO_ANOMALIA_CERRADA == anomalia.getEstadoAnomalia().getIdEstadoAnomalia()) {
			addAnomaliaJSP.setLectura(Boolean.TRUE);
		}

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(addAnomaliaJSP.getPage())) {
			page = Integer.parseInt(addAnomaliaJSP.getPage()) - 1;
		}
		model.addAttribute("addAnomalia", addAnomaliaJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		cargarListasAnomaliasInformeAnomalia(model, addAnomaliaJSP.getIdAnomalia(), page, size);

		return "addAnomalia";
	}

	/**
	 * Añade una evidencia al informe de la anomalia
	 * 
	 * @param addAnomaliaJSP objeto JSP con los datos de la pantalla
	 * @param model          objeto model de la pantalla
	 * @param session        sesion de la pantalla
	 * @return string con el nombre de la pantalla
	 */
	@RequestMapping(value = "/anadirEvidenciaAnomaliaInformeAnomalia", method = RequestMethod.POST)
	public String anadirEvidenciaAnomaliaInformeAnomalia(AddAnomaliaJSP addAnomaliaJSP, Model model,
			HttpSession session) {

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(addAnomaliaJSP.getPage())) {
			page = Integer.parseInt(addAnomaliaJSP.getPage()) - 1;
		}

		List<String> listTextoErrorEvidencia = new ArrayList<>();
		boolean errorEvidencia = false;
		if (StringUtils.isBlank(addAnomaliaJSP.getEvidencia().getOriginalFilename())) {
			listTextoErrorEvidencia.add("El documento de la evidencia es obligatorio");
			errorEvidencia = true;
		}

		if (StringUtils.isBlank(addAnomaliaJSP.getDescripcionEvidencia())) {

			listTextoErrorEvidencia.add("La descripción de la evidencia es obligatorio");
			errorEvidencia = true;
		}

		if (errorEvidencia) {
			model.addAttribute("erroresEvidencia", listTextoErrorEvidencia);
			model.addAttribute("addAnomalia", addAnomaliaJSP);
			cargarListasAnomaliasInformeAnomalia(model, addAnomaliaJSP.getIdAnomalia(), page, size);
			return "addAnomalia";
		}

		try {
			addAnomaliaService.creacionAnexo(addAnomaliaJSP, session);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("addAnomalia", addAnomaliaJSP);
			cargarListasAnomaliasInformeAnomalia(model, addAnomaliaJSP.getIdAnomalia(), page, size);
			return "addAnomalia";
		}

		addAnomaliaJSP.setDescripcionEvidencia(null);

		model.addAttribute("addAnomalia", addAnomaliaJSP);
		model.addAttribute("info", " Se ha añadido correctamente");
		cargarListasAnomaliasInformeAnomalia(model, addAnomaliaJSP.getIdAnomalia(), page, size);
		return "addAnomalia";

	}

	@RequestMapping(value = "/paginacionTablaAddAnomalia", method = RequestMethod.POST)
	public String paginacionTablaAddAnomalia(AddAnomaliaJSP addAnomaliaJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(addAnomaliaJSP.getPage())) {
			addAnomaliaJSP.setPage(addAnomaliaJSP.getPage().substring(addAnomaliaJSP.getPage().indexOf(",") + 1,
					addAnomaliaJSP.getPage().length()));
			page = Integer.parseInt(addAnomaliaJSP.getPage()) - 1;
		}

		model.addAttribute("addAnomalia", addAnomaliaJSP);
		cargarListasAnomaliasInformeAnomalia(model, addAnomaliaJSP.getIdAnomalia(), page, size);
		return "addAnomalia";
	}

	private void cargarListasAnomaliasInformeAnomalia(Model model, String idAnomalia, int page, int size) {
		model.addAttribute("listAnexosAnomalias",
				anexoAnomaliaDAO.findByAnomalia(Integer.parseInt(idAnomalia), PageRequest.of(page, size)));
	}

	/**
	 * Descarga el pdf correspondiente a la evidencia de una anomalia
	 * 
	 * @param addAnomaliaJSP objeto JSP con los datos de la pantalla
	 * @param model          objeto model de la pantalla
	 * @param response
	 */
	@RequestMapping(value = "/verEvidenciaAnomalia", method = RequestMethod.POST)
	public void verEvidenciaAnomalia(AddAnomaliaJSP addAnomaliaJSP, Model model, HttpServletResponse response) {
		try {
			switch (addAnomaliaJSP.getIdTipoInspeccion()) {
			case Constantes.TIPO_INSPECCION_ISO:
				descargarPDFAnexoISO(addAnomaliaJSP, response);
				break;

			case Constantes.TIPO_INSPECCION_ISEET:
				descargarPDFAnexoISEET(addAnomaliaJSP, response);
				break;

			case Constantes.TIPO_INSPECCION_ISMP:
				descargarPDFAnexoISMP(addAnomaliaJSP, response);
				break;

			case Constantes.TIPO_INSPECCION_ISCC:
				descargarPDFAnexoISCC(addAnomaliaJSP, response);
				break;

			default:
				break;
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void descargarPDFAnexoISO(AddAnomaliaJSP addAnomaliaJSP, HttpServletResponse response) throws IOException {

		AnexoAnomalia anexo = anexoAnomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnexo()));
		Documento documento = anexo.getEvidencia().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);

	}

	private void descargarPDFAnexoISEET(AddAnomaliaJSP addAnomaliaJSP, HttpServletResponse response)
			throws IOException {

		AnexoAnomalia anexo = anexoAnomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnexo()));
		Documento documento = anexo.getEvidencia().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);
	}

	private void descargarPDFAnexoISMP(AddAnomaliaJSP addAnomaliaJSP, HttpServletResponse response) throws IOException {

		AnexoAnomalia anexo = anexoAnomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnexo()));
		Documento documento = anexo.getEvidencia().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);

	}

	private void descargarPDFAnexoISCC(AddAnomaliaJSP addAnomaliaJSP, HttpServletResponse response) throws IOException {

		AnexoAnomalia anexo = anexoAnomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnexo()));
		Documento documento = anexo.getEvidencia().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);

	}

	/**
	 * Descarga el pdf correspondiente al informe de una anomalia
	 * 
	 * @param addAnomaliaJSP objeto JSP con los datos de la pantalla
	 * @param model          objeto model de la pantalla
	 * @param response
	 */
	@RequestMapping(value = "/verAnomaliaInformeAnomalia", method = RequestMethod.POST)
	public String verAnomaliaInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomaliaJSP, Model model) {
		AddAnomaliaJSP addAnomaliaJSP = rellenarJSP(crearInformeAnomaliaJSP);
		addAnomaliaJSP.setIdAnomalia(crearInformeAnomaliaJSP.getIdAnomalia());

		Anomalia anomaliaBBDD = anomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnomalia()));

		addAnomaliaJSP.setIdTipoAnomalia(anomaliaBBDD.getTipoAnomalia().getIdTipoAnomalia().toString());
		addAnomaliaJSP.setIdEstado(anomaliaBBDD.getEstadoAnomalia().getIdEstadoAnomalia().toString());
		addAnomaliaJSP.setDescripcionAnomalia(anomaliaBBDD.getDescripcionSituacion());
		addAnomaliaJSP.setMedidasAdoptadas(anomaliaBBDD.getMedidasAdoptadas());
		addAnomaliaJSP.setDatosTecnicos(anomaliaBBDD.getDatosTecnicos());
		addAnomaliaJSP.setLimitaciones(anomaliaBBDD.getLimitacionesExplotacion());
		addAnomaliaJSP.setControlSeguimiento(anomaliaBBDD.getControlSeguimiento());
		addAnomaliaJSP.setIdResponsable(anomaliaBBDD.getResponsableResolucion().getIdPersonal().toString());
		addAnomaliaJSP.setLectura(Boolean.TRUE);

		addAnomaliaJSP.setIdPersonal(crearInformeAnomaliaJSP.getIdPersonal());
		addAnomaliaJSP.setPaginaVuelta(crearInformeAnomaliaJSP.getPaginaVuelta());

		cargarListasAnomaliasInformeAnomalia(model, addAnomaliaJSP.getIdAnomalia(), 0, 5);

		model.addAttribute("addAnomalia", addAnomaliaJSP);

		return "addAnomalia";
	}

	/**
	 * Descarga el pdf correspondiente al informe de una anomalia
	 * 
	 * @param addAnomaliaJSP objeto JSP con los datos de la pantalla
	 * @param model          objeto model de la pantalla
	 * @param response
	 */
	@RequestMapping(value = "/modificarAnomaliaInformeAnomalia", method = RequestMethod.POST)
	public String modificarAnomaliaInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomaliaJSP, Model model) {
		AddAnomaliaJSP addAnomaliaJSP = rellenarJSP(crearInformeAnomaliaJSP);
		addAnomaliaJSP.setIdAnomalia(crearInformeAnomaliaJSP.getIdAnomalia());

		Anomalia anomaliaBBDD = anomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnomalia()));

		addAnomaliaJSP.setIdTipoAnomalia(anomaliaBBDD.getTipoAnomalia().getIdTipoAnomalia().toString());
		addAnomaliaJSP.setIdEstado(anomaliaBBDD.getEstadoAnomalia().getIdEstadoAnomalia().toString());
		addAnomaliaJSP.setDescripcionAnomalia(anomaliaBBDD.getDescripcionSituacion());
		addAnomaliaJSP.setMedidasAdoptadas(anomaliaBBDD.getMedidasAdoptadas());
		addAnomaliaJSP.setDatosTecnicos(anomaliaBBDD.getDatosTecnicos());
		addAnomaliaJSP.setLimitaciones(anomaliaBBDD.getLimitacionesExplotacion());
		addAnomaliaJSP.setControlSeguimiento(anomaliaBBDD.getControlSeguimiento());
		addAnomaliaJSP.setIdResponsable(anomaliaBBDD.getResponsableResolucion().getIdPersonal().toString());
		addAnomaliaJSP.setLectura(Boolean.FALSE);

		addAnomaliaJSP.setIdPersonal(crearInformeAnomaliaJSP.getIdPersonal());
		addAnomaliaJSP.setPaginaVuelta(crearInformeAnomaliaJSP.getPaginaVuelta());

		cargarListasAnomaliasInformeAnomalia(model, addAnomaliaJSP.getIdAnomalia(), 0, 5);

		model.addAttribute("addAnomalia", addAnomaliaJSP);

		return "addAnomalia";
	}

}