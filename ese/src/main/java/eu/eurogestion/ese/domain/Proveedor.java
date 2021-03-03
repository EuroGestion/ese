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
@Table(name = "proveedor")
public class Proveedor implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_proveedor", unique = true, nullable = false)
	private Integer idProveedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compania", nullable = false, foreignKey = @ForeignKey(name = "fk_proveedor_compania"))
	@ToString.Exclude
	private Compania compania;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_proveedor", nullable = false, foreignKey = @ForeignKey(name = "fk_proveedor_estado_proveedor"))
	@ToString.Exclude
	private EstadoProveedor estadoProveedor;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_resolucion", length = 10)
	private Date fechaResolucion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo67", foreignKey = @ForeignKey(name = "fk_proveedor_evidencia_tipo67"))
	@ToString.Exclude
	private Evidencia evidencia67;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo68", foreignKey = @ForeignKey(name = "fk_proveedor_evidencia_tipo68"))
	@ToString.Exclude
	private Evidencia evidencia68;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo69", foreignKey = @ForeignKey(name = "fk_proveedor_evidencia_tipo69"))
	@ToString.Exclude
	private Evidencia evidencia69;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo70", foreignKey = @ForeignKey(name = "fk_proveedor_evidencia_tipo70"))
	@ToString.Exclude
	private Evidencia evidencia70;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo72", foreignKey = @ForeignKey(name = "fk_proveedor_evidencia_tipo72"))
	@ToString.Exclude
	private Evidencia evidencia72;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo73", foreignKey = @ForeignKey(name = "fk_proveedor_evidencia_tipo73"))
	@ToString.Exclude
	private Evidencia evidencia73;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo74", foreignKey = @ForeignKey(name = "fk_proveedor_evidencia_tipo74"))
	@ToString.Exclude
	private Evidencia evidencia74;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "proveedor")
	@ToString.Exclude
	private List<AnexoProveedor> listAnexoProveedor;

}
