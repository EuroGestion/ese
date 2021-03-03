package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Tren;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface TrenDAO extends JpaRepository<Tren, Integer> {

	/**
	 * Obtiene los Trenes en activo a partir de su numero.
	 * 
	 * @param numero numero del tren
	 * @return Tren si lo encuentra, null en caso contrario
	 */
	public Page<Tren> findByNumeroContainingIgnoreCaseAndFechaBajaIsNull(String numero, Pageable pageable);

	/**
	 * Obtiene los Trenes en activo
	 * 
	 * @return Tren si lo encuentra, null en caso contrario
	 */
	public List<Tren> findByFechaBajaIsNull();

	/**
	 * Obtiene los Trenes en activo
	 * 
	 * @return Tren si lo encuentra, null en caso contrario
	 */
	public Page<Tren> findByFechaBajaIsNull(Pageable pageable);

	public Long countByFechaBajaIsNull();

	public Long countByNumeroContainingIgnoreCaseAndFechaBajaIsNull(String numero);

}
