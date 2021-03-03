package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "tramo")
public class Tramo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_tramo", unique = true, nullable = false)
	private Integer idTramo;

	@Column(name = "nombre", length = 50, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "punto_destino", nullable = false, foreignKey = @ForeignKey(name = "fk_tramo_punto_destino"))
	@ToString.Exclude
	private PuntoInfraestructura puntoDestino;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "punto_origen", nullable = false, foreignKey = @ForeignKey(name = "fk_tramo_punto_origen"))
	@ToString.Exclude
	private PuntoInfraestructura puntoOrigen;
	
	@Column(name = "es_especial", nullable = false)
	private Boolean esEspecial;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tramo")
	@ToString.Exclude
	private List<Tren> listTren;
}
