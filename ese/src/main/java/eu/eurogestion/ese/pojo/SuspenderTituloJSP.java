package eu.eurogestion.ese.pojo;

import java.util.Date;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class SuspenderTituloJSP {
	private String idPersonal;
	private String idTitulo;
	private String nombre;
	private String titulo;
	private String fechaCaducidad;
	private String idCausa;
	private String fechaSuspension;
	private String nombreResponsableSeguridad;
	private String passwordResponsableSeguridad;
}