package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleRolJSP {
	private String idRol;
	private boolean lectura;

	private String nombre;
	private String idOpcion;
	private String idTipoPermiso;
	private String idRolPermiso;
	private String page;

}
