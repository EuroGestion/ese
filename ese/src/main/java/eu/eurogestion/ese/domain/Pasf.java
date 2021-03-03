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
@Table(name = "pasf")
public class Pasf implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_pasf", unique = true, nullable = false)
	private Integer idPasf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_pasf", foreignKey = @ForeignKey(name = "fk_pasf_estado_pasf"))
	@ToString.Exclude
	private EstadoPasf estadoPasf;

	@Column(name = "anno", nullable = false)
	private Integer anno;

	@Column(name = "descarrilamiento")
	private Integer descarrilamiento;

	@Column(name = "colision")
	private Integer colision;

	@Column(name = "accidente_pn")
	private Integer accidentePn;

	@Column(name = "incendio")
	private Integer incendio;

	@Column(name = "arrollamiento_via")
	private Integer arrollamientoVia;

	@Column(name = "arrollamiento_interseccion", nullable = false)
	private Integer arrollamientoInterseccion;

	@Column(name = "caida_personas", nullable = false)
	private Integer caidaPersonas;

	@Column(name = "suicidio", nullable = false)
	private Integer suicidio;

	@Column(name = "descomposición_carga", nullable = false)
	private Integer descomposiciónCarga;

	@Column(name = "detencion_tren", nullable = false)
	private Integer detencionTren;

	@Column(name = "invasion_via", nullable = false)
	private Integer invasionVia;

	@Column(name = "incidente_te", nullable = false)
	private Integer incidenteTe;

	@Column(name = "rebase_senal", nullable = false)
	private Integer rebaseSenal;

	@Column(name = "conato_colision", nullable = false)
	private Integer conatoColision;

	@Column(name = "enganche", nullable = false)
	private Integer enganche;

	@Column(name = "otros", nullable = false)
	private Integer otros;

	@Column(name = "cursos", nullable = false)
	private Integer cursos;

	@Column(name = "revisiones", nullable = false)
	private Integer revisiones;

	@Column(name = "iso", nullable = false)
	private Integer iso;

	@Column(name = "issc", nullable = false)
	private Integer issc;

	@Column(name = "ismp", nullable = false)
	private Integer ismp;

	@Column(name = "iseet", nullable = false)
	private Integer iseet;

	@Column(name = "cad", nullable = false)
	private Integer cad;

	@Column(name = "auditorias", nullable = false)
	private Integer auditorias;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo42", foreignKey = @ForeignKey(name = "fk_pasf_evidencia_tipo42"))
	@ToString.Exclude
	private Evidencia evidencia;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pasf")
	@ToString.Exclude
	private List<AuditoriaPasf> listAuditoriaPasf;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pasf")
	@ToString.Exclude
	private List<CursoPasf> listCursoPasf;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pasf")
	@ToString.Exclude
	private List<InspeccionPasf> listInspeccionPasf;
}
