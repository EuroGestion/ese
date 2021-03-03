package eu.eurogestion.ese.pojo;

import java.util.Date;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class TareasPendientesJSP {
	private String idTarea;
	
	private String idUsuario;
	private String page;
	private String order;
	private String direccion;
}
