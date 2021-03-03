package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISOJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface GenerarInformeISOService {

	void firmaISOResponsableSeguridad(GenerarInformeISOJSP generarInformeISOJSP, HttpSession sesion, Model model)
			throws EseException;

	void firmaISOInspectorSeguridad(GenerarInformeISOJSP generarInformeISOJSP, HttpSession sesion, Model model)
			throws EseException;

	/**
	 * Metodo que guarda un informe iso con los parametros pasados en la entrada
	 * 
	 * @param generarInformeISOJSP el objeto con los parametros de pantalla.
	 * 
	 * @throws EseException
	 */
	void guardarInformeISO(GenerarInformeISOJSP generarInformeISOJSP) throws EseException;

	/**
	 * metodo que genera un GenerarInformeISOJSP dado el id de una ISO
	 * 
	 * @param idISO id del objeto de dominio ISO
	 * @return
	 */
	GenerarInformeISOJSP createGenerarInformeISOJSPToIdISO(Integer idISO);

}
