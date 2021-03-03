package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.CursoAlumno;

/**
 * @author Rmerino, alvaro
 *
 */
public interface CursoAlumnoDAOCustom {

	Long countByYearAndType(Integer anno, Integer tipo);

	Page<CursoAlumno> findAllByPersonal(String idPersonal, PageRequest pageRequest);

}
