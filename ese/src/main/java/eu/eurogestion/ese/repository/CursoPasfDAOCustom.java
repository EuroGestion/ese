package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.CursoPasf;

/**
 * @author Rmerino, alvaro
 *
 */
public interface CursoPasfDAOCustom {

	public Page<CursoPasf> findCursoPasfByPasf(String idPasf, PageRequest pageRequest);

	public Integer sumByPasfAndCategory(Integer idPasf, Integer category);

}
