package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class RealizarCADJSP {
	private Integer idInformeCAD;
	private String idPersonalSometidoControl;
	private boolean consentimiento;
	private Integer idEstadoInspeccion;
	private String nombrePersonalSometidoControl;
	private String passwordPersonalSometidoControl;
	private String medicamentos;
	private String observacionesConsentimiento;
	private String lugar;
	private String idTipoControl;
	private String idDelegadoSeguridad1;
	private String idDelegadoSeguridad2;
	private String observaciones;
	private String primeraPrueba;
	private String horaPrimeraPrueba;
	private String resultadoPrimeraPrueba;
	private String segundaPrueba;
	private String horaSegundaPrueba;
	private String resultadoSegundaPrueba;
	private String numeroMuestraSangre;
	private String analisisSangre;
	private String idResultadoSangre;
	private String numeroMuestraOrina;
	private String analisisOrina;
	private String idResultadoOrina;
	private String observacionesRecogidaPrueba;
	private String observacionesLaboratorioPrueba;
	private String lugarIdentificacion;
	private String diaHoraIdentificacion;
	private String medidasCautelaresTomadas;
	private String paginaAnterior;
	private Float limiteAlcohol;
	private String idCentroMedico;
	private boolean lectura;

	private String idPersonal;
	
	private String nombrePersonalMedico;
	private String dniPersonalMedico;
	private String nombreDelegadoSeguridad;
	private String dniDelegadoSeguridad;
	
	private String nombreDelegadoADIF;
	private String dniDelegadoADIF;
	private String nombreTecnicoCIAF;
	private String dniTecnicoCIAF;

}
