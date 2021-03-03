package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "opcion")
public class Opcion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_opcion", unique = true, nullable = false)
	private Integer idOpcion;

	@Column(name = "descripcion", nullable = false, length = 50)
	private String descripcion;

	@Column(name = "nombre", nullable = false, length = 50)
	private String nombre;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "opcion")
	@ToString.Exclude
	private List<Permiso> listPermiso;
}
