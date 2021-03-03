package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorModeloMaterialJSP {
	private String idModeloMaterial;
	private String idTipoMaterial;
	private String serie;
	private String subserie;
	private String page;
	private String order;
	private String direccion;
}
