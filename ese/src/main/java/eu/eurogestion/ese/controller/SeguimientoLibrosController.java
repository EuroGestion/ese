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
import eu.eurogestion.ese.domain.Libro;
import eu.eurogestion.ese.domain.LibroPersonal;
import eu.eurogestion.ese.pojo.BuscadorLibrosJSP;
import eu.eurogestion.ese.pojo.SeguimientoLibrosJSP;
import eu.eurogestion.ese.repository.LibroDAO;
import eu.eurogestion.ese.repository.LibroPersonalDAO;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class SeguimientoLibrosController {

	/**
	 * Repositorio de la clase de dominio ModeloMaterial
	 */
	@Autowired
	public LibroDAO libroDAO;

	@Autowired
	public LibroPersonalDAO libroPersonalDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorLibrosJSP objeto con los campos de filtro de la pantalla
	 * @param model             objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verSeguimientoLibro", method = RequestMethod.POST)
	public String verSeguimientoLibro(BuscadorLibrosJSP buscadorLibrosJSP, Model model) {

		SeguimientoLibrosJSP seguimientoLibrosJSP = new SeguimientoLibrosJSP();
		Libro libro = libroDAO.getOne(Integer.parseInt(buscadorLibrosJSP.getIdLibro()));
		seguimientoLibrosJSP.setTitulo(libro.getTitulo());
		seguimientoLibrosJSP.setIdLibro(libro.getIdLibro());
		cargarLista(seguimientoLibrosJSP, model, 0, 10);
		model.addAttribute("seguimientoLibros", seguimientoLibrosJSP);
		return "seguimientoLibros";
	}

	@RequestMapping(value = "/paginacionTablaSeguimientoLibroPersonales", method = RequestMethod.POST)
	public String paginacionTablaSeguimientoLibroPersonales(SeguimientoLibrosJSP seguimientoLibrosJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(seguimientoLibrosJSP.getPage())) {
			page = Integer.parseInt(seguimientoLibrosJSP.getPage()) - 1;
		}

		model.addAttribute("seguimientoLibros", seguimientoLibrosJSP);
		cargarLista(seguimientoLibrosJSP, model, page, size);
		return "seguimientoLibros";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorLibrosJSP objeto con los campos de filtro de la pantalla
	 * @param model             objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verAcuseLibroPersonal", method = RequestMethod.POST)
	public void verAcuseLibroPersonal(SeguimientoLibrosJSP seguimientoLibrosJSP, Model model,
			HttpServletResponse response) {
		try {
			LibroPersonal libroPersonal = libroPersonalDAO
					.getOne(Integer.parseInt(seguimientoLibrosJSP.getIdLibroPersonal()));
			Documento documento = libroPersonal.getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	private void cargarLista(SeguimientoLibrosJSP seguimientoLibrosJSP, Model model, int page, int size) {
		model.addAttribute("libroPersonales", libroPersonalDAO
				.findLibrosPersonalByFilters(seguimientoLibrosJSP.getIdLibro(), PageRequest.of(page, size)));
	}

}