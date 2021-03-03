package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.Tramo;

/**
 * @author Rmerino, alvaro
 *
 */
public interface TramoDAOCustom {

	Page<Tramo> findAllByFilters(String nombre, String idPuntoOrigen, String idPuntoDestino, PageRequest pageRequest);
}
