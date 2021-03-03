package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.IdiomaPersona;

/**
 * @author Rmerino, alvaro
 *
 */
public interface IdiomaPersonaDAOCustom {

	Page<IdiomaPersona> findIdiomaPersonaByPersonal(String idPersonal, PageRequest pageRequest);

	boolean existIdiomaPersonaByPersonalAndIdiomaAndFechaBajaNull(String idPersonal, String idIdioma);

}
