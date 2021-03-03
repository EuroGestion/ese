package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Libro;

/**
 * @author Rmerino, alvaro
 *
 */
public interface LibroDAOCustom {

	/**
	 * Obtiene una lista de Libros a partir de unos filtros
	 * 
	 * @param titulo titulo del Libro a buscar
	 * @param fecha  fecha de la subida del Libro a buscar
	 * @return Lista Libros (0-n).
	 */
	public Page<Libro> findLibrosByFilters(String titulo, String fecha, PageRequest pageRequest);

}
