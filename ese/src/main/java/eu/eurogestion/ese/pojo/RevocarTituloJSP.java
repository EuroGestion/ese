package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class RevocarTituloJSP {
	private String idTitulo;
	private String idSuspension;
	private String idPersonal;
	private Boolean isSuspension;
	private String nombre;
	private String titulo;
	private String fechaCaducidad;
	private String causa;
	private String idCausaRevocacion;
	private String fechaRevocacion;
	private String sancionesEmpresariales;
	private String observaciones;
	private String observacionesSuspension;
	private String fechaResolucionSuspension;
	private String causaResolucionSuspension;
	private String nombreResponsableSeguridad;
	private String passwordResponsableSeguridad;

}
