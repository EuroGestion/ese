package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class ProgramarInspeccionJSP {
	private String idTipoInspeccion;
	private String referencia;
	private String fechaInspeccion;
	private String idtren;
	private String lugar;
	private String idInspectorAsignado;
	private String idCreador;
}
