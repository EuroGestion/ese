package eu.eurogestion.ese.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Personal;

/**
 * @author Rmerino, alvaro
 *
 */
public interface PersonalDAOCustom {

	/**
	 * Obtiene una lista de Personales en estado de Baja a partir del dni
	 * 
	 * @param dni      Dni del Personal a buscar
	 * @param idCargo  Cargo del Personal a buscar
	 * @param nombre   Nombre del Personal a buscar
	 * @param apellido Apellido del Personal a buscar
	 * @return Lista Personales (0-n).
	 */
	List<Personal> findPersonalByFilters(String dni, String idCargo, String nombre, String apellido);

	Page<Personal> findBuscadorPersonalByFilters(String dni, String idCargo, String nombre, String apellido,
			PageRequest pageRequest);

	Page<Personal> findPlanificarFormRevByFiltersTotales(String dni, String idCargo, String nombre, String apellido,
			Set<String> listIdPersonal, PageRequest pageRequest);

	Long countPlanificarFormRevByFiltersTotales(String dni, String idCargo, String nombre, String apellido,
			Set<String> listIdPersonal);

	Page<Personal> findPlanificarFormRevByFiltersSeleccionados(Set<String> listIdPersonal, PageRequest pageRequest);

	Long countPlanificarFormRevByFiltersSeleccionados(Set<String> listIdPersonal);

	List<Personal> findPersonalByFilters(String dni, String idCargo, String nombre, String apellido,
			List<Integer> listIdPersonal);

	/**
	 * Obtiene una lista de Maquinistas en activo
	 * 
	 * @return Lista Personales (0-n).
	 */
	List<Personal> findAllMaquinistas();

	List<Personal> findByList(List<Integer> listIdPersonal);
	
	Personal findByNombreUsuarioAndClaveAndFechaBajaIsNull(String nombreUsuario, String clave);
	
	
}
