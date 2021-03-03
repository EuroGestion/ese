package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.UsuarioJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface RegistroService {

	void registroUsuario(UsuarioJSP formularioUser) throws EseException;

	void modificarRegistroUsuario(UsuarioJSP formularioUser) throws EseException;

	void anadirEvidenciaIdiomaPersona(UsuarioJSP formularioUser, HttpSession sesion) throws EseException;

	void activarIdiomaPersona(UsuarioJSP formularioUser) throws EseException;

	void desactivarIdiomaPersona(UsuarioJSP formularioUser) throws EseException;

	void anadirDocumentoPersonal(UsuarioJSP formularioUser, HttpSession sesion) throws EseException;

}
