package eu.eurogestion.ese.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Pasf;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface PasfDAO extends JpaRepository<Pasf, Integer>, PasfDAOCustom {

	public Pasf findByAnno(Integer anno);
}
