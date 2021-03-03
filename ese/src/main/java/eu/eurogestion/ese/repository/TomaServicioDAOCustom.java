package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.TomaServicio;

/**
 * @author Rmerino, alvaro
 *
 */
public interface TomaServicioDAOCustom {

	Page<TomaServicio> findAllByFilters(String idPersonal, String numeroTren, String idEstado, PageRequest pageRequest);
}
