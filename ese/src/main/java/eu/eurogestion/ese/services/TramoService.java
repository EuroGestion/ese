package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleTramoJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface TramoService {

	void guardarTramo(DetalleTramoJSP detalleTramoJSP);

}
