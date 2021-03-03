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

import eu.eurogestion.ese.domain.EstadoProveedor;
import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.BuscadorHomologacionJSP;
import eu.eurogestion.ese.repository.EstadoProveedorDAO;
import eu.eurogestion.ese.repository.ProveedorDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorHomologacionController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public ProveedorDAO proveedorDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public EstadoProveedorDAO estadoProveedorDAO;

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("estadosProveedores")
	public List<EstadoProveedor> listEstadoProveedor() {
		List<EstadoProveedor> lista = new ArrayList<>();
		EstadoProveedor estadoProveedor = new EstadoProveedor();
		estadoProveedor.setValor("Selecciona uno:");
		lista.add(estadoProveedor);
		lista.addAll(estadoProveedorDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorHomologacion", method = RequestMethod.GET)
	public String buscadorHomologacion(Model model) {
		model.addAttribute("buscadorHomologacion", new BuscadorHomologacionJSP());
		cargarLista(new BuscadorHomologacionJSP(), model, 0, 10);
		return "buscadorHomologacion";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorHomologacionJSP objeto con los campos de filtro de la pantalla
	 * @param model                   objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorHomologacion", method = RequestMethod.POST)
	public String filtrarBuscadorHomologacion(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model) {

		model.addAttribute("buscadorHomologacion", buscadorHomologacionJSP);
		cargarLista(buscadorHomologacionJSP, model, 0, 10);
		return "buscadorHomologacion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorHomologacion", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorHomologacion(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model) {
		model.addAttribute("buscadorHomologacion", new BuscadorHomologacionJSP());
		cargarLista(new BuscadorHomologacionJSP(), model, 0, 10);
		return "buscadorHomologacion";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorHomologacion", method = RequestMethod.POST)
	public String paginacionTablaBuscadorHomologacion(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorHomologacionJSP.getPage())) {
			buscadorHomologacionJSP.setPage(buscadorHomologacionJSP.getPage().substring(
					buscadorHomologacionJSP.getPage().indexOf(",") + 1, buscadorHomologacionJSP.getPage().length()));
			page = Integer.parseInt(buscadorHomologacionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorHomologacion", buscadorHomologacionJSP);
		cargarLista(buscadorHomologacionJSP, model, page, size);
		return "buscadorHomologacion";
	}
	
	@RequestMapping(value = "/ordenTablaBuscadorHomologacion", method = RequestMethod.POST)
	public String ordenTablaBuscadorHomologacion(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorHomologacionJSP.getPage())) {
			page = Integer.parseInt(buscadorHomologacionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorHomologacion", buscadorHomologacionJSP);
		cargarLista(buscadorHomologacionJSP, model, page, size);
		return "buscadorHomologacion";
	}

	private void cargarLista(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model, int page, int size) {

		Order order = Order.desc("idProveedor");
		if (StringUtils.isNotBlank(buscadorHomologacionJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorHomologacionJSP.getDireccion())) {

			if ("asc".equals(buscadorHomologacionJSP.getDireccion())) {
				order = Order.asc(buscadorHomologacionJSP.getOrder());
			} else {
				order = Order.desc(buscadorHomologacionJSP.getOrder());
			}

		}
		model.addAttribute("proveedores",
				proveedorDAO.findAllProveedoresByFilters(buscadorHomologacionJSP.getDocumento(),
						buscadorHomologacionJSP.getNombre(), buscadorHomologacionJSP.getIdEstado(),
						PageRequest.of(page, size, Sort.by(order))));
	}

}