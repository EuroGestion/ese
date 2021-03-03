package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.ViewInspeccion;

/**
 * @author Rmerino, alvaro
 *
 */
public interface ViewInspeccionDAOCustom {

	Page<ViewInspeccion> findInspeccionByFilters(String idTipo, String idInspector, String idEstado, String codigo,
			String fecha, String idTren, String idOrigen, String nve, PageRequest pageRequest);

	Page<ViewInspeccion> findByPersonal(String personal, PageRequest pageRequest);
}
