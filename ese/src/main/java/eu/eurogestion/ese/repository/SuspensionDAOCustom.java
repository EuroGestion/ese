package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Suspension;

/**
 * @author Rmerino, alvaro
 *
 */
public interface SuspensionDAOCustom {

	Page<Suspension> findAllByPersonal(String idPersonal, PageRequest pageRequest);
}
