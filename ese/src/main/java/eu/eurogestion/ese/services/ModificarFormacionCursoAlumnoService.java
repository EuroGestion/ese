package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ModificarFormacionCursoAlumnoJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ModificarFormacionCursoAlumnoService {

	/**
	 * Metodo que modifica un CursoAlumno al estado "Superado" con los parametros
	 * pasados en la entrada
	 * 
	 * @param modificarFormacionCursoAlumnoJSP el objeto con los parametros de
	 *                                         pantalla.
	 * @param sesion                           objeto session de la aplicacion
	 * @throws EseException
	 */
	void superarCursoAlumno(ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumnoJSP, HttpSession sesion)
			throws EseException;

	/**
	 * Metodo que modifica un CursoAlumno al estado "No superado" con los parametros
	 * pasados en la entrada
	 * 
	 * @param modificarFormacionCursoAlumnoJSP el objeto con los parametros de
	 *                                         pantalla.
	 * @throws EseException
	 */
	void noSuperarCursoAlumno(ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumnoJSP) throws EseException;

}
