package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.RevisionPsico;

/**
 * @author Rmerino, alvaro
 *
 */
public interface RevisionPsicoDAOCustom {

	RevisionPsico findLastRevisionPsico(String idPersonal);

	Page<RevisionPsico> findRevisionPsicoByFilters(String idCentro, String dni, String fechaRevision,
			PageRequest pageRequest);

	Page<RevisionPsico> findAllByPersonal(String idPersonal, PageRequest pageRequest);

	Long findRevisionPsicoByYear(Integer anno);

}
