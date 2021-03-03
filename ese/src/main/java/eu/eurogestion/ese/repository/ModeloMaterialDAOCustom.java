package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.ModeloMaterial;

public interface ModeloMaterialDAOCustom {

	public Page<ModeloMaterial> findByFilters(String idTipoMaterial, String serie, String subserie,
			PageRequest pageRequest);
}
