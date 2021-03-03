package eu.eurogestion.ese.pojo;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class SelectPersonalFormRevJSP implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6969506906201722527L;

	private String idCargo;
	private String nombre;
	private String apellido;
	private String dni;
	private Boolean isFormacion;
	private Boolean isRevision;
	private Set<String> listaPersonalTotal;
	private String idPersonal;
	private String pageTotal;
	private String pageSeleccionados;
}
