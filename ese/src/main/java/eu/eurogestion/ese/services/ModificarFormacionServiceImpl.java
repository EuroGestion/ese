package eu.eurogestion.ese.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.CursoAlumno;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ModificarFormacionJSP;
import eu.eurogestion.ese.repository.CursoAlumnoDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoCursoAlumnoDAO;
import eu.eurogestion.ese.repository.EstadoCursoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ModificarFormacionServiceImpl implements ModificarFormacionService {

	/** Repositories & Services **/

	@Autowired
	public CursoDAO cursoDAO;

	@Autowired
	public CursoAlumnoDAO cursoAlumnoDAO;

	@Autowired
	public EstadoCursoAlumnoDAO estadoCursoAlumnoDAO;

	@Autowired
	public EstadoCursoDAO estadoCursoDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public Curso planificarCurso(ModificarFormacionJSP modificarFormacionJSP) throws EseException {

		Curso curso;
		Curso cursoBBDD;
		try {
			curso = cursoDAO.getOne(Integer.parseInt(modificarFormacionJSP.getIdCurso()));
			curso.setEstadoCurso(estadoCursoDAO.getOne(Constantes.ESTADO_CURSO_PLANIFICADO));
			curso.setFechaInicio(Utiles.parseDatePantalla(modificarFormacionJSP.getFechaInicio()));
			curso.setFechaFin(Utiles.parseDatePantalla(modificarFormacionJSP.getFechaFin()));
			cursoBBDD = cursoDAO.save(curso);
		} catch (RuntimeException e) {
			throw new EseException(e.getMessage());
		}

		return cursoBBDD;
	}

	@Override
	public Curso enviarCFCurso(ModificarFormacionJSP modificarFormacionJSP, HttpSession sesion) throws EseException {

		Evidencia evidencia = crearEvidencia(modificarFormacionJSP.getEvidencia(),
				modificarFormacionJSP.getFechaDocumento());

		Curso curso;
		Curso cursoBBDD;

		curso = cursoDAO.getOne(Integer.parseInt(modificarFormacionJSP.getIdCurso()));
		curso.setEstadoCurso(estadoCursoDAO.getOne(Constantes.ESTADO_CURSO_PENDIENTE_APROVACION));
		curso.setFechaInicio(Utiles.parseDatePantalla(modificarFormacionJSP.getFechaInicio()));
		curso.setFechaFin(Utiles.parseDatePantalla(modificarFormacionJSP.getFechaFin()));
		curso.setEvidencia1(evidencia);

		cursoBBDD = cursoDAO.save(curso);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		return cursoBBDD;
	}

	@Override
	public Curso aprobarCurso(ModificarFormacionJSP modificarFormacionJSP, HttpSession sesion) throws EseException {

		Evidencia evidencia = crearEvidencia(modificarFormacionJSP.getEvidencia(),
				modificarFormacionJSP.getFechaDocumento());

		Curso curso;
		Curso cursoBBDD;

		curso = cursoDAO.getOne(Integer.parseInt(modificarFormacionJSP.getIdCurso()));
		curso.setEstadoCurso(estadoCursoDAO.getOne(Constantes.ESTADO_CURSO_APROBADO));
		curso.setFechaInicio(Utiles.parseDatePantalla(modificarFormacionJSP.getFechaInicio()));
		curso.setFechaFin(Utiles.parseDatePantalla(modificarFormacionJSP.getFechaFin()));
		curso.setEvidencia2(evidencia);

		cursoBBDD = cursoDAO.save(curso);

		for (CursoAlumno cursoAlumno : cursoBBDD.getListCursoAlumno()) {
			cursoAlumno.setEstadoCursoAlumno(estadoCursoAlumnoDAO.getOne(Constantes.ESTADO_CURSO_ALUMNO_EN_CURSO));
			cursoAlumnoDAO.save(cursoAlumno);
		}

		blockChainService.uploadDocumento(evidencia.getDocumento());

		return cursoBBDD;
	}

	private Evidencia crearEvidencia(MultipartFile evidenciaJSP, String fechaDocumentoJSP) throws EseException {

		byte[] fichero;
		try {
			fichero = evidenciaJSP.getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_APROBAR_CURSO);
		String tipoFichero = evidenciaJSP.getContentType();
		Date fechaDocumento = Utiles.parseDatePantalla(fechaDocumentoJSP);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				fechaDocumento, tipoFichero);

		return evidencia;
	}

	@Override
	public void borrarCurso(ModificarFormacionJSP modificarFormacionJSP) throws EseException {

		Curso curso = cursoDAO.getOne(Integer.parseInt(modificarFormacionJSP.getIdCurso()));
		Evidencia evidencia1 = curso.getEvidencia1();
		Evidencia evidencia2 = curso.getEvidencia2();

		if (curso.getListCursoAlumno() != null) {
			for (CursoAlumno cursoAlumno : curso.getListCursoAlumno()) {
				cursoAlumnoDAO.delete(cursoAlumno);
			}
		}

		cursoDAO.delete(curso);

		if (evidencia1 != null) {
			borrarEvidencia(evidencia1);
		}
		if (evidencia2 != null) {
			borrarEvidencia(evidencia2);
		}
	}

	private void borrarEvidencia(Evidencia evidencia) throws EseException {
		evidenciaDAO.delete(evidencia);
		documentoDAO.delete(evidencia.getDocumento());
	}
}
