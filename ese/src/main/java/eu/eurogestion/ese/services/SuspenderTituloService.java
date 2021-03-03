package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.SuspenderTituloJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface SuspenderTituloService {

	void suspenderTitulo(SuspenderTituloJSP suspenderTituloJSP, HttpSession sesion) throws EseException;

}
