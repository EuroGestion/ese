package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "accidente")
public class Accidente implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_accidente", unique = true, nullable = false)
	private Integer idAccidente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compania", nullable = false, foreignKey = @ForeignKey(name = "fk_accidente_compania"))
	@ToString.Exclude
	private Compania compania;

	@Column(name = "numero_suceso", nullable = false)
	private String numeroSuceso;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_accidente", nullable = false, length = 10)
	private Date fechaAccidente;

	@Temporal(TemporalType.TIME)
	@Column(name = "hora_accidente", nullable = false)
	private Date horaAccidente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_accidente", nullable = false, foreignKey = @ForeignKey(name = "fk_accidente_tipo_accidente"))
	@ToString.Exclude
	private TipoAccidente tipoAccidente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "causa_accidente", nullable = false, foreignKey = @ForeignKey(name = "fk_accidente_causa"))
	@ToString.Exclude
	private CausaAccidente causaAccidente;

	@Column(name = "interviene_ciaf", nullable = false)
	private Boolean intervieneCiaf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_accidente", nullable = false, foreignKey = @ForeignKey(name = "fk_accidente_estado_accidente"))
	@ToString.Exclude
	private EstadoAccidente estadoAccidente;

	@Column(name = "lugar_accidente", length = 50)
	private String lugarAccidente;

	@Column(name = "descripcion", length = 500)
	private String descripcion;

	@Column(name = "condiciones_atmosfericas", length = 500)
	private String condicionesAtmosfericas;

	@Column(name = "circulaciones_implicadas", length = 500)
	private String circulacionesImplicadas;

	@Column(name = "composicion_trenes", length = 500)
	private String composicionTrenes;

	@Column(name = "fallecidos_tren")
	private Integer fallecidosTren;

	@Column(name = "fallecidos_ajenos")
	private Integer fallecidosAjenos;

	@Column(name = "heridos_tren")
	private Integer heridosTren;

	@Column(name = "heridos_ajenos")
	private Integer heridosAjenos;

	@Column(name = "danos_materiales_material", length = 300)
	private String danosMaterialesMaterial;

	@Column(name = "danos_materiales_infraestructura", length = 300)
	private String danosMaterialesInfraestructura;

	@Column(name = "investigador_ciaf_nombre", length = 90)
	private String investigadorCiafNombre;

	@Column(name = "investigador_ciaf_telefono", length = 14)
	private String investigadorCiafTelefono;

	@Column(name = "investigador_ciaf_email", length = 50)
	private String investigadorCiafEmail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable_seguridad", foreignKey = @ForeignKey(name = "fk_accidente_responsable_seguridad"))
	@ToString.Exclude
	private Personal responsableSeguridad;

	@Column(name = "perturbaciones_servicio", length = 500)
	private String perturbacionesServicio;

	@Column(name = "previsiones_restablecimiento", length = 500)
	private String previsionesRestablecimiento;

	@Column(name = "primeras_medidas", length = 500)
	private String primerasMedidas;

	@Column(name = "notificaciones_efectuadas", length = 500)
	private String notificacionesEfectuadas;

	@Column(name = "observaciones_ficha_accidente", length = 450)
	private String observacionesFichaAccidente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo50", foreignKey = @ForeignKey(name = "fk_accidente_evidencia_50"))
	@ToString.Exclude
	private Evidencia evidenciaTipo50;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo51", foreignKey = @ForeignKey(name = "fk_accidente_evidencia_51"))
	@ToString.Exclude
	private Evidencia evidenciaTipo51;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo60", foreignKey = @ForeignKey(name = "fk_accidente_evidencia_60"))
	@ToString.Exclude
	private Evidencia evidenciaTipo60;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo61", foreignKey = @ForeignKey(name = "fk_accidente_evidencia_61"))
	@ToString.Exclude
	private Evidencia evidenciaTipo61;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo63", foreignKey = @ForeignKey(name = "fk_accidente_evidencia_63"))
	@ToString.Exclude
	private Evidencia evidenciaTipo63;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo64", foreignKey = @ForeignKey(name = "fk_accidente_evidencia_64"))
	@ToString.Exclude
	private Evidencia evidenciaTipo64;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo65", foreignKey = @ForeignKey(name = "fk_accidente_evidencia_65"))
	@ToString.Exclude
	private Evidencia evidenciaTipo65;

	@Column(name = "se_delega_investigacion", nullable = false)
	private Boolean seDelegaInvestigacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_delegado", foreignKey = @ForeignKey(name = "fk_accidente_delegado"))
	@ToString.Exclude
	private Personal delegado;

	@Column(name = "observaciones_interviene_ciaf", length = 500)
	private String observacionesIntervieneCiaf;

	@Column(name = "numero_referencia_final", length = 45)
	private String numeroReferenciaFinal;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_informe_final", length = 10)
	private Date fechaInformeFinal;

	@Column(name = "hechos_suceso", length = 450)
	private String hechosSuceso;

	@Column(name = "hechos_circunstancias", length = 450)
	private String hechosCircunstancias;

	@Column(name = "hechos_danos", length = 450)
	private String hechosDanos;

	@Column(name = "hechos_circunstancias_externas", length = 450)
	private String hechosCircunstanciasExternas;

	@Column(name = "resumen_declaraciones", length = 450)
	private String resumenDeclaraciones;

	@Column(name = "resumen_sgs", length = 450)
	private String resumenSgs;

	@Column(name = "resumen_normativa", length = 450)
	private String resumenNormativa;

	@Column(name = "resumen_funcionamiento", length = 450)
	private String resumenFuncionamiento;

	@Column(name = "resumen_documentacion", length = 450)
	private String resumenDocumentacion;

	@Column(name = "resumen_interfaz", length = 450)
	private String resumenInterfaz;

	@Column(name = "resumen_otros", length = 450)
	private String resumenOtros;

	@Column(name = "analisis_descripcion", length = 450)
	private String analisisDescripcion;

	@Column(name = "analisis_deliberacion", length = 450)
	private String analisisDeliberacion;

	@Column(name = "analisis_conclusiones", length = 450)
	private String analisisConclusiones;

	@Column(name = "analisis_observaciones", length = 450)
	private String analisisObservaciones;

	@Column(name = "medidas_adoptadas", length = 450)
	private String medidasAdoptadas;

	@Column(name = "recomendaciones", length = 450)
	private String recomendaciones;

	@Column(name = "datos_complementarios", length = 450)
	private String datosComplementarios;

	@Column(name = "ficha_medidas_ano", length = 4)
	private String fichaMedidasAno;

	@Column(name = "ficha_medidas_ref_informe", length = 25)
	private String fichaMedidasRefInforme;

	@Column(name = "ficha_medidas_adoptadas", length = 450)
	private String fichaMedidasAdoptadas;

	@Column(name = "ficha_medidas_proyectadas", length = 450)
	private String fichaMedidasProyectadas;
	
	@Column(name = "ficha_medidas_observaciones", length = 450)
	private String fichaMedidasObservaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_ficha_accidente", foreignKey = @ForeignKey(name = "fk_accidente_firma_ficha_accidente"))
	@ToString.Exclude
	private Personal firmaFichaAccidente;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_ficha_accidente", length = 10)
	private Date fechahoraFirmaFichaAccidente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_ficha_estructura", foreignKey = @ForeignKey(name = "fk_accidente_firma_ficha_estructura"))
	@ToString.Exclude
	private Personal firmaFichaEstructura;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_ficha_estructura", length = 10)
	private Date fechahoraFirmaFichaEstructura;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_ficha_delegacion_responsable", foreignKey = @ForeignKey(name = "fk_accidente_firma_ficha_delegacion_responsable"))
	@ToString.Exclude
	private Personal firmaFichaDelegacionResponsable;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_ficha_delegacion_responsable", length = 10)
	private Date fechahoraFirmaFichaDelegacionResponsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_ficha_delegacion_delegado", foreignKey = @ForeignKey(name = "fk_accidente_id_firma_ficha_deleg_del"))
	@ToString.Exclude
	private Personal firmaFichaDelegacionDelegado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_ficha_delegacion_delegado", length = 10)
	private Date fechahoraFirmaFichaDelegacionDelegado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_informe_final_responsable", foreignKey = @ForeignKey(name = "fk_accidente_id_firma_informe_final_resp"))
	@ToString.Exclude
	private Personal firmaInformeFinalResponsable;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_informe_final_responsable", length = 10)
	private Date fechahoraFirmaInformeFinalResponsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_informe_final_delegado", foreignKey = @ForeignKey(name = "fk_accidente_id_firma_informe_final_delegado"))
	@ToString.Exclude
	private Personal firmaInformeFinalDelegado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_informe_final_delegado", length = 10)
	private Date fechahoraFirmaInformeFinalDelegado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_ficha_comentarios_ciaf", foreignKey = @ForeignKey(name = "fk_acidente_id_firma_ficha_comentarios_ciaf"))
	@ToString.Exclude
	private Personal firmaFichaComentariosCiaf;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_ficha_comentarios_ciaf", length = 10)
	private Date fechahoraFirmaFichaComentariosCiaf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_ficha_medidas", foreignKey = @ForeignKey(name = "fk_id_firma_ficha_medidas"))
	@ToString.Exclude
	private Personal firmaFichaMedidas;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_ficha_medidas", length = 10)
	private Date fechahoraFirmaFichaMedidas;

	@Column(name = "num_id_inf_ciaf", length = 25)
	private String numIdInfCiaf;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_inf_ciaf", length = 10)
	private Date fechaInfCiaf;

	@Column(name = "comentarios_al_inf_ciaf", length = 400)
	private String comentariosAlInfCiaf;

	@Column(name = "observaciones_al_inf_ciaf", length = 300)
	private String observacionesAlInfCiaf;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accidente")
	@ToString.Exclude
	private List<AnexoAccidente> listAnexoAccidente;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accidente")
	@ToString.Exclude
	private List<MedidaAccidente> listMedidaAccidente;

}
