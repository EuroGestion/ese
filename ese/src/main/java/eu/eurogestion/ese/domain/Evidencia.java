package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

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

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "evidencia")
public class Evidencia implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_evidencia", unique = true, nullable = false)
	private Integer idEvidencia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_evidencia", nullable = false, foreignKey = @ForeignKey(name = "fk_evidencia_tipo_evidencia"))
	@ToString.Exclude
	private TipoEvidencia tipoEvidencia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento", nullable = false, foreignKey = @ForeignKey(name = "fk_evidencia_documento"))
	@ToString.Exclude
	private Documento documento;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia")
	@ToString.Exclude
	private List<Titulo> listTitulo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia1")
	@ToString.Exclude
	private List<Curso> listCurso01;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia2")
	@ToString.Exclude
	private List<Curso> listCurso02;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia")
	@ToString.Exclude
	private List<CursoAlumno> listCursoAlumno;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia")
	@ToString.Exclude
	private List<RevisionPsico> listRevisionPsico;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia")
	@ToString.Exclude
	private List<Suspension> listSuspension;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia7")
	@ToString.Exclude
	private List<Iso> listIso07;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evidencia9")
	@ToString.Exclude
	private List<Iso> listIso09;
}