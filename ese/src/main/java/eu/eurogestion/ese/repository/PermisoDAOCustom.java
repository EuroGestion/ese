package eu.eurogestion.ese.repository;

import eu.eurogestion.ese.domain.Permiso;

/**
 * @author Rmerino, alvaro
 *
 */
public interface PermisoDAOCustom {

	Permiso findByIdOpcionAndIdTipoPermiso(String idOpcion, String idTipoPermiso);
}
