package eu.eurogestion.ese.pojo;

import java.util.List;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class CrearComposicionJSP {
	private String fechaInicio;
	private String fechaFin;
	private String idTren;
	private boolean vieneCrearTren;
	private String idMaterial;
	private List<String> listaDias;
	private List<Integer> listaIdsComposiciones;

	private String pageTotalMateriales;
	private String pageTotalMaterialesComposicion;

}
