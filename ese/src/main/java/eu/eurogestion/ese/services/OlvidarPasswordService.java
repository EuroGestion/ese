package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.OlvidarPasswordJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface OlvidarPasswordService {

	void confirmacionOlvidarPassword(OlvidarPasswordJSP olvidarPasswordJSP) throws EseException;

	boolean emailValido(OlvidarPasswordJSP olvidarPasswordJSP);

}
