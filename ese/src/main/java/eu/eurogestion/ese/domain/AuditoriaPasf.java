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
@Table(name = "auditoria_pasf")
public class AuditoriaPasf implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_auditoria_psf", unique = true, nullable = false)
	private Integer idAuditoriaPasf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pasf", nullable = false, foreignKey = @ForeignKey(name = "pk_auditoria_pasf_pasf"))
	@ToString.Exclude
	private Pasf pasf;

	@Column(name = "descripcion", length = 250)
	private String descripcion;

	@Column(name = "duracion")
	private Integer duracion;
}
