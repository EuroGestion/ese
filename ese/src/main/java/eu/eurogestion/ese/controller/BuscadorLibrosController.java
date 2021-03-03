package eu.eurogestion.ese.controller;

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

import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.BuscadorLibrosJSP;
import eu.eurogestion.ese.repository.LibroDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorLibrosController {

	/**
	 * Repositorio de la clase de dominio ModeloMaterial
	 */
	@Autowired
	public LibroDAO libroDAO;

	/**
	 * Metodo que devuelve una lista de cenntros de formacion para un select
	 * 
	 * @return lista de objetos Compania
	 */

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorLibros", method = RequestMethod.GET)
	public String buscadorModificarFormacion(Model model) {

		model.addAttribute("buscadorLibros", new BuscadorLibrosJSP());
		cargarLista(new BuscadorLibrosJSP(), model, 0, 10);
		return "buscadorLibros";
	}

	@RequestMapping(value = "/buscadorLibrosCrearLibro", method = RequestMethod.GET)
	public String buscadorLibrosCrearLibro(Model model) {

		model.addAttribute("buscadorLibros", new BuscadorLibrosJSP());
		cargarLista(new BuscadorLibrosJSP(), model, 0, 10);
		model.addAttribute("info", " Se ha creado correctamente");
		return "buscadorLibros";
	}

	@RequestMapping(value = "/volverbuscadorLibros", method = RequestMethod.POST)
	public String volverbuscadorLibros(Model model) {

		model.addAttribute("buscadorLibros", new BuscadorLibrosJSP());
		cargarLista(new BuscadorLibrosJSP(), model, 0, 10);
		return "buscadorLibros";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorLibrosJSP objeto con los campos de filtro de la pantalla
	 * @param model             objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorLibros", method = RequestMethod.POST)
	public String filtrarBuscadorModificarFormacion(BuscadorLibrosJSP buscadorLibrosJSP, Model model) {

		cargarLista(buscadorLibrosJSP, model, 0, 10);
		model.addAttribute("buscadorLibros", buscadorLibrosJSP);
		return "buscadorLibros";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorLibrosJSP objeto con los campos de filtro de la pantalla
	 * @param model             objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorLibros", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorModificarFormacion(BuscadorLibrosJSP buscadorLibrosJSP, Model model) {
		model.addAttribute("buscadorLibros", new BuscadorLibrosJSP());
		cargarLista(new BuscadorLibrosJSP(), model, 0, 10);
		return "buscadorLibros";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorLibros", method = RequestMethod.POST)
	public String paginacionTablaBuscadorLibros(BuscadorLibrosJSP buscadorLibrosJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorLibrosJSP.getPage())) {
			buscadorLibrosJSP.setPage(buscadorLibrosJSP.getPage().substring(
					buscadorLibrosJSP.getPage().indexOf(",") + 1, buscadorLibrosJSP.getPage().length()));
			page = Integer.parseInt(buscadorLibrosJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorLibros", buscadorLibrosJSP);
		cargarLista(buscadorLibrosJSP, model, page, size);
		return "buscadorLibros";
	}
	
	@RequestMapping(value = "/ordenTablaBuscadorLibros", method = RequestMethod.POST)
	public String ordenTablaBuscadorLibros(BuscadorLibrosJSP buscadorLibrosJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorLibrosJSP.getPage())) {
			page = Integer.parseInt(buscadorLibrosJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorLibros", buscadorLibrosJSP);
		cargarLista(buscadorLibrosJSP, model, page, size);
		return "buscadorLibros";
	}

	private void cargarLista(BuscadorLibrosJSP buscadorLibrosJSP, Model model, int page, int size) {

		Order order = Order.desc("idLibro");
		if (StringUtils.isNotBlank(buscadorLibrosJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorLibrosJSP.getDireccion())) {

			if ("asc".equals(buscadorLibrosJSP.getDireccion())) {
				order = Order.asc(buscadorLibrosJSP.getOrder());
			} else {
				order = Order.desc(buscadorLibrosJSP.getOrder());
			}

		}
		
		model.addAttribute("libros", libroDAO.findLibrosByFilters(buscadorLibrosJSP.getTitulo(),
				buscadorLibrosJSP.getFecha(), PageRequest.of(page, size, Sort.by(order))));
	}
}