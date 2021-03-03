package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

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
@Table(name = "tipo_titulo")
public class TipoTitulo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_tipo_titulo", unique = true, nullable = false)
	private Integer idTipoTitulo;

	@Column(name = "valor", length = 45, nullable = false)
	private String valor;

	@Column(name = "modelo_documento", length = 100, nullable = false)
	private String modeloDocumento;
}
