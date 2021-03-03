package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.LibroPersonal;

/**
 * @author Rmerino, alvaro
 *
 */
public interface LibroPersonalDAOCustom {

	/**
	 * Obtiene una lista de Libros de personales a partir de unos filtros
	 * 
	 * @param idLibro id del libro del LibroPersonal
	 * @return Lista LibroPersonal (0-n).
	 */
	public Page<LibroPersonal> findLibrosPersonalByFilters(Integer idLibro,PageRequest pageRequest);

	public Page<LibroPersonal> findByFilters(String idPersonal, String titulo, String fecha, PageRequest pageRequest);

	public Long findWithoutEvidence(String idPersonal);
}
