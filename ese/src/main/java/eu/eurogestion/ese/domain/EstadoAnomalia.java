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
@Table(name = "estado_anomalia")
public class EstadoAnomalia implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_estado_anomalia", unique = true, nullable = false)
	private Integer idEstadoAnomalia;

	@Column(name = "valor", length = 45, nullable = false)
	private String valor;

}