package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "tren")
public class Tren implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_tren", unique = true, nullable = false)
	private Integer idTren;

	@Column(name = "numero", length = 40, nullable = false)
	private String numero;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tramo", nullable = false, foreignKey = @ForeignKey(name = "fk_tren_tramo"))
	@ToString.Exclude
	private Tramo tramo;

	@Temporal(TemporalType.TIME)
	@Column(name = "hora_inicio")
	private Date horaInicio;

	@Temporal(TemporalType.TIME)
	@Column(name = "hora_fin")
	private Date horaFin;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_baja", length = 10)
	private Date fechaBaja;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento", nullable = false, foreignKey = @ForeignKey(name = "fk_tren_documento"))
	@ToString.Exclude
	private Documento documento;
}
