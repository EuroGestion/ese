package eu.eurogestion.ese.pojo;

import java.util.Date;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleTituloJSP {
	private String idPersonal;
	private String idTitulo;
	private String tipoTitulo;
	private String estadoTitulo;
	private Date validoDesde;
	private Date fechaCaducidad;
	private String restricciones;
	private String lugarTrabajo;
	private Boolean isSuspension;
	private String idSuspensionTitulo;
	private String idRevocacionTitulo;
}