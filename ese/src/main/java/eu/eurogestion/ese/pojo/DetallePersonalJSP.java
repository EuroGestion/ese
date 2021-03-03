package eu.eurogestion.ese.pojo;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import eu.eurogestion.ese.domain.HistoricoMaquinista;
import eu.eurogestion.ese.domain.ViewInspeccion;
import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetallePersonalJSP {
	private String idPersonal;
	private String idTitulo;
	private String idCursoAlumno;
	private String idRevision;
	private String nombre;
	private String cargo;
	private String foto;
	private String dni;
	private String numeroEmpleado;
	private String usuario;
	private String email;
	private String fechaAlta;
	private String nacionalidad;
	private String direccionCompleta;
	private Date fechaBaja;
	private boolean habilitar;

	// TODO Falta la implementacion cuando se creen los dominios

	private String idInspeccion;
	private String idReporte;

	private List<HistoricoMaquinista> reportes;
	private List<ViewInspeccion> inspecciones;
	private MultipartFile licenciaConduccion;
	private boolean tieneLicenciaConduccion;

	private String pageTitulos;
	private String pageCursos;
	private String pageRevisiones;
	private String pageInspecciones;
	private String pageReportes;
}
