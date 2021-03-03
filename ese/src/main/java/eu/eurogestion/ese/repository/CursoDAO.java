package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Curso;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface CursoDAO extends JpaRepository<Curso, Integer>, CursoDAOCustom {

	public Curso findFirstByIdCursoInOrderByFechaCaducidadAsc(List<Integer> listId);
}
