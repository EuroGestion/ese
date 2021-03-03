package eu.eurogestion.ese.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Permiso;
import eu.eurogestion.ese.domain.RolPermiso;
import eu.eurogestion.ese.pojo.DetalleRolJSP;
import eu.eurogestion.ese.repository.OpcionDAO;
import eu.eurogestion.ese.repository.PermisoDAO;
import eu.eurogestion.ese.repository.RolDAO;
import eu.eurogestion.ese.repository.RolPermisoDAO;
import eu.eurogestion.ese.repository.TipoPermisoDAO;
import eu.eurogestion.ese.utils.Constantes;

@Service
public class RolPermisoServiceImpl implements RolPermisoService {

	/** Repositories & Services **/

	@Autowired
	public RolPermisoDAO rolPermisoDAO;

	@Autowired
	public PermisoDAO permisoDAO;

	@Autowired
	public OpcionDAO opcionDAO;

	@Autowired
	public TipoPermisoDAO tipoPermisoDAO;

	@Autowired
	public RolDAO rolDAO;

	/** Functions **/

	@Override
	public void addRolPermiso(DetalleRolJSP detalleRolJSP) {
		RolPermiso rolPermiso = new RolPermiso();
		rolPermiso.setPermiso(obtenerPermiso(detalleRolJSP));
		rolPermiso.setRol(rolDAO.getOne(Integer.parseInt(detalleRolJSP.getIdRol())));
		rolPermisoDAO.save(rolPermiso);
	}

	@Override
	public void eliminarPermisoRol(DetalleRolJSP detalleRolJSP) {

		RolPermiso rolPermiso = rolPermisoDAO.getOne(Integer.parseInt(detalleRolJSP.getIdRolPermiso()));
		rolPermisoDAO.delete(rolPermiso);

	}

	@Override
	public void cambiarPermisoRol(DetalleRolJSP detalleRolJSP) {

		RolPermiso rolPermiso = rolPermisoDAO.getOne(Integer.parseInt(detalleRolJSP.getIdRolPermiso()));

		Integer idTipoPermisoCambio;
		if (Constantes.TIPO_PERMISO_ESCRITURA.equals(rolPermiso.getPermiso().getTipoPermiso().getIdTipoPermiso())) {
			idTipoPermisoCambio = Constantes.TIPO_PERMISO_LECTURA;
		} else {
			idTipoPermisoCambio = Constantes.TIPO_PERMISO_ESCRITURA;
		}
		rolPermiso.setPermiso(permisoDAO.findByIdOpcionAndIdTipoPermiso(
				rolPermiso.getPermiso().getOpcion().getIdOpcion().toString(), idTipoPermisoCambio.toString()));
		rolPermisoDAO.save(rolPermiso);

	}

	private Permiso obtenerPermiso(DetalleRolJSP detalleRolJSP) {

		Permiso permiso = permisoDAO.findByIdOpcionAndIdTipoPermiso(detalleRolJSP.getIdOpcion(),
				detalleRolJSP.getIdTipoPermiso());

		if (permiso == null) {
			Permiso permisoNuevo = new Permiso();
			permisoNuevo.setOpcion(opcionDAO.getOne(Integer.parseInt(detalleRolJSP.getIdOpcion())));
			permisoNuevo.setTipoPermiso(tipoPermisoDAO.getOne(Integer.parseInt(detalleRolJSP.getIdTipoPermiso())));

			return permisoDAO.save(permisoNuevo);
		}

		return permiso;

	}

}
