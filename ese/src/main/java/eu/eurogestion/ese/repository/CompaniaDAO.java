package eu.eurogestion.ese.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Compania;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface CompaniaDAO extends JpaRepository<Compania, Integer>, CompaniaDAOCustom {

	/**
	 * Obtiene todas los Companias en activo.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	@Query("SELECT com FROM Compania com  WHERE com.fechaBaja is null")
	public List<Compania> findAllCompaniaAlta();

}
