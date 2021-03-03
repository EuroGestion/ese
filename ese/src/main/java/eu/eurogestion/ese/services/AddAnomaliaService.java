package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Anomalia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AddAnomaliaJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface AddAnomaliaService {

	void creacionAnexo(AddAnomaliaJSP addAnomaliaJSP, HttpSession session) throws EseException;

	Anomalia guardarAnomalia(AddAnomaliaJSP addAnomaliaJSP) throws EseException;

}
