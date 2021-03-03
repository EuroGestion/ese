package eu.eurogestion.ese.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleRolJSP;
import eu.eurogestion.ese.repository.RolDAO;

@Service
public class RolServiceImpl implements RolService {

	/** Repositories & Services **/

	@Autowired
	public RolDAO rolDAO;

	/** Functions **/

	@Override
	public Rol crearRol(DetalleRolJSP detalleRolJSP) throws EseException {

		Rol rol = new Rol();
		rol.setNombre(detalleRolJSP.getNombre());

		return rolDAO.save(rol);
	}

	@Override
	public void guardarRol(DetalleRolJSP detalleRolJSP) {

		Rol tren = rolDAO.getOne(Integer.parseInt(detalleRolJSP.getIdRol()));
		tren.setNombre(detalleRolJSP.getNombre());

		rolDAO.save(tren);

	}

}
