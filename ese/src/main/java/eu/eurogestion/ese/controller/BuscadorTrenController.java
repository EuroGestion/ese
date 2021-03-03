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

import eu.eurogestion.ese.pojo.BuscadorTrenJSP;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.TrenService;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorTrenController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TrenService trenService;

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorTren", method = RequestMethod.GET)
	public String buscadorCompania(Model model) {
		model.addAttribute("buscadorTren", new BuscadorTrenJSP());
		cargarListaValores(new BuscadorTrenJSP(), model, 0, 10);
		return "buscadorTren";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorTrenJSP objeto con los campos de filtro de la pantalla
	 * @param model           objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorTren", method = RequestMethod.POST)
	public String filtrarBuscadorModificarRevision(BuscadorTrenJSP buscadorTrenJSP, Model model) {

		model.addAttribute("buscadorTren", buscadorTrenJSP);
		cargarListaValores(buscadorTrenJSP, model, 0, 10);
		return "buscadorTren";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorTren", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorModificarRevision(BuscadorTrenJSP buscadorTrenJSP, Model model) {
		model.addAttribute("buscadorTren", new BuscadorTrenJSP());
		cargarListaValores(new BuscadorTrenJSP(), model, 0, 10);
		return "buscadorTren";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/bajaTren", method = RequestMethod.POST)
	public String bajaTren(BuscadorTrenJSP buscadorTrenJSP, Model model) {

		trenService.bajaTren(buscadorTrenJSP);

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTrenJSP.getPage())) {
			Long elementosTotales = countTotalElementos(buscadorTrenJSP, model);

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(buscadorTrenJSP.getPage());
			if (totalPaginas < pageJSP) {
				buscadorTrenJSP.setPage(String.valueOf(pageJSP - 1));
			}
			page = Integer.parseInt(buscadorTrenJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorTren", buscadorTrenJSP);
		model.addAttribute("info", "Se ha dado de baja correctamente");
		cargarListaValores(buscadorTrenJSP, model, page, size);
		return "buscadorTren";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorTrenes", method = RequestMethod.POST)
	public String paginacionTablaBuscadorTrenes(BuscadorTrenJSP buscadorTrenJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTrenJSP.getPage())) {
			buscadorTrenJSP.setPage(buscadorTrenJSP.getPage().substring(buscadorTrenJSP.getPage().indexOf(",") + 1,
					buscadorTrenJSP.getPage().length()));
			page = Integer.parseInt(buscadorTrenJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorTren", buscadorTrenJSP);
		cargarListaValores(buscadorTrenJSP, model, page, size);
		return "buscadorTren";
	}

	@RequestMapping(value = "/ordenTablaBuscadorTrenes", method = RequestMethod.POST)
	public String ordenTablaBuscadorTrenes(BuscadorTrenJSP buscadorTrenJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTrenJSP.getPage())) {
			page = Integer.parseInt(buscadorTrenJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorTren", buscadorTrenJSP);
		cargarListaValores(buscadorTrenJSP, model, page, size);
		return "buscadorTren";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/errorVerDetalleTren", method = RequestMethod.POST)
	public String errorVerDetalleTren(BuscadorTrenJSP buscadorTrenJSP, Model model) {
		// TODO falta añadirle el error
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTrenJSP.getPage())) {
			page = Integer.parseInt(buscadorTrenJSP.getPage()) - 1;
		}
		model.addAttribute("buscadorTren", buscadorTrenJSP);
		cargarListaValores(buscadorTrenJSP, model, page, size);
		return "buscadorTren";
	}

	private void cargarListaValores(BuscadorTrenJSP buscadorTrenJSP, Model model, int page, int size) {

		Order order = Order.desc("idTren");
		if (StringUtils.isNotBlank(buscadorTrenJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorTrenJSP.getDireccion())) {

			if ("asc".equals(buscadorTrenJSP.getDireccion())) {
				order = Order.asc(buscadorTrenJSP.getOrder());
			} else {
				order = Order.desc(buscadorTrenJSP.getOrder());
			}

		}

		if (StringUtils.isNotBlank(buscadorTrenJSP.getNumeroTren())) {
			model.addAttribute("trenes", trenDAO.findByNumeroContainingIgnoreCaseAndFechaBajaIsNull(
					buscadorTrenJSP.getNumeroTren(), PageRequest.of(page, size, Sort.by(order))));
		} else {
			model.addAttribute("trenes", trenDAO.findByFechaBajaIsNull(PageRequest.of(page, size, Sort.by(order))));
		}
	}

	private Long countTotalElementos(BuscadorTrenJSP buscadorTrenJSP, Model model) {

		if (StringUtils.isNotBlank(buscadorTrenJSP.getNumeroTren())) {
			return trenDAO.countByNumeroContainingIgnoreCaseAndFechaBajaIsNull(buscadorTrenJSP.getNumeroTren());
		} else {
			return trenDAO.countByFechaBajaIsNull();
		}
	}

}