package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorLibrosJSP {
	private String idLibro;
	private String fecha;
	private String titulo;
	private String page;
	private String order;
	private String direccion;

}
