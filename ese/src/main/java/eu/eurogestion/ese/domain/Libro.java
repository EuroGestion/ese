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
@Table(name = "libro")
public class Libro implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_libro", unique = true, nullable = false)
	private Integer idLibro;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_subida", nullable = false, length = 10)
	private Date fechaSubida;

	@Column(name = "titulo", length = 200)
	private String titulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo_69", foreignKey = @ForeignKey(name = "fk_libro_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;

}
