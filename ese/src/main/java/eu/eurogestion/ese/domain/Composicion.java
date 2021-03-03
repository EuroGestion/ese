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
@Table(name = "composicion")
public class Composicion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_composicion", unique = true, nullable = false)
	private Integer idComposicion;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", length = 10, nullable = false)
	private Date fecha;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tren", nullable = false, foreignKey = @ForeignKey(name = "fk_composicion_tren"))
	@ToString.Exclude
	private Tren tren;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "composicion")
	@ToString.Exclude
	private List<MaterialComposicion> listMaterialComposicion;
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "composicion")
	@ToString.Exclude
	private List<HistoricoMaquinista> listHistoricoMaquinista;
}
