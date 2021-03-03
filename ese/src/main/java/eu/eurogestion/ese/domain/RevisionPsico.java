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
@Table(name = "revision_psico")
public class RevisionPsico implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_revision_psico", unique = true, nullable = false)
	private Integer idRevisionPsico;

	@Column(name = "causa", length = 200, nullable = false)
	private String causa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_revision", foreignKey = @ForeignKey(name = "fk_revision_psico_estado_revision"))
	@ToString.Exclude
	private EstadoRevision estadoRevision;

	@Temporal(TemporalType.DATE)
	@Column(name = "valido_desde", length = 10, nullable = false)
	private Date validoDesde;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_caducidad", length = 10)
	private Date fechaCaducidad;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_realizacion", length = 10, nullable = false)
	private Date fechaRealizacion;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", nullable = false, foreignKey = @ForeignKey(name = "fk_revision_psico_personal"))
	@ToString.Exclude
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compania", nullable = false, foreignKey = @ForeignKey(name = "fk_revision_psico_compania"))
	@ToString.Exclude
	private Compania compania;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo41", foreignKey = @ForeignKey(name = "fk_revision_psico_evidencia_41"))
	@ToString.Exclude
	private Evidencia evidencia;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "revisionPsico")
	@ToString.Exclude
	private List<Titulo> listTitulo;
}
