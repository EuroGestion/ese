package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CrearLibroJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface CrearLibroService {

	void generarLibro(CrearLibroJSP crearLibroJSP,HttpSession session) throws EseException;

}
