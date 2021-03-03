package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AnexosInvestigacionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface AnexosInvestigacionService {

	void guardarEvidenciaMedidasAdoptadas(AnexosInvestigacionJSP anexosInvestigacion, HttpSession sesion)
			throws EseException;

	void guardarEvidenciaDocumentosAnexos(AnexosInvestigacionJSP anexosInvestigacion, HttpSession sesion)
			throws EseException;

}
