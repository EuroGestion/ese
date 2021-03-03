package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Titulo;

/**
 * @author Rmerino, alvaro
 *
 */
public interface TituloDAOCustom {

	Page<Titulo> findAllByPersonal(String idPersonal, PageRequest pageRequest);
}
