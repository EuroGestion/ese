package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "iso")
public class Iso implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_iso", unique = true, nullable = false)
	private Integer idIso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_is", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_is"))
	@ToString.Exclude
	private Is is;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_maquinista", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_maquinista"))
	@ToString.Exclude
	private Personal maquinista;

	@Temporal(TemporalType.TIME)
	@Column(name = "inicio_servicio")
	private Date horaInicioServicio;

	@Temporal(TemporalType.TIME)
	@Column(name = "fin_servicio")
	private Date horaFinServicio;

	@Column(name = "observaciones", length = 200)
	private String observacionesCirculacionFerroviaria;

	@Column(name = "nve", length = 12)
	private String nveLocomotora;

	@Column(name = "tiempo_descanso", length = 50)
	private String tiempoDescanso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lnm_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_lnm_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme libroNormasMaquinistaLNMVerificacion;

	@Column(name = "lnm_observaciones", length = 45)
	private String libroNormasMaquinistaLNMObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lim_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_lim_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme libroItinerarioMaquinistaLIMVerificacion;

	@Column(name = "lim_observaciones", length = 45)
	private String libroItinerarioMaquinistaLIMObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "333_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_333_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme llave333Verificacion;

	@Column(name = "333_observaciones", length = 45)
	private String llave333Observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cuadradillo_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_cuadra_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme llaveCuadradilloVerificacion;

	@Column(name = "cuadradillo_observaciones", length = 45)
	private String llaveCuadradilloObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "licencia_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_licencia_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme licenciaOTituloConducccionVerificacion;

	@Column(name = "licencia_observaciones", length = 45)
	private String licenciaOTituloConducccionObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doc_regla_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_doc_regla_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme docReglamentariaVerificacion;

	@Column(name = "doc_regla_observaciones", length = 45)
	private String docReglamentariaObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movil_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_movil_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme telefonoMovilVerificacion;

	@Column(name = "movil_observaciones", length = 45)
	private String telefonoMovilObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tableta_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_tableta_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme tabletaUOrdenadorPortatilVerificacion;

	@Column(name = "tableta_observaciones", length = 45)
	private String tabletaUOrdenadorPortatilObservaciones;

	@Column(name = "observaciones_dot_personal", length = 200)
	private String observacionesDotacionPersonal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "senales_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_senales_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme senalesColaOChapasReflectantesVerificacion;

	@Column(name = "senales_numero")
	private Integer senalesColaOChapasReflectantesNumero;

	@Column(name = "senales_observacones", length = 45)
	private String senalesColaOChapasReflectantesObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "linternas_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_linterna_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme linternasLucesBlancoRojoVerificacion;

	@Column(name = "linternas_numero")
	private Integer linternasLucesBlancoRojoNumero;

	@Column(name = "linternas_observaciones", length = 45)
	private String linternasLucesBlancoRojoObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "banderines_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_banderines_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme banderinesRojosVerificacion;

	@Column(name = "banderines_numero")
	private Integer banderinesRojosNumero;

	@Column(name = "banderines_observaciones", length = 45)
	private String banderinesRojosObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calces_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_calces_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme calcesAntiderivaVerificacion;

	@Column(name = "calces_numero")
	private Integer calcesAntiderivaNumero;

	@Column(name = "calces_observaciones", length = 45)
	private String calcesAntiderivaObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "barras_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_barras_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme barrasOUtilesCortocircuitoVerificacion;

	@Column(name = "barras_numero")
	private Integer barrasOUtilesCortocircuitoNumero;

	@Column(name = "barras_observaciones", length = 45)
	private String barrasOUtilesCortocircuitoObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "llaves_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_llaves_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme juegoLlavesCerraduraVehiculosVerificacion;

	@Column(name = "llaves_numero")
	private Integer juegoLlavesCerraduraVehiculosNumero;

	@Column(name = "llaves_observacones", length = 45)
	private String juegoLlavesCerraduraVehiculosObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manual_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_manual_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme manualConduccionMaterialTraccionVerificacion;

	@Column(name = "manual_numero")
	private Integer manualConduccionMaterialTraccionNumero;

	@Column(name = "manual_observaciones", length = 45)
	private String manualConduccionMaterialTraccionObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "libro_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_libro_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme libroAveriasVerificacion;

	@Column(name = "libro_numero")
	private Integer libroAveriasNumero;

	@Column(name = "libro_observaciones", length = 45)
	private String libroAveriasObservaciones;

	@Column(name = "observaciones_dot_locomotora", length = 200)
	private String observacionesDotacionLocomotora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destreza_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_destreza_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme destrezaConduccionVerificacion;

	@Column(name = "destreza_observaciones", length = 45)
	private String destrezaConduccionObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "normativa_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_normativa_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme cumpleDocumentacionYNormativaVerificacion;

	@Column(name = "normativa_observaciones", length = 45)
	private String cumpleDocumentacionYNormativaObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tiempos_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_is_tiempos_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme cumpleTiemposMaximosConduccionVerificacion;

	@Column(name = "tiempos_observaciones", length = 45)
	private String cumpleTiemposMaximosConduccionObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dispositivos_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_dispositivos_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme cumpleUsoAdecuadoDispositivosVerificacion;

	@Column(name = "dispositivos_observaciones", length = 45)
	private String cumpleUsoAdecuadoDispositivosObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operaciones_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_operaciones_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme suficienteYCorrectaFormacionOperacionesVerificacion;

	@Column(name = "operaciones", length = 45)
	private String suficienteYCorrectaFormacionOperacionesObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "antes_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_antes_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme antesServicioVerificacion;

	@Column(name = "antes_observaciones", length = 45)
	private String antesServicioObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tras_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_iso_tras_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme trasServicioVerificacion;

	@Column(name = "tras_observacione", length = 45)
	private String trasServicioObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "relevo_estado", nullable = false, foreignKey = @ForeignKey(name = "fk_is_relevo_verif"))
	@ToString.Exclude
	private EstadoVerificacionInforme enRelevoVerificacion;

	@Column(name = "relevo_observaciones", length = 45)
	private String enRelevoObservaciones;

	@Column(name = "observaciones_acompa", length = 200)
	private String observacionesAcompa;

	@Column(name = "medidas_cautelares", length = 400)
	private String medidasCautelaresAdoptadas;

	@Column(name = "anexos", length = 400)
	private String documentosAnexos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo07", foreignKey = @ForeignKey(name = "fk_iso_evidencia_tipo07"))
	@ToString.Exclude
	private Evidencia evidencia7;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo09", foreignKey = @ForeignKey(name = "fk_iso_evidencia_tipo09"))
	@ToString.Exclude
	private Evidencia evidencia9;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_inspector", foreignKey = @ForeignKey(name = "fk_iso_firma_inspector"))
	@ToString.Exclude
	private Personal firmaInspector;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_inspector")
	private Date fechahoraFirmaInspector;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_responsable", foreignKey = @ForeignKey(name = "fk_iso_firma_responsable"))
	@ToString.Exclude
	private Personal firmaResponsable;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_responsable")
	private Date fechahoraFirmaResponsable;
}
