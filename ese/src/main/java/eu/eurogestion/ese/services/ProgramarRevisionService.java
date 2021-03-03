package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ProgramarRevisionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ProgramarRevisionService {

	public void crearRevision(ProgramarRevisionJSP programarRevision) throws EseException;

}
