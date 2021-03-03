package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.AnexoInformeAnomalia;

public interface AnexoInformeAnomaliaDAOCustom {

	public Page<AnexoInformeAnomalia> findByInformeAnomalia(Integer idInformeAnomalia, PageRequest pageRequest);
}
