package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.HomologacionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface HomologacionService {

	void guardarEvidenciaDocumentosAnexos(HomologacionJSP homologacionJSP, HttpSession sesion) throws EseException;

	void guardarProveedor(HomologacionJSP homologacionJSP, HttpSession sesion, Model model) throws EseException;

}
