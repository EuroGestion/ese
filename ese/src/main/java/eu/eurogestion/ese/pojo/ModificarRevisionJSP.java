package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class ModificarRevisionJSP {
	private String idRevision;
	private String causa;
	private String fechaRevision;
	private String fechaDocumento;
	private String fechaCaducidad;
	private String validoDesde;
	private String observaciones;
	private String resolucion;
	private MultipartFile evidencia;

}