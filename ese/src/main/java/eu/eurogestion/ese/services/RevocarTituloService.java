package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.RevocarTituloJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface RevocarTituloService {

	void revocarTitulo(RevocarTituloJSP revocarTituloJSP, HttpSession sesion) throws EseException;

}
