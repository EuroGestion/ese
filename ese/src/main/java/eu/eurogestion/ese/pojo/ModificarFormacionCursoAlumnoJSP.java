package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class ModificarFormacionCursoAlumnoJSP {
	private String estado;
	private String titulo;
	private String centroFormacion;
	private String idCursoAlumno;
	private String idPersonal;
	private String resolucion;
	private String fechaDocumento;
	private String validoDesde;
	private String fechaCaducidad;
	private MultipartFile evidencia;

}
