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
@Table(name = "estado_titulo")
public class EstadoTitulo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_estado_titulo", unique = true, nullable = false)
	private Integer idEstadoTitulo;

	@Column(name = "valor", length = 50, nullable = false)
	private String valor;

	@Column(name = "valor_bc", length = 50, nullable = false)
	private String valorBC;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estadoTitulo")
	@ToString.Exclude
	private List<Titulo> listTitulo;

}