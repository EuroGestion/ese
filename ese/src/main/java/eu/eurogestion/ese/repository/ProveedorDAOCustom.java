package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Proveedor;

/**
 * @author Rmerino, alvaro
 *
 */
public interface ProveedorDAOCustom {

	public Page<Proveedor> findAllProveedoresByFilters(String documento, String nombre, String estado,
			PageRequest pageRequest);
}
