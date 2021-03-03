package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "linea")
public class Linea implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_linea", unique = true, nullable = false)
	private Integer idLinea;

	@Column(name = "codigo_linea", length = 5, nullable = false)
	private String codigoLinea;

	@Column(name = "orden", nullable = false)
	private Integer orden;

	@Column(name = "pk")
	private BigDecimal pk;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_punto_infraestructura", foreignKey = @ForeignKey(name = "fk_linea_punto"))
	@ToString.Exclude
	private PuntoInfraestructura puntoInfraestructura;

}
