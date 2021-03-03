package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.EstadoHistorico;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.BuscadorTomaServicioJSP;
import eu.eurogestion.ese.repository.EstadoHistoricoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TomaServicioDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorTomaServicioController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TomaServicioDAO tomaServicioDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public EstadoHistoricoDAO estadoHistoricoDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Metodo que devuelve una lista de maquinistas para una tabla
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("maquinistas")
	public List<Personal> listMaquinistasAll() {
		List<Personal> lista = new ArrayList<>();
		Personal personal = new Personal();
		personal.setNombre("Selecciona uno:");
		lista.add(personal);
		lista.addAll(personalDAO.findAllMaquinistas());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de maquinistas para una tabla
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("estadosTomaServicio")
	public List<EstadoHistorico> listEstadosTomaServicioAll() {
		List<EstadoHistorico> lista = new ArrayList<>();
		EstadoHistorico personal = new EstadoHistorico();
		personal.setValor("Selecciona uno:");
		lista.add(personal);
		lista.addAll(estadoHistoricoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorTomaServicio", method = RequestMethod.GET)
	public String buscadorTomaServicio(Model model) {
		model.addAttribute("buscadorTomaServicio", new BuscadorTomaServicioJSP());
		cargarListaValores(new BuscadorTomaServicioJSP(), model, 0, 10);
		return "buscadorTomaServicio";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorTrenJSP objeto con los campos de filtro de la pantalla
	 * @param model           objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorTomaServicio", method = RequestMethod.POST)
	public String filtrarBuscadorTomaServicio(BuscadorTomaServicioJSP buscadorTomaServicioJSP, Model model) {

		model.addAttribute("buscadorTomaServicio", buscadorTomaServicioJSP);
		cargarListaValores(buscadorTomaServicioJSP, model, 0, 10);
		return "buscadorTomaServicio";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorTomaServicio", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorTomaServicio(BuscadorTomaServicioJSP buscadorTomaServicioJSP, Model model) {
		model.addAttribute("buscadorTomaServicio", new BuscadorTomaServicioJSP());
		cargarListaValores(new BuscadorTomaServicioJSP(), model, 0, 10);
		return "buscadorTomaServicio";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorCompaniaTomaServicio", method = RequestMethod.POST)
	public String paginacionTablaBuscadorCompaniaTomaServicio(BuscadorTomaServicioJSP buscadorTomaServicioJSP,
			Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTomaServicioJSP.getPage())) {
			buscadorTomaServicioJSP.setPage(buscadorTomaServicioJSP.getPage().substring(
					buscadorTomaServicioJSP.getPage().indexOf(",") + 1, buscadorTomaServicioJSP.getPage().length()));
			page = Integer.parseInt(buscadorTomaServicioJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorTomaServicio", buscadorTomaServicioJSP);
		cargarListaValores(buscadorTomaServicioJSP, model, page, size);
		return "buscadorTomaServicio";
	}
	
	
	@RequestMapping(value = "/ordenTablaBuscadorTomaServicio", method = RequestMethod.POST)
	public String ordenTablaBuscadorTomaServicio(BuscadorTomaServicioJSP buscadorTomaServicioJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorTomaServicioJSP.getPage())) {
			page = Integer.parseInt(buscadorTomaServicioJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorTomaServicio", buscadorTomaServicioJSP);
		cargarListaValores(buscadorTomaServicioJSP, model, page, size);
		return "buscadorTomaServicio";
	}

	private void cargarListaValores(BuscadorTomaServicioJSP buscadorTomaServicioJSP, Model model, int page, int size) {

		if (StringUtils.isNotBlank(buscadorTomaServicioJSP.getOrder())
				&& "nombreCompleto".equals(buscadorTomaServicioJSP.getOrder())) {
			Direction direccion = Direction.DESC;
			if ("asc".equals(buscadorTomaServicioJSP.getDireccion())) {
				direccion = Direction.ASC;
			}

			model.addAttribute("tomaServicios",
					tomaServicioDAO.findAllByFilters(buscadorTomaServicioJSP.getIdMaquinista(),
							buscadorTomaServicioJSP.getNumeroTren(), buscadorTomaServicioJSP.getIdEstadoTomaServicio(),
							PageRequest.of(page, size, direccion, "personal.apellido1", "personal.nombre")));
			return;
		}

		Order order = Order.desc("idTomaServicio");
		if (StringUtils.isNotBlank(buscadorTomaServicioJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorTomaServicioJSP.getDireccion())) {

			if ("asc".equals(buscadorTomaServicioJSP.getDireccion())) {
				order = Order.asc(buscadorTomaServicioJSP.getOrder());
			} else {
				order = Order.desc(buscadorTomaServicioJSP.getOrder());
			}

		}

		model.addAttribute("tomaServicios",
				tomaServicioDAO.findAllByFilters(buscadorTomaServicioJSP.getIdMaquinista(),
						buscadorTomaServicioJSP.getNumeroTren(), buscadorTomaServicioJSP.getIdEstadoTomaServicio(),
						PageRequest.of(page, size, Sort.by(order))));

	}

}