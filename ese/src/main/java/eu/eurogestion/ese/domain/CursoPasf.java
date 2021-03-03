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
@Table(name = "curso_pasf")
public class CursoPasf implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_curso_pasf", unique = true, nullable = false)
	private Integer idCursoPasf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pasf", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_pasf_pasf"))
	@ToString.Exclude
	private Pasf pasf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_pasf_categoria"))
	@ToString.Exclude
	private TipoCurso categoria;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cargo", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_pasf_cargo"))
	@ToString.Exclude
	private Cargo cargo;

	@Column(name = "descripcion", length = 250)
	private String descripcion;

	@Column(name = "num_asistentes")
	private Integer numAsistentes;

	@Column(name = "duracion")
	private Integer duracion;
}
