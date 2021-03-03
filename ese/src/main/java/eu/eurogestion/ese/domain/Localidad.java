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
@Table(name = "localidad")
public class Localidad implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_localidad", unique = true, nullable = false)
	private Integer idLocalidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", nullable = false, foreignKey = @ForeignKey(name = "fk_localidad_pais"))
	@ToString.Exclude
	private Pais pais;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provincia", nullable = false, foreignKey = @ForeignKey(name = "fk_localidad_provincia"))
	@ToString.Exclude
	private Provincia provincia;

	@Column(name = "nombre", length = 255, nullable = false)
	private String nombre;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "localidad")
	@ToString.Exclude
	private List<Compania> listCompania;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "localidad")
	@ToString.Exclude
	private List<Personal> listPersonal;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "localidad")
	@ToString.Exclude
	private List<Titulo> listTitulo;

}