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
@Table(name = "rol")
public class Rol implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_rol", unique = true, nullable = false)
	private Integer idRol;

	@Column(name = "nombre", length = 60)
	private String nombre;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rol")
	@ToString.Exclude
	private List<Personal> listPersonal;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rol")
	@ToString.Exclude
	private List<RolPermiso> listRolPermiso;

}
