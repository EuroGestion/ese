package eu.eurogestion.ese.pojo;

import java.util.List;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class HabilitacionJSP {
	private String idPersonal;
	private String idTipoTitulo;
	private String numeroReferencia;
	private String fechaExpedicion;
	private String categoriasConduccion;
	private String notas;
	private String infoAdicional;
	private List<String> listaCursos;
	private String idRevPsicofisica;
	private String revPsicofisica;
	private String fechaRevPsicofisica;
//	private String fechaExpiracion;

}
