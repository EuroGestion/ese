package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.exception.EseException;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface BlockChainService {

	void uploadDocumento(Documento documento) throws EseException;

	String downloadDocumento(String idDocumento) throws EseException;

}
