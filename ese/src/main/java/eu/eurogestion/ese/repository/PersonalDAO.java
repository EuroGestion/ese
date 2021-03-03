package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Personal;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface PersonalDAO extends JpaRepository<Personal, Integer>, PersonalDAOCustom {

	/**
	 * Obtiene un Personal a partir de nameUser y clave.
	 * 
	 * @param nameUser Usuario a buscar
	 * @param clave    Password del usuario a buscar
	 * @return Personal si lo encuentra, null en caso contrario
	 */
	
	
	/**
	 * Obtiene un Personal a partir de nameUser y clave.
	 * 
	 * @param nameUser Usuario a buscar
	 * @param clave    Password del usuario a buscar
	 * @return Personal si lo encuentra, null en caso contrario
	 */
	public Personal findByEmailAndFechaBajaIsNull(String email);

	/**
	 * Obtiene un Personal a partir de nameUser
	 * 
	 * @param nameUser Usuario a buscar
	 * @return Personal si lo encuentra, null en caso contrario
	 */
	public Personal findByNombreUsuario(String nameUser);

	public List<Personal> findByFechaBajaIsNull();
}
