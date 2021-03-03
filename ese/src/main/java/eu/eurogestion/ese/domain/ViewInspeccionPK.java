package eu.eurogestion.ese.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Embeddable
public class ViewInspeccionPK implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id_inspeccion")
	private Integer idInspeccion;

	@Column(name = "id_tipo_inspeccion")
	private Integer idTipoInspeccion;

}
