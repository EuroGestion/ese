package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "imagenes")
public class Imagenes implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_imagenes", unique = true, nullable = false)
	private Integer idImagenes;

	@Column(name = "nombre", length = 50, nullable = false)
	private String nombre;

	@Column(name = "valor", length = 50)
	private Blob valor;

}
