package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
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
@Table(name = "curso")
public class Curso implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_curso", unique = true, nullable = false)
	private Integer idCurso;

	@Column(name = "titulo_curso", length = 100, nullable = false)
	private String tituloCurso;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_inicio", length = 10)
	private Date fechaInicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_fin", length = 10)
	private Date fechaFin;

	@Temporal(TemporalType.DATE)
	@Column(name = "valido_desde", length = 10)
	private Date validoDesde;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_caducidad", length = 10)
	private Date fechaCaducidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_centro_for", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_centro_for"))
	@ToString.Exclude
	private Compania centroFor;

	@Column(name = "numero_horas")
	private Float numeroHoras;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_curso", nullable = false, foreignKey = @ForeignKey(name = "fk_curso_tipo_curso"))
	@ToString.Exclude
	private TipoCurso tipoCurso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modelo_material", foreignKey = @ForeignKey(name = "fk_curso_modelo_material"))
	@ToString.Exclude
	private ModeloMaterial modeloMaterial;

	@Column(name = "infraestructura", length = 200)
	private String infraestructura;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo01", foreignKey = @ForeignKey(name = "fk_curso_evidencia_tipo01"))
	@ToString.Exclude
	private Evidencia evidencia1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo02", foreignKey = @ForeignKey(name = "fk_curso_evidencia_tipo02"))
	@ToString.Exclude
	private Evidencia evidencia2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_curso", foreignKey = @ForeignKey(name = "fk_curso_estado_curso"))
	@ToString.Exclude
	private EstadoCurso estadoCurso;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "curso")
	@ToString.Exclude
	private List<TituloCurso> listTituloCurso;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "curso")
	@ToString.Exclude
	private List<CursoAlumno> listCursoAlumno;
}
