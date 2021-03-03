package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface MaterialService {

	void bajaMaterial(Integer idMaterial) throws EseException;

}
