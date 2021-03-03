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
@Table(name = "modelo_material")
public class ModeloMaterial implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_modelo_material", unique = true, nullable = false)
	private Integer idModeloMaterial;

	@Column(name = "serie", length = 3, nullable = false)
	private String serie;

	@Column(name = "subserie", length = 3, nullable = false)
	private String subserie;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_material", nullable = false, foreignKey = @ForeignKey(name = "fk_modelo_material_tipo"))
	@ToString.Exclude
	private TipoMaterial tipoMaterial;
	
	@Column(name = "notas", length = 12, nullable = false)
	private String notas;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloMaterial")
	@ToString.Exclude
	private List<Curso> listCurso;
}
