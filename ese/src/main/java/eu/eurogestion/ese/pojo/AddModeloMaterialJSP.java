package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class AddModeloMaterialJSP {
	private String idModeloMaterial;
	private String idTipoMaterial;
	private String serie;
	private String subserie;
	private String notas;
	private boolean lectura;

}
