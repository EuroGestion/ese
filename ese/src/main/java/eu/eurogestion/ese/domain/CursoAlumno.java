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
@Table(name = "curso_alumno")
public class CursoAlumno implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_curso_alumno", unique = true, nullable = false)
	private Integer idCursoAlumno;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_alumno_curso"))
	@ToString.Exclude
	private Curso curso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_alumno_personal"))
	@ToString.Exclude
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_curso_alumno", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_alumno_estado_curso_alumno"))
	@ToString.Exclude
	private EstadoCursoAlumno estadoCursoAlumno;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo03", foreignKey = @ForeignKey(name = "fk_curso_alumno_evidencia_tipo03"))
	@ToString.Exclude
	private Evidencia evidencia;
}
