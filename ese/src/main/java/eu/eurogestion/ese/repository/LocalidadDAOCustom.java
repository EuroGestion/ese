package eu.eurogestion.ese.repository;

import java.util.List;

import eu.eurogestion.ese.domain.Localidad;

public interface LocalidadDAOCustom {

	public List<Localidad> findLocalidadByPaisAndProvincia(Integer idPais, Integer idProvincia);
}
