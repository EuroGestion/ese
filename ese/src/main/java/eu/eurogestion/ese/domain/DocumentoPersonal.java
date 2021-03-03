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
@Table(name = "documento_personal")
public class DocumentoPersonal implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_documento_personal", unique = true, nullable = false)
	private Integer idDocumentoPersonal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", foreignKey = @ForeignKey(name = "fk_documento_personal_personal"))
	@ToString.Exclude
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento", foreignKey = @ForeignKey(name = "fk_documento_personal_documento"))
	@ToString.Exclude
	private Documento documento;

	
}
