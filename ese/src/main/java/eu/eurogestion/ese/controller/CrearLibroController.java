package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorLibrosJSP;
import eu.eurogestion.ese.pojo.CrearLibroJSP;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.LibroDAO;
import eu.eurogestion.ese.repository.LibroPersonalDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.services.CrearLibroService;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class CrearLibroController {

	/**
	 * Repositorio de la clase de dominio ModeloMaterial
	 */
	@Autowired
	public LibroDAO libroDAO;

	@Autowired
	public LibroPersonalDAO libroPersonalDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public CargoDAO cargoDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public CrearLibroService crearLibroService;

	/**
	 * Metodo que devuelve una lista de trenes para una tabla
	 * 
	 * @return lista de objetos Tren
	 */
	@ModelAttribute("cargos")
	public List<Cargo> listTrenesAll() {
		List<Cargo> lista = new ArrayList<>();
		Cargo personal = new Cargo();
		personal.setDescripcion("Selecciona uno:");
		lista.add(personal);
		lista.addAll(cargoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorLibrosJSP objeto con los campos de filtro de la pantalla
	 * @param model             objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/crearLibro", method = RequestMethod.POST)
	public String crearLibro(BuscadorLibrosJSP buscadorLibrosJSP, Model model) {

		CrearLibroJSP crearLibroJSP = new CrearLibroJSP();
		crearLibroJSP.setUsuariosSeleccionados(Boolean.FALSE);
		model.addAttribute("crearLibro", crearLibroJSP);
		cargarListas(crearLibroJSP, model);

		return "crearLibro";
	}

	@RequestMapping(value = "/filtrarBuscadorCrearLibro", method = RequestMethod.POST)
	public String filtrarBuscadorCrearLibro(CrearLibroJSP crearLibroJSP, Model model) {

		model.addAttribute("crearLibro", crearLibroJSP);
		cargarListas(crearLibroJSP, model);

		return "crearLibro";
	}

	@RequestMapping(value = "/borrarFiltrosBuscadorCrearLibro", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorCrearLibro(CrearLibroJSP crearLibroJSP, Model model) {

		crearLibroJSP.setDni(null);
		crearLibroJSP.setNombre(null);
		crearLibroJSP.setApellido(null);
		crearLibroJSP.setIdCargo(null);

		model.addAttribute("crearLibro", crearLibroJSP);
		cargarListas(crearLibroJSP, model);

		return "crearLibro";
	}

	@RequestMapping(value = "/seleccionarUsuariosLibro", method = RequestMethod.POST)
	public String seleccionarUsuariosLibro(CrearLibroJSP crearLibroJSP, Model model) {

		if (CollectionUtils.isEmpty(crearLibroJSP.getListPersonalesLibro())) {
			model.addAttribute("error", "Tienes que tener al menos un personal al que mandarle el libro");
			model.addAttribute("crearLibro", crearLibroJSP);
			cargarListas(crearLibroJSP, model);

			return "crearLibro";
		}

		crearLibroJSP.setUsuariosSeleccionados(Boolean.TRUE);

		model.addAttribute("crearLibro", crearLibroJSP);
		cargarListas(crearLibroJSP, model);

		return "crearLibro";
	}

	@RequestMapping(value = "/volverSeleccionarUsuariosLibro", method = RequestMethod.POST)
	public String volverSeleccionarUsuariosLibro(CrearLibroJSP crearLibroJSP, Model model) {
		crearLibroJSP.setUsuariosSeleccionados(Boolean.FALSE);

		model.addAttribute("crearLibro", crearLibroJSP);
		cargarListas(crearLibroJSP, model);

		return "crearLibro";
	}

	@RequestMapping(value = "/addLibroPersonal", method = RequestMethod.POST)
	public String addLibroPersonal(CrearLibroJSP crearLibroJSP, Model model) {
		crearLibroJSP.getListPersonalesLibro().add(Integer.valueOf(crearLibroJSP.getIdPersonalAdd()));

		model.addAttribute("crearLibro", crearLibroJSP);
		model.addAttribute("info", " Se ha añadido correctamente");
		cargarListas(crearLibroJSP, model);

		return "crearLibro";
	}

	@RequestMapping(value = "/eliminarLibroPersonal", method = RequestMethod.POST)
	public String eliminarLibroPersonal(CrearLibroJSP crearLibroJSP, Model model) {
		crearLibroJSP.getListPersonalesLibro().remove(Integer.valueOf(crearLibroJSP.getIdPersonalEliminar()));

		model.addAttribute("crearLibro", crearLibroJSP);
		model.addAttribute("info", "Se ha eliminado correctamente");
		cargarListas(crearLibroJSP, model);

		return "crearLibro";
	}

	@RequestMapping(value = "/generarLibro", method = RequestMethod.POST)
	public String generarLibro(CrearLibroJSP crearLibroJSP, Model model, HttpSession session) {

		try {
			crearLibroService.generarLibro(crearLibroJSP, session);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("error", "Ha ocurrido un error al generar el libro");
			model.addAttribute("crearLibro", crearLibroJSP);
			cargarListas(crearLibroJSP, model);

			return "crearLibro";
		}

		return "redirect:/buscadorLibrosCrearLibro";

	}

	private void cargarListas(CrearLibroJSP crearLibro, Model model) {

		model.addAttribute("personalesTotales",
				personalDAO.findPersonalByFilters(crearLibro.getDni(), crearLibro.getIdCargo(), crearLibro.getNombre(),
						crearLibro.getApellido(), crearLibro.getListPersonalesLibro()));
		model.addAttribute("personalesLibro", personalDAO.findByList(crearLibro.getListPersonalesLibro()));

	}

}