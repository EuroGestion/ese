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

import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorComposicionJSP;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.ComposicionService;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorComposicionController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio ModeloMaterial
	 */
	@Autowired
	public ComposicionDAO composicionDAO;

	/**
	 * Repositorio de la clase de dominio ModeloMaterial
	 */
	@Autowired
	public ComposicionService composicionService;

	/**
	 * Metodo que devuelve una lista de cenntros de formacion para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("trenes")
	public List<Tren> listCentroFormacionAll() {
		List<Tren> lista = new ArrayList<>();
		Tren compania = new Tren();
		compania.setNumero("Selecciona uno:");
		lista.add(compania);
		lista.addAll(trenDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorComposicion", method = RequestMethod.GET)
	public String buscadorModificarFormacion(Model model) {

		model.addAttribute("buscadorComposicion", new BuscadorComposicionJSP());
		cargarLista(new BuscadorComposicionJSP(), model, 0, 10);
		return "buscadorComposicion";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorComposicionJSP objeto con los campos de filtro de la pantalla
	 * @param model                  objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorComposicion", method = RequestMethod.POST)
	public String filtrarBuscadorModificarFormacion(BuscadorComposicionJSP buscadorComposicionJSP, Model model) {

		cargarLista(buscadorComposicionJSP, model, 0, 10);
		model.addAttribute("buscadorComposicion", buscadorComposicionJSP);
		return "buscadorComposicion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorComposicionJSP objeto con los campos de filtro de la pantalla
	 * @param model                  objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorComposicion", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorModificarFormacion(BuscadorComposicionJSP buscadorComposicionJSP, Model model) {
		model.addAttribute("buscadorComposicion", new BuscadorComposicionJSP());
		cargarLista(new BuscadorComposicionJSP(), model, 0, 10);
		return "buscadorComposicion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorComposicionJSP objeto con los campos de filtro de la pantalla
	 * @param model                  objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/eliminarComposicion", method = RequestMethod.POST)
	public String eliminarComposicion(BuscadorComposicionJSP buscadorComposicionJSP, Model model) {

		try {
			composicionService.eliminarComposicion(Integer.parseInt(buscadorComposicionJSP.getIdComposicion()));
		} catch (NumberFormatException | EseException e) {
			log.error(e.getMessage());
		}
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorComposicionJSP.getPage())) {
			Long elementosTotales = composicionDAO.countListComposicionesByFilters(buscadorComposicionJSP.getIdTren(),
					buscadorComposicionJSP.getFecha());

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(buscadorComposicionJSP.getPage());
			if (totalPaginas < pageJSP) {
				buscadorComposicionJSP.setPage(String.valueOf(pageJSP - 1));
			}
			page = Integer.parseInt(buscadorComposicionJSP.getPage()) - 1;
		}
		model.addAttribute("buscadorComposicion", buscadorComposicionJSP);
		model.addAttribute("info", "Se ha eliminado correctamente");
		cargarLista(buscadorComposicionJSP, model, page, size);
		return "buscadorComposicion";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorComposicion", method = RequestMethod.POST)
	public String paginacionTablaBuscadorComposicion(BuscadorComposicionJSP buscadorComposicionJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorComposicionJSP.getPage())) {
			buscadorComposicionJSP.setPage(buscadorComposicionJSP.getPage().substring(
					buscadorComposicionJSP.getPage().indexOf(",") + 1, buscadorComposicionJSP.getPage().length()));
			page = Integer.parseInt(buscadorComposicionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorComposicion", buscadorComposicionJSP);
		cargarLista(buscadorComposicionJSP, model, page, size);
		return "buscadorComposicion";
	}

	@RequestMapping(value = "/ordenTablaBuscadorComposicion", method = RequestMethod.POST)
	public String ordenTablaBuscadorComposicion(BuscadorComposicionJSP buscadorComposicionJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorComposicionJSP.getPage())) {
			page = Integer.parseInt(buscadorComposicionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorComposicion", buscadorComposicionJSP);
		cargarLista(buscadorComposicionJSP, model, page, size);
		return "buscadorComposicion";
	}

	private void cargarLista(BuscadorComposicionJSP buscadorComposicionJSP, Model model, int page, int size) {

		Order order = Order.desc("idComposicion");
		if (StringUtils.isNotBlank(buscadorComposicionJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorComposicionJSP.getDireccion())) {

			if ("asc".equals(buscadorComposicionJSP.getDireccion())) {
				order = Order.asc(buscadorComposicionJSP.getOrder());
			} else {
				order = Order.desc(buscadorComposicionJSP.getOrder());
			}

		}

		model.addAttribute("composiciones",
				composicionDAO.findListComposicionesByFilters(buscadorComposicionJSP.getIdTren(),
						buscadorComposicionJSP.getFecha(), PageRequest.of(page, size, Sort.by(order))));
	}
}