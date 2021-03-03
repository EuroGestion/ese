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
@Table(name = "resultado_inspeccion")
public class ResultadoInspeccion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_resultado_inspeccion", unique = true, nullable = false)
	private Integer idResultadoInspeccion;

	@Column(name = "valor", length = 45, nullable = false)
	private String valor;
}
