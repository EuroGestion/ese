package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Tramo;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface TramoDAO extends JpaRepository<Tramo, Integer>, TramoDAOCustom {

	public List<Tramo> findByEsEspecialFalse(Sort sort);

}
