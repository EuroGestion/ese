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

import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.pojo.ProgramarFormacionJSP;
import eu.eurogestion.ese.pojo.ProgramarRevisionJSP;
import eu.eurogestion.ese.pojo.SelectPersonalFormRevJSP;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class SelectPersonalFormRevController {

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
	@RequestMapping(value = "/selectUserFormacion", method = RequestMethod.GET)
	public String selectUserFormacion(Model model) {

		SelectPersonalFormRevJSP selectPersonalFormRevJSP = new SelectPersonalFormRevJSP();
		selectPersonalFormRevJSP.setIsFormacion(Boolean.TRUE);
		cargarLista(selectPersonalFormRevJSP, model, 0, 0, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRevJSP);
		return "selectUserFormRev";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/selectUserRevision", method = RequestMethod.GET)
	public String selectUserRevision(Model model) {

		SelectPersonalFormRevJSP selectPersonalFormRevJSP = new SelectPersonalFormRevJSP();
		selectPersonalFormRevJSP.setIsRevision(Boolean.TRUE);
		cargarLista(selectPersonalFormRevJSP, model, 0, 0, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRevJSP);
		return "selectUserFormRev";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param selectPersonalFormRev objeto con los campos de filtro de la pantalla
	 * @param model                 objeto model de la pantalla
	 * @param session               objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarFormRev", method = RequestMethod.POST)
	public String filtrarPersonal(SelectPersonalFormRevJSP selectPersonalFormRev, Model model, HttpSession session) {

		int pageSeleccionados = 0;

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageSeleccionados())) {
			pageSeleccionados = Integer.parseInt(selectPersonalFormRev.getPageSeleccionados()) - 1;
		}
		cargarLista(selectPersonalFormRev, model, 0, pageSeleccionados, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
		return "selectUserFormRev";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param formularioPersonal objeto con los campos de filtro de la pantalla
	 * @param model              objeto model de la pantalla
	 * @param session            objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosFormRev", method = RequestMethod.POST)
	public String borrarFiltrosPersonal(SelectPersonalFormRevJSP selectPersonalFormRev, Model model,
			HttpSession session) {

		SelectPersonalFormRevJSP selectPersonalFormRevJSP = new SelectPersonalFormRevJSP();
		selectPersonalFormRevJSP.setListaPersonalTotal(selectPersonalFormRev.getListaPersonalTotal());

		selectPersonalFormRevJSP.setIsFormacion(selectPersonalFormRev.getIsFormacion());
		selectPersonalFormRevJSP.setIsRevision(selectPersonalFormRev.getIsRevision());

		int pageSeleccionados = 0;

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageSeleccionados())) {
			pageSeleccionados = Integer.parseInt(selectPersonalFormRev.getPageSeleccionados()) - 1;
		}
		cargarLista(selectPersonalFormRevJSP, model, 0, pageSeleccionados, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRevJSP);

		return "selectUserFormRev";
	}

	@RequestMapping(value = "/volverFormacionSelectUser", method = RequestMethod.POST)
	public String volverprogramarFormacion(ProgramarFormacionJSP programarFormacion, Model model) {
		SelectPersonalFormRevJSP selectPersonalFormRev = convertProgramarFormacionJSPToSelectPersonalFormRevJSP(
				programarFormacion);

		cargarLista(selectPersonalFormRev, model, 0, 0, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
		return "selectUserFormRev";
	}

	@RequestMapping(value = "/volverRevisionSelectUser", method = RequestMethod.POST)
	public String volverProgramarRevicion(ProgramarRevisionJSP programarRevision, Model model) {
		SelectPersonalFormRevJSP selectPersonalFormRev = convertProgramarRevisionJSPToSelectPersonalFormRevJSP(
				programarRevision);

		cargarLista(selectPersonalFormRev, model, 0, 0, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
		return "selectUserFormRev";
	}

	@RequestMapping(value = "/errorValidacionFormacionRevision", method = RequestMethod.POST)
	public String volverprogramarFormacion1(SelectPersonalFormRevJSP selectPersonalFormRev, Model model) {

		int pageTotal = 0; // default page number is 0 (yes it is weird)
		int pageSeleccionados = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageSeleccionados())) {
			pageSeleccionados = Integer.parseInt(selectPersonalFormRev.getPageSeleccionados()) - 1;
		}

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageTotal())) {
			pageTotal = Integer.parseInt(selectPersonalFormRev.getPageTotal()) - 1;
		}
		cargarLista(selectPersonalFormRev, model, pageTotal, pageSeleccionados, size);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
		return "selectUserFormRev";
	}

	@RequestMapping(value = "/paginacionTablaSelectUserFormRevTotal", method = RequestMethod.POST)
	public String paginacionTablaSelectUserFormRevTotal(SelectPersonalFormRevJSP selectPersonalFormRevJSP, Model model,
			HttpSession session) {
		int pageTotal = 0; // default page number is 0 (yes it is weird)
		int pageSeleccionados = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(selectPersonalFormRevJSP.getPageTotal())) {
			selectPersonalFormRevJSP.setPageTotal(selectPersonalFormRevJSP.getPageTotal().substring(
					selectPersonalFormRevJSP.getPageTotal().indexOf(",") + 1,
					selectPersonalFormRevJSP.getPageTotal().length()));
			pageTotal = Integer.parseInt(selectPersonalFormRevJSP.getPageTotal()) - 1;
		}

		if (StringUtils.isNotBlank(selectPersonalFormRevJSP.getPageSeleccionados())) {
			pageSeleccionados = Integer.parseInt(selectPersonalFormRevJSP.getPageSeleccionados()) - 1;
		}

		model.addAttribute("selectPersonalFormRev", selectPersonalFormRevJSP);
		cargarLista(selectPersonalFormRevJSP, model, pageTotal, pageSeleccionados, size);
		return "selectUserFormRev";
	}

	@RequestMapping(value = "/paginacionTablaSelectUserFormRevSeleccionados", method = RequestMethod.POST)
	public String paginacionTablaSelectUserFormRevSeleccionados(SelectPersonalFormRevJSP selectPersonalFormRevJSP,
			Model model, HttpSession session) {
		int pageTotal = 0; // default page number is 0 (yes it is weird)
		int pageSeleccionados = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(selectPersonalFormRevJSP.getPageSeleccionados())) {
			selectPersonalFormRevJSP.setPageSeleccionados(selectPersonalFormRevJSP.getPageSeleccionados().substring(
					selectPersonalFormRevJSP.getPageSeleccionados().indexOf(",") + 1,
					selectPersonalFormRevJSP.getPageSeleccionados().length()));
			pageSeleccionados = Integer.parseInt(selectPersonalFormRevJSP.getPageSeleccionados()) - 1;
		}

		if (StringUtils.isNotBlank(selectPersonalFormRevJSP.getPageTotal())) {
			pageTotal = Integer.parseInt(selectPersonalFormRevJSP.getPageTotal()) - 1;
		}

		model.addAttribute("selectPersonalFormRev", selectPersonalFormRevJSP);
		cargarLista(selectPersonalFormRevJSP, model, pageTotal, pageSeleccionados, size);
		return "selectUserFormRev";
	}

	@RequestMapping(value = "/addPersonalSelectFormRev", method = RequestMethod.POST)
	public String addPersonalSelectFormRev(SelectPersonalFormRevJSP selectPersonalFormRev, Model model,
			HttpSession session) {

		selectPersonalFormRev.getListaPersonalTotal().add(selectPersonalFormRev.getIdPersonal());

		int pageSeleccionados = 0; // default page number is 0 (yes it is weird)
		int pageTotal = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageSeleccionados())) {
			pageSeleccionados = Integer.parseInt(selectPersonalFormRev.getPageSeleccionados()) - 1;

		}

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageTotal())) {
			Long elementosTotales = personalDAO.countPlanificarFormRevByFiltersTotales(selectPersonalFormRev.getDni(),
					selectPersonalFormRev.getIdCargo(), selectPersonalFormRev.getNombre(),
					selectPersonalFormRev.getApellido(), selectPersonalFormRev.getListaPersonalTotal());

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(selectPersonalFormRev.getPageTotal());
			if (totalPaginas < pageJSP) {
				selectPersonalFormRev.setPageTotal(String.valueOf(pageJSP - 1));
			}
			pageTotal = Integer.parseInt(selectPersonalFormRev.getPageTotal()) - 1;
		}
		cargarLista(selectPersonalFormRev, model, pageTotal, pageSeleccionados, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
		model.addAttribute("info"," Se ha añadido correctamente");
		return "selectUserFormRev";
	}

	@RequestMapping(value = "/eliminarPersonalSelectFormRev", method = RequestMethod.POST)
	public String eliminarPersonalSelectFormRev(SelectPersonalFormRevJSP selectPersonalFormRev, Model model,
			HttpSession session) {

		selectPersonalFormRev.getListaPersonalTotal().remove(selectPersonalFormRev.getIdPersonal());

		int pageSeleccionados = 0; // default page number is 0 (yes it is weird)
		int pageTotal = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageTotal())) {
			pageTotal = Integer.parseInt(selectPersonalFormRev.getPageTotal()) - 1;

		}

		if (StringUtils.isNotBlank(selectPersonalFormRev.getPageSeleccionados())) {
			Long elementosTotales = personalDAO
					.countPlanificarFormRevByFiltersSeleccionados(selectPersonalFormRev.getListaPersonalTotal());

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(selectPersonalFormRev.getPageSeleccionados());
			if (totalPaginas < pageJSP) {
				selectPersonalFormRev.setPageSeleccionados(String.valueOf(pageJSP - 1));
			}
			pageSeleccionados = Integer.parseInt(selectPersonalFormRev.getPageSeleccionados()) - 1;
		}
		cargarLista(selectPersonalFormRev, model, pageTotal, pageSeleccionados, 5);
		model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
		model.addAttribute("info","Se ha eliminado correctamente");
		return "selectUserFormRev";
	}

	private SelectPersonalFormRevJSP convertProgramarFormacionJSPToSelectPersonalFormRevJSP(
			ProgramarFormacionJSP programarFormacion) {
		SelectPersonalFormRevJSP selectPersonalFormRevJSP = new SelectPersonalFormRevJSP();
		selectPersonalFormRevJSP.setIdCargo(programarFormacion.getIdCargoSelect());
		selectPersonalFormRevJSP.setNombre(programarFormacion.getNombreSelect());
		selectPersonalFormRevJSP.setApellido(programarFormacion.getApellidoSelect());
		selectPersonalFormRevJSP.setDni(programarFormacion.getDniSelect());
		selectPersonalFormRevJSP.setListaPersonalTotal(programarFormacion.getListaPersonalTotalSelect());
		selectPersonalFormRevJSP.setIsFormacion(programarFormacion.getIsFormacionSelect());
		selectPersonalFormRevJSP.setIsRevision(programarFormacion.getIsRevisionSelect());
		return selectPersonalFormRevJSP;

	}

	private SelectPersonalFormRevJSP convertProgramarRevisionJSPToSelectPersonalFormRevJSP(
			ProgramarRevisionJSP programarRevision) {
		SelectPersonalFormRevJSP selectPersonalFormRevJSP = new SelectPersonalFormRevJSP();
		selectPersonalFormRevJSP.setIdCargo(programarRevision.getIdCargoSelect());
		selectPersonalFormRevJSP.setNombre(programarRevision.getNombreSelect());
		selectPersonalFormRevJSP.setApellido(programarRevision.getApellidoSelect());
		selectPersonalFormRevJSP.setDni(programarRevision.getDniSelect());
		selectPersonalFormRevJSP.setListaPersonalTotal(programarRevision.getListaPersonalTotalSelect());
		selectPersonalFormRevJSP.setIsFormacion(programarRevision.getIsFormacionSelect());
		selectPersonalFormRevJSP.setIsRevision(programarRevision.getIsRevisionSelect());
		return selectPersonalFormRevJSP;

	}

	private void cargarLista(SelectPersonalFormRevJSP selectPersonalFormRev, Model model, int pageTotal,
			int pageSeleccionados, int size) {
		model.addAttribute("personasTotales",
				personalDAO.findPlanificarFormRevByFiltersTotales(selectPersonalFormRev.getDni(),
						selectPersonalFormRev.getIdCargo(), selectPersonalFormRev.getNombre(),
						selectPersonalFormRev.getApellido(), selectPersonalFormRev.getListaPersonalTotal(),
						PageRequest.of(pageTotal, size)));
		model.addAttribute("personasSeleccionadas", personalDAO.findPlanificarFormRevByFiltersSeleccionados(
				selectPersonalFormRev.getListaPersonalTotal(), PageRequest.of(pageSeleccionados, size)));
	}

}