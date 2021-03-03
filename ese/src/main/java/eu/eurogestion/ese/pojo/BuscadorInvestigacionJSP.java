package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorInvestigacionJSP {
	private String idTipoInvestigacion;
	private String idCausa;
	private String nIdentificacion;
	private String fecha;
	private String lugar;
	private String idInvestigacion;
	private String page;
	private String order;
	private String direccion;
}
