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
@Table(name = "idioma_persona")
public class IdiomaPersona implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_idioma_persona", unique = true, nullable = false)
	private Integer idIdiomaPersona;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_idioma", nullable = false, foreignKey = @ForeignKey(name = "fk_idioma_persona_idioma"))
	@ToString.Exclude
	private Idioma idioma;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_personal", nullable = false, foreignKey = @ForeignKey(name = "fk_idioma_persona_personal"))
	@ToString.Exclude
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo82", foreignKey = @ForeignKey(name = "fk_idioma_persona_evidencia82"))
	@ToString.Exclude
	private Evidencia evidencia82;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_nivel_idioma", nullable = false, foreignKey = @ForeignKey(name = "fk_idioma_persona_nivel_idioma"))
	@ToString.Exclude
	private NivelIdioma nivelIdioma;

	@Column(name = "notas", length = 15, nullable = false)
	private String notas;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", nullable = true)
	private Date fecha;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_baja", nullable = true)
	private Date fechaBaja;

}
