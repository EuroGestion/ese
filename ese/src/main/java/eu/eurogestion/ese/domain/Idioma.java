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
@Table(name = "idioma")
public class Idioma implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_idioma", unique = true, nullable = false)
	private Integer idIdioma;

	@Column(name = "nombre", length = 50, nullable = false)
	private String nombre;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "idioma")
	@ToString.Exclude
	private List<IdiomaPersona> listIdiomaPersona;

}
