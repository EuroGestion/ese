package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Composicion;
import eu.eurogestion.ese.exception.EseException;

public interface ComposicionDAOCustom {

	/**
	 * Obtiene una composicion en base a unos filtros.
	 * 
	 * @param idTren id del tren de la composicion
	 * @param fecha  fecha de la composicion
	 * @return Composicion
	 */
	Composicion findComposicionByFilters(String idTren, String fecha) throws EseException;

	/**
	 * Obtiene una lista de composicion en base a unos filtros.
	 * 
	 * @param idTren id del tren de la composicion
	 * @param fecha  fecha de la composicion
	 * @return Composicion
	 */
	Page<Composicion> findListComposicionesByFilters(String idTren, String fecha, PageRequest pageRequest);

	Long countListComposicionesByFilters(String idTren, String fecha);

	/**
	 * Obtiene una lista de composicion en base a unos filtros.
	 * 
	 * @param idTren id del tren de la composicion
	 * @param fecha  fecha de la composicion
	 * @return Composicion
	 */
	boolean existeComposicionesByFilters(String idTren, String fechaInicio, String fechaFin);

}
