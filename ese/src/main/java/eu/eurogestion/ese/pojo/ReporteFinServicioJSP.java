package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class ReporteFinServicioJSP {
	private String idReporteFinServicio;
	private Boolean lectura;
	private Integer idEstadoReporteFinServicio;
	private String fecha;
	private String nveLocomotora;
	private String idTren;
	private Boolean practico;
	private String idMaquinistaPredecesor;
	private String idMaquinistaSucesor;
	private String idPuntoOrigen;
	private String idPuntoFin;
	private String fechaToma;
	private String fechaSalida;
	private String fechaLlegada;
	private String fechaDeje;
	private String estacionRefrigerio;
	private String inicioRefrigerio;
	private String finRefrigerio;
	private String viajeAntesServicioInicio;
	private String viajeAntesServicioFin;
	private String viajeDespuesServicioInicio;
	private String viajeDespuesServicioFin;
	private String em2000Inicio;
	private String em2000Fin;
	private String telocInicio;
	private String telocFin;
	private String nivelCombustibleInicio;
	private String nivelCombustibleFin;
	private String estacionamiento;
	private String kwHFin;
	private String senhalesColaReflectantes;
	private String senhalesColaLuminosa;
	private String linternas;
	private String banderinesRojos;
	private String calcesAntideriva;
	private String barraCortocircuito;
	private String llaveTrinquete;
	private String dotacionOtros;
	private String estacionRepostaje;
	private String litrosRepostaje;
	private String averiasLocomotora;
	private String averiasMaterialRemolcado;
	private String otraInformacionRelevante;

	private String paginaVuelta;
	private String idPersonal;

}
