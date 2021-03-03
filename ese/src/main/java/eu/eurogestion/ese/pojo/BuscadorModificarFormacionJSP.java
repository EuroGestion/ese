package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorModificarFormacionJSP {
	private String idEstado;
	private String idCentroFormacion;
	private String fechaInicio;
	private String fechaFin;
	private String idCurso;
	private String page;
	private String order;
	private String direccion;

}
