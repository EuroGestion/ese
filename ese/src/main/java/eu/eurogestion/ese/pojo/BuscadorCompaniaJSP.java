package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorCompaniaJSP {
	private String idCompania;
	private String nombre;
	private String documento;
	private String idTipoCompania;
	private String idInvestigacion;
	private String page;
	private String order;
	private String direccion;
}
