package eu.eurogestion.ese.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Iseet;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface IseetDAO extends JpaRepository<Iseet, Integer>, IseetDAOCustom {

}
