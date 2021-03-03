package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.CursoAlumno;
import eu.eurogestion.ese.domain.EstadoCursoAlumno;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleFormacionJSP;
import eu.eurogestion.ese.pojo.ModificarFormacionCursoAlumnoJSP;
import eu.eurogestion.ese.repository.CursoAlumnoDAO;
import eu.eurogestion.ese.repository.EstadoCursoAlumnoDAO;
import eu.eurogestion.ese.services.ModificarFormacionCursoAlumnoService;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ModificarFormacionCursoAlumnoController {

	/**
	 * Repositorio de la clase de dominio EstadoCursoAlumno
	 */
	@Autowired
	public EstadoCursoAlumnoDAO estadoCursoAlumnoDAO;

	/**
	 * Repositorio de la clase de dominio CursoAlumno
	 */
	@Autowired
	public CursoAlumnoDAO cursoAlumnoDAO;

	/**
	 * Servicio de la modificacion de la formacion
	 */
	@Autowired
	public ModificarFormacionCursoAlumnoService modificarFormacionCursoAlumnoService;

	/**
	 * Metodo que devuelve una lista de Cursos para una tabla
	 * 
	 * @return lista de objetos Curso
	 */
	@ModelAttribute("resoluciones")
	public List<EstadoCursoAlumno> listMaterialesAll() {
		List<EstadoCursoAlumno> lista = new ArrayList<>();
		lista.addAll(estadoCursoAlumnoDAO.findAllById(
				Arrays.asList(Constantes.ESTADO_CURSO_ALUMNO_SUPERADO, Constantes.ESTADO_CURSO_ALUMNO_NO_SUPERADO)));
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarFormacionCursoAlumno", method = RequestMethod.POST)
	public String modificarFormacion(DetalleFormacionJSP detalleFormacionJSP, Model model) {
		CursoAlumno curso = cursoAlumnoDAO.getOne(Integer.parseInt(detalleFormacionJSP.getIdCursoAlumno()));
		model.addAttribute("modificarFormacionCursoAlumno",
				convertCursoAlumnoToModificarFormacionCursoAlumnoJSP(curso));
		return "modificarFormacionCursoAlumno";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/aceptarModificarCursoAlumno", method = RequestMethod.POST)
	public String superarCursoAlumno(ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumnoJSP, Model model,
			HttpSession sesion) {

		try {
			if (Constantes.ESTADO_CURSO_ALUMNO_SUPERADO.toString()
					.equals(modificarFormacionCursoAlumnoJSP.getResolucion())) {
				modificarFormacionCursoAlumnoService.superarCursoAlumno(modificarFormacionCursoAlumnoJSP, sesion);
			} else {
				modificarFormacionCursoAlumnoService.noSuperarCursoAlumno(modificarFormacionCursoAlumnoJSP);
			}
		} catch (EseException e) {
			model.addAttribute("modificarFormacionCursoAlumno", modificarFormacionCursoAlumnoJSP);
			log.error(e.getMessage());
			return "modificarFormacionCursoAlumno";
		}

		return "forward:/volverSuperarNoSuperarModificarFormacionCursoAlumno";
	}

	/**
	 * Metodo que mapea el objeto de dominio Curso en el objeto
	 * ModificarFormacionJSP
	 * 
	 * @param cursoAlumno
	 * @return Objeto ModificarFormacionJSP mapeado
	 */
	private ModificarFormacionCursoAlumnoJSP convertCursoAlumnoToModificarFormacionCursoAlumnoJSP(
			CursoAlumno cursoAlumno) {
		ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumno = new ModificarFormacionCursoAlumnoJSP();
		modificarFormacionCursoAlumno.setIdCursoAlumno(cursoAlumno.getIdCursoAlumno().toString());
		modificarFormacionCursoAlumno.setIdPersonal(cursoAlumno.getPersonal().getIdPersonal().toString());
		modificarFormacionCursoAlumno.setEstado(cursoAlumno.getCurso().getEstadoCurso().getValor());
		modificarFormacionCursoAlumno.setTitulo(cursoAlumno.getCurso().getTituloCurso());
		modificarFormacionCursoAlumno.setCentroFormacion(cursoAlumno.getCurso().getCentroFor().getNombre());

		return modificarFormacionCursoAlumno;
	}
}