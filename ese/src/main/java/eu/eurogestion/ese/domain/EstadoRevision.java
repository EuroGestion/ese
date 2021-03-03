package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "estado_revision")
public class EstadoRevision implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_estado_revision", unique = true, nullable = false)
	private Integer idEstadoRevision;

	@Column(name = "valor", length = 45, nullable = false)
	private String valor;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estadoRevision")
	@ToString.Exclude
	private List<RevisionPsico> listRevisionPsico;

}
