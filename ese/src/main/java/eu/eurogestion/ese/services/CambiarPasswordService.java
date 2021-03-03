package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CambiarPasswordJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface CambiarPasswordService {

	void confirmacionCambiarPassword(CambiarPasswordJSP cambiarPasswordJSP) throws EseException;

	boolean mismaPasswordPropia(CambiarPasswordJSP cambiarPasswordJSP);

}
