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
@Table(name = "historico_maquinista")
public class HistoricoMaquinista implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_historico_maquinista", unique = true, nullable = false)
	private Integer idHistoricoMaquinista;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", length = 10, nullable = false)
	private Date fecha;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_historico", nullable = false, foreignKey = @ForeignKey(name = "fk_historico_estado_historico"))
	@ToString.Exclude
	private EstadoHistorico estadoHistorico;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tren", nullable = false, foreignKey = @ForeignKey(name = "fk_historico_tren"))
	@ToString.Exclude
	private Tren tren;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", nullable = false, foreignKey = @ForeignKey(name = "fk_historico_maquinista"))
	@ToString.Exclude
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pto_origen", nullable = false, foreignKey = @ForeignKey(name = "fk_historico_pto_origen"))
	@ToString.Exclude
	private PuntoInfraestructura ptoOrigen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pto_fin", nullable = false, foreignKey = @ForeignKey(name = "fk_historico_pto_fin"))
	@ToString.Exclude
	private PuntoInfraestructura ptoFin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "predecesor", foreignKey = @ForeignKey(name = "fk_historico_predecesor"))
	@ToString.Exclude
	private Personal predecesor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucesor", foreignKey = @ForeignKey(name = "fk_historico_sucesor"))
	@ToString.Exclude
	private Personal sucesor;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hora_toma", nullable = false)
	private Date horaToma;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hora_deje", nullable = false)
	private Date horaDeje;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hora_ini_refrigerio")
	private Date horaIniRefrigerio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hora_fin_refrigerio")
	private Date horaFinRefrigerio;

	@Column(name = "estacion_refrigerio", length = 50)
	private String estacionRefrigerio;

	@Column(name = "practico")
	private Boolean practico;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hora_salida", nullable = false)
	private Date horaSalida;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hora_llegada", nullable = false)
	private Date horaLlegada;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inicio_viaje_antes")
	private Date inicioViajeAntes;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fin_viaje_antes")
	private Date finViajeAntes;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inicio_viaje_despues")
	private Date inicioViajeDespues;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fin_viaje_despues")
	private Date finViajeDespues;

	@Column(name = "inicio_em2000", length = 15)
	private String inicioEm2000;

	@Column(name = "fin_em2000", length = 15)
	private String finEm2000;

	@Column(name = "inicio_teloc", length = 15)
	private String inicioTeloc;

	@Column(name = "fin_teloc", length = 15)
	private String finTeloc;

	@Column(name = "nivel_combustible_inicio", length = 6)
	private String nivelCombustibleInicio;

	@Column(name = "nivel_combustible_fin", length = 6)
	private String nivelCombustibleFin;

	@Column(name = "kw_h_fin", length = 15)
	private String kwHFin;

	@Column(name = "estacionamiento_loco", length = 50)
	private String estacionamientoLoco;

	@Column(name = "senales_cola_reflectante", length = 50)
	private String senalesColaReflectante;

	@Column(name = "senales_cola_luminosas", length = 50)
	private String senalesColaLuminosas;

	@Column(name = "linternas", length = 50)
	private String linternas;

	@Column(name = "banderinies_rojos", length = 50)
	private String banderiniesRojos;

	@Column(name = "calces_antideriva", length = 50)
	private String calcesAntideriva;

	@Column(name = "barras_cortocircuito", length = 50)
	private String barrasCortocircuito;

	@Column(name = "llave_trinquete", length = 50)
	private String llaveTrinquete;

	@Column(name = "otros", length = 50)
	private String otros;

	@Column(name = "estacion_repostaje", length = 30)
	private String estacionRepostaje;

	@Column(name = "litros_repostados", length = 10)
	private String litrosRepostados;

	@Column(name = "averias_locomotora", length = 100)
	private String averiasLocomotora;

	@Column(name = "averias_remolcado", length = 100)
	private String averiasRemolcado;

	@Column(name = "otra_informacion", length = 100)
	private String otraInformacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_composicion", nullable = false, foreignKey = @ForeignKey(name = "fk_historico_composicion"))
	@ToString.Exclude
	private Composicion composicion;

	@Column(name = "nve", length = 12, nullable = false)
	private String nve;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo76", foreignKey = @ForeignKey(name = "fk_iso_evidencia_tipo76"))
	@ToString.Exclude
	private Evidencia evidencia76;

}
