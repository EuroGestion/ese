package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorTramoJSP {
	private String idTramo;
	private String nombre;
	private String idPuntoOrigen;
	private String idPuntoDestino;
	private String page;
	private String order;
	private String direccion;
}
