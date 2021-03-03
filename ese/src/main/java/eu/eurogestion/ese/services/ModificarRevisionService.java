package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ModificarRevisionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ModificarRevisionService {

	/**
	 * Metodo que modifica una revision psicofisica al estado "APTO" o "NO APTO" con
	 * los parametros pasados en la entrada
	 * 
	 * @param modificarRevisionJSP el objeto con los parametros de pantalla.
	 * @param sesion               objeto session de la aplicacion
	 * 
	 * @throws EseException
	 */
	void modificarRevision(ModificarRevisionJSP modificarRevisionJSP, HttpSession sesion) throws EseException;

}
