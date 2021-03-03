package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class GenerarInformeISMPJSP {
	// GLOBALES
	private String pagina;
	private String idISMP;
	private Boolean isLectura;
	private String paginaVuelta;
	private Integer idEstadoISMP;
	private Boolean isFirmado;

	// FRAGMENTO1 ISMP
	private String numeroReferencia;
	private String fecha;
	private String idTren;
	private String lugarInspeccion;
	private String tipoMercancias;
	private String numeroVagonesComposicion;
	private String numeroContenedoresEtiquetadoBuenEstado;
	private String numeroContenedoresEtiquetadoMalEstado;
	private String numeroContenedoresEtiquetadoIncorrecto;
	private String numeroContenedoresEtiquetadoInspeccionado;
	private String faltaTotalEtiquetas;
	private String faltaAlgunaEtiqueta;
	private String etiquetasInadecuadas;
	private String etiquetasTamanoInadecuado;
	private String etiquetasOtras;
	private String etiquetasObservaciones;

	// FRAGMENTO2 ISMP

	private String numeroContenedoresPanelesBuenEstado;
	private String numeroContenedoresPanelesMalEstado;
	private String numeroContenedoresPanelesIncorrecto;
	private String numeroContenedoresPanelesInspeccionado;
	private String numeroONUIncorrecto;
	private String faltaPanelAlgunLateral;
	private String faltaPanel2Laterales;
	private String panelesTamanoInadecuado;
	private String panelesOtras;
	private String panelesObservaciones;
	private String vacioCorrecta;
	private String vacioIncorrecta;
	private String vacioTotal;
	private String cargadoCorrecta;
	private String cargadoIncorrecta;
	private String cargadoTotal;
	private String numeroDocumentosCorrecta;
	private String numeroDocumentosIncorrecta;
	private String numeroDocumentosTotal;

	// FRAGMENTO3 ISMP

	private String datosCorrectoOrdenIncorrecto;
	private String datosCorrectoExcesoLexico;
	private String denominacionIncorrecta;
	private String numeroPeligroIncorrecto;
	private String faltaFraseTransporteVacio;
	private String faltaVariosDatos;
	private String faltaMencionRelativa;
	private String cartaOtras;
	private boolean recepcionPersonalConduccion;
	private String recepcionPersonalConduccionObservaciones;
	private boolean recepcionCarta;
	private String recepcionCartaObservaciones;
	private String cartaObservaciones;

	// FRAGMENTO4 ISMP

	private boolean reconocimientoRealizaTerminalCiclo;
	private String reconocimientoRealizaTerminalCicloObservaciones;
	private boolean reconocimientoArchivaModeloCiclo;
	private String reconocimientoArchivaModeloCicloObservaciones;
	private boolean reconocimientoTerminalMMPP;
	private String reconocimientoTerminalMMPPObservaciones;
	private boolean reconocimientoRealizaTerminalPosteriorCarga;
	private String reconocimientoRealizaTerminalPosteriorCargaObservaciones;
	private boolean reconocimientoArchivaModeloPosteriorCarga;
	private String reconocimientoArchivaModeloPosteriorCargaObservaciones;
	private boolean reconocimientoDepositoNoEstanco;
	private String reconocimientoDepositoNoEstancoObservaciones;
	private boolean reconocimientoDefectosManifiestos;
	private String reconocimientoDefectosManifiestosObservaciones;
	private boolean reconocimientoSobrepasaNivel;
	private String reconocimientoSobrepasaNivelObservaciones;
	private String reconocimientoObservaciones;

	// FRAGMENTO5 ISMP

	private boolean prescripcionNotificaPersonal;
	private String prescripcionNotificaPersonalObservaciones;
	private boolean prescripcionCorrespondeTexto;
	private String prescripcionCorrespondeTextoObservaciones;
	private String medidasCautelares;
	private String listaAnexos;

	// FRAGMENTO6 ISMP
	private Integer idFirmaInspectorSeguridad;
	private String nombreInspectorSeguridad;
	private Integer idFirmaResponsableSeguridad;
	private String nombreResponsableSeguridad;
	private String passwordInspectorSeguridad;
	private String passwordResponsableSeguridad;

}
