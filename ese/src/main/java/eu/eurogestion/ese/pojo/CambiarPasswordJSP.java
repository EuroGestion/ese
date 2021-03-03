package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class CambiarPasswordJSP {
	// GLOBALES
	private String idPersonal;
	private Boolean esPropia;
	private String passwordAntigua;
	private String passwordNueva;
	private String passwordNuevaConfirmacion;

}
