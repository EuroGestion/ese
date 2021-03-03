package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.SuspensionActivaTituloJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface SuspensionActivaTituloService {

	void recuperarTitulo(SuspensionActivaTituloJSP suspensionActivaTituloJSP) throws EseException;

	void caducarTitulo(SuspensionActivaTituloJSP suspensionActivaTituloJSP) throws EseException;

}
