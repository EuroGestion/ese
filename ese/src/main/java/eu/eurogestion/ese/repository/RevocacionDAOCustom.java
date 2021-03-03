package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Revocacion;

/**
 * @author Rmerino, alvaro
 *
 */
public interface RevocacionDAOCustom {

	Page<Revocacion> findAllByPersonal(String idPersonal, PageRequest pageRequest);
}
