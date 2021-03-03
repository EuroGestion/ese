package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class DetalleTomaServicioJSP {
	private String idTomaServicio;
	private Boolean lectura;
	private Integer idEstadoTomaServicio;
	private String fecha;
	private String hora;
	private String nveLocomotora;
	private String idTren;
	private String lugar;
	private String motivo;
	private Boolean realizarAccion;
	private Boolean documentacionReglamentaria;
	private Boolean libroTelefonemas;
	private Boolean libroAverias;

	private Boolean dotacionUtilesServicio;
	private Boolean senalizacionCabezaCola;
	private Boolean visibilidadAdecuada;
	private Boolean anomaliasRodajeCaja;

	private Boolean anomaliasSuspension;
	private Boolean anomaliasChoqueTraccion;
	private Boolean estadoPrecintos;
	private Boolean posicionPalancaCambiador;

	private Boolean frenosEstacionamiento;
	private Boolean configuracionFrenado;
	private Boolean dispositivoVigilanciaHM;
	private Boolean valvulaEmergenciaSeta;

	private Boolean pruebasFreno;
	private Boolean pruebaInversionMarcha;
	private Boolean asfaCorrecto;
	private Boolean equipoRadioTelefonia;

	private Boolean inspeccionVisual;
	private Boolean datosDocumentoTren;
	private Boolean libroTelefonemasRelevo;
	private Boolean noExistenNotificaciones;

	private String observaciones;

}
