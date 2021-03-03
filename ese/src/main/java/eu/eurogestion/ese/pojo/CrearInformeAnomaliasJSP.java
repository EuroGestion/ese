package eu.eurogestion.ese.pojo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class CrearInformeAnomaliasJSP {
	private String codigoInforme;
	private String idInformeAnomalias;
	private String numeroInspeccion;
	private String fechaInspeccion;
	private String nombreInspector;
	private String descripcionDatosInspeccion;
	private String observacionesDatosInspeccion;
	private String medidasCautelares;
	private MultipartFile evidencia;
	private String descripcionEvidencia;
	private String idAnexo;
	private String anomaliasAbiertas;
	private String idAnomalia;
	private List<String> anexosInformesAnomalias;
	private List<String> anomaliasInformesAnomalias;
	private Integer idTipoInspeccion;
	private boolean lectura;
	private boolean firmado;
	private Integer idFirmaInspectorSeguridad;
	private String nombreInspectorSeguridad;
	private String passwordInspectorSeguridad;
	private Integer idFirmaResponsableSeguridad;
	private String nombreResponsableSeguridad;
	private String passwordResponsableSeguridad;

	private boolean tieneEvidencia;

	private String idPersonal;
	private String paginaVuelta;
	private String pageAnexos;
	private String pageAnomalias;

}