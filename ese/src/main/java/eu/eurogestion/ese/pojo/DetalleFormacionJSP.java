package eu.eurogestion.ese.pojo;

import java.util.Date;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleFormacionJSP {
	private String idPersonal;
	private String idCursoAlumno;
	private String nombre;
	private String cargo;
	private String tipoCurso;
	private String tituloCurso;
	private String estadoCurso;
	private Date validez;
	private Date fechaCaducidad;
	private String horas;
	private String detalle;
	private String centroFormacion;
	private Date fechaInicio;
	private Date fechaFin;
	private String observaciones;
	private boolean evidenciaEnvioSolicitud;
	private boolean evidenciaAprobacionContenido;
	private boolean evidenciaSuperacion;

}
