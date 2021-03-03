package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorTrenJSP {
	private String numeroTren;
	private String idTren;
	private String page;
	private String order;
	private String direccion;
}
