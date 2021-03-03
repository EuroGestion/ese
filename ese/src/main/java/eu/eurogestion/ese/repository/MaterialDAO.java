package eu.eurogestion.ese.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Material;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface MaterialDAO extends JpaRepository<Material, Integer>, MaterialDAOCustom {

}
