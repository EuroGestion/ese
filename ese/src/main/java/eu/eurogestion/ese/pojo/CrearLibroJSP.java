package eu.eurogestion.ese.pojo;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class CrearLibroJSP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2015266089036692724L;

	private List<Integer> listPersonalesLibro;
	private Boolean usuariosSeleccionados;

	private String dni;
	private String nombre;
	private String apellido;
	private String idCargo;
	private String titulo;
	private String idPersonalAdd;
	private String idPersonalEliminar;
	private MultipartFile documento;

}
