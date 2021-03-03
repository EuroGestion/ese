package eu.eurogestion.ese.pojo;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class SancionesJSP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2015266089036692724L;

	private String idPersonal;
	private String idSuspension;
	private String idRevocacion;
	private String pageSuspensiones;
	private String pageRevocaciones;

}
