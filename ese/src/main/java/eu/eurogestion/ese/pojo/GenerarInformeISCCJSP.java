package eu.eurogestion.ese.pojo;

import lombok.Data;

/**
 * @author Rmerino, alvaro
 *
 */
@Data
public class GenerarInformeISCCJSP {
	// GLOBALES
	private String pagina;
	private String idISCC;
	private Boolean isLectura;
	private String paginaVuelta;
	private Integer idEstadoISCC;
	private Boolean isFirmado;

	// FRAGMENTO ISCC 1
	private String numeroReferencia;
	private String fecha;
	private String idTren;
	private String lugarInspeccion;
	private String tiposMercancias;
	private String vagonesInspeccionadosLugar;
	private String numeroVagonesComposicion;
	private String observacionesTrafico;

	// FRAGMENTO ISCC 2
	private String transporteSeRealiza;
	private String cohesionUnidades;
	private String mercanciasCubiertas;
	private String cargaDistribucionCarga;
	private String cargaDistribucionCargaObservaciones;
	private String cargaCargasConcentradas;
	private String cargaCargasConcentradasObservaciones;
	private String cargaDistanciaBrida;
	private String cargaDistanciaBridaObservaciones;
	private String cargaAlturaCentro;
	private String cargaAlturaCentroObservaciones;
	private String cargaRebasaGalibo;
	private String cargaRebasaGaliboObservaciones;
	private String cargaRebasaLimites;
	private String cargaRebasaLimitesObservaciones;
	private String observacionesCarga;

	// FRAGMENTO ISCC 3
	private String vagonesSeConservan;
	private String vagonesSeConservanObservaciones;
	private String vagonesCerradosTechos;
	private String vagonesCerradosTechosObservaciones;
	private String vagonesCerradasPuertas;
	private String vagonesCerradasPuertasObservaciones;
	private String vagonesCerradasTapas;
	private String vagonesCerradasTapasObservaciones;
	private String vagonesCerradasCompuertas;
	private String vagonesCerradasCompuertasObservaciones;
	private String vagonesCerradasValvulas;
	private String vagonesCerradasValvulasObservaciones;
	private String vagonesBordesLevantados;
	private String vagonesBordesLevantadosObservaciones;
	private String vagonesBordesBajados;
	private String vagonesBordesBajadosObservaciones;
	private String vagonesBordesBajadosInmoviles;
	private String vagonesBordesBajadosInmovilesObservaciones;
	private String vagonesVisiblesInscripciones;
	private String vagonesVisiblesInscripcionesObservaciones;
	private String vagonesTelerosAmovibles;
	private String vagonesTelerosAmoviblesObservaciones;
	private String vagonesTelerosPivotantes;
	private String vagonesTelerosPivotantesObservaciones;
	private String vagonesTelerosPivotantesHorizontal;
	private String vagonesTelerosPivotantesHorizontalObservaciones;
	private String vagonesCadenasTeleros;
	private String vagonesCadenasTelerosObservaciones;
	private String vagonesDispositivosTension;
	private String vagonesDispositivosTensionObservaciones;

	// FRAGMENTO ISCC 4

	private String vagonesPiso;
	private String vagonesPisoObservaciones;
	private String vagonesMercancias;
	private String vagonesMercanciasObservaciones;
	private String vagonesParedes;
	private String vagonesParedesObservaciones;
	private String vagonesPuertas;
	private String vagonesPuertasObservaciones;
	private String vagonesAnillas;
	private String vagonesAnillasObservaciones;
	private String vagonesEquipamiento;
	private String vagonesEquipamientoObservaciones;
	private String observacionesVagones;

	// FRAGMENTO ISCC 5
	private String acondicionamientoReparto;
	private String acondicionamientoRepartoObservaciones;
	private String acondicionamientoMercancias;
	private String acondicionamientoMercanciasObservaciones;
	private String acondicionamientoPesadas;
	private String acondicionamientoPesadasObservaciones;
	private String acondicionamientoSusceptibles;
	private String acondicionamientoSusceptiblesObservaciones;
	private String acondicionamientoSolidamenteSujeta;
	private String acondicionamientoSolidamenteSujetaObservaciones;
	private String acondicionamientoLonaProxima;
	private String acondicionamientoLonaProximaObservaciones;
	private String acondicionamientoLonaSeparacion;
	private String acondicionamientoLonaSeparacionObservaciones;
	private String acondicionamientoVerificarParedes;
	private String acondicionamientoVerificarParedesObservaciones;
	private String acondicionamientoVerificarCalces;
	private String acondicionamientoVerificarCalcesObservaciones;
	private String acondicionamientoVerificarAmarre;
	private String acondicionamientoVerificarAmarreObservaciones;
	private String acondicionamientoVerificarDosAmarras;
	private String acondicionamientoVerificarDosAmarrasObservaciones;

