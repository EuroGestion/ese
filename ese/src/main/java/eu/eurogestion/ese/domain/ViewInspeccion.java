package eu.eurogestion.ese.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Immutable
@Entity
@Table(name = "view_inspecciones")
public class ViewInspeccion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ViewInspeccionPK viewInspeccionPK;

	@Column(name = "codigo", length = 45)
	private String codigo;

	@Column(name = "tipo_inspeccion", length = 60)
	private String tipoInspeccion;

	@Column(name = "codigo_tipo_inspeccion", length = 10)
	private String codigoTipoInspeccion;

	@Column(name = "id_estado_inspeccion")
	private Integer idEstadoInspeccion;

	@Column(name = "estado_inspeccion", length = 45)
	private String estadoInspeccion;

	@Column(name = "id_inspector")
	private Integer idInspector;

	@Column(name = "nve", length = 12)
	private String nve;

	@Column(name = "id_tren")
	private Integer idTren;

	@Column(name = "origen")
	private String origen;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", length = 10)
	private Date fecha;

	@Column(name = "tiene_informe_anomalias")
	private Boolean tieneInformeAnomalias;
	
	@Column(name = "cerrado_informe_anomalias")
	private Boolean cerradoInformeAnomalias;

	@Column(name = "id_personal")
	private Integer idPersonal;

}
