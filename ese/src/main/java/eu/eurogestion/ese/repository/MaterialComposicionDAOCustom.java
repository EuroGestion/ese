package eu.eurogestion.ese.repository;

import java.util.List;

import eu.eurogestion.ese.domain.MaterialComposicion;

public interface MaterialComposicionDAOCustom {

	public List<MaterialComposicion> findByIdsComposicionAndIdMaterial(List<Integer> listIdComposicion,
			Integer idMaterial);

}