	// FRAGMENTO ISCC 6
	private String acondicionamientoVerificarCalces7;
	private String acondicionamientoVerificarCalces7Observaciones;
	private String acondicionamientoVerificarBastidores;
	private String acondicionamientoVerificarBastidoresObservaciones;
	private String acondicionamientoVerificarCunas;
	private String acondicionamientoVerificarCunasObservaciones;
	private String acondicionamientoVerificarTransversal;
	private String acondicionamientoVerificarTransversalObservaciones;
	private String acondicionamientoVerificarLongitudinal;
	private String acondicionamientoVerificarLongitudinalObservaciones;
	private String acondicionamientoVerificarBobinasMenos10;
	private String acondicionamientoVerificarBobinasMenos10Observaciones;
	private String acondicionamientoVerificarBobinasMas10;
	private String acondicionamientoVerificarBobinasMas10Observaciones;
	private String acondicionamientoBascularAgrupamiento;
	private String acondicionamientoBascularAgrupamientoObservaciones;
	private String acondicionamientoBascularPuntales;
	private String acondicionamientoBascularPuntalesObservaciones;
	private String acondicionamientoBascularCaballetes;
	private String acondicionamientoBascularCaballetesObservaciones;

	// FRAGMENTO ISCC 7
	private String acondicionamientoApiladaRepartidas;
	private String acondicionamientoApiladaRepartidasObservaciones;
	private String acondicionamientoApiladaAnchura;
	private String acondicionamientoApiladaAnchuraObservaciones;
	private String acondicionamientoApiladaCapas;
	private String acondicionamientoApiladaCapasObservaciones;
	private String acondicionamientoApiladaRozamiento;
	private String acondicionamientoApiladaRozamientoObservaciones;
	private String acondicionamientoApiladaBastidores;
	private String acondicionamientoApiladaBastidoresObservaciones;
	private String acondicionamientoApiladaUnidades;
	private String acondicionamientoApiladaUnidadesObservaciones;
	private String acondicionamientoApiladaCintas;
	private String acondicionamientoApiladaCintasObservaciones;
	private String acondicionamientoApiladaParteInferior;
	private String acondicionamientoApiladaParteInferiorObservaciones;
	private String acondicionamientoApiladaIntercalares;
	private String acondicionamientoApiladaIntercalaresObservaciones;

	// FRAGMENTO ISCC 8
	private String acondicionamientoApiladaImbricadas;
	private String acondicionamientoApiladaImbricadasObservaciones;
	private String acondicionamientoApiladaOscilar;
	private String acondicionamientoApiladaOscilarObservaciones;
	private String acondicionamientoApiladaTubos;
	private String acondicionamientoApiladaTubosObservaciones;
	private String acondicionamientoApiladaCapasCalces;
	private String acondicionamientoApiladaCapasCalcesObservaciones;
	private String acondicionamientoRigidas;
	private String acondicionamientoRigidasObservaciones;
	private String acondicionamientoFlexibles;
	private String acondicionamientoFlexiblesObservaciones;
	private String observacionesAcondicionamiento;

	// FRAGMENTO ISCC 9
	private String mediosCalcesRetencion;
	private String mediosCalcesRetencionObservaciones;
	private String mediosCalcesRodadura;
	private String mediosCalcesRodaduraObservaciones;
	private String mediosIntercalares;
	private String mediosIntercalaresObservaciones;
	private String mediosAmarres;
	private String mediosAmarresObservaciones;
	private String observacionesMedios;
	private String medidasCautelares;
	private String listaAnexos;

	// FRAGMENTO ISCC 10
	private Integer idFirmaInspectorSeguridad;
	private String nombreInspectorSeguridad;
	private String passwordInspectorSeguridad;
	private Integer idFirmaResponsableSeguridad;
	private String nombreResponsableSeguridad;
	private String passwordResponsableSeguridad;

}
