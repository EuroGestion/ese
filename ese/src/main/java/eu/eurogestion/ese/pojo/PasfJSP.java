package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class PasfJSP {
	private Integer idPasf;
	private Integer idEstadoPasf;
	private boolean lectura;

	private String anno;
	private String descarrilamiento;
	private String colision;
	private String accidentePasoNivel;
	private String incendio;
	private String arrollamientoVia;
	private String arrollamientoInterseccion;
	private String caidaPersonas;
	private String suicidio;
	private String descomposicion;
	private String detencion;
	private String invasionVia;
	private String incidentesTransportesExcepcionales;
	private String rebaseSennal;
	private String conatoColision;
	private String enganchePantografo;
	private String otros;
	private String cursosFormativos;
	private String revisionesAptitudPsicofisica;
	private String iso;
	private String issc;
	private String ismp;
	private String iseet;
	private String isrc;
	private String cad;
	private String auditorias;

	private String cursoCategoria;
	private String cursoCargo;
	private String cursoDescripcion;
	private String cursoAsistentes;
	private String cursoDuracion;

	private String inspeccionTipo;
	private String inspeccionDescripcion;
	private String inspeccionNumero;
	private String inspeccionDuracion;

	private String auditoriaDescripcion;
	private String auditoriaDuracion;

	private String pageAccionesFormativas;
	private String pageAccionesSeguridad;
	private String pageIniciativasSeguridad;

}
