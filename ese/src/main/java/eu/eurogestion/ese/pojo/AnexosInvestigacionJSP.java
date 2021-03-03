package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class AnexosInvestigacionJSP {
	private Integer idInvestigacion;
	private Integer idEstadoInvestigacion;
	private String numIdentificacion;
	private String lugar;
	private String fechaInspeccion;
	private String hora;
	private String causa;

	private MultipartFile evidenciaDocAnexo;
	private String descripcionEvidenciaDocAnexo;
	private String idAnexo;

	private MultipartFile evidenciaMedidasAdoptadas;
	private String descripcionEvidenciaMedidasAdoptadas;
	private String idMedidasAdoptadas;
	private String pageMedidasAdoptadas;
	private String pageAnexos;

}
