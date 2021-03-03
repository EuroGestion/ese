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
import javax.persistence.OneToOne;
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
@Table(name = "revocacion")
public class Revocacion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_revocacion", unique = true, nullable = false)
	private Integer idRevocacion;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_revocacion", length = 10, nullable = false)
	private Date fechaRevocacion;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@Column(name = "sanciones_empresa", length = 200)
	private String sancionesEmpresa;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_titulo", nullable = false, foreignKey = @ForeignKey(name = "fk_revocacion_titulo"))
	@ToString.Exclude
	private Titulo titulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_causa_revocacion", nullable = false, foreignKey = @ForeignKey(name = "fk_revocacion_causa_revocacion"))
	@ToString.Exclude
	private CausaRevocacion causaRevocacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia06", nullable = false, foreignKey = @ForeignKey(name = "fk_revocacion_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_responsable", foreignKey = @ForeignKey(name = "fk_revocacion_firma_responsable"))
	@ToString.Exclude
	private Personal firmaResponsable;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_responsable")
	private Date fechahoraFirmaResponsable;
}