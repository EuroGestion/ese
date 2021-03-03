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
@Table(name = "libro_personal")
public class LibroPersonal implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_libro_personal", unique = true, nullable = false)
	private Integer idLibroPersonal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_libro", foreignKey = @ForeignKey(name = "fk_libro_personal_libro"))
	@ToString.Exclude
	private Libro libro;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", foreignKey = @ForeignKey(name = "fk_libro_personal_personal"))
	@ToString.Exclude
	private Personal personal;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo_70", foreignKey = @ForeignKey(name = "fk_libro_personal_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;

}
