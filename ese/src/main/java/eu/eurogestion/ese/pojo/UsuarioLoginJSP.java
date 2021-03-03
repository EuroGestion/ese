package eu.eurogestion.ese.pojo;

import java.util.Map;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class UsuarioLoginJSP {
	private String nombre;
	private String password;

	private Map<String, Integer> lista;

}
