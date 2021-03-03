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
import javax.persistence.OneToOne;
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
@Table(name = "informe_anomalias")
public class InformeAnomalias implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_informe_anomalias", unique = true, nullable = false)
	private Integer idInformeAnomalias;

	@Column(name = "codigo_informe", length = 20)
	private String codigoInforme;

	@Column(name = "descripcion_inspeccion", length = 200)
	private String descripcionInspeccion;

	@Column(name = "observaciones_inspeccion", length = 300)
	private String observacionesInspeccion;

	@Column(name = "descripcion_medidas_cautelares", length = 500)
	private String descripcionMedidasCautelares;

	@Column(name = "datos_complementarios", length = 500)
	private String datosCompplementarios;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo20", foreignKey = @ForeignKey(name = "fk_informe_anomalias_evidencia20"))
	@ToString.Exclude
	private Evidencia evidencia20;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_inspector", foreignKey = @ForeignKey(name = "fk_informe_anomalias_inspector"))
	@ToString.Exclude
	private Personal firmaInspector;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_inspector")
	private Date fechahoraFirmaInspector;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_responsable", foreignKey = @ForeignKey(name = "fk_informe_anomalias_responsable"))
	@ToString.Exclude
	private Personal firmaResponsable;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_responsable")
	private Date fechahoraFirmaResponsable;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "informeAnomalias")
	@ToString.Exclude
	private List<AnexoInformeAnomalia> listAnexoInformeAnomalia;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "informeAnomalias")
	@ToString.Exclude
	private List<Anomalia> listAnomalia;

	@OneToOne(mappedBy = "informeAnomalias")
	@ToString.Exclude
	private Is Is;

}