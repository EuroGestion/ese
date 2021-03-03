package eu.eurogestion.ese.pojo;

import java.util.List;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorPersonalJSP {
	private String idCargo;
	private String nombre;
	private String apellido;
	private String dni;
	private List<Integer> listIdPersonal;
	private String idPersonal;
	private String page;
	private String order;
	private String direccion;
}
