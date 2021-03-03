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

import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.pojo.BuscadorPersonalJSP;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorPersonalController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Repositorio de la clase de dominio Cargo
	 */
	@Autowired
	public CargoDAO cargoDAO;

	/**
	 * Repositorio de la clase de dominio RevisionPsico
	 */
	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public CursoDAO cursoDAO;

	/**
	 * Metodo que devuelve una lista de Cargos para un select
	 * 
	 * @return lista de objetos Cargo
	 */
	@ModelAttribute("cargos")
	public List<Cargo> listCargoAll() {
		List<Cargo> lista = new ArrayList<>();
		Cargo cargo = new Cargo();
		cargo.setNombre("Selecciona uno:");
		lista.add(cargo);
		lista.addAll(cargoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorPersonal", method = RequestMethod.GET)
	public String login(Model model) {

		model.addAttribute("buscadorPersonal", new BuscadorPersonalJSP());
		cargarLista(new BuscadorPersonalJSP(), model, 0, 10);
		return "buscadorPersonal";
	}

	@RequestMapping(value = "/buscadorPersonalDetallePersonalBaja", method = RequestMethod.GET)
	public String buscadorPersonalDetallePersonalBaja(Model model) {

		model.addAttribute("buscadorPersonal", new BuscadorPersonalJSP());
		model.addAttribute("info", "Se ha dado de baja correctamente");
		cargarLista(new BuscadorPersonalJSP(), model, 0, 10);
		return "buscadorPersonal";
	}

	@RequestMapping(value = "/buscadorPersonalRegisterPersonal", method = RequestMethod.GET)
	public String buscadorPersonalRegisterPersonal(Model model) {

		model.addAttribute("buscadorPersonal", new BuscadorPersonalJSP());
		model.addAttribute("info", " Se ha creado correctamente");
		cargarLista(new BuscadorPersonalJSP(), model, 0, 10);
		return "buscadorPersonal";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorPersonal objeto con los campos de filtro de la pantalla
	 * @param model            objeto model de la pantalla
	 * @param session          objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/paginacionTablaBuscadorPersonal", method = RequestMethod.POST)
	public String paginacionTablaBuscadorPersonal(BuscadorPersonalJSP buscadorPersonal, Model model,
			HttpSession session) {

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10;
		if (StringUtils.isNotBlank(buscadorPersonal.getPage())) {
			buscadorPersonal.setPage(buscadorPersonal.getPage().substring(buscadorPersonal.getPage().indexOf(",") + 1,
					buscadorPersonal.getPage().length()));
			page = Integer.parseInt(buscadorPersonal.getPage()) - 1;
		}
		cargarLista(buscadorPersonal, model, page, size);

		model.addAttribute("buscadorPersonal", buscadorPersonal);
		return "buscadorPersonal";
	}

	@RequestMapping(value = "/filtrarBuscadorPersonal", method = RequestMethod.POST)
	public String filtrarPersonal(BuscadorPersonalJSP buscadorPersonal, Model model, HttpSession session) {

		cargarLista(buscadorPersonal, model, 0, 10);

		model.addAttribute("buscadorPersonal", buscadorPersonal);
		return "buscadorPersonal";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorPersonal objeto con los campos de filtro de la pantalla
	 * @param model            objeto model de la pantalla
	 * @param session          objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorPersonal", method = RequestMethod.POST)
	public String borrarFiltrosPersonal(BuscadorPersonalJSP buscadorPersonal, Model model, HttpSession session) {
		model.addAttribute("buscadorPersonal", new BuscadorPersonalJSP());
		cargarLista(new BuscadorPersonalJSP(), model, 0, 10);
		return "buscadorPersonal";
	}

	/**
	 * Metodo que se llama si hay algun error en la carga del detalle del personal
	 * 
	 * @param buscadorPersonal objeto con los campos de filtro de la pantalla
	 * @param model            objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/errorVerDetallePersonal", method = RequestMethod.POST)
	public String errorVerDetallePersonal(BuscadorPersonalJSP buscadorPersonal, Model model) {
		model.addAttribute("buscadorPersonal", new BuscadorPersonalJSP());

		return "buscadorPersonal";
	}

	@RequestMapping(value = "/ordenTablaBuscadorPersonal", method = RequestMethod.POST)
	public String ordenTablaBuscadorPersonal(BuscadorPersonalJSP buscadorPersonalJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorPersonalJSP.getPage())) {
			page = Integer.parseInt(buscadorPersonalJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorPersonal", buscadorPersonalJSP);
		cargarLista(buscadorPersonalJSP, model, page, size);
		return "buscadorPersonal";
	}

	private void cargarLista(BuscadorPersonalJSP buscadorPersonal, Model model, int page, int size) {

		if (StringUtils.isNotBlank(buscadorPersonal.getOrder())
				&& "nombreCompleto".equals(buscadorPersonal.getOrder())) {
			Direction direccion = Direction.DESC;
			if ("asc".equals(buscadorPersonal.getDireccion())) {
				direccion = Direction.ASC;
			}

			model.addAttribute("personas",
					personalDAO.findBuscadorPersonalByFilters(buscadorPersonal.getDni(), buscadorPersonal.getIdCargo(),
							buscadorPersonal.getNombre(), buscadorPersonal.getApellido(),
							PageRequest.of(page, size, direccion, "apellido1", "nombre")));
			return;
		}

		Order order = Order.desc("idPersonal");
		if (StringUtils.isNotBlank(buscadorPersonal.getOrder())
				&& StringUtils.isNotBlank(buscadorPersonal.getDireccion())) {

			if ("asc".equals(buscadorPersonal.getDireccion())) {
				order = Order.asc(buscadorPersonal.getOrder());
			} else {
				order = Order.desc(buscadorPersonal.getOrder());
			}

		}
		model.addAttribute("personas",
				personalDAO.findBuscadorPersonalByFilters(buscadorPersonal.getDni(), buscadorPersonal.getIdCargo(),
						buscadorPersonal.getNombre(), buscadorPersonal.getApellido(),
						PageRequest.of(page, size, Sort.by(order))));
	}
}
