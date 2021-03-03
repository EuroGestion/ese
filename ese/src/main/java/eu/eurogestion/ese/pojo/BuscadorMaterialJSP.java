package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorMaterialJSP {
	private String idMaterial;
	private String idModeloMaterial;
	private String nve;
	private String page;
	private String order;
	private String direccion;

}
