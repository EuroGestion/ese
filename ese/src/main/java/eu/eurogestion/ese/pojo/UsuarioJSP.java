package eu.eurogestion.ese.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class UsuarioJSP {
	private String idPersonal;
	private String documento;
	private String nombre;
	private String apellido;
	private String apellido2;
	private String fechaNacimiento;
	private String tipoVia;
	private String via;
	private String numero;
	private String planta;
	private String puerta;
	private String cp;
	private String usuario;
	private String idCompania;
	private String idCargo;
	private String password;
	private String passwordConfirm;
	private String idRol;
	private String licencia;
	private String docEmpresa;
	private String email;
	private String telefono;
	private MultipartFile foto;
	private String nacionalidad;
	private String lugarNacimiento;
	private String idPais;
	private String idProvincia;
	private String idLocalidad;
	private boolean lectura;

	private String idIdioma;
	private String idNivelIdioma;
	private String notasEvidencia;
	private String fechaIdioma;
	private MultipartFile evidencia;

	private String idIdiomaPersona;
	private String page;

	private String tituloDocumento;
	private String observacionesDocumento;
	private String fechaDocumento;
	private MultipartFile evidenciaDocumento;

	private String idDocumentoPersona;
	private String pageDocumento;
}
