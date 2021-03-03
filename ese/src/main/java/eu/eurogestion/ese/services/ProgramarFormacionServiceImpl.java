package eu.eurogestion.ese.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.CursoAlumno;
import eu.eurogestion.ese.domain.EstadoCurso;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ProgramarFormacionJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.CursoAlumnoDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.EstadoCursoAlumnoDAO;
import eu.eurogestion.ese.repository.EstadoCursoDAO;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoCursoDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ProgramarFormacionServiceImpl implements ProgramarFormacionService {

	/** Repositories & Services **/

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public ModeloMaterialDAO modeloMaterialDAO;

	@Autowired
	public TipoCursoDAO tipoCursoDAO;

	@Autowired
	public CursoDAO cursoDAO;

	@Autowired
	public CursoAlumnoDAO cursoAlumnoDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public EstadoCursoDAO estadoCursoDAO;

	@Autowired
	public EstadoCursoAlumnoDAO estadoCursoAlumnoDAO;

	/** Functions **/

	@Override
	public void crearCurso(ProgramarFormacionJSP programarFormacion) throws EseException {
		Curso curso = new Curso();
		curso.setTipoCurso(tipoCursoDAO.getOne(Integer.parseInt(programarFormacion.getIdTipoCurso())));
		curso.setCentroFor(companiaDAO.getOne(Integer.parseInt(programarFormacion.getIdCentroFormacion())));
		curso.setTituloCurso(programarFormacion.getTitulo());

		if (programarFormacion.getIsInfraestructura()) {
			curso.setInfraestructura(programarFormacion.getInfraestructura());
		} else if (programarFormacion.getIsMaterial()) {
			curso.setModeloMaterial(modeloMaterialDAO.getOne(Integer.parseInt(programarFormacion.getIdMaterial())));
		} else {
			curso.setInfraestructura(programarFormacion.getNormativa());
		}

		if (StringUtils.isNotBlank(programarFormacion.getHoras())) {
			curso.setNumeroHoras(Float.parseFloat(programarFormacion.getHoras()));
		}
		if (StringUtils.isNotBlank(programarFormacion.getObservaciones())) {

			curso.setObservaciones(programarFormacion.getObservaciones());
		}
		if (StringUtils.isNotBlank(programarFormacion.getFechaInicio())) {
			curso.setFechaInicio(Utiles.parseDatePantalla(programarFormacion.getFechaInicio()));
		}
		if (StringUtils.isNotBlank(programarFormacion.getFechaFin())) {
			curso.setFechaFin(Utiles.parseDatePantalla(programarFormacion.getFechaFin()));
		}

		Integer idEstadoCurso = Constantes.ESTADO_CURSO_CREADO;
		boolean isPlaned = StringUtils.isNotBlank(programarFormacion.getFechaFin());
		if (isPlaned) {
			idEstadoCurso = Constantes.ESTADO_CURSO_PLANIFICADO;
		}
		EstadoCurso estadoCurso = estadoCursoDAO.getOne(idEstadoCurso);
		curso.setEstadoCurso(estadoCurso);
		Curso cursoBBDD = cursoDAO.save(curso);

		for (String idPersonal : programarFormacion.getListaPersonalTotalSelect()) {
			CursoAlumno cursoAlumno = new CursoAlumno();
			Personal personal = personalDAO.getOne(Integer.parseInt(idPersonal));
			cursoAlumno.setCurso(cursoBBDD);
			cursoAlumno.setPersonal(personal);
			cursoAlumno.setEstadoCursoAlumno(estadoCursoAlumnoDAO.getOne(Constantes.ESTADO_CURSO_ALUMNO_INSCRITO));

			cursoAlumnoDAO.save(cursoAlumno);
		}

	}

}
