package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class GenerarAccidenteJSP {
	private Integer idAccidente;
	private Integer idEstadoAccidente;
	private String numeroIdentificacion;
	private String idEmpresa;
	private String idTipoAccidente;
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
	private String observacionesFichaAccidente;
	private String heridosVictimaTren;
	private String muertesVictimaTren;
	private String heridosVictimaAjenaTren;
	private String muertesVictimaAjenaTren;
	private Boolean intervieneCIAF;
	private String idResponsableSeguridad;
	private String nombreResponsableCIAF;
	private String telefonoResponsableCIAF;
	private String correoResponsableCIAF;
	private String observacionesIntervieneCIAF;
	private String pagina;

	private Boolean firmadoFichaNotificacion;
	private Boolean firmadoInformeFinal;
	private Boolean confirmadoIntervieneCIAF;
	private Boolean confirmadoDelegaInvestigacion;

	private Boolean delegaInvestigacion;
	private String idDelegadoInvestigacion;
	private boolean lectura;

	private String numeroInformeFinal;
	private String fechaInformeFinal;
	private String sucesoInformeFinal;
	private String circunstanciasSucesoInformeFinal;
	private String danosInformeFinal;
	private String circunstanciasExternasInformeFinal;
	private String resumenDeclaracionesTestigosInformeFinal;
	private String sistemaGestionSeguridadInformeFinal;
	private String normativaInformeFinal;
	private String funcionamientoMaterialRodanteInformeFinal;
	private String documentacionGestionCirculacionInformeFinal;
	private String interfazHombreMaquinaInformeFinal;
	private String otrosSucesosAnterioresInformeFinal;
	private String descripcionDefinitivaInformeFinal;
	private String deliberacionInformeFinal;
	private String conclusionesInformeFinal;
	private String observacionesAdicionalesInformeFinal;
	private String medidasAdoptadasInformeFinal;
	private String recomendacionesInformeFinal;
	private String datosComplementariosInformeFinal;

	private Integer idFirmaFichaAccidente;
	private String nombreFirmaFichaAccidente;
	private String passwordFirmaFichaAccidente;
	private Integer idFirmaFichaEstructura;
	private String nombreFirmaFichaEstructura;
	private String passwordFirmaFichaEstructura;

	private Integer idFirmaFichaNotificacionResponsableSeguridad;
	private String nombreFirmaFichaNotificacionResponsableSeguridad;
	private String passwordFirmaFichaNotificacionResponsableSeguridad;
	private Integer idFirmaFichaNotificacionDelegadoSeguridad;
	private String nombreFirmaFichaNotificacionDelegadoSeguridad;
	private String passwordFirmaFichaNotificacionDelegadoSeguridad;

	private Integer idFirmaInformeFinalResponsableSeguridad;
	private String nombreFirmaInformeFinalResponsableSeguridad;
	private String passwordFirmaInformeFinalResponsableSeguridad;
	private Integer idFirmaInformeFinalDelegadoSeguridad;
	private String nombreFirmaInformeFinalDelegadoSeguridad;
	private String passwordFirmaInformeFinalDelegadoSeguridad;

	private String numIdentificacion;
	private String fechaCIAF;
	private MultipartFile evidencia;
	private String comentariosInformeCIAF;
	private String observacionesIntervencionCIAF;

	private Integer idFirmaFichaComentarios;
	private String nombreFirmaFichaComentarios;
	private String passwordFirmaFichaComentarios;

	private String annoReferencia;
	private String numRefInformeFinalCIAF;
	private String medidasAdoptadas;
	private String medidasProyectadas;
	private String observaciones;

	private Integer idFirmaFichaMedidas;
	private String nombreFirmaFichaMedidas;
	private String passwordFirmaFichaMedidas;

}