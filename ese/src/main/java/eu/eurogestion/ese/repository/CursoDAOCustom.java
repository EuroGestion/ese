package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Curso;

/**
 * @author Rmerino, alvaro
 *
 */
public interface CursoDAOCustom {

	public List<Curso> findCursoByPersonal(String idPersonal);

	/**
	 * Obtiene una lista de Cursos segun los filtros pasados.
	 * 
	 * @param idEstado          Id del estado a filtrar, en caso de no introducir
	 *                          ninguna busca por todos los estados validos.
	 * @param idCentroFormacion Id del centro donde se realiza el curso.
	 * @param fechaInicio       Fecha de inicio del curso.
	 * @param fechaFin          Fecha de finalizacion del curso.
	 * @return Lista de Cursos (0-n).
	 */
	public Page<Curso> findCursoByFilters(String idEstado, String idCentroFormacion, String fechaInicio,
			String fechaFin, PageRequest pageRequest);

	public Long findCursoByYear(Integer anno);

}
