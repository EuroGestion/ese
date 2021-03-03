package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Anomalia;

public interface AnomaliaDAOCustom {

	public Page<Anomalia> findByInformeAnomalia(Integer idInformeAnomalia, PageRequest pageRequest);

	public Long countByInformeAnomalia(Integer idInformeAnomalia);
}
