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
@Table(name = "suspension")
public class Suspension implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_suspension", unique = true, nullable = false)
	private Integer idSuspension;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_suspension", length = 10, nullable = false)
	private Date fechaSuspension;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_resolucion", length = 10)
	private Date fechaResolucion;

	@Column(name = "causa_resolucion", length = 100)
	private String causaResolucion;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_titulo", nullable = false, foreignKey = @ForeignKey(name = "fk_suspension_titulo"))
	@ToString.Exclude
	private Titulo titulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_causa_suspension", nullable = false, foreignKey = @ForeignKey(name = "fk_suspension_causa"))
	@ToString.Exclude
	private CausaSuspension causaSuspension;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia05", nullable = false, foreignKey = @ForeignKey(name = "fk_suspension_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_responsable", foreignKey = @ForeignKey(name = "fk_suspension_firma_responsable"))
	@ToString.Exclude
	private Personal firmaResponsable;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_responsable")
	private Date fechahoraFirmaResponsable;
}
