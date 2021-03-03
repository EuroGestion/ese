package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.EstadoCurso;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorModificarFormacionJSP;
import eu.eurogestion.ese.pojo.ModificarFormacionJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.EstadoCursoDAO;
import eu.eurogestion.ese.services.ModificarFormacionService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class ModificarFornacionController {

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
	 * Servicio de la modificacion de la formacion
	 */
	@Autowired
	public ModificarFormacionService modificarFormacionService;

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
	 * Metodo que devuelve una lista de Cursos para una tabla
	 * 
	 * @return lista de objetos Curso
	 */
	@ModelAttribute("cursos")
	public List<Curso> listMaterialesAll() {
		List<Curso> lista = new ArrayList<>();
		lista.addAll(cursoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarFormacion", method = RequestMethod.POST)
	public String modificarFormacion(BuscadorModificarFormacionJSP buscadorModificarFormacionJSP, Model model) {
		Curso curso = cursoDAO.getOne(Integer.parseInt(buscadorModificarFormacionJSP.getIdCurso()));
		model.addAttribute("modificarFormacion", convertCursoToModificarFormacionJSP(curso));
		return "modificarFormacion";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/planificarCurso", method = RequestMethod.POST)
	public String estadoPlanificarCurso(ModificarFormacionJSP modificarFormacionJSP, Model model) {

		Curso curso;
		try {
			curso = modificarFormacionService.planificarCurso(modificarFormacionJSP);
		} catch (EseException e) {
			model.addAttribute("modificarFormacion", modificarFormacionJSP);
			log.error(e.getMessage());
			return "modificarFormacion";
		}

		model.addAttribute("modificarFormacion", convertCursoToModificarFormacionJSP(curso));
		model.addAttribute("info", " Se ha creado correctamente");
		return "modificarFormacion";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/enviarCentroFormacionCurso", method = RequestMethod.POST)
	public String enviarCentroFormacionCurso(ModificarFormacionJSP modificarFormacionJSP, Model model,
			HttpSession sesion) {

		Curso curso;
		try {
			curso = modificarFormacionService.enviarCFCurso(modificarFormacionJSP, sesion);
		} catch (EseException e) {
			model.addAttribute("modificarFormacion", modificarFormacionJSP);
			log.error(e.getMessage());
			return "modificarFormacion";
		}
		model.addAttribute("info", " Se ha añadido correctamente");
		model.addAttribute("modificarFormacion", convertCursoToModificarFormacionJSP(curso));
		return "modificarFormacion";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/aprobarCurso", method = RequestMethod.POST)
	public String aprobarCurso(ModificarFormacionJSP modificarFormacionJSP, Model model, HttpSession sesion) {

		try {
			modificarFormacionService.aprobarCurso(modificarFormacionJSP, sesion);
		} catch (EseException e) {
			model.addAttribute("modificarFormacion", modificarFormacionJSP);
			log.error(e.getMessage());
			return "modificarFormacion";
		}
		return "redirect:/buscadorModificarFormacionModificarFormacionAprobar";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/noAprobarCurso", method = RequestMethod.POST)
	public String noAprovarCurso(ModificarFormacionJSP modificarFormacionJSP, Model model) {

		try {
			modificarFormacionService.borrarCurso(modificarFormacionJSP);
		} catch (EseException e) {
			model.addAttribute("modificarFormacion", modificarFormacionJSP);
			log.error(e.getMessage());
			return "modificarFormacion";
		}
		return "redirect:/buscadorModificarFormacionModificarFormacionNoAprobar";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/anularCurso", method = RequestMethod.POST)
	public String anularCurso(ModificarFormacionJSP modificarFormacionJSP, Model model) {

		try {
			modificarFormacionService.borrarCurso(modificarFormacionJSP);
		} catch (EseException e) {
			model.addAttribute("modificarFormacion", modificarFormacionJSP);
			log.error(e.getMessage());
			return "modificarFormacion";
		}
		return "redirect:/buscadorModificarFormacionModificarFormacionNoAprobar";
	}

	/**
	 * Metodo que mapea el objeto de dominio Curso en el objeto
	 * ModificarFormacionJSP
	 * 
	 * @param curso
	 * @return Objeto ModificarFormacionJSP mapeado
	 */
	private ModificarFormacionJSP convertCursoToModificarFormacionJSP(Curso curso) {
		ModificarFormacionJSP modificarFormacion = new ModificarFormacionJSP();
		modificarFormacion.setIdCurso(curso.getIdCurso().toString());
		modificarFormacion.setEstado(curso.getEstadoCurso().getValor());
		modificarFormacion.setTitulo(curso.getTituloCurso());
		modificarFormacion.setCentroFormacion(curso.getCentroFor().getNombre());
		if (curso.getFechaInicio() != null) {
			modificarFormacion.setFechaInicio(
					Utiles.convertDateToString(curso.getFechaInicio(), Constantes.FORMATO_FECHA_PANTALLA));
		}
		if (curso.getFechaFin() != null) {
			modificarFormacion
					.setFechaFin(Utiles.convertDateToString(curso.getFechaFin(), Constantes.FORMATO_FECHA_PANTALLA));
		}
		return modificarFormacion;
	}
}