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
@Table(name = "punto_infraestructura")
public class PuntoInfraestructura implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_punto_infraestructura", unique = true, nullable = false)
	private Integer idPuntoInfraestructura;

	@Column(name = "nombre", length = 30, nullable = false)
	private String nombre;

	@Column(name = "codigo", length = 5, nullable = false)
	private String codigo;

	@Column(name = "nombre_largo", length = 50, nullable = false)
	private String nombreLargo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "puntoOrigen")
	@ToString.Exclude
	private List<Tramo> listTramoOrigen;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "puntoDestino")
	@ToString.Exclude
	private List<Tramo> listTramoDestino;
}
