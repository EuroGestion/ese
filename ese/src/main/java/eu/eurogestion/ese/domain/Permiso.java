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
@Table(name = "permiso")
public class Permiso implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_permiso", unique = true, nullable = false)
	private Integer idPermiso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_permiso", nullable = false, foreignKey = @ForeignKey(name = "fk_permiso_tipo_permiso"))
	@ToString.Exclude
	private TipoPermiso tipoPermiso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_opcion", nullable = false, foreignKey = @ForeignKey(name = "fk_permiso_opcion"))
	@ToString.Exclude
	private Opcion opcion;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "permiso")
	@ToString.Exclude
	private List<RolPermiso> listRolPermiso;
}
