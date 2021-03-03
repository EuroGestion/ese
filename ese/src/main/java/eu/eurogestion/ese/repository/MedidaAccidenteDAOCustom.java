package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import eu.eurogestion.ese.domain.MedidaAccidente;

public interface MedidaAccidenteDAOCustom {

	public Page<MedidaAccidente> findByIdAccidente(Integer idAccidente, PageRequest pageRequest);
}
