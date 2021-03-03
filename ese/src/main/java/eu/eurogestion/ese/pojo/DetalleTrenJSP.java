package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleTrenJSP {
	private String idTren;
	private boolean lectura;
	private boolean esEspecial;
	private boolean tieneDocumento;

	private String numeroTren;
	private String idTramo;
	private String idOrigen;
	private String idDestino;
	private String nombre;
	private String horaInicio;
	private String horaFin;
	private String idComposicion;
	private String observaciones;
	private String page;
	private MultipartFile documento;

}
