package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.GenerarAccidenteJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface GenerarAccidenteService {

	Accidente guardarAccidente(GenerarAccidenteJSP generarAccidenteJSP) throws EseException;

	void confirmarIntervieneCIAF(GenerarAccidenteJSP generarAccidenteJSP) throws EseException;

	void confirmarDelegaInvestigacion(GenerarAccidenteJSP generarAccidenteJSP) throws EseException;

	void firmaAccidenteFichaAccidente(GenerarAccidenteJSP generarAccidenteJSP, Model model) throws EseException;

	void firmaAccidenteFichaEstructura(GenerarAccidenteJSP generarAccidenteJSP, Model model) throws EseException;

	void firmaAccidenteFichaNotificacionResponsableSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException;

	void firmaAccidenteFichaNotificacionDelegadoSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException;

	void firmaAccidenteInformeFinalResponsableSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException;

	void firmaAccidenteInformeFinalDelegadoSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model)
			throws EseException;

	void anadirDocumentoInformeProvisionalCIAF(GenerarAccidenteJSP generarAccidenteJSP) throws EseException;

	void firmaAccidenteFichaComentarios(GenerarAccidenteJSP generarAccidenteJSP, Model model) throws EseException;

	void firmaAccidenteFichaMedidas(GenerarAccidenteJSP generarAccidenteJSP, Model model) throws EseException;

}
