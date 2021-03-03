package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Compania;

public interface CompaniaDAOCustom {

	/**
	 * Obtiene todas los Companias en activo correspondientes a centros de
	 * formacion.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	List<Compania> findAllCompaniaFormacion();

	/**
	 * Obtiene todas los Companias en activo correspondientes a centros medicos.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	List<Compania> findAllCompaniaCentroMedico();

	/**
	 * Obtiene todas los Companias en activo correspondientes en base a unos
	 * filtros.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	Page<Compania> findAllCompaniaAltaByFilters(String documento, String nombre, String idTipoCompania,PageRequest pageRequest);
}
