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
@Table(name = "material_composicion")
public class MaterialComposicion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_material_composicion", unique = true, nullable = false)
	private Integer idMaterialComposicion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_material", foreignKey = @ForeignKey(name = "fk_material_composicion_material"))
	@ToString.Exclude
	private Material material;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_Composicion", foreignKey = @ForeignKey(name = "fk_material_composicion_composicion"))
	@ToString.Exclude
	private Composicion composicion;

}
