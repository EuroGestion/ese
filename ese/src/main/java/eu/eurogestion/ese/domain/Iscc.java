package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "iscc")
public class Iscc implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_iscc", unique = true, nullable = false)
	private Integer idIscc;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_is", nullable = false, foreignKey = @ForeignKey(name = "fk_iscc_is"))
	@ToString.Exclude
	private Is is;

	@Column(name = "tipos_de_mercancias", length = 400)
	private String tiposMercancias;

	@Column(name = "numero_vagones", length = 4)
	private String numeroVagones;

	@Column(name = "trafico_vagones_inspeccionado", length = 400)
	private String traficoVagonesInspeccionado;

	@Column(name = "trafico_observaciones", length = 400)
	private String traficoObservaciones;

	@Column(name = "carga_transporte_se_realiza_observaciones", length = 45)
	private String cargaTransporteRealizaObservaciones;

	@Column(name = "carga_la_cohesion_observaciones", length = 45)
	private String cargaCohesionObservaciones;

	@Column(name = "carga_las_mercancias_observaciones", length = 45)
	private String cargaMercanciasObservaciones;

	@Column(name = "carga_la_distribucion")
	private Integer cargaDistribucion;

	@Column(name = "carga_la_distribucion_observaciones", length = 45)
	private String cargaDistribucionObservaciones;

	@Column(name = "carga_cargas_concentradas")
	private Integer cargaCargasConcentradas;

	@Column(name = "carga_cargas_concentradas_observaciones", length = 45)
	private String cargaCargasConcentradasObservaciones;

	@Column(name = "carga_la_distancia")
	private Integer cargaDistancia;

	@Column(name = "carga_la_distancia_observaciones", length = 45)
	private String cargaDistanciaObservaciones;

	@Column(name = "carga_la_altura")
	private Integer cargaAltura;

	@Column(name = "carga_la_altura_observaciones", length = 45)
	private String cargaAlturaObservaciones;

	@Column(name = "carga_se_rebasa")
	private Integer cargaRebasa;

	@Column(name = "carga_se_rebasa_observaciones", length = 45)
	private String cargaRebasaObservaciones;

	@Column(name = "carga_el_cargamento")
	private Integer cargaCargamento;

	@Column(name = "carga_el_cargamento_observaciones", length = 45)
	private String cargaCargamentoObservaciones;

	@Column(name = "carga_observaciones", length = 400)
	private String cargaObservaciones;

	@Column(name = "vagones_se_conservan")
	private Integer vagonesConservan;

	@Column(name = "vagones_se_conservan_observaciones", length = 45)
	private String vagonesConservanObservaciones;

	@Column(name = "vagones_cerrados_techos")
	private Integer vagonesCerradosTechos;

	@Column(name = "vagones_cerrados_techos_observaciones", length = 45)
	private String vagonesCerradosTechosObservaciones;

	@Column(name = "vagones_cerradas_puertas")
	private Integer vagonesCerradasPuertas;

	@Column(name = "vagones_cerradas_puertas_observaciones", length = 45)
	private String vagonesCerradasPuertasObservaciones;

	@Column(name = "vagones_cerradas_tapas")
	private Integer vagonesCerradasTapas;

	@Column(name = "vagones_cerradas_tapas_observaciones", length = 45)
	private String vagonesCerradasTapasObservaciones;

	@Column(name = "vagones_cerradas_compuertas")
	private Integer vagonesCerradasCompuertas;

	@Column(name = "vagones_cerradas_compuertas_observaciones", length = 45)
	private String vagonesCerradasCompuertasObservaciones;

	@Column(name = "vagones_cerradas_valvulas")
	private Integer vagonesCerradasValvulas;

	@Column(name = "vagones_cerradas_valvulas_observaciones", length = 45)
	private String vagonesCerradasValvulasObservaciones;

	@Column(name = "vagones_bordes_levantados")
	private Integer vagonesBordesLevantados;

	@Column(name = "vagones_bordes_levantados_observaciones", length = 45)
	private String vagonesBordesLevantadosObservaciones;

	@Column(name = "vagones_bordes_bajados_inmovil")
	private Integer vagonesBordesBajadosInmovil;

	@Column(name = "vagones_bordes_bajados_inmovil_observaciones", length = 45)
	private String vagonesBordesBajadosInmovilObservaciones;

	@Column(name = "vagones_bordes_bajados")
	private Integer vagonesBordesBajados;

	@Column(name = "vagones_bordes_bajados_observaciones", length = 45)
	private String vagonesBordesBajadosObservaciones;

	@Column(name = "vagones_visibles_inscripciones")
	private Integer vagonesVisiblesInscripciones;

	@Column(name = "vagones_visibles_inscripciones_observaciones", length = 45)
	private String vagonesVisiblesInscripcionesObservaciones;

	@Column(name = "vagones_teleros_amovibles")
	private Integer vagonesTelerosAmovibles;

	@Column(name = "vagones_teleros_amovibles_observaciones", length = 45)
	private String vagonesTelerosAmoviblesObservaciones;

	@Column(name = "vagones_teleros_pivotantes")
	private Integer vagonesTelerosPivotantes;

	@Column(name = "vagones_teleros_pivotantes_observaciones", length = 45)
	private String vagonesTelerosPivotantesObservaciones;

	@Column(name = "vagones_teleros_pivotantes_horizontal")
	private Integer vagonesTelerosPivotantesHorizontal;

	@Column(name = "vagones_teleros_pivotantes_horizontal_observaciones", length = 45)
	private String vagonesTelerosPivotantesHorizontalObservaciones;

	@Column(name = "vagones_cadenas_teleros")
	private Integer vagonesCadenasTeleros;

	@Column(name = "vagones_cadenas_teleros_observaciones", length = 45)
	private String vagonesCadenasTelerosObservaciones;

	@Column(name = "vagones_dispositivos_tension")
	private Integer vagonesDispositivosTension;

	@Column(name = "vagones_dispositivos_tension_observaciones", length = 45)
	private String vagonesDispositivosTensionObservaciones;

	@Column(name = "vagones_piso")
	private Integer vagonesPiso;

	@Column(name = "vagones_piso_observaciones", length = 45)
	private String vagonesPisoObservaciones;

	@Column(name = "vagones_mercancias")
	private Integer vagonesMercancias;

	@Column(name = "vagones_mercancias_observaciones", length = 45)
	private String vagonesMercanciasObservaciones;

	@Column(name = "vagones_paredes")
	private Integer vagonesParedes;

	@Column(name = "vagones_paredes_observaciones", length = 45)
	private String vagonesParedesObservaciones;

	@Column(name = "vagones_puertas")
	private Integer vagonesPuertas;

	@Column(name = "vagones_puertas_observaciones", length = 45)
	private String vagonesPuertasObservaciones;

	@Column(name = "vagones_anillas")
	private Integer vagonesAnillas;

	@Column(name = "vagones_anillas_observaciones", length = 45)
	private String vagonesAnillasObservaciones;

	@Column(name = "vagones_equipamientos")
	private Integer vagonesEquipamientos;

	@Column(name = "vagones_equipamientos_observaciones", length = 45)
	private String vagonesEquipamientosObservaciones;

	@Column(name = "vagones_observaciones", length = 400)
	private String vagonesObservaciones;

	@Column(name = "acondicionamiento_reparto")
	private Integer acondicionamientoReparto;

	@Column(name = "acondicionamiento_reparto_observaciones", length = 45)
	private String acondicionamientoRepartoObservaciones;

	@Column(name = "acondicionamiento_mercancias")
	private Integer acondicionamientoMercancias;

	@Column(name = "acondicionamiento_mercancias_observaciones", length = 45)
	private String acondicionamientoMercanciasObservaciones;

	@Column(name = "acondicionamiento_pesadas")
	private Integer acondicionamientoPesadas;

	@Column(name = "acondicionamiento_pesadas_observaciones", length = 45)
	private String acondicionamientoPesadasObservaciones;

	@Column(name = "acondicionamiento_susceptibles")
	private Integer acondicionamientoSusceptibles;

	@Column(name = "acondicionamiento_susceptibles_observaciones", length = 45)
	private String acondicionamientoSusceptiblesObservaciones;

	@Column(name = "acondicionamientoSolidamenteSujeta")
	private Integer acondicionamientoSolidamenteSujeta;

	@Column(name = "acondicionamiento_solidamente_sujeta_observaciones", length = 45)
	private String acondicionamientoSolidamenteSujetaObservaciones;

	@Column(name = "acondicionamiento_lona_proxima")
	private Integer acondicionamientoLonaProxima;

	@Column(name = "acondicionamiento_lona_proxima_observaciones", length = 45)
	private String acondicionamientoLonaProximaObservaciones;

	@Column(name = "acondicionamiento_lona_separacion")
	private Integer acondicionamientoLonaSeparacion;

	@Column(name = "acondicionamiento_lona_separacion_observaciones", length = 45)
	private String acondicionamientoLonaSeparacionObservaciones;

	@Column(name = "acondicionamiento_verificar_paredes")
	private Integer acondicionamientoVerificarParedes;

	@Column(name = "acondicionamiento_verificar_paredes_observaciones", length = 45)
	private String acondicionamientoVerificarParedesObservaciones;

	@Column(name = "acondicionamiento_verificar_calces")
	private Integer acondicionamientoVerificarCalces;

	@Column(name = "acondicionamiento_verificar_calces_observaciones", length = 45)
	private String acondicionamientoVerificarCalcesObservaciones;

	@Column(name = "acondicionamiento_verificar_amarre")
	private Integer acondicionamientoVerificarAmarre;

	@Column(name = "acondicionamiento_verificar_amarre_observaciones", length = 45)
	private String acondicionamientoVerificarAmarreObservaciones;

	@Column(name = "acondicionamiento_verificar_dos_amarras")
	private Integer acondicionamientoVerificarDosAmarras;

	@Column(name = "acondicionamiento_verificar_dos_ammarras_observaciones", length = 45)
	private String acondicionamientoVerificarDosAmarrasObservaciones;

	@Column(name = "acondicionamiento_verificar_calces_7")
	private Integer acondicionamientoVerificarCalces7;

	@Column(name = "acondicionamiento_verificar_calces_7_observaciones", length = 45)
	private String acondicionamientoVerificarCalces7Observaciones;

	@Column(name = "acondicionamiento_verificar_bastidores")
	private Integer acondicionamientoVerificarBastidores;

	@Column(name = "acondicionamiento_verificar_bastidores_observaciones", length = 45)
	private String acondicionamientoVerificarBastidoresObservaciones;

	@Column(name = "acondicionamiento_verificar_cunas")
	private Integer acondicionamientoVerificarCunas;

	@Column(name = "acondicionamiento_verificar_cunas_observaciones", length = 45)
	private String acondicionamientoVerificarCunasObservaciones;

	@Column(name = "acondicionamiento_verificar_transversal")
	private Integer acondicionamientoVerificarTransversal;

	@Column(name = "acondicionamiento_verificar_transversal_observaciones", length = 45)
	private String acondicionamientoVerificarTransversalObservaciones;

	@Column(name = "acondicionamiento_verificar_longitudinal")
	private Integer acondicionamientoVerificarLongitudinal;

	@Column(name = "acondicionamiento_verificar_longitudinal_observaciones", length = 45)
	private String acondicionamientoVerificarLongitudinalObservaciones;

	@Column(name = "acondicionamiento_verificar_bobinas_menos_10")
	private Integer acondicionamientoVerificarBobinasMenos10;

	@Column(name = "acondicionamiento_verificar_bobinas_menos_10_observaciones", length = 45)
	private String acondicionamientoVerificarBobinasMenos10Observaciones;

	@Column(name = "acondicionamiento_verificar_bobinas_mas_10")
	private Integer acondicionamientoVerificarBobinasMas10;

	@Column(name = "acondicionamiento_verificar_bobinas_mas_10_observaciones", length = 45)
	private String acondicionamientoVerificarBobinasMas10Observaciones;

	@Column(name = "acondicionamiento_bascular_agrupamiento")
	private Integer acondicionamientoBascularAgrupamiento;

	@Column(name = "acondicionamiento_bascular_agrupamiento_observaciones", length = 45)
	private String acondicionamientoBascularAgrupamientoObservaciones;

	@Column(name = "acondicionamiento_bascular_puntales")
	private Integer acondicionamientoBascularPuntales;

	@Column(name = "acondicionamiento_bascular_puntales_observaciones", length = 45)
	private String acondicionamientoBascularPuntalesObservaciones;

	@Column(name = "acondicionamiento_bascular_caballetes")
	private Integer acondicionamientoBascularCaballetes;

	@Column(name = "acondicionamiento_bascular_caballetes_observaciones", length = 45)
	private String acondicionamientoBascularCaballetesObservaciones;

	@Column(name = "acondicionamiento_apilada_repartidas")
	private Integer acondicionamientoApiladaRepartidas;

	@Column(name = "acondicionamiento_apilada_repartidas_observaciones", length = 45)
	private String acondicionamientoApiladaRepartidasObservaciones;

	@Column(name = "acondicionamiento_apilada_anchura")
	private Integer acondicionamientoApiladaAnchura;

	@Column(name = "acondicionamiento_apilada_anchura_observaciones", length = 45)
	private String acondicionamientoApiladaAnchuraObservaciones;

	@Column(name = "acondicionamiento_apilada_capas")
	private Integer acondicionamientoApiladaCapas;

	@Column(name = "acondicionamiento_apilada_capas_observaciones", length = 45)
	private String acondicionamientoApiladaCapasObservaciones;

	@Column(name = "acondicionamiento_apilada_rozamiento")
	private Integer acondicionamientoApiladaRozamiento;

	@Column(name = "acondicionamiento_apilada_rozamiento_observaciones", length = 45)
	private String acondicionamientoApiladaRozamientoObservaciones;

	@Column(name = "acondicionamiento_apilada_bastidores")
	private Integer acondicionamientoApiladaBastidores;

	@Column(name = "acondicionamiento_apilada_bastidores_observaciones", length = 45)
	private String acondicionamientoApiladaBastidoresObservaciones;

	@Column(name = "acondicionamiento_apilada_unidades")
	private Integer acondicionamientoApiladaUnidades;

	@Column(name = "acondicionamiento_apilada_unidades_observaciones", length = 45)
	private String acondicionamientoApiladaUnidadesObservaciones;

	@Column(name = "acondicionamiento_apilada_cintas")
	private Integer acondicionamientoApiladaCintas;

	@Column(name = "acondicionamiento_apilada_cintas_observaciones", length = 45)
	private String acondicionamientoApiladaCintasObservaciones;

	@Column(name = "acondicionamiento_apilada_parte_inferior")
	private Integer acondicionamientoApiladaParteInferior;

	@Column(name = "acondicionamiento_apilada_parte_inferior_observaciones", length = 45)
	private String acondicionamientoApiladaParteInferiorObservaciones;

	@Column(name = "acondicionamiento_apilada_intercalares")
	private Integer acondicionamientoApiladaIntercalares;

	@Column(name = "acondicionamiento_apilada_intercalares_observaciones", length = 45)
	private String acondicionamientoApiladaIntercalaresObservaciones;

	@Column(name = "acondicionamiento_apilada_imbricadas")
	private Integer acondicionamientoApiladaImbricadas;

	@Column(name = "acondicionamiento_apilada_imbricadas_observaciones", length = 45)
	private String acondicionamientoApiladaImbricadasObservaciones;

	@Column(name = "acondicionamiento_apilada_oscilar")
	private Integer acondicionamientoApiladaOscilar;

	@Column(name = "acondicionamiento_apilada_oscilar_observaciones", length = 45)
	private String acondicionamientoApiladaOscilarObservaciones;

	@Column(name = "acondicionamiento_apilada_tubos")
	private Integer acondicionamientoApiladaTubos;

	@Column(name = "acondicionamiento_apilada_tubos_observaciones", length = 45)
	private String acondicionamientoApiladaTubosObservaciones;

	@Column(name = "acondicionamiento_apilada_capas_calces")
	private Integer acondicionamientoApiladaCapasCalces;

	@Column(name = "acondicionamiento_apilada_capas_calces_observaciones", length = 45)
	private String acondicionamientoApiladaCapasCalcesObservaciones;

	@Column(name = "acondicionamiento_rigidas")
	private Integer acondicionamientoRigidas;

	@Column(name = "acondicionamiento_rigidas_observaciones", length = 45)
	private String acondicionamientoRigidasObservaciones;

	@Column(name = "acondicionamiento_flexibles")
	private Integer acondicionamientoFlexibles;

	@Column(name = "acondicionamiento_flexibles_observaciones", length = 45)
	private String acondicionamientoFlexiblesObservaciones;

	@Column(name = "acondicionamiento_observaciones", length = 400)
	private String acondicionamientoObservaciones;

	@Column(name = "medios_calces_retencion")
	private Integer mediosCalcesRetencion;

	@Column(name = "medios_calces_retencion_observaciones", length = 45)
	private String mediosCalcesRetencionObservaciones;

	@Column(name = "medios_calces_rodadura")
	private Integer mediosCalcesRodadura;

	@Column(name = "medios_calces_rodadura_observaciones", length = 45)
	private String mediosCalcesRodaduraObservaciones;

	@Column(name = "medios_intercalares")
	private Integer mediosIntercalares;

	@Column(name = "medios_intercalares_observaciones", length = 45)
	private String mediosIntercalaresObservaciones;

	@Column(name = "medios_amarres")
	private Integer mediosAmarres;

	@Column(name = "medios_amarres_observaciones", length = 45)
	private String mediosAmarresObservaciones;

	@Column(name = "medios_observaciones", length = 400)
	private String mediosObservaciones;

	@Column(name = "medidas_cautelares", length = 400)
	private String medidasCautelares;

	@Column(name = "lista_anexos", length = 450)
	private String listaAnexos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo30", foreignKey = @ForeignKey(name = "fk_iscc_evidencia30"))
	@ToString.Exclude
	private Evidencia evidencia30;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo31", foreignKey = @ForeignKey(name = "fk_iscc_evidencia31"))
	@ToString.Exclude
	private Evidencia evidencia31;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_inspector", foreignKey = @ForeignKey(name = "fk_iscc_firma_inspector"))
	@ToString.Exclude
	private Personal firmaInspector;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_inspector")
	private Date fechahoraFirmaInspector;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_responsable", foreignKey = @ForeignKey(name = "fk_iscc_firma_responsable"))
	@ToString.Exclude
	private Personal firmaResponsable;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_responsable")
	private Date fechahoraFirmaResponsable;
}
