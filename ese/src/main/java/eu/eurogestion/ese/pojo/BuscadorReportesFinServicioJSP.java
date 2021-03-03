package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class BuscadorReportesFinServicioJSP {
	private String idMaquinista;
	private String numeroTren;
	private String fecha;
	private String idHistoricoMaquinista;
	private String page;
	private String order;
	private String direccion;
}
