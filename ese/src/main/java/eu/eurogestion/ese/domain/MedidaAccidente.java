package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

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
@Table(name = "medida_accidente")
public class MedidaAccidente implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_medida_accidente", unique = true, nullable = false)
	private Integer idMedidaAccidente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accidente", nullable = false, foreignKey = @ForeignKey(name = "fk_medida_accidente_accidente"))
	@ToString.Exclude
	private Accidente accidente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia", nullable = false, foreignKey = @ForeignKey(name = "fk_medida_accidente_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;

	@Column(name = "descripcion", length = 100, nullable = true)
	private String descripcion;

}