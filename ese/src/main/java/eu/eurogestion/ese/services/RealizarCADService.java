package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Cad;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.RealizarCADJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface RealizarCADService {

	void autorizarCAD(RealizarCADJSP realizarCADJSP, HttpSession session) throws EseException;

	void noAutorizarCAD(RealizarCADJSP realizarCADJSP, HttpSession session) throws EseException;

	RealizarCADJSP convertIdCADToRealizarCADJSP(Integer idCAD);

	Cad guardarCAD(RealizarCADJSP realizarCAD);

	void crearFichaCAD(RealizarCADJSP realizarCADJSP, HttpSession session) throws EseException;

}
