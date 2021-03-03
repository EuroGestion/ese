package eu.eurogestion.ese.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.BuscadorRolesJSP;
import eu.eurogestion.ese.repository.RolDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorRolesController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public RolDAO rolDAO;

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorRoles", method = RequestMethod.GET)
	public String buscadorRoles(Model model) {
		model.addAttribute("buscadorRoles", new BuscadorRolesJSP());
		cargarLista(new BuscadorRolesJSP(), model, 0, 10);
		return "buscadorRoles";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorHomologacionJSP objeto con los campos de filtro de la pantalla
	 * @param model                   objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorRoles", method = RequestMethod.POST)
	public String filtrarBuscadorRoles(BuscadorRolesJSP buscadorRolesJSP, Model model) {

		model.addAttribute("buscadorRoles", buscadorRolesJSP);
		cargarLista(buscadorRolesJSP, model, 0, 10);
		return "buscadorRoles";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorRoles", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorRoles(BuscadorRolesJSP buscadorRolesJSP, Model model) {
		model.addAttribute("buscadorRoles", new BuscadorRolesJSP());
		cargarLista(new BuscadorRolesJSP(), model, 0, 10);
		return "buscadorRoles";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorRoles", method = RequestMethod.POST)
	public String paginacionTablaBuscadorRoles(BuscadorRolesJSP buscadorRolesJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorRolesJSP.getPage())) {
			buscadorRolesJSP.setPage(buscadorRolesJSP.getPage().substring(buscadorRolesJSP.getPage().indexOf(",") + 1,
					buscadorRolesJSP.getPage().length()));
			page = Integer.parseInt(buscadorRolesJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorRoles", buscadorRolesJSP);
		cargarLista(buscadorRolesJSP, model, page, size);
		return "buscadorRoles";
	}
	
	@RequestMapping(value = "/ordenTablaBuscadorRoles", method = RequestMethod.POST)
	public String ordenTablaBuscadorRoles(BuscadorRolesJSP buscadorRolesJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorRolesJSP.getPage())) {
			page = Integer.parseInt(buscadorRolesJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorRoles", buscadorRolesJSP);
		cargarLista(buscadorRolesJSP, model, page, size);
		return "buscadorRoles";
	}

	private void cargarLista(BuscadorRolesJSP buscadorRolesJSP, Model model, int page, int size) {

		Order order = Order.desc("idRol");
		if (StringUtils.isNotBlank(buscadorRolesJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorRolesJSP.getDireccion())) {

			if ("asc".equals(buscadorRolesJSP.getDireccion())) {
				order = Order.asc(buscadorRolesJSP.getOrder());
			} else {
				order = Order.desc(buscadorRolesJSP.getOrder());
			}

		}

		Page<Rol> listaRoles;
		if (StringUtils.isNotBlank(buscadorRolesJSP.getNombre())) {
			listaRoles = rolDAO.findByNombreContainingIgnoreCase(buscadorRolesJSP.getNombre(),
					PageRequest.of(page, size, Sort.by(order)));
		} else {
			listaRoles = rolDAO.findAll(PageRequest.of(page, size, Sort.by(order)));
		}

		model.addAttribute("roles", listaRoles);
	}

}