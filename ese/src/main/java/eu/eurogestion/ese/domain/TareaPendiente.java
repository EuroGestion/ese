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
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "tarea_pendiente", uniqueConstraints = @UniqueConstraint(columnNames = { "tabla_tarea_pte",
		"id_tarea_pte" }))
public class TareaPendiente implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_tarea_pendiente", unique = true, nullable = false)
	private Integer idTareaPendiente;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", length = 10, nullable = false)
	private Date fecha;

	@Column(name = "tabla_tarea_pte", length = 30, nullable = false)
	private String tablaTareaPte;

	@Column(name = "id_tarea_pte", nullable = false)
	private Integer idTareaPte;

	@Column(name = "leido", nullable = false)
	private Boolean leido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_destinatario", nullable = false, foreignKey = @ForeignKey(name = "fk_tarea_pendiente_destino"))
	@ToString.Exclude
	private Personal destinatario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_origen", nullable = false, foreignKey = @ForeignKey(name = "fk_tarea_pendiente_origen"))
	@ToString.Exclude
	private Personal origen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_tarea", nullable = false, foreignKey = @ForeignKey(name = "fk_tipo_tarea"))
	@ToString.Exclude
	private TipoTarea tipoTarea;

	/**
	 * Metodo que devuelve el nombre, apellidos y cargo del creador de la tarea. Si
	 * la tarea la obtiene el sistema autoaticamente, el creador asignado sera el
	 * administrador.
	 * 
	 * @return Nombre, apellidos y cargo del creador de la tarea.
	 */
	public String getCreadorTarea() {
		StringBuilder sb = new StringBuilder();
		
		if (this.getOrigen() != null && this.getOrigen().getApellido1() != null) {
			sb = sb.append(this.getOrigen().getApellido1());
		}
		if (this.getOrigen() != null && this.getOrigen().getApellido2() != null) {
			sb = sb.append(" ").append(this.getOrigen().getApellido2());
		}
		if (this.getOrigen() != null && this.getOrigen().getNombre() != null) {
			sb = sb.append(", ").append(this.getOrigen().getNombre());
		}
		if (this.getOrigen() != null && this.getOrigen().getCargo() != null
				&& this.getOrigen().getCargo().getNombre() != null) {
			sb = sb.append(" - ").append(this.getOrigen().getCargo().getNombre());
		}
		return sb.toString();
	}
}
