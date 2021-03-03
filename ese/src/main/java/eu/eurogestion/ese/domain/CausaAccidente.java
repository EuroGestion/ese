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
@Table(name = "causa_accidente")
public class CausaAccidente implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_causa_accidente", unique = true, nullable = false)
	private Integer idCausaAccidente;

	@Column(name = "valor", length = 50)
	private String valor;
}