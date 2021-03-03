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
@Table(name = "toma_servicio")
public class TomaServicio implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_toma_servicio", unique = true, nullable = false)
	private Integer idTomaServicio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", nullable = false, foreignKey = @ForeignKey(name = "fk_toma_servicio_personal"))
	@ToString.Exclude
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tren", nullable = false, foreignKey = @ForeignKey(name = "fk_toma_servicio_tren"))
	@ToString.Exclude
	private Tren tren;

	@Column(name = "nve", length = 12, nullable = false)
	private String nve;

	@Column(name = "lugar_inspeccion", length = 50, nullable = false)
	private String lugarInspeccion;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha")
	private Date fecha;

	@Temporal(TemporalType.TIME)
	@Column(name = "hora")
	private Date hora;

	@Column(name = "motivo", length = 50)
	private String motivo;

	@Column(name = "realizar_accion", nullable = false)
	private Boolean realizarAccion;

	@Column(name = "documentacion_reglamentaria", nullable = false)
	private Boolean documentacionReglamentaria;

	@Column(name = "libro_telefonemas", nullable = false)
	private Boolean libroTelefonemas;

	@Column(name = "libro_averias", nullable = false)
	private Boolean libroAverias;

	@Column(name = "dotacion_utiles_servicio", nullable = false)
	private Boolean dotacionUtilesServicio;

	@Column(name = "senalizacion_cabeza_cola", nullable = false)
	private Boolean senalizacionCabezaCola;

	@Column(name = "visibilidad_adecuada", nullable = false)
	private Boolean visibilidadAdecuada;

	@Column(name = "anomalias_rodaje_caja", nullable = false)
	private Boolean anomaliasRodajeCaja;

	@Column(name = "anomalias_suspension", nullable = false)
	private Boolean anomaliasSuspension;

	@Column(name = "anomalias_choque_traccion", nullable = false)
	private Boolean anomaliasChoqueTraccion;

	@Column(name = "estado_precintos", nullable = false)
	private Boolean estadoPrecintos;

	@Column(name = "posicion_palanca_cambiador", nullable = false)
	private Boolean posicionPalancaCambiador;

	@Column(name = "frenos_estacionamiento", nullable = false)
	private Boolean frenosEstacionamiento;

	@Column(name = "configuracion_frenado", nullable = false)
	private Boolean configuracionFrenado;

	@Column(name = "dispositivo_vigilancia_hm", nullable = false)
	private Boolean dispositivoVigilanciaHm;

	@Column(name = "valvula_emergencia_seta", nullable = false)
	private Boolean valvulaEmergenciaSeta;

	@Column(name = "pruebas_freno", nullable = false)
	private Boolean pruebasFreno;

	@Column(name = "prueba_inversion_marcha", nullable = false)
	private Boolean pruebaInversionMarcha;

	@Column(name = "asfa_correcto", nullable = false)
	private Boolean asfaCorrecto;

	@Column(name = "equipo_radiotelefonia", nullable = false)
	private Boolean equipoRadiotelefonia;

	@Column(name = "inspeccion_visual", nullable = false)
	private Boolean inspeccionVisual;

	@Column(name = "datos_documento_tren", nullable = false)
	private Boolean datosDocumentoTren;

	@Column(name = "libro_telefonemas_relevo", nullable = false)
	private Boolean libroTelefonemasRelevo;

	@Column(name = "no_existen_notificaciones", nullable = false)
	private Boolean noExistenNotificaciones;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo77", foreignKey = @ForeignKey(name = "fa_toma_servicio_evidencia_tipo77"))
	@ToString.Exclude
	private Evidencia evidencia77;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_toma", foreignKey = @ForeignKey(name = "fk_toma_servicio_estado"))
	@ToString.Exclude
	private EstadoHistorico estadoToma;
}
