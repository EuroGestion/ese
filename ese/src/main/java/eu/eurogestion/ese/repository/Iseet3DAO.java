package eu.eurogestion.ese.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Iseet3;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface Iseet3DAO extends JpaRepository<Iseet3, Integer> {

}
