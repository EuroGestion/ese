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

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.pojo.BuscadorModificarRevisionJSP;
import eu.eurogestion.ese.pojo.ModificarRevisionJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorModificarRevisionController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio RevisionPsico
	 */
	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	/**
	 * Metodo que devuelve una lista de cenntros medicos para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("centrosMedicos")
	public List<Compania> listCentroFormacionAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAllCompaniaCentroMedico());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorModificarRevision", method = RequestMethod.GET)
	public String buscadorModificarFormacion(Model model) {

		model.addAttribute("buscadorModificarRevision", new BuscadorModificarRevisionJSP());
		cargarListaValores(new BuscadorModificarRevisionJSP(), model, 0, 10);
		return "buscadorModificarRevision";
	}

	@RequestMapping(value = "/buscadorModificarRevisionProgramarRevision", method = RequestMethod.GET)
	public String buscadorModificarRevisionProgramarRevision(Model model) {

		model.addAttribute("buscadorModificarRevision", new BuscadorModificarRevisionJSP());
		cargarListaValores(new BuscadorModificarRevisionJSP(), model, 0, 10);
		model.addAttribute("info", " Se ha creado correctamente");
		return "buscadorModificarRevision";
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorModificarRevisionCalificacionRevision", method = RequestMethod.GET)
	public String buscadorModificarRevisionCalificacionRevision(Model model) {

		model.addAttribute("buscadorModificarRevision", new BuscadorModificarRevisionJSP());
		cargarListaValores(new BuscadorModificarRevisionJSP(), model, 0, 10);
		model.addAttribute("info", "Se ha calificado correctamente");
		return "buscadorModificarRevision";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorModificarRevision", method = RequestMethod.POST)
	public String filtrarBuscadorModificarRevision(BuscadorModificarRevisionJSP buscadorModificarRevisionJSP,
			Model model) {

		cargarListaValores(buscadorModificarRevisionJSP, model, 0, 10);

		model.addAttribute("buscadorModificarRevision", buscadorModificarRevisionJSP);
		return "buscadorModificarRevision";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorModificarRevision", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorModificarRevision(BuscadorModificarRevisionJSP buscadorModificarRevisionJSP,
			Model model) {
		model.addAttribute("buscadorModificarRevision", new BuscadorModificarRevisionJSP());
		cargarListaValores(new BuscadorModificarRevisionJSP(), model, 0, 10);
		return "buscadorModificarRevision";
	}

	/**
	 * Metodo que te devuelve a la pantalla de buscador modificar formacion
	 * 
	 * @param modificarFormacionJSP
	 * @param model
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverRevision", method = RequestMethod.POST)
	public String volverCurso(ModificarRevisionJSP modificarRevisionJSP, Model model) {

		model.addAttribute("buscadorModificarRevision", new BuscadorModificarRevisionJSP());
		cargarListaValores(new BuscadorModificarRevisionJSP(), model, 0, 10);
		return "buscadorModificarRevision";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorModificarRevision", method = RequestMethod.POST)
	public String paginacionTablaBuscadorModificarRevision(BuscadorModificarRevisionJSP buscadorModificarRevisionJSP,
			Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorModificarRevisionJSP.getPage())) {
			buscadorModificarRevisionJSP.setPage(buscadorModificarRevisionJSP.getPage().substring(
					buscadorModificarRevisionJSP.getPage().indexOf(",") + 1,
					buscadorModificarRevisionJSP.getPage().length()));
			page = Integer.parseInt(buscadorModificarRevisionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorModificarRevision", buscadorModificarRevisionJSP);
		cargarListaValores(buscadorModificarRevisionJSP, model, page, size);
		return "buscadorModificarRevision";
	}

	@RequestMapping(value = "/ordenTablaBuscadorModificarRevision", method = RequestMethod.POST)
	public String ordenTablaBuscadorModificarRevision(BuscadorModificarRevisionJSP buscadorModificarRevisionJSP,
			Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorModificarRevisionJSP.getPage())) {
			page = Integer.parseInt(buscadorModificarRevisionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorModificarRevision", buscadorModificarRevisionJSP);
		cargarListaValores(buscadorModificarRevisionJSP, model, page, size);
		return "buscadorModificarRevision";
	}

	private void cargarListaValores(BuscadorModificarRevisionJSP buscadorModificarRevisionJSP, Model model, int page,
			int size) {

		if (StringUtils.isNotBlank(buscadorModificarRevisionJSP.getOrder())
				&& "nombreCompleto".equals(buscadorModificarRevisionJSP.getOrder())) {
			Direction direccion = Direction.DESC;
			if ("asc".equals(buscadorModificarRevisionJSP.getDireccion())) {
				direccion = Direction.ASC;
			}

			model.addAttribute("revisiones",
					revisionPsicoDAO.findRevisionPsicoByFilters(buscadorModificarRevisionJSP.getIdCentroMedico(),
							buscadorModificarRevisionJSP.getDni(), buscadorModificarRevisionJSP.getFechaRevision(),
							PageRequest.of(page, size, direccion,  "personal.apellido1", "personal.nombre")));
			return;
		}

		Order order = Order.desc("idRevisionPsico");
		if (StringUtils.isNotBlank(buscadorModificarRevisionJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorModificarRevisionJSP.getDireccion())) {

			if ("asc".equals(buscadorModificarRevisionJSP.getDireccion())) {
				order = Order.asc(buscadorModificarRevisionJSP.getOrder());
			} else {
				order = Order.desc(buscadorModificarRevisionJSP.getOrder());
			}

		}

		model.addAttribute("revisiones",
				revisionPsicoDAO.findRevisionPsicoByFilters(buscadorModificarRevisionJSP.getIdCentroMedico(),
						buscadorModificarRevisionJSP.getDni(), buscadorModificarRevisionJSP.getFechaRevision(),
						PageRequest.of(page, size, Sort.by(order))));
	}

}