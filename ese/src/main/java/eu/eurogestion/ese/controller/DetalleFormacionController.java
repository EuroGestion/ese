package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.CursoAlumno;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.pojo.DetalleFormacionJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.repository.CursoAlumnoDAO;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class DetalleFormacionController {

	/**
	 * Repositorio de la clase de dominio CursoAlumno
	 */
	@Autowired
	public CursoAlumnoDAO cursoAlumnoDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleCurso", method = RequestMethod.POST)
	public String verDetallePersonal(DetallePersonalJSP detallePersonalJSP, Model model) {

		model.addAttribute("detalleFormacion", convertCursoAlumnoToDetalleFormacionJSP(
				cursoAlumnoDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdCursoAlumno()))));

		return "detalleFormacion";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/descargarEvidenciaEnvioSolicitud", method = RequestMethod.POST)
	public void descargarEvidenciaEnvioSolicitud(DetalleFormacionJSP detalleFormacionJSP, Model model,
			HttpServletResponse response) {
		try {
			CursoAlumno cursoAlumno = cursoAlumnoDAO.getOne(Integer.parseInt(detalleFormacionJSP.getIdCursoAlumno()));
			Documento documento = cursoAlumno.getCurso().getEvidencia1().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/descargarEvidenciaAprobarContenidos", method = RequestMethod.POST)
	public void descargarEvidenciaAprobarContenidos(DetalleFormacionJSP detalleFormacionJSP, Model model,
			HttpServletResponse response) {
		try {
			CursoAlumno cursoAlumno = cursoAlumnoDAO.getOne(Integer.parseInt(detalleFormacionJSP.getIdCursoAlumno()));
			Documento documento = cursoAlumno.getCurso().getEvidencia2().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/descargarEvidenciaSuperarCurso", method = RequestMethod.POST)
	public void descargarEvidenciaSuperarCurso(DetalleFormacionJSP detalleFormacionJSP, Model model,
			HttpServletResponse response) {
		try {
			CursoAlumno cursoAlumno = cursoAlumnoDAO.getOne(Integer.parseInt(detalleFormacionJSP.getIdCursoAlumno()));
			Documento documento = cursoAlumno.getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	private DetalleFormacionJSP convertCursoAlumnoToDetalleFormacionJSP(CursoAlumno cursoAlumno) {
		DetalleFormacionJSP detalleFormacionJSP = new DetalleFormacionJSP();
		Personal personal = cursoAlumno.getPersonal();
		Curso curso = cursoAlumno.getCurso();

		detalleFormacionJSP.setIdPersonal(personal.getIdPersonal().toString());
		detalleFormacionJSP.setIdCursoAlumno(cursoAlumno.getIdCursoAlumno().toString());

		String nombre = personal.getNombre() + " " + personal.getApellido1();
		if (StringUtils.isNotBlank(personal.getApellido2())) {
			nombre += " " + personal.getApellido2();
		}
		detalleFormacionJSP.setNombre(nombre);
		detalleFormacionJSP.setCargo(personal.getCargo().getNombre());
		detalleFormacionJSP.setTipoCurso(curso.getTipoCurso().getValor());
		detalleFormacionJSP.setTituloCurso(curso.getTituloCurso());
		detalleFormacionJSP.setEstadoCurso(cursoAlumno.getEstadoCursoAlumno().getValor());
		detalleFormacionJSP.setValidez(curso.getValidoDesde());
		detalleFormacionJSP.setFechaCaducidad(curso.getFechaCaducidad());
		if (curso.getNumeroHoras() != null) {
			detalleFormacionJSP.setHoras(curso.getNumeroHoras().toString());
		}
		switch (curso.getTipoCurso().getValor()) {
		case Constantes.TIPO_CURSO_MATERIAL_NOMBRE:
			detalleFormacionJSP
					.setDetalle(curso.getModeloMaterial().getSerie() + "." + curso.getModeloMaterial().getSubserie());
			break;
		default:
			detalleFormacionJSP.setDetalle(curso.getInfraestructura());

		}

		detalleFormacionJSP.setCentroFormacion(curso.getCentroFor().getNombre());
		detalleFormacionJSP.setFechaInicio(curso.getFechaInicio());
		detalleFormacionJSP.setFechaFin(curso.getFechaFin());
		detalleFormacionJSP.setObservaciones(curso.getObservaciones());
		if (Constantes.ESTADO_CURSO_APROBADO.equals(curso.getEstadoCurso().getIdEstadoCurso())) {
			if (cursoAlumno.getCurso().getEvidencia1() != null) {
				detalleFormacionJSP.setEvidenciaEnvioSolicitud(Boolean.TRUE);
			}
			if (cursoAlumno.getCurso().getEvidencia2() != null) {
				detalleFormacionJSP.setEvidenciaAprobacionContenido(Boolean.TRUE);
			}
			if (cursoAlumno.getEvidencia() != null) {
				detalleFormacionJSP.setEvidenciaSuperacion(Boolean.TRUE);
			}
		}

		return detalleFormacionJSP;
	}
}