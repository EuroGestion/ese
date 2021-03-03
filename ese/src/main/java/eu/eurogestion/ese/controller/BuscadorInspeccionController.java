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

import eu.eurogestion.ese.domain.EstadoInspeccion;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoInspeccion;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.pojo.BuscadorInspeccionJSP;
import eu.eurogestion.ese.repository.EstadoInspeccionDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoInspeccionDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.repository.ViewInspeccionDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorInspeccionController {

	/**
	 * Repositorio de la clase de dominio ViewInspeccion
	 */
	@Autowired
	public ViewInspeccionDAO viewInspeccionDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Repositorio de la clase de dominio TipoInspeccion
	 */
	@Autowired
	public TipoInspeccionDAO tipoInspeccionDAO;

	/**
	 * Repositorio de la clase de dominio EstadoInspeccion
	 */
	@Autowired
	public EstadoInspeccionDAO estadoInspeccionDAO;

	/**
	 * Repositorio de la clase de dominio Tren
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("inspectores")
	public List<Personal> listPersonalAll() {
		List<Personal> lista = new ArrayList<>();
		Personal personal = new Personal();
		personal.setNombre("Selecciona uno:");
		lista.add(personal);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("tiposInspeccion")
	public List<TipoInspeccion> listTipoInspeccionAll() {
		List<TipoInspeccion> lista = new ArrayList<>();
		TipoInspeccion tipoInspeccion = new TipoInspeccion();
		tipoInspeccion.setValor("Selecciona uno:");
		lista.add(tipoInspeccion);
		lista.addAll(tipoInspeccionDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("estadosInspecciones")
	public List<EstadoInspeccion> listEstadoInspeccionAll() {
		List<EstadoInspeccion> lista = new ArrayList<>();
		EstadoInspeccion estadoInspeccion = new EstadoInspeccion();
		estadoInspeccion.setValor("Selecciona uno:");
		lista.add(estadoInspeccion);
		lista.addAll(estadoInspeccionDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Tren para un select
	 * 
	 * @return lista de objetos Tren
	 */
	@ModelAttribute("trenes")
	public List<Tren> listTrenAll() {
		List<Tren> lista = new ArrayList<>();
		Tren tren = new Tren();
		tren.setNumero("Selecciona uno:");
		lista.add(tren);
		lista.addAll(trenDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorInspeccion", method = RequestMethod.GET)
	public String buscadorInspeccion(Model model) {

		model.addAttribute("buscadorInspeccion", new BuscadorInspeccionJSP());
		cargarListaValores(new BuscadorInspeccionJSP(), model, 0, 10);
		return "buscadorInspeccion";
	}

	@RequestMapping(value = "/buscadorInspeccionProgramarInspeccion", method = RequestMethod.GET)
	public String buscadorInspeccionProgramarInspeccion(Model model) {

		model.addAttribute("buscadorInspeccion", new BuscadorInspeccionJSP());
		cargarListaValores(new BuscadorInspeccionJSP(), model, 0, 10);
		model.addAttribute("info", " Se ha creado correctamente");
		return "buscadorInspeccion";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorInspeccion", method = RequestMethod.POST)
	public String filtrarBuscadorInspeccion(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		cargarListaValores(buscadorInspeccionJSP, model, 0, 10);

		model.addAttribute("buscadorInspeccion", buscadorInspeccionJSP);

		return "buscadorInspeccion";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorInspeccion", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorInspeccion(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		model.addAttribute("buscadorInspeccion", new BuscadorInspeccionJSP());
		cargarListaValores(new BuscadorInspeccionJSP(), model, 0, 10);
		return "buscadorInspeccion";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorInspeccion", method = RequestMethod.POST)
	public String paginacionTablaBuscadorInspeccion(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorInspeccionJSP.getPage())) {
			buscadorInspeccionJSP.setPage(buscadorInspeccionJSP.getPage().substring(
					buscadorInspeccionJSP.getPage().indexOf(",") + 1, buscadorInspeccionJSP.getPage().length()));
			page = Integer.parseInt(buscadorInspeccionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorInspeccion", buscadorInspeccionJSP);
		cargarListaValores(buscadorInspeccionJSP, model, page, size);
		return "buscadorInspeccion";
	}
	@RequestMapping(value = "/ordenTablaBuscadorInspeccion", method = RequestMethod.POST)
	public String ordenTablaBuscadorInspeccion(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorInspeccionJSP.getPage())) {
			page = Integer.parseInt(buscadorInspeccionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorInspeccion", buscadorInspeccionJSP);
		cargarListaValores(buscadorInspeccionJSP, model, page, size);
		return "buscadorInspeccion";
	}
	private void cargarListaValores(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model, int page, int size) {

		Order order = Order.desc("viewInspeccionPK.idInspeccion");
		if (StringUtils.isNotBlank(buscadorInspeccionJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorInspeccionJSP.getDireccion())) {

			if ("asc".equals(buscadorInspeccionJSP.getDireccion())) {
				order = Order.asc(buscadorInspeccionJSP.getOrder());
			} else {
				order = Order.desc(buscadorInspeccionJSP.getOrder());
			}

		}

		model.addAttribute("inspecciones",
				viewInspeccionDAO.findInspeccionByFilters(buscadorInspeccionJSP.getIdTipoInspeccion(),
						buscadorInspeccionJSP.getIdInspector(), buscadorInspeccionJSP.getIdEstado(),
						buscadorInspeccionJSP.getCodigo(), buscadorInspeccionJSP.getFecha(),
						buscadorInspeccionJSP.getIdTren(), buscadorInspeccionJSP.getOrigen(),
						buscadorInspeccionJSP.getNve(), PageRequest.of(page, size, Sort.by(order))));
	}

}