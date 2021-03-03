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
@Table(name = "compania")
public class Compania implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_compania", unique = true, nullable = false)
	private Integer idCompania;

	@Column(name = "nombre", nullable = false, length = 200)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_compania", nullable = false, foreignKey = @ForeignKey(name = "fk_compania_tipo_compania"))
	@ToString.Exclude
	private TipoCompania tipoCompania;

	@Column(name = "documento", nullable = false, length = 14)
	private String documento;

	@Column(name = "licencia", nullable = false, length = 25)
	private String licencia;

	@Column(name = "tipo_via", length = 10)
	private String tipoVia;

	@Column(name = "via", length = 30)
	private String via;

	@Column(name = "numero", length = 11)
	private String numero;

	@Column(name = "planta", length = 11)
	private String planta;

	@Column(name = "puerta", length = 10)
	private String puerta;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_baja", length = 10)
	private Date fechaBaja;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_alta", nullable = false, length = 10)
	private Date fechaAlta;

	@Column(name = "telefono", length = 14)
	private String telefono;

	@Column(name = "email", length = 50)
	private String email;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localidad", nullable = false, foreignKey = @ForeignKey(name = "fk_compania_localidad"))
	@ToString.Exclude
	private Localidad localidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provincia", nullable = false, foreignKey = @ForeignKey(name = "fk_compania_provincia"))
	@ToString.Exclude
	private Provincia provincia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", nullable = false, foreignKey = @ForeignKey(name = "fk_compania_pais"))
	@ToString.Exclude
	private Pais pais;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "compania")
	@ToString.Exclude
	private List<Personal> listPersonal;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "centroFor")
	@ToString.Exclude
	private List<Curso> listCurso;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "compania")
	@ToString.Exclude
	private List<RevisionPsico> listRevisionPsico;

}
