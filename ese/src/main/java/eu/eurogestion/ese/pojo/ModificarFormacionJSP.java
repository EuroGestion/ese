package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class ModificarFormacionJSP {
	private String idCurso;
	private String fechaInicio;
	private String fechaFin;
	private String fechaDocumento;
	private String estado;
	private String titulo;
	private String centroFormacion;
	private MultipartFile evidencia;

}
