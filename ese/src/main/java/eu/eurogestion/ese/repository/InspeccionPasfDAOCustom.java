package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.InspeccionPasf;

/**
 * @author Rmerino, alvaro
 *
 */
public interface InspeccionPasfDAOCustom {

	public Page<InspeccionPasf> findInspeccionPasfByPasf(String idPasf, PageRequest pageRequest);

}
