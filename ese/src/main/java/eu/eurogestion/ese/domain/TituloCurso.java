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
@Table(name = "titulo_cursos")
public class TituloCurso implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_titulo_cursos", unique = true, nullable = false)
	private Integer idTituloCurso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_titulo", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_cursos_titulo"))
	@ToString.Exclude
	private Titulo titulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_cursos_curso"))
	@ToString.Exclude
	private Curso curso;
}
