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
@Table(name = "titulo")
public class Titulo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_titulo", unique = true, nullable = false)
	private Integer idTitulo;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@Temporal(TemporalType.DATE)
	@Column(name = "valido_desde", length = 10, nullable = false)
	private Date validoDesde;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_caducidad", length = 10, nullable = false)
	private Date fechaCaducidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_persona"))
	@ToString.Exclude
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_titulo", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_tipo_titulo"))
	@ToString.Exclude
	private TipoTitulo tipoTitulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localidad", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_localidad"))
	@ToString.Exclude
	private Localidad localidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_pais"))
	@ToString.Exclude
	private Pais pais;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_revision_psico", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_revision_psico"))
	@ToString.Exclude
	private RevisionPsico revisionPsico;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provincia", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_provincia"))
	@ToString.Exclude
	private Provincia provincia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_titulo", nullable = false, foreignKey = @ForeignKey(name = "fk_titulo_estado_titulo"))
	@ToString.Exclude
	private EstadoTitulo estadoTitulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia", foreignKey = @ForeignKey(name = "fk_titulo_evidencia"))
	@ToString.Exclude
	private Evidencia evidencia;

	@Column(name = "categoria", length = 50)
	private String categoria;

	@Column(name = "info_adicional_categoria", length = 200)
	private String infoAdicionalCategoria;

	@Column(name = "restricciones", length = 200)
	private String restricciones;

	@Column(name = "num_referencia", length = 15)
	private String numReferencia;

	@Column(name = "nota_tipo_titulo", length = 200)
	private String notaTipoTitulo;

	@Column(name = "lugar_trabajo", length = 100)
	private String lugarTrabajo;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_anulado", length = 10)
	private Date fechaAnulado;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "titulo")
	@ToString.Exclude
	private List<TituloCurso> listTituloCursos;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "titulo")
	@ToString.Exclude
	private List<Suspension> listSuspension;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "titulo")
	@ToString.Exclude
	private Revocacion revocacion;

}
