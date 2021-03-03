package eu.eurogestion.ese.services;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ReporteFinServicioJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ReporteFinServicioService {

	void guardarReporteFinServicio(ReporteFinServicioJSP reporteFinServicioJSP, HttpSession sesion) throws EseException;

	void generarFichaReporteFinServicio(ReporteFinServicioJSP reporteFinServicioJSP, HttpSession sesion)
			throws EseException;

}
