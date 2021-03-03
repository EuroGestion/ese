package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleComposicionJSP {
	private String fecha;
	private String idTren;
	private boolean vieneCrearTren;
	private boolean lectura;
	private String idMaterial;
	private Integer idComposicion;

	private String pageTotalMateriales;
	private String pageTotalMaterialesComposicion;

}
