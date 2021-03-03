package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.AnexoProveedor;

public interface AnexoProveedorDAOCustom {

	public Page<AnexoProveedor> findByIdProveedor(Integer idProveedor, PageRequest pageRequest);
}
