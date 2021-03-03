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
@Table(name = "material")
public class Material implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_material", unique = true, nullable = false)
	private Integer idMaterial;

	@Column(name = "nve", length = 12, nullable = false)
	private String nve;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_baja", length = 10)
	private Date fechaBaja;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modelo_material", foreignKey = @ForeignKey(name = "fk_material_modelo_material"))
	@ToString.Exclude
	private ModeloMaterial modeloMaterial;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "material")
	@ToString.Exclude
	private List<MaterialComposicion> listMaterialComposicion;

}
