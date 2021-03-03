package eu.eurogestion.ese.pojo;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class ProgramarFormacionJSP {
	private String idTipoCurso;
	private Boolean isNormativa;
	private String normativa;
	private Boolean isInfraestructura;
	private String infraestructura;
	private Boolean isMaterial;
	private String idMaterial;
	private String idCentroFormacion;
	private String fechaInicio;
	private String fechaFin;
	private String titulo;
	private String horas;
	private String observaciones;
	private String nAsistentes;
	private MultipartFile evidencia;
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
