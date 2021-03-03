package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.AnexoAnomalia;

public interface AnexoAnomaliaDAOCustom {

	public Page<AnexoAnomalia> findByAnomalia(Integer idAnomalia, PageRequest pageRequest);
}
