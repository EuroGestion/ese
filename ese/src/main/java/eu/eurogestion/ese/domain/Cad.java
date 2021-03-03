package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
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
@Table(name = "cad")
public class Cad implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_cad", unique = true, nullable = false)
	private Integer idCad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_is", nullable = false, foreignKey = @ForeignKey(name = "fk_iscc_is"))
	@ToString.Exclude
	private Is is;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal_controlado", nullable = false, foreignKey = @ForeignKey(name = "fk_cad_personal_controlado"))
	@ToString.Exclude
	private Personal personalControlado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_personal")
	private Date fechahoraFirmaPersonal;

	@Column(name = "consentimiento")
	private Boolean consentimiento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_cad", nullable = false, foreignKey = @ForeignKey(name = "fk_cad_tipo_cad"))
	@ToString.Exclude
	private TipoCad tipoCad;

	@Column(name = "codigo_informe", length = 200)
	private String codigoInforme;

	@Column(name = "medicamentos", length = 200)
	private String medicamentos;

	@Column(name = "observaciones_autorizacion", length = 200)
	private String observacionesAutorizacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal_delegado1", foreignKey = @ForeignKey(name = "fk_cad_delegado_1"))
	@ToString.Exclude
	private Personal delegado1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal_delegado2", foreignKey = @ForeignKey(name = "fk_cad_delegado_2"))
	@ToString.Exclude
	private Personal delegado2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo37", foreignKey = @ForeignKey(name = "fk_cad_evidencia_37"))
	@ToString.Exclude
	private Evidencia evidenciaTipo37;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo38", foreignKey = @ForeignKey(name = "fk_cad_evidencia_38"))
	@ToString.Exclude
	private Evidencia evidenciaTipo38;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@Column(name = "etilometro_pr1_valor")
	private BigDecimal etilometroPr1Valor;

	@Column(name = "etilometro_pr2_valor")
	private BigDecimal etilometroPr2Valor;

	@Temporal(TemporalType.TIME)
	@Column(name = "etilometro_pr1_hora")
	private Date etilometroPr1Hora;

	@Temporal(TemporalType.TIME)
	@Column(name = "etilometro_pr2_hora")
	private Date etilometroPr2Hora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "etilometro_pr1_resultado", foreignKey = @ForeignKey(name = "fk_cad_resultado_pr1"))
	@ToString.Exclude
	private ResultadoCad etilometroPr1Resultado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "etilometro_pr2_resultado", foreignKey = @ForeignKey(name = "fk_cad_resultado_pr2"))
	@ToString.Exclude
	private ResultadoCad etilometroPr2Resultado;

	@Column(name = "sangre_numero_muestra", length = 45)
	private String sangreNumeroMuestra;

	@Column(name = "sangre_analisis")
	private BigDecimal sangreAnalisis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sangre_resultado", foreignKey = @ForeignKey(name = "fk_cad_sangre_resultado"))
	@ToString.Exclude
	private ResultadoCad sangreResultado;

	@Column(name = "orina_numero_muestra", length = 45)
	private String orinaNumeroMuestra;

	@Column(name = "orina_analisis")
	private BigDecimal orinaAnalisis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orina_resultado", foreignKey = @ForeignKey(name = "fk_cad_orina_resultado"))
	@ToString.Exclude
	private ResultadoCad orinaResultado;

	@Column(name = "observaciones_muestras", length = 200)
	private String observacionesMuestras;

	@Column(name = "observaciones_laboratorio", length = 200)
	private String observacionesLaboratorio;

	@Column(name = "lugar_identificacion", length = 200)
	private String lugarIdentificacion;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_identificaciones")
	private Date fechahoraIdentificaciones;

	@Column(name = "nombre_personal_medico", length = 200)
	private String nombrePersonalMedico;

	@Column(name = "documento_personal_medico", length = 14)
	private String documentoPersonalMedico;

	@Column(name = "nombre_delegado_seguridad", length = 200)
	private String nombreDelegadoSeguridad;

	@Column(name = "documento_delegado_seguridad", length = 14)
	private String documentoDelegadoSeguridad;

	@Column(name = "nombre_delegado_adif", length = 200)
	private String nombreDelegadoADIF;

	@Column(name = "documento_delegado_adif", length = 14)
	private String documentoDelegadoADIF;

	@Column(name = "nombre_tecnico_ciaf", length = 200)
	private String nombreTecnicoCIAF;

	@Column(name = "documento_tecnico_ciaf", length = 14)
	private String documentoTecnicoCIAF;

	@Column(name = "medidas_cautelares", length = 400)
	private String medidasCautelares;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_centro_medico", foreignKey = @ForeignKey(name = "fk_cad_compania"))
	@ToString.Exclude
	private Compania centroMedico;

}
