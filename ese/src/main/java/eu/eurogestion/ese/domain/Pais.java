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
@Table(name = "pais")
public class Pais implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_pais", unique = true, nullable = false)
	private Integer idPais;

	@Column(name = "nombre", length = 255, nullable = false)
	private String nombre;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	@ToString.Exclude
	private List<Provincia> listProvincia;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	@ToString.Exclude
	private List<Localidad> listLocalidad;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	@ToString.Exclude
	private List<Personal> listPersonal;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	@ToString.Exclude
	private List<Compania> listCompania;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	@ToString.Exclude
	private List<Titulo> listTitulo;
}