package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Opcion;
import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.domain.TipoPermiso;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorRolesJSP;
import eu.eurogestion.ese.pojo.DetalleRolJSP;
import eu.eurogestion.ese.repository.OpcionDAO;
import eu.eurogestion.ese.repository.RolDAO;
import eu.eurogestion.ese.repository.RolPermisoDAO;
import eu.eurogestion.ese.repository.TipoPermisoDAO;
import eu.eurogestion.ese.services.RolPermisoService;
import eu.eurogestion.ese.services.RolService;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class DetalleRolController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public RolDAO rolDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public RolPermisoDAO rolPermisoDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public RolService rolService;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TipoPermisoDAO tipoPermisoDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public OpcionDAO opcionDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public RolPermisoService rolPermisoService;

	/**
	 * Metodo que devuelve una lista de Personas para una tabla
	 * 
	 * @return lista de objetos PersonalTablaJSP
	 */
	@ModelAttribute("tiposPermiso")
	public List<TipoPermiso> listTipoPermisolAll() {
		List<TipoPermiso> lista = new ArrayList<>();
		TipoPermiso tramo = new TipoPermiso();
		tramo.setValor("Selecciona uno:");
		lista.add(tramo);
		lista.addAll(tipoPermisoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleRol", method = RequestMethod.POST)
	public String verDetalleRol(BuscadorRolesJSP buscadorRolesJSP, Model model) {

		model.addAttribute("detalleRol",
				convertRolToDetalleRolJSP(rolDAO.getOne(Integer.parseInt(buscadorRolesJSP.getIdRol())), Boolean.TRUE));
		cargarPermisos(buscadorRolesJSP.getIdRol(), model, 0, 5);
		return "detalleRol";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarDetalleRol", method = RequestMethod.POST)
	public String modificarDetalleRol(BuscadorRolesJSP buscadorRolesJSP, Model model) {

		model.addAttribute("detalleRol",
				convertRolToDetalleRolJSP(rolDAO.getOne(Integer.parseInt(buscadorRolesJSP.getIdRol())), Boolean.FALSE));
		cargarPermisos(buscadorRolesJSP.getIdRol(), model, 0, 5);
		cargarOpciones(buscadorRolesJSP.getIdRol(), model);
		return "detalleRol";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/nuevoRol", method = RequestMethod.GET)
	public String nuevoRol(Model model) {

		DetalleRolJSP detalleRolJSP = new DetalleRolJSP();
		detalleRolJSP.setLectura(Boolean.FALSE);
		model.addAttribute("detalleRol", detalleRolJSP);

		return "detalleRol";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/crearRol", method = RequestMethod.POST)
	public String crearRol(DetalleRolJSP detalleRolJSP, Model model) {

		Rol rol;
		try {
			rol = rolService.crearRol(detalleRolJSP);
		} catch (EseException e) {
			// TODO falta añadir error
			detalleRolJSP.setIdRol(null);
			log.error(e.getMessage());
			model.addAttribute("detalleRol", detalleRolJSP);
			return "detalleRol";
		}
		detalleRolJSP.setIdRol(rol.getIdRol().toString());
		cargarPermisos(detalleRolJSP.getIdRol(), model, 0, 5);
		cargarOpciones(detalleRolJSP.getIdRol(), model);
		model.addAttribute("detalleRol", detalleRolJSP);
		model.addAttribute("info", " Se ha creado correctamente");
		return "detalleRol";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarDetalleRol", method = RequestMethod.POST)
	public String guardarDetalleRol(DetalleRolJSP detalleRolJSP, Model model) {

		rolService.guardarRol(detalleRolJSP);
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleRolJSP.getPage())) {
			page = Integer.parseInt(detalleRolJSP.getPage()) - 1;
		}

		model.addAttribute("detalleRol", detalleRolJSP);
		cargarPermisos(detalleRolJSP.getIdRol(), model, page, size);
		cargarOpciones(detalleRolJSP.getIdRol(), model);

		model.addAttribute("info", "Se ha guardado correctamente");
		return "detalleRol";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorComposicionJSP objeto con los campos de filtro de la pantalla
	 * @param model                  objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/eliminarPermisoRol", method = RequestMethod.POST)
	public String eliminarPermisoRol(DetalleRolJSP detalleRolJSP, Model model) {

		rolPermisoService.eliminarPermisoRol(detalleRolJSP);

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleRolJSP.getPage())) {
			Long elementosTotales = rolPermisoDAO.countAllByIdRol(detalleRolJSP.getIdRol());

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(detalleRolJSP.getPage());
			if (totalPaginas < pageJSP) {
				detalleRolJSP.setPage(String.valueOf(pageJSP - 1));
			}
			page = Integer.parseInt(detalleRolJSP.getPage()) - 1;
		}

		cargarPermisos(detalleRolJSP.getIdRol(), model, page, size);
		cargarOpciones(detalleRolJSP.getIdRol(), model);
		model.addAttribute("detalleRol", detalleRolJSP);
		model.addAttribute("info", "Se ha eliminado correctamente");
		return "detalleRol";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorComposicionJSP objeto con los campos de filtro de la pantalla
	 * @param model                  objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/cambiarPermisoRol", method = RequestMethod.POST)
	public String cambiarPermisoRol(DetalleRolJSP detalleRolJSP, Model model) {

		rolPermisoService.cambiarPermisoRol(detalleRolJSP);

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleRolJSP.getPage())) {
			page = Integer.parseInt(detalleRolJSP.getPage()) - 1;
		}

		model.addAttribute("detalleRol", detalleRolJSP);
		cargarPermisos(detalleRolJSP.getIdRol(), model, page, size);
		cargarOpciones(detalleRolJSP.getIdRol(), model);
		model.addAttribute("info", "Se ha cambiado correctamente");
		return "detalleRol";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorComposicionJSP objeto con los campos de filtro de la pantalla
	 * @param model                  objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/addRolPermiso", method = RequestMethod.POST)
	public String addRolPermiso(DetalleRolJSP detalleRolJSP, Model model) {

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleRolJSP.getPage())) {
			page = Integer.parseInt(detalleRolJSP.getPage()) - 1;
		}

		if (StringUtils.isBlank(detalleRolJSP.getIdOpcion()) || StringUtils.isBlank(detalleRolJSP.getIdTipoPermiso())) {

			model.addAttribute("error", "La opción y el tipo de permiso es obligatorio");
			model.addAttribute("detalleRol", detalleRolJSP);
			cargarOpciones(detalleRolJSP.getIdRol(), model);
			cargarPermisos(detalleRolJSP.getIdRol(), model, page, size);

			return "detalleRol";

		}

		rolPermisoService.addRolPermiso(detalleRolJSP);
		detalleRolJSP.setIdOpcion(null);
		detalleRolJSP.setIdTipoPermiso(null);
		model.addAttribute("detalleRol", detalleRolJSP);
		cargarPermisos(detalleRolJSP.getIdRol(), model, page, size);
		cargarOpciones(detalleRolJSP.getIdRol(), model);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "detalleRol";
	}

	@RequestMapping(value = "/paginacionTablaDetalleRol", method = RequestMethod.POST)
	public String paginacionTablaDetalleRol(DetalleRolJSP detalleRolJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleRolJSP.getPage())) {
			detalleRolJSP.setPage(detalleRolJSP.getPage().substring(detalleRolJSP.getPage().indexOf(",") + 1,
					detalleRolJSP.getPage().length()));
			page = Integer.parseInt(detalleRolJSP.getPage()) - 1;
		}

		model.addAttribute("detalleRol", detalleRolJSP);
		cargarPermisos(detalleRolJSP.getIdRol(), model, page, size);
		cargarOpciones(detalleRolJSP.getIdRol(), model);

		return "detalleRol";
	}

	private DetalleRolJSP convertRolToDetalleRolJSP(Rol rol, Boolean lectura) {

		DetalleRolJSP detalleRolJSP = new DetalleRolJSP();
		detalleRolJSP.setIdRol(rol.getIdRol().toString());
		detalleRolJSP.setLectura(lectura);
		detalleRolJSP.setNombre(rol.getNombre());
		return detalleRolJSP;
	}

	private void cargarPermisos(String idRol, Model model, int page, int size) {
		model.addAttribute("rolPermisos", rolPermisoDAO.findAllByIdRol(idRol, PageRequest.of(page, size)));
	}

	private void cargarOpciones(String idRol, Model model) {
		List<Opcion> lista = new ArrayList<>();
		Opcion tramo = new Opcion();
		tramo.setDescripcion("Selecciona uno:");
		lista.add(tramo);
		lista.addAll(opcionDAO.findAllByIdRol(idRol));

		model.addAttribute("opciones", lista);
	}

}