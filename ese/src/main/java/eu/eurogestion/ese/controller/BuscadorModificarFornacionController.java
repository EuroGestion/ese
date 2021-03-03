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

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.EstadoCurso;
import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.BuscadorModificarFormacionJSP;
import eu.eurogestion.ese.pojo.ModificarFormacionJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.EstadoCursoDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorModificarFornacionController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio ModeloMaterial
	 */
	@Autowired
	public EstadoCursoDAO estadoCursoDAO;

	/**
	 * Repositorio de la clase de dominio TipoCurso
	 */
	@Autowired
	public CursoDAO cursoDAO;

	/**
	 * Metodo que devuelve una lista de cenntros de formacion para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("centrosFormacion")
	public List<Compania> listCentroFormacionAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAllCompaniaFormacion());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de estados de un Curso Cargos para un select
	 * 
	 * @return lista de objetos EstadoCurso
	 */
	@ModelAttribute("estados")
	public List<EstadoCurso> listTipoCargoAll() {
		List<EstadoCurso> lista = new ArrayList<>();
		EstadoCurso tipoCurso = new EstadoCurso();
		tipoCurso.setValor("Selecciona uno:");
		lista.add(tipoCurso);
		lista.addAll(estadoCursoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorModificarFormacion", method = RequestMethod.GET)
	public String buscadorModificarFormacion(Model model) {

		model.addAttribute("buscadorModificarFormacion", new BuscadorModificarFormacionJSP());
		cargarLista(new BuscadorModificarFormacionJSP(), model, 0, 10);
		return "buscadorModificarFormacion";
	}

	@RequestMapping(value = "/buscadorModificarFormacionProgramarFormacion", method = RequestMethod.GET)
	public String buscadorModificarFormacionProgramarFormacion(Model model) {

		model.addAttribute("buscadorModificarFormacion", new BuscadorModificarFormacionJSP());
		cargarLista(new BuscadorModificarFormacionJSP(), model, 0, 10);
		model.addAttribute("info", " Se ha creado correctamente");
		return "buscadorModificarFormacion";
	}

	@RequestMapping(value = "/buscadorModificarFormacionModificarFormacionAprobar", method = RequestMethod.GET)
	public String buscadorModificarFormacionModificarFormacionAprobar(Model model) {

		model.addAttribute("buscadorModificarFormacion", new BuscadorModificarFormacionJSP());
		cargarLista(new BuscadorModificarFormacionJSP(), model, 0, 10);
		model.addAttribute("info", "Se ha aprobado correctamente");
		return "buscadorModificarFormacion";
	}

	@RequestMapping(value = "/buscadorModificarFormacionModificarFormacionNoAprobar", method = RequestMethod.GET)
	public String buscadorModificarFormacionModificarFormacionNoAprobar(Model model) {

		model.addAttribute("buscadorModificarFormacion", new BuscadorModificarFormacionJSP());
		cargarLista(new BuscadorModificarFormacionJSP(), model, 0, 10);
		model.addAttribute("info", "Se ha cancelado el curso correctamente");
		return "buscadorModificarFormacion";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorModificarFormacionJSP objeto con los campos de filtro de la
	 *                                      pantalla
	 * @param model                         objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorModificarFormacion", method = RequestMethod.POST)
	public String filtrarBuscadorModificarFormacion(BuscadorModificarFormacionJSP buscadorModificarFormacionJSP,
			Model model) {

		cargarLista(buscadorModificarFormacionJSP, model, 0, 10);

		model.addAttribute("buscadorModificarFormacion", buscadorModificarFormacionJSP);
		return "buscadorModificarFormacion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarFormacionJSP objeto con los campos de filtro de la
	 *                                      pantalla
	 * @param model                         objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorModificarFormacion", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorModificarFormacion(BuscadorModificarFormacionJSP buscadorModificarFormacionJSP,
			Model model) {
		model.addAttribute("buscadorModificarFormacion", new BuscadorModificarFormacionJSP());
		cargarLista(new BuscadorModificarFormacionJSP(), model, 0, 10);
		return "buscadorModificarFormacion";
	}

	/**
	 * Metodo que te devuelve a la pantalla de buscador modificar formacion
	 * 
	 * @param modificarFormacionJSP
	 * @param model
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverCurso", method = RequestMethod.POST)
	public String volverCurso(ModificarFormacionJSP modificarFormacionJSP, Model model) {

		model.addAttribute("buscadorModificarFormacion", new BuscadorModificarFormacionJSP());
		cargarLista(new BuscadorModificarFormacionJSP(), model, 0, 10);
		return "buscadorModificarFormacion";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorModificarFormacion", method = RequestMethod.POST)
	public String paginacionTablaBuscadorModificarFormacion(BuscadorModificarFormacionJSP buscadorModificarFormacionJSP,
			Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorModificarFormacionJSP.getPage())) {
			buscadorModificarFormacionJSP.setPage(buscadorModificarFormacionJSP.getPage().substring(
					buscadorModificarFormacionJSP.getPage().indexOf(",") + 1, buscadorModificarFormacionJSP.getPage().length()));
			page = Integer.parseInt(buscadorModificarFormacionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorModificarFormacion", buscadorModificarFormacionJSP);
		cargarLista(buscadorModificarFormacionJSP, model, page, size);
		return "buscadorModificarFormacion";
	}
	
	@RequestMapping(value = "/ordenTablaBuscadorModificarFormacion", method = RequestMethod.POST)
	public String ordenTablaBuscadorModificarFormacion(BuscadorModificarFormacionJSP buscadorModificarFormacionJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorModificarFormacionJSP.getPage())) {
			page = Integer.parseInt(buscadorModificarFormacionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorModificarFormacion", buscadorModificarFormacionJSP);
		cargarLista(buscadorModificarFormacionJSP, model, page, size);
		return "buscadorModificarFormacion";
	}


	private void cargarLista(BuscadorModificarFormacionJSP buscadorModificarFormacionJSP, Model model, int page,
			int size) {
		Order order = Order.desc("idCurso");
		if (StringUtils.isNotBlank(buscadorModificarFormacionJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorModificarFormacionJSP.getDireccion())) {

			if ("asc".equals(buscadorModificarFormacionJSP.getDireccion())) {
				order = Order.asc(buscadorModificarFormacionJSP.getOrder());
			} else {
				order = Order.desc(buscadorModificarFormacionJSP.getOrder());
			}

		}
		model.addAttribute("cursos", cursoDAO.findCursoByFilters(buscadorModificarFormacionJSP.getIdEstado(),
				buscadorModificarFormacionJSP.getIdCentroFormacion(), buscadorModificarFormacionJSP.getFechaInicio(),
				buscadorModificarFormacionJSP.getFechaFin(), PageRequest.of(page, size, Sort.by(order))));
	}

}