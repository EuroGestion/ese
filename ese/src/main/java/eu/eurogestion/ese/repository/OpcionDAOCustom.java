package eu.eurogestion.ese.repository;

import java.util.List;

import eu.eurogestion.ese.domain.Opcion;

/**
 * @author Rmerino, alvaro
 *
 */
public interface OpcionDAOCustom {

	/**
	 * Obtiene la lista de Opciones de un rol
	 * 
	 * @param idRol id del rol al que se le van a buscar los permisos
	 * @return Lista Permisos (0-n).
	 */
	List<Opcion> findAllByIdRol(String idRol);
}
