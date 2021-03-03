package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Blob;
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
import javax.persistence.Transient;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "personal")
public class Personal implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_personal", unique = true, nullable = false)
	private Integer idPersonal;

	@Column(name = "documento", nullable = false, length = 14)
	private String documento;

	@Column(name = "nombre", nullable = false, length = 30)
	private String nombre;

	@Column(name = "apellido1", length = 30)
	private String apellido1;

	@Column(name = "apellido2", length = 30)
	private String apellido2;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_nac", length = 10)
	private Date fechaNac;

	@Column(name = "tipo_via", length = 10)
	private String tipoVia;

	@Column(name = "via", length = 30)
	private String via;

	@Column(name = "numero", length = 11)
	private String numero;

	@Column(name = "planta", length = 11)
	private String planta;

	@Column(name = "puerta", length = 2)
	private String puerta;

	@Column(name = "cp", length = 5)
	private String cp;

	@Column(name = "nombre_usuario", length = 20)
	private String nombreUsuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compania", nullable = false, foreignKey = @ForeignKey(name = "fk_personal_compania"))
	@ToString.Exclude
	private Compania compania;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cargo", nullable = false, foreignKey = @ForeignKey(name = "fk_personal_cargo"))
	@ToString.Exclude
	private Cargo cargo;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_baja", length = 10)
	private Date fechaBaja;

	@Column(name = "clave", length = 200)
	private String clave;

	@Column(name = "licencia", length = 20)
	private String licencia;

	@Column(name = "doc_empresa", length = 20)
	private String docEmpresa;

	@Column(name = "email", nullable = false, length = 50)
	private String email;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_rol", nullable = false, foreignKey = @ForeignKey(name = "fk_personal_rol"))
	@ToString.Exclude
	private Rol rol;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_alta", length = 10, nullable = false)
	private Date fechaAlta;

	@Column(name = "foto")
	private Blob foto;

	@Column(name = "nacionalidad", length = 50, nullable = false)
	private String nacionalidad;

	@Column(name = "lugar_nacimiento", length = 100, nullable = false)
	private String lugarNacimiento;

	@Column(name = "telefono", length = 14)
	private String telefono;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localidad", nullable = false, foreignKey = @ForeignKey(name = "fk_personal_localidad"))
	@ToString.Exclude
	private Localidad localidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provincia", nullable = false, foreignKey = @ForeignKey(name = "fk_personal_provincia"))
	@ToString.Exclude
	private Provincia provincia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", nullable = false, foreignKey = @ForeignKey(name = "fk_personal_pais"))
	@ToString.Exclude
	private Pais pais;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo81", foreignKey = @ForeignKey(name = "fk_personal_evidencia_tipo81"))
	@ToString.Exclude
	private Evidencia evidencia81;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "personal")
	@ToString.Exclude
	private List<CursoAlumno> listCursoAlumno;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "personal")
	@ToString.Exclude
	private List<Titulo> listTitulo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "personal")
	@ToString.Exclude
	private List<RevisionPsico> listRevisionPsico;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "personal")
	@ToString.Exclude
	private List<IdiomaPersona> listIdiomaPersona;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "destinatario")
	@ToString.Exclude
	private List<TareaPendiente> listTareaPendienteDestinatario;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "origen")
	@ToString.Exclude
	private List<TareaPendiente> listTareaPendienteOrigen;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "maquinista")
	@ToString.Exclude
	private List<Iso> listIsoMaquinista;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "responsableResolucion")
	@ToString.Exclude
	private List<Anomalia> listAnomalia;

	@Transient
	public String getNombreCompleto() {
		String nombreString = "";

		if (apellido1 != null) {
			nombreString += apellido1;
		}

		if (apellido2 != null) {
			nombreString += " " + apellido2;
		}

		if (apellido1 != null || apellido2 != null) {
			nombreString += ", ";
		}
		nombreString += nombre;

		return nombreString;
	}
}
