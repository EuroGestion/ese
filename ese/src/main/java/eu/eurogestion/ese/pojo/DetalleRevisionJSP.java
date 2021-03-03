package eu.eurogestion.ese.pojo;

import java.util.Date;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleRevisionJSP {
	private String idPersonal;
	private String idRevision;
	private String nombre;
	private String cargo;
	private String causa;
	private String resultado;
	private Date validez;
	private Date fechaCaducidad;
	private String centroMedico;
	private Date fechaRealizacion;
	private String observaciones;
	private boolean evidenciaResultadoPrueba;
}