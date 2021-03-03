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
@Table(name = "is_informes")
public class Is implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_is", unique = true, nullable = false)
	private Integer idIs;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_creador", nullable = false, foreignKey = @ForeignKey(name = "fk_is_creador"))
	@ToString.Exclude
	private Personal creador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tren", foreignKey = @ForeignKey(name = "fk_is_tren"))
	@ToString.Exclude
	private Tren tren;

	@Column(name = "lugar", length = 50)
	private String lugar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_inspeccion", nullable = false, foreignKey = @ForeignKey(name = "fk_is_tipo_inspeccion"))
	@ToString.Exclude
	private TipoInspeccion tipoInspeccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_inspector", nullable = false, foreignKey = @ForeignKey(name = "fk_is_inspector"))
	@ToString.Exclude
	private Personal inspector;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_inspeccion", nullable = false, foreignKey = @ForeignKey(name = "fk_is_estado_inspeccion"))
	@ToString.Exclude
	private EstadoInspeccion estadoInspeccion;

	@Column(name = "num_referencia", length = 45)
	private String numReferencia;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_inspeccion")
	private Date fechaInspeccion;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_informe_anomalias", foreignKey = @ForeignKey(name = "fk_is_informe_anomalias"))
	@ToString.Exclude
	private InformeAnomalias informeAnomalias;

	@OneToOne(mappedBy = "is")
	@ToString.Exclude
	private Iso ISO;

	@OneToOne(mappedBy = "is")
	@ToString.Exclude
	private Iscc ISCC;

	@OneToOne(mappedBy = "is")
	@ToString.Exclude
	private Iseet ISEET;

	@OneToOne(mappedBy = "is")
	@ToString.Exclude
	private Ismp ISMP;

	@OneToOne(mappedBy = "is")
	@ToString.Exclude
	private Cad cad;
}
