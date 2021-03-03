package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleRolJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface RolPermisoService {

	void addRolPermiso(DetalleRolJSP detalleRolJSP);

	void eliminarPermisoRol(DetalleRolJSP detalleRolJSP);
	void cambiarPermisoRol(DetalleRolJSP detalleRolJSP);

}
