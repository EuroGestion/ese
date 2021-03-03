package eu.eurogestion.ese.pojo;

import java.util.Date;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class SuspensionActivaTituloJSP {

	private String idTitulo;
	private String idPersonal;
	private String idSuspension;
	private String nombre;
	private String titulo;
	private String fechaCaducidad;
	private String causa;
	private String fechaSuspension;
	private String causaResolucion;
	private String fechaResolucion;
	private String observaciones;

}
