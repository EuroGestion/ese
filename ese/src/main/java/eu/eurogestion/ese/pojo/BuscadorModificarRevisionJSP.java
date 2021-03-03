package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorModificarRevisionJSP {
	private String idCentroMedico;
	private String fechaRevision;
	private String dni;
	private String idRevision;
	private String page;
	private String order;
	private String direccion;

}
