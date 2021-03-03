package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.PuntoInfraestructura;
import eu.eurogestion.ese.pojo.BuscadorTramoJSP;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TramoDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorTramoController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TramoDAO tramoDAO;

	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	@ModelAttribute("puntosInfraestructura")
	public List<PuntoInfraestructura> listPuntoInfraestructuraAll() {
		List<PuntoInfraestructura> lista = new ArrayList<>();
		PuntoInfraestructura tramo = new PuntoInfraestructura();
		tramo.setNombre("Selecciona uno:");
		lista.add(tramo);
		lista.addAll(puntoInfraestructuraDAO.findAll(Sort.by(Order.asc("nombreLargo"))));
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorTramo", method = RequestMethod.GET)
	public String buscadorTramo(Model model) {
		model.addAttribute("buscadorTramo", new BuscadorTramoJSP());
		cargarListaValores(new BuscadorTramoJSP(), model, 0, 10);
		return "buscadorTramo";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorTramoJSP objeto con los campos de filtro de la pantalla
	 * @param model            objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorTramo", method = RequestMethod.POST)
	public String filtrarBuscadorTramo(BuscadorTramoJSP buscadorTramoJSP, Model model) {

		model.addAttribute("buscadorTramo", buscadorTramoJSP);
		cargarListaValores(buscadorTramoJSP, model, 0, 10);
		return "buscadorTramo";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorTramo", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorTramo(BuscadorTramoJSP buscadorTramoJSP, Model model) {
		model.addAttribute("buscadorTramo", new BuscadorTramoJSP());
		cargarListaValores(new BuscadorTramoJSP(), model, 0, 10);
		return "buscadorTramo";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorTramos", method = RequestMethod.POST)
	public String paginacionTablaBuscadorTramos(BuscadorTramoJSP buscadorTramoJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTramoJSP.getPage())) {
			buscadorTramoJSP.setPage(buscadorTramoJSP.getPage().substring(buscadorTramoJSP.getPage().indexOf(",") + 1,
					buscadorTramoJSP.getPage().length()));
			page = Integer.parseInt(buscadorTramoJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorTramo", buscadorTramoJSP);
		cargarListaValores(buscadorTramoJSP, model, page, size);
		return "buscadorTramo";
	}

	@RequestMapping(value = "/ordenTablaBuscadorTramos", method = RequestMethod.POST)
	public String ordenTablaBuscadorTrenes(BuscadorTramoJSP buscadorTramoJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTramoJSP.getPage())) {
			page = Integer.parseInt(buscadorTramoJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorTramo", buscadorTramoJSP);
		cargarListaValores(buscadorTramoJSP, model, page, size);
		return "buscadorTramo";
	}

	private void cargarListaValores(BuscadorTramoJSP buscadorTramoJSP, Model model, int page, int size) {

		Order order = Order.desc("idTramo");
		if (StringUtils.isNotBlank(buscadorTramoJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorTramoJSP.getDireccion())) {

			if ("asc".equals(buscadorTramoJSP.getDireccion())) {
				order = Order.asc(buscadorTramoJSP.getOrder());
			} else {
				order = Order.desc(buscadorTramoJSP.getOrder());
			}

		}

		model.addAttribute("tramos",
				tramoDAO.findAllByFilters(buscadorTramoJSP.getNombre(), buscadorTramoJSP.getIdPuntoOrigen(),
						buscadorTramoJSP.getIdPuntoDestino(), PageRequest.of(page, size, Sort.by(order))));

	}

}