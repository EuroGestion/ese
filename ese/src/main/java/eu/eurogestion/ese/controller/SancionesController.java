package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Revocacion;
import eu.eurogestion.ese.domain.Suspension;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.SancionesJSP;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevocacionDAO;
import eu.eurogestion.ese.repository.SuspensionDAO;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class SancionesController {

	/**
	 * Repositorio de la clase de dominio Titulo
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Repositorio de la clase de dominio Suspension
	 */
	@Autowired
	public SuspensionDAO suspensionDAO;

	/**
	 * Repositorio de la clase de dominio Revocacion
	 */
	@Autowired
	public RevocacionDAO revocacionDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verSanciones", method = RequestMethod.POST)
	public String verDetalleTitulo(DetallePersonalJSP detallePersonalJSP, Model model) {

		SancionesJSP sancionesJSP = new SancionesJSP();
		sancionesJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());
		model.addAttribute("sanciones", sancionesJSP);
		cargarLista(sancionesJSP, model, 0, 0, 5);
		return "sanciones";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSuspensionTitulo", method = RequestMethod.POST)
	public void verEvidenciaSuspensionTitulo(SancionesJSP sancionesJSP, Model model, HttpServletResponse response) {
		try {
			Suspension suspension = suspensionDAO.getOne(Integer.parseInt(sancionesJSP.getIdSuspension()));
			Documento documento = suspension.getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaRevocacionTitulo", method = RequestMethod.POST)
	public void descargarEvidenciaSuspensionTitulo(SancionesJSP sancionesJSP, Model model,
			HttpServletResponse response) {
		try {
			Revocacion revocacion = revocacionDAO.getOne(Integer.parseInt(sancionesJSP.getIdRevocacion()));
			Documento documento = revocacion.getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/paginacionTablaSancionesSuspension", method = RequestMethod.POST)
	public String paginacionTablaSancionesSuspension(SancionesJSP sancionesJSP, Model model, HttpSession session) {
		int pageSuspensiones = 0; // default page number is 0 (yes it is weird)
		int pageRevocaciones = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(sancionesJSP.getPageSuspensiones())) {
			sancionesJSP.setPageSuspensiones(sancionesJSP.getPageSuspensiones().substring(
					sancionesJSP.getPageSuspensiones().indexOf(",") + 1, sancionesJSP.getPageSuspensiones().length()));
			pageSuspensiones = Integer.parseInt(sancionesJSP.getPageSuspensiones()) - 1;
		}

		if (StringUtils.isNotBlank(sancionesJSP.getPageRevocaciones())) {

			pageRevocaciones = Integer.parseInt(sancionesJSP.getPageRevocaciones()) - 1;
		}

		model.addAttribute("sanciones", sancionesJSP);
		cargarLista(sancionesJSP, model, pageSuspensiones, pageRevocaciones, size);
		return "sanciones";
	}

	@RequestMapping(value = "/paginacionTablaSancionesRevocaciones", method = RequestMethod.POST)
	public String paginacionTablaSancionesRevocaciones(SancionesJSP sancionesJSP, Model model, HttpSession session) {
		int pageSuspensiones = 0; // default page number is 0 (yes it is weird)
		int pageRevocaciones = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(sancionesJSP.getPageSuspensiones())) {

			pageSuspensiones = Integer.parseInt(sancionesJSP.getPageSuspensiones()) - 1;
		}

		if (StringUtils.isNotBlank(sancionesJSP.getPageRevocaciones())) {
			sancionesJSP.setPageRevocaciones(sancionesJSP.getPageRevocaciones().substring(
					sancionesJSP.getPageRevocaciones().indexOf(",") + 1, sancionesJSP.getPageRevocaciones().length()));
			pageRevocaciones = Integer.parseInt(sancionesJSP.getPageRevocaciones()) - 1;
		}

		model.addAttribute("sanciones", sancionesJSP);
		cargarLista(sancionesJSP, model, pageSuspensiones, pageRevocaciones, size);
		return "sanciones";
	}

	private void cargarLista(SancionesJSP sancionesJSP, Model model, int pageSuspensiones, int pageRevocaciones,
			int size) {

		model.addAttribute("suspensiones",
				suspensionDAO.findAllByPersonal(sancionesJSP.getIdPersonal(), PageRequest.of(pageSuspensiones, size)));
		model.addAttribute("revocaciones",
				revocacionDAO.findAllByPersonal(sancionesJSP.getIdPersonal(), PageRequest.of(pageRevocaciones, size)));

	}

}