package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.RolPermiso;

/**
 * @author Rmerino, alvaro
 *
 */
public interface RolPermisoDAOCustom {

	/**
	 * Obtiene la lista de Permisos de un rol
	 * 
	 * @param idRol id del rol al que se le van a buscar los permisos
	 * @return Lista Permisos (0-n).
	 */
	Page<RolPermiso> findAllByIdRol(String idRol, PageRequest pageRequest);

	Long countAllByIdRol(String idRol);
}
