package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.AnexoAccidente;

public interface AnexoAccidenteDAOCustom {

	public Page<AnexoAccidente> findByIdAccidente(Integer idAccidente, PageRequest pageRequest);
}
