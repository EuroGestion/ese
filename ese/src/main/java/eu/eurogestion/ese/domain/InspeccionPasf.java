package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "inspeccion_pasf")
public class InspeccionPasf implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_inspeccion_pasf", unique = true, nullable = false)
	private Integer idInspeccionPasf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pasf", nullable = false, foreignKey = @ForeignKey(name = "id_inspeccion_pasf"))
	@ToString.Exclude
	private Pasf pasf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_inspeccion", nullable = false, foreignKey = @ForeignKey(name = "fk_inspeccion_pasf_tipo_inspeccion"))
	@ToString.Exclude
	private TipoInspeccion tipoInspeccion;

	@Column(name = "descripcion", length = 250)
	private String descripcion;

	@Column(name = "num_inspecciones")
	private Integer numInspecciones;

	@Column(name = "duracion")
	private Integer duracion;
}
