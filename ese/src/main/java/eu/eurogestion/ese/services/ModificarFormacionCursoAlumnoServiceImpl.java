package eu.eurogestion.ese.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.CursoAlumno;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ModificarFormacionCursoAlumnoJSP;
import eu.eurogestion.ese.repository.CursoAlumnoDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoCursoAlumnoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ModificarFormacionCursoAlumnoServiceImpl implements ModificarFormacionCursoAlumnoService {

	/** Repositories & Services **/

	@Autowired
	public CursoAlumnoDAO cursoAlumnoDAO;

	@Autowired
	public EstadoCursoAlumnoDAO estadoCursoAlumnoDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public CursoDAO cursoDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void superarCursoAlumno(ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumnoJSP,
			HttpSession sesion) throws EseException {

		byte[] fichero;
		try {
			fichero = modificarFormacionCursoAlumnoJSP.getEvidencia().getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_SUPERAR_CURSO);
		String tipoFichero = modificarFormacionCursoAlumnoJSP.getEvidencia().getContentType();
		Date fechaDocumento = Utiles.parseDatePantalla(modificarFormacionCursoAlumnoJSP.getFechaDocumento());

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				fechaDocumento, tipoFichero);

		CursoAlumno cursoAlumno = cursoAlumnoDAO
				.getOne(Integer.parseInt(modificarFormacionCursoAlumnoJSP.getIdCursoAlumno()));
		cursoAlumno.setEstadoCursoAlumno(estadoCursoAlumnoDAO.getOne(Constantes.ESTADO_CURSO_ALUMNO_SUPERADO));

		cursoAlumno.setEvidencia(evidencia);

		cursoAlumnoDAO.save(cursoAlumno);
		Curso curso = cursoAlumno.getCurso();
		curso.setFechaCaducidad(Utiles.parseDatePantalla(modificarFormacionCursoAlumnoJSP.getFechaCaducidad()));
		curso.setValidoDesde(Utiles.parseDatePantalla(modificarFormacionCursoAlumnoJSP.getValidoDesde()));
		cursoDAO.save(curso);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	@Override
	public void noSuperarCursoAlumno(ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumnoJSP)
			throws EseException {

		CursoAlumno cursoAlumno = cursoAlumnoDAO
				.getOne(Integer.parseInt(modificarFormacionCursoAlumnoJSP.getIdCursoAlumno()));
		cursoAlumno.setEstadoCursoAlumno(estadoCursoAlumnoDAO.getOne(Constantes.ESTADO_CURSO_ALUMNO_NO_SUPERADO));

		cursoAlumnoDAO.save(cursoAlumno);
	}
}
