package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleRolJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface RolService {

	Rol crearRol(DetalleRolJSP detalleRolJSP) throws EseException;

	void guardarRol(DetalleRolJSP detalleRolJSP);

}
