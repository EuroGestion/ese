package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleTomaServicioJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface DetalleTomaServicioService {

	void guardarDetalleTomaServicio(DetalleTomaServicioJSP detalleTomaServicioJSP, HttpSession sesion)
			throws EseException;

	void generarFichaDetalleTomaServicio(DetalleTomaServicioJSP detalleTomaServicioJSP, HttpSession sesion)
			throws EseException;

}
