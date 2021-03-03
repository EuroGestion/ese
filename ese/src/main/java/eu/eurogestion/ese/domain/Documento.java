package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Blob;
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
@Table(name = "documento")
public class Documento implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_documento", unique = true, nullable = false)
	private Integer idDocumento;

	@Column(name = "titulo", length = 50, nullable = false)
	private String titulo;

	@Column(name = "hash_md5", length = 100, nullable = false)
	private String hashDocumento;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_subida", length = 10, nullable = false)
	private Date fechaSubida;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_documento", length = 10, nullable = false)
	private Date fechaDocumento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_doc_padre", foreignKey = @ForeignKey(name = "fk_documento_doc_padre"))
	@ToString.Exclude
	private Documento docPadre;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@Column(name = "fichero", nullable = false)
	private Blob fichero;

	@Column(name = "tipo_fichero", length = 45, nullable = false)
	private String tipoFichero;
}
