package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class GenerarInformeISOJSP {
	// GLOBALES
	private String pagina;
	private String idISO;
	private Boolean isLectura;
	private String paginaVuelta;
	private Integer idEstadoISO;
	private Boolean isFirmado;
	private String idPersonal;

	// FRAGMENTO1 ISO
	private String personalConduccion;
	private String nveLocomotora;
	private String fecha;
	private String horaInicioServicio;
	private String horaFinServicio;
	private String tiemposDescanso;
	private String observacionesCirculacionFerroviaria;
	private String verificadoLNM;
	private String observacionesLNM;
	private String verificadoLIM;
	private String observacionesLIM;
	private String verificadoLlaves333;
	private String observacionesLlaves333;
	private String verificadoLlaveCuadradillo;
	private String observacionesLlaveCuadradillo;
	private String verificadoTituloConduccion;
	private String observacionesTituloConduccion;
	private String verificadoDocReglamentaria;
	private String observacionesDocReglamentaria;
	private String verificadoTelefonoMovil;
	private String observacionesTelefonoMovil;
	private String verificadoTabletaPortatil;
	private String observacionesTabletaPortatil;
	private String observacionesDotacionPersonal;

	// FRAGMENTO2 ISO

	private String verificadoChapa;
	private String numeroChapa;
	private String observacionesChapa;
	private String verificadoLinternas;
	private String numeroLinternas;
	private String observacionesLinternas;
	private String verificadoBanderines;
	private String numeroBanderines;
	private String observacionesBanderines;
	private String verificadoCalces;
	private String numeroCalces;
	private String observacionesCalces;
	private String verificadoBarras;
	private String numeroBarras;
	private String observacionesBarras;
	private String verificadoJuegoLlaves;
	private String numeroJuegoLlaves;
	private String observacionesJuegoLlaves;
	private String verificadoManual;
	private String numeroManual;
	private String observacionesManual;
	private String verificadoLibro;
	private String numeroLibro;
	private String observacionesLibro;
	private String observacionesLocomotora;
	private String verificadoDestreza;
	private String observacionesDestreza;
	private String verificadoCumple;
	private String observacionesCumple;
	private String verificadoCumpleTiempo;
	private String observacionesCumpleTiempo;
	private String verificadoDispositivos;
	private String observacionesDispositivos;
	private String verificadoPoseeFormacion;
	private String observacionesPoseeFormacion;

	// FRAGMENTO3 ISO

	private String verificadoAntesServicio;
	private String observacionesAntesServicio;
	private String verificadoTrasServicio;
	private String observacionesTrasServicio;
	private String verificadoRelevo;
	private String observacionesRelevo;
	private String observacionesIdoneidad;
	private String observacionesMedidasCautelares;
	private String observacionesDocumentoAnexo;
	
	private Integer idFirmaInspectorSeguridad;
	private String nombreInspectorSeguridad;
	private String passwordInspectorSeguridad;
	private Integer idFirmaResponsableSeguridad;
	private String nombreResponsableSeguridad;
	private String passwordResponsableSeguridad;

}
