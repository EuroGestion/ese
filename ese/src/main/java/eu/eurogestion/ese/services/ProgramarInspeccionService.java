package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ProgramarInspeccionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ProgramarInspeccionService {

	void registroInspeccionYTarea(ProgramarInspeccionJSP formProgramarInspeccion, HttpSession sesion)
			throws EseException;

}
