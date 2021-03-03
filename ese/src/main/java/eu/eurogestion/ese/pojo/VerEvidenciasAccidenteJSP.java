package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class VerEvidenciasAccidenteJSP {

	private String idAccidente;
	private Boolean tieneFichaAccidentes;
	private Boolean tieneFichaEstructura;
	private Boolean tieneFichaNotificacion;
	private Boolean tieneInformeFinal;
	private Boolean tieneEvidenciaRecibidoCIAF;
	private Boolean tieneFichaComentarios;
	private Boolean tieneFichaMedidas;

}
