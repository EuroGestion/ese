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
@Table(name = "anomalia")
public class Anomalia implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_anomalia", unique = true, nullable = false)
	private Integer idAnomalia;

	@Column(name = "descripcion_situacion", length = 300)
	private String descripcionSituacion;

	@Column(name = "medidas_adoptadas", length = 300)
	private String medidasAdoptadas;

	@Column(name = "datos_tecnicos", length = 300)
	private String datosTecnicos;

	@Column(name = "limitaciones_explotacion", length = 300)
	private String limitacionesExplotacion;

	@Column(name = "control_seguimiento", length = 300)
	private String controlSeguimiento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_informe_anomalias", nullable = false, foreignKey = @ForeignKey(name = "fk_anomalia_informe_anomalia"))
	@ToString.Exclude
	private InformeAnomalias informeAnomalias;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_anomalia", nullable = false, foreignKey = @ForeignKey(name = "fk_anomalia_tipo_anomalia"))
	@ToString.Exclude
	private TipoAnomalia tipoAnomalia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_anomalia", nullable = false, foreignKey = @ForeignKey(name = "fx_anomalia_estado"))
	@ToString.Exclude
	private EstadoAnomalia estadoAnomalia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable_resolucion", nullable = false, foreignKey = @ForeignKey(name = "fk_anomalia_personal"))
	@ToString.Exclude
	private Personal responsableResolucion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencial_tipo_12", foreignKey = @ForeignKey(name = "fk_anomalia_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_apertura", nullable = false, length = 10)
	private Date fechaApertura;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_resolucion", length = 10)
	private Date fechaResolucion;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anomalia")
	@ToString.Exclude
	private List<AnexoAnomalia> listAnexoAnomalia;
}
