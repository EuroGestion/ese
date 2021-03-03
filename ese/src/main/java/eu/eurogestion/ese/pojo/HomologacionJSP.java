package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class HomologacionJSP {
	private String idCompaniaHomologacion;
	private String idProveedor;
	private Integer idEstadoProveedor;
	private boolean lectura;
	private MultipartFile evidencia;
	private String descripcionEvidencia;
	private String idAnexoProveedor;
	private Boolean resultadoHomologacion;
	private String fechaHomologacion;

	private String descripcionEvidenciaEnvioInformacion;
	private String descripcionEvidenciaInformacionRecibida;
	private String descripcionEvidenciaSolicitudDocumentacion;
	private String descripcionEvidenciaDocumentacionRecibida;
	private String descripcionEvidenciaResultadoHomologacion;
	private String descripcionEvidenciaComunicacion;
	private String page;

}
