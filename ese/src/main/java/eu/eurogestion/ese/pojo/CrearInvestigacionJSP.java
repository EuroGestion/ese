package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class CrearInvestigacionJSP {
	private Integer idInvestigacion;
	private Integer idEstadoInvestigacion;
	private String numeroIdentificacion;
	private String idEmpresa;
	private String idTipoInvestigacion;
	private String idCausa;
	private String fecha;
	private String hora;
	private String lugar;
	private String descripcionSucesoInvestigacion;
	private String condicionesAtmosfericasInvestigacion;
	private String circulacionesImplicadasInvestigacion;
	private String composicionTrenesInvestigacion;
	private String danhiosMaterialRodanteInvestigacion;
	private String danhiosInfraestructuraInvestigacion;
	private String perturbacionesServicio;
	private String previsionesRestablecimiento;
	private String primerasMedidasAdoptadas;
	private String notificacionesEfectuadas;
	private String heridosVictimaTren;
	private String muertesVictimaTren;
	private String heridosVictimaAjenaTren;
	private String muertesVictimaAjenaTren;
	private Boolean intervieneCIAF;
	private String idResponsableSeguridad;
	private String nombreResponsableCIAF;
	private String telefonoResponsableCIAF;
	private String correoResponsableCIAF;
	private Boolean delegaInvestigacion;
	private String idDelegadoInvestigacion;
	private String pantallaVolver;
	private MultipartFile evidenciaInformeRecibidoCIAF;
	private boolean lectura;

}