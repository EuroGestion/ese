package eu.eurogestion.ese.repository;

import java.util.List;

import eu.eurogestion.ese.domain.Provincia;

public interface ProvinciaDAOCustom {

	public List<Provincia> findProvinciaByPais(Integer idPais);
}
