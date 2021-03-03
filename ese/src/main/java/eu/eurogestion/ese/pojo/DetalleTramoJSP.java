package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleTramoJSP {
	private String idTramo;
	private boolean lectura;

	private String nombre;
	private String idOrigen;
	private String idDestino;

}
