package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ProgramarFormacionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ProgramarFormacionService {

	void crearCurso(ProgramarFormacionJSP programarFormacionJSP) throws EseException;

}
