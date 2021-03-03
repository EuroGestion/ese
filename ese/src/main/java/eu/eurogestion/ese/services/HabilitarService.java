package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.HabilitacionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface HabilitarService {

	Titulo altaTitulo(HabilitacionJSP habilitacion, HttpSession sesion) throws EseException;

}
