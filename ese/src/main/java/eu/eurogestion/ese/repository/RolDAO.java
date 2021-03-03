package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Rol;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface RolDAO extends JpaRepository<Rol, Integer> {

	Page<Rol> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

	Page<Rol> findAll(Pageable pageable);

}
