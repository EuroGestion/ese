package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.DocumentoPersonal;

/**
 * @author Rmerino, alvaro
 *
 */
public interface DocumentoPersonalDAOCustom {

	Page<DocumentoPersonal> findAllByIdPersonal(String idPersonal, PageRequest pageRequest);

}
