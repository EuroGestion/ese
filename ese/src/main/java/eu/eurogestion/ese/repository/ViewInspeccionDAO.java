package eu.eurogestion.ese.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.ViewInspeccion;
import eu.eurogestion.ese.domain.ViewInspeccionPK;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface ViewInspeccionDAO extends JpaRepository<ViewInspeccion, ViewInspeccionPK>, ViewInspeccionDAOCustom {

}
