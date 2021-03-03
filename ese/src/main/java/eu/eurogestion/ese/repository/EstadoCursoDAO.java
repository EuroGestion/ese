package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.EstadoCurso;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface EstadoCursoDAO extends JpaRepository<EstadoCurso, Integer> {

	public List<EstadoCurso> findByIdEstadoCursoIn(List<Integer> listId);

}