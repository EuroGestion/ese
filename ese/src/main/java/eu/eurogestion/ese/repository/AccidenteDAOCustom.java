package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Accidente;

/**
 * @author Rmerino, alvaro
 *
 */
public interface AccidenteDAOCustom {

	public Page<Accidente> findAccidenteByFilters(String tipo, String causa, String numIdentificacion, String fecha,
			String lugar, PageRequest pageRequest);

	public Long countAccidenteByDateAndCausa(Integer anno, Integer causa);
}
