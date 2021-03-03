package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.PasfJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface PasfService {

	void guardarPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException;

	void guardarCursoPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException;

	void guardarInspeccionPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException;

	void guardarAuditoriaPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException;

	void aprobarPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException;

}
