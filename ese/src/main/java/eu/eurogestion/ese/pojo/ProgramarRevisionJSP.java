package eu.eurogestion.ese.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class ProgramarRevisionJSP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2015266089036692724L;

	private String idCentroMedico;

	private String fechaRevision;
	private String causa;
	private String observaciones;
	private String nAsistentes;
	private Boolean isPersonal;
	private Boolean isSelectUser;

	private String idCargoSelect;
	private String nombreSelect;
	private String apellidoSelect;
	private String dniSelect;
	private Boolean isFormacionSelect;
	private Boolean isRevisionSelect;
	private Set<String> listaPersonalTotalSelect;
}
