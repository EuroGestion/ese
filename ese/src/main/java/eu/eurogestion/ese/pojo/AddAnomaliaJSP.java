package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class AddAnomaliaJSP {
	private String idInformeAnomalia;
	private String idAnomalia;
	private String numReferencia;
	private String fechaInspeccion;
	private String nombreInspector;
	private String idTipoAnomalia;
	private String idEstado;
	private String descripcionAnomalia;
	private String medidasAdoptadas;
	private String datosTecnicos;
	private String limitaciones;
	private String controlSeguimiento;
	private String idResponsable;
	private MultipartFile evidencia;
	private String descripcionEvidencia;
	private String idAnexo;

	private String codigoInforme;
	private String descripcion;
	private String observaciones;
	private String medidasCautelares;
	private Integer idTipoInspeccion;
	private boolean lectura;
	private boolean lecturaInformeAnomalia;

	private String paginaVuelta;
	private String idPersonal;
	private String page;

}
