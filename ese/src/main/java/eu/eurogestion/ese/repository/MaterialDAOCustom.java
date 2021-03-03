package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Material;

public interface MaterialDAOCustom {

	Page<Material> findByIdComposicion(List<Integer> listIdComposicion, PageRequest pageRequest);

	Long countByIdComposicion(List<Integer> listIdComposicion);

	Page<Material> findByNotIdComposicion(List<Integer> listIdComposicion, PageRequest pageRequest);

	Long countByNotIdComposicion(List<Integer> listIdComposicion);

	Page<Material> findByFilters(String modeloMaterial, String nve, PageRequest pageRequest);

	Long countByFilters(String modeloMaterial, String nve);
}
