package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISCCJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface GenerarInformeISCCService {

	void firmaISCCResponsableSeguridad(GenerarInformeISCCJSP generarInformeISCCJSP, HttpSession sesion, Model model)
			throws EseException;

	void firmaISCCInspectorSeguridad(GenerarInformeISCCJSP generarInformeISCCJSP, HttpSession sesion, Model model)
			throws EseException;

	/**
	 * Metodo que guarda un informe ISCC con los parametros pasados en la entrada
	 * 
	 * @param generarInformeISCCJSP el objeto con los parametros de pantalla.
	 * 
	 * @throws EseException
	 */
	void guardarInformeISCC(GenerarInformeISCCJSP generarInformeISCCJSP) throws EseException;

	/**
	 * metodo que genera un GenerarInformeISCCJSP dado el id de una ISCC
	 * 
	 * @param idISCC id del objeto de dominio ISCC
	 * @return
	 */
	GenerarInformeISCCJSP createGenerarInformeISCCJSPToIdISCC(Integer idISCC);

}
