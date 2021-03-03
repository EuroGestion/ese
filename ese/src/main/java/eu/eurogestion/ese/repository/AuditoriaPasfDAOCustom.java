package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.AuditoriaPasf;

/**
 * @author Rmerino, alvaro
 *
 */
public interface AuditoriaPasfDAOCustom {

	public Page<AuditoriaPasf> findAuditoriaPasfByPasf(String idPasf, PageRequest pageRequest);

}
