package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ModificarFormacionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ModificarFormacionService {

	/**
	 * Metodo que modifica un Curso al estado "Planificado" con los parametros
	 * pasados en la entrada
	 * 
	 * @param modificarFormacionJSP el objeto con los parametros de pantalla.
	 * @return El objeto de dominio Curso modificado
	 * @throws EseException
	 */
	Curso planificarCurso(ModificarFormacionJSP modificarFormacionJSP) throws EseException;

	/**
	 * Metodo que modifica un Curso al estado "Pendiente Aprobacion" con los
	 * parametros pasados en la entrada
	 * 
	 * @param modificarFormacionJSP el objeto con los parametros de pantalla.
	 * @param sesion                objeto session de la aplicacion
	 * @return El objeto de dominio Curso modificado
	 * @throws EseException
	 */
	Curso enviarCFCurso(ModificarFormacionJSP modificarFormacionJSP, HttpSession sesion) throws EseException;

	/**
	 * Metodo que modifica un Curso al estado "Aprobado" con los parametros pasados
	 * en la entrada
	 * 
	 * @param modificarFormacionJSP el objeto con los parametros de pantalla.
	 * @param sesion                objeto session de la aplicacion
	 * @return El objeto de dominio Curso modificado
	 * @throws EseException
	 */
	Curso aprobarCurso(ModificarFormacionJSP modificarFormacionJSP, HttpSession sesion) throws EseException;

	/**
	 * Metodo que elminina un curso y sus relaciones de la base de datos con los
	 * parametros pasados en la entrada
	 * 
	 * @param modificarFormacionJSP
	 * @throws EseException
	 */
	void borrarCurso(ModificarFormacionJSP modificarFormacionJSP) throws EseException;

}
