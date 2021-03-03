package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.LibroPersonal;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.DetalleLibrosPersonalJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.LibroPersonalDAO;
import eu.eurogestion.ese.services.DetalleLibrosPersonalService;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rmerino
 *
 */
@Slf4j
@Controller
public class DetalleLibrosPersonalController {

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public DetalleLibrosPersonalService detalleLibrosPersonalService;

	@Autowired
	public LibroPersonalDAO libroPersonalDAO;

	@RequestMapping(value = "/detalleLibrosPersonal", method = RequestMethod.GET)
	public String detalleLibrosPersonal(Model model, HttpSession session) {

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) session.getAttribute("usuario");

		DetalleLibrosPersonalJSP detalleLibrosPersonalJSP = new DetalleLibrosPersonalJSP();
		detalleLibrosPersonalJSP.setIdPersonal(usuarioRegistrado.getIdPersonal());

		model.addAttribute("detalleLibrosPersonal", detalleLibrosPersonalJSP);
		cargarLista(detalleLibrosPersonalJSP, model, 0, 10);
		return "detalleLibrosPersonal";
	}

	@RequestMapping(value = "/verFirmaDocumentacionTareaPendiente", method = RequestMethod.POST)
	public String verFirmaDocumentacionTareaPendiente(Model model, HttpSession session) {

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) session.getAttribute("usuario");

		DetalleLibrosPersonalJSP detalleLibrosPersonalJSP = new DetalleLibrosPersonalJSP();
		detalleLibrosPersonalJSP.setIdPersonal(usuarioRegistrado.getIdPersonal());

		model.addAttribute("detalleLibrosPersonal", detalleLibrosPersonalJSP);
		cargarLista(detalleLibrosPersonalJSP, model, 0, 10);
		return "detalleLibrosPersonal";
	}

	@RequestMapping(value = "/filtrarDetalleLibrosPersonal", method = RequestMethod.POST)
	public String filtrarDetalleLibrosPersonal(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP, Model model,
			HttpSession session) {

		model.addAttribute("detalleLibrosPersonal", detalleLibrosPersonalJSP);
		cargarLista(detalleLibrosPersonalJSP, model, 0, 10);
		return "detalleLibrosPersonal";
	}

	@RequestMapping(value = "/borrarFiltrosDetalleLibrosPersonal", method = RequestMethod.POST)
	public String borrarFiltrosDetalleLibrosPersonal(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP, Model model,
			HttpSession session) {

		detalleLibrosPersonalJSP.setTitulo(null);
		detalleLibrosPersonalJSP.setIdLibroPersonal(null);
		detalleLibrosPersonalJSP.setFecha(null);

		model.addAttribute("detalleLibrosPersonal", detalleLibrosPersonalJSP);
		cargarLista(detalleLibrosPersonalJSP, model, 0, 10);
		return "detalleLibrosPersonal";
	}

	@RequestMapping(value = "/verDetalleLibrosPersonal", method = RequestMethod.POST)
	public void verDetalleLibrosPersonal(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP, Model model,
			HttpServletResponse response) {

		try {
			LibroPersonal libroPersonal = libroPersonalDAO
					.getOne(Integer.parseInt(detalleLibrosPersonalJSP.getIdLibroPersonal()));
			Documento documento = libroPersonal.getLibro().getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/paginacionTablaLibrosPersonal", method = RequestMethod.POST)
	public String paginacionTablaLibrosPersonal(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(detalleLibrosPersonalJSP.getPage())) {
			detalleLibrosPersonalJSP.setPage(detalleLibrosPersonalJSP.getPage().substring(
					detalleLibrosPersonalJSP.getPage().indexOf(",") + 1, detalleLibrosPersonalJSP.getPage().length()));
			page = Integer.parseInt(detalleLibrosPersonalJSP.getPage()) - 1;
		}

		model.addAttribute("detalleLibrosPersonal", detalleLibrosPersonalJSP);
		cargarLista(detalleLibrosPersonalJSP, model, page, size);
		return "detalleLibrosPersonal";
	}

	@RequestMapping(value = "/firmarDetalleLibrosPersonal", method = RequestMethod.POST)
	public String firmarDetalleLibrosPersonal(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP, Model model,
			HttpSession session) {

		try {
			detalleLibrosPersonalService.firmarLibro(detalleLibrosPersonalJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
		}
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(detalleLibrosPersonalJSP.getPage())) {
			page = Integer.parseInt(detalleLibrosPersonalJSP.getPage()) - 1;
		}

		model.addAttribute("detalleLibrosPersonal", detalleLibrosPersonalJSP);
		cargarLista(detalleLibrosPersonalJSP, model, page, size);
		model.addAttribute("info", "Se ha firmado correctamente");
		return "detalleLibrosPersonal";
	}
	
	@RequestMapping(value = "/ordenTablaDetalleLibrosPersonal", method = RequestMethod.POST)
	public String ordenTablaDetalleLibrosPersonal(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(detalleLibrosPersonalJSP.getPage())) {
			page = Integer.parseInt(detalleLibrosPersonalJSP.getPage()) - 1;
		}

		model.addAttribute("detalleLibrosPersonal", detalleLibrosPersonalJSP);
		cargarLista(detalleLibrosPersonalJSP, model, page, size);
		return "detalleLibrosPersonal";
	}

	private void cargarLista(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP, Model model, int page, int size) {

		Order order = Order.desc("idLibroPersonal");
		if (StringUtils.isNotBlank(detalleLibrosPersonalJSP.getOrder())
				&& StringUtils.isNotBlank(detalleLibrosPersonalJSP.getDireccion())) {

			if ("asc".equals(detalleLibrosPersonalJSP.getDireccion())) {
				order = Order.asc(detalleLibrosPersonalJSP.getOrder());
			} else {
				order = Order.desc(detalleLibrosPersonalJSP.getOrder());
			}

		}

		model.addAttribute("librosPersonal",
				libroPersonalDAO.findByFilters(detalleLibrosPersonalJSP.getIdPersonal(),
						detalleLibrosPersonalJSP.getTitulo(), detalleLibrosPersonalJSP.getFecha(),
						PageRequest.of(page, size, Sort.by(order))));
	}

}