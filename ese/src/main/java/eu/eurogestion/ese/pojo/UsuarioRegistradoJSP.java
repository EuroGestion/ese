package eu.eurogestion.ese.pojo;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class UsuarioRegistradoJSP {
	private String nombre;
	private String apellido;
	private String cargo;
	private String idPersonal;
	private Map<String, Integer> mapaPermisos;
	private Map<String, List<String>> mapaOpciones;

	private String logoAplicacion;
	private String logoEmpresa;

	public boolean tienePermiso(String permiso) {

		for (String opcionMenu : mapaOpciones.get(permiso)) {

			if (mapaPermisos.get(opcionMenu) != null && mapaPermisos.get(opcionMenu) != 0) {
				return true;
			}
		}

		return false;
	}

	public boolean tienePermisoEscritura(String permiso) {

		return mapaPermisos.get(permiso) != null && mapaPermisos.get(permiso) == 2;

	}

}
