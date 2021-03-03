package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CrearInformeAnomaliasJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface CrearInformeAnomaliasService {

	void guardarInformeAnomalias(CrearInformeAnomaliasJSP crearInformeAnomalias);

	void anadirEvidenciaInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomalias, HttpSession sesion)
			throws EseException;

	void firmaCrearInformeAnomaliasInspectorSeguridad(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model)
			throws EseException;

	void firmaCrearInformeAnomaliasResponsableSeguridad(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model)
			throws EseException;

}
