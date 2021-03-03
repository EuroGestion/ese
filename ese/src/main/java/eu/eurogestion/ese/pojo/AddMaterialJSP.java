package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class AddMaterialJSP {
	private String idMaterial;
	private String idTipoMaterial;
	private String idModeloMaterial;
	private String idModeloMaterialGuardar;
	private String serie;
	private String subserie;
	private String nve;
	private String modelo;
	private boolean lectura;
	private String page;

}
