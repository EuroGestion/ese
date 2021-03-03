package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorTrenJSP;
import eu.eurogestion.ese.pojo.DetalleTrenJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface TrenService {

	void bajaTren(BuscadorTrenJSP trenJSP);

	Tren crearTren(DetalleTrenJSP detalleTrenJSP) throws EseException;

	void guardarTren(DetalleTrenJSP detalleTrenJSP);
	
	void anadirDocumentoTren(DetalleTrenJSP detalleTrenJSP) throws EseException;

}
