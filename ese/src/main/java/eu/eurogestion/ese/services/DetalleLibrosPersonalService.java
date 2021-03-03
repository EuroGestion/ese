package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleLibrosPersonalJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface DetalleLibrosPersonalService {

	public void firmarLibro(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP) throws EseException;

}
