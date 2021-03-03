package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorTomaServicioJSP {
	private String idMaquinista;
	private String numeroTren;
	private String idEstadoTomaServicio;
	private String idTomaServicio;
	private String page;
	private String order;
	private String direccion;
}
