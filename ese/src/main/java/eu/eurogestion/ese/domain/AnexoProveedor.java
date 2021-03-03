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
@Table(name = "anexo_proveedor")
public class AnexoProveedor implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_anexo_proveedor", unique = true, nullable = false)
	private Integer idAnexoProveedor;

	@Column(name = "descripcion", length = 100)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proveedor", nullable = false, foreignKey = @ForeignKey(name = "fk_anexo_proveedor"))
	@ToString.Exclude
	private Proveedor proveedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia", nullable = false, foreignKey = @ForeignKey(name = "fk_anexo_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;
}
