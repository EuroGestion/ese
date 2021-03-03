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
@Table(name = "responsable")
public class Responsable implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_responsable", unique = true, nullable = false)
	private Integer idResponsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", nullable = false, foreignKey = @ForeignKey(name = "fk_responsables_personal"))
	@ToString.Exclude
	private Personal personal;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_inicio", nullable = false)
	private Date fechaInicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_fin")
	private Date fechaFin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_responsable", nullable = false, foreignKey = @ForeignKey(name = "fk_responsable_tipo_responsable"))
	@ToString.Exclude
	private TipoResponsable tipoResponsable;
}
