package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.HistoricoMaquinista;

public interface HistoricoMaquinistaDAOCustom {

	/**
	 * Obtiene todas los HistoricoMaquinista correspondientes en base a unos
	 * filtros.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	Page<HistoricoMaquinista> findAllReportesFinServicioByPersonal(String idPersonal, PageRequest pageRequest);

	Page<HistoricoMaquinista> findAllReportesFinServicioByFilters(String idPersonal, String numeroTren, String fecha,
			PageRequest pageRequest);
}
