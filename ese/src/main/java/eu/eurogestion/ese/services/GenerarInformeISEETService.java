package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISEETJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface GenerarInformeISEETService {

	
	void firmaISEETResponsableSeguridad(GenerarInformeISEETJSP generarInformeISEETJSP, HttpSession sesion, Model model)
			throws EseException;

	void firmaISEETInspectorSeguridad(GenerarInformeISEETJSP generarInformeISEETJSP, HttpSession sesion, Model model)
			throws EseException;

	/**
	 * Metodo que guarda un informe ISEET con los parametros pasados en la entrada
	 * 
	 * @param generarInformeISEETJSP el objeto con los parametros de pantalla.
	 * 
	 * @throws EseException
	 */
	void guardarInformeISEET(GenerarInformeISEETJSP generarInformeISEETJSP) throws EseException;

	/**
	 * metodo que genera un GenerarInformeISEETJSP dado el id de una ISEET
	 * 
	 * @param idISEET id del objeto de dominio ISEET
	 * @return
	 */
	GenerarInformeISEETJSP createGenerarInformeISEETJSPToIdISEET(Integer idISEET);

}
