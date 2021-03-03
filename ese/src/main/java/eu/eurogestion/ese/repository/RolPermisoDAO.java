package eu.eurogestion.ese.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.RolPermiso;

/**
 * @author Rmerino
 *
 */
@Repository
@Transactional
public interface RolPermisoDAO extends JpaRepository<RolPermiso, Integer>, RolPermisoDAOCustom {

}
