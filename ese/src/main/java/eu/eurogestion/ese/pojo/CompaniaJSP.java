package eu.eurogestion.ese.pojo;

import java.io.File;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class CompaniaJSP {
	private String idCompania;
	private String nombre;
	private String tipoCompania;
	private String documento;
	private String tipoVia;
	private String via;
	private String numero;
	private String planta;
	private String puerta;
	private String telefono;
	private File logotipo;
	private String idPais;
	private String idProvincia;
	private String idLocalidad;
	private String licencia;
	private String email;
	private String observaciones;
	private boolean lectura;

}
