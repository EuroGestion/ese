package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarInformeISMPJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface GenerarInformeISMPService {

	void firmaISMPResponsableSeguridad(GenerarInformeISMPJSP generarInformeISMPJSP, HttpSession sesion, Model model)
			throws EseException;

	void firmaISMPInspectorSeguridad(GenerarInformeISMPJSP generarInformeISMPJSP, HttpSession sesion, Model model)
			throws EseException;

	/**
	 * Metodo que guarda un informe ISMP con los parametros pasados en la entrada
	 * 
	 * @param generarInformeISMPJSP el objeto con los parametros de pantalla.
	 * 
	 * @throws EseException
	 */
	void guardarInformeISMP(GenerarInformeISMPJSP generarInformeISMPJSP) throws EseException;

	/**
	 * metodo que genera un GenerarInformeISMPJSP dado el id de una ISMP
	 * 
	 * @param idISMP id del objeto de dominio ISMP
	 * @return
	 */
	GenerarInformeISMPJSP createGenerarInformeISMPJSPToIdISMP(Integer idISMP);

}
