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
@Table(name = "ismp")
public class Ismp implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_ismp", unique = true, nullable = false)
	private Integer idIsmp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_is", nullable = false, foreignKey = @ForeignKey(name = "fk_ismp_is"))
	@ToString.Exclude
	private Is is;

	@Column(name = "tipos_de_mercancias", length = 400)
	private String tiposMercancias;

	@Column(name = "numero_vagones", length = 400)
	private String numeroVagones;

	@Column(name = "etiquetas_vagones_buen_estado")
	private Integer etiquetasVagonesBuenEstado;

	@Column(name = "etiquetas_vagones_mal")
	private Integer etiquetasVagonesMal;

	@Column(name = "etiquetas_vagones_incorrecto")
	private Integer etiquetasVagonesIncorrecto;

	@Column(name = "etiquetas_vagones_inspeccionados")
	private Integer etiquetasVagonesInspeccionados;

	@Column(name = "falta_total_etiquetas")
	private Integer faltaTotalEtiquetas;

	@Column(name = "falta_alguna_etiqueta")
	private Integer faltaAlgunaEtiqueta;

	@Column(name = "etiqueta_inadecuada")
	private Integer etiquetaInadecuada;

	@Column(name = "tamano_inadecuado")
	private Integer tamanoInadecuado;

	@Column(name = "otras")
	private Integer otras;

	@Column(name = "etiquetas_observaciones", length = 400)
	private String etiquetasObservaciones;

	@Column(name = "paneles_vagones_buen_estado")
	private Integer panelesVagonesBuenEstado;

	@Column(name = "paneles_vagones_mal_estado")
	private Integer panelesVagonesMalEstado;

	@Column(name = "paneles_vagones_incorrecto")
	private Integer panelesVagonesIncorrecto;

	@Column(name = "paneles_vagones_inspeccionados")
	private Integer panelesVagonesInspeccionados;

	@Column(name = "numero_onu_incorrecto")
	private Integer numeroOnuIncorrecto;

	@Column(name = "falta_panel_lateral")
	private Integer faltaPanelLateral;

	@Column(name = "falta_panel_dos_laterales")
	private Integer faltaPanelDosLaterales;

	@Column(name = "tamano_inadecuada_panel")
	private Integer tamanoInadecuadaPanel;

	@Column(name = "paneles_otras")
	private Integer panelesOtras;

	@Column(name = "paneles_observaciones", length = 400)
	private String panelesObservaciones;

	@Column(name = "carta_vacio_correcta")
	private Integer cartaVacioCorrecta;

	@Column(name = "carta_vacio_incorrecta")
	private Integer cartaVacioIncorrecta;

	@Column(name = "carta_cargado_correcta")
	private Integer cartaCargadoCorrecta;

	@Column(name = "carta_cargado_incorrecta")
	private Integer cartaCargadoIncorrecta;

	@Column(name = "documentos_inspeccionados_correcta")
	private Integer documentosInspeccionadosCorrecta;

	@Column(name = "documentos_inspeccionados_incorrecta")
	private Integer documentosInspeccionadosIncorrecta;

	@Column(name = "carta_orden_inadecuado")
	private Integer cartaOrdenInadecuado;

	@Column(name = "carta_exceso_lexico")
	private Integer cartaExcesoLexico;

	@Column(name = "carta_denominacion_incorrecta")
	private Integer cartaDenominacionIncorrecta;

	@Column(name = "carta_numero_incorrecto")
	private Integer cartaNumeroIncorrecto;

	@Column(name = "carta_falta_frase")
	private Integer cartaFaltaFrase;

	@Column(name = "carta_falta_datos")
	private Integer cartaFaltaDatos;

	@Column(name = "carta_falta_mencion")
	private Integer cartaFaltaMencion;

	@Column(name = "carta_otras")
	private Integer cartaOtras;

	@Column(name = "carta_observaciones", length = 400)
	private String cartaObservaciones;

	@Column(name = "recepcion_boi")
	private Boolean recepcionBoi;

	@Column(name = "recepcion_boi_observaciones", length = 45)
	private String recepcionBoiObservaciones;

	@Column(name = "recepcion_ficha")
	private Boolean recepcionFicha;

	@Column(name = "recepcion_ficha_observaciones", length = 45)
	private String recepcionFichaObservaciones;

	@Column(name = "reconocimiento_material_terminal")
	private Boolean reconocimientoMaterialTerminal;

	@Column(name = "reconocimiento_material_archiva")
	private Boolean reconocimientoMaterialArchiva;

	@Column(name = "reconocimiento_material_origen")
	private Boolean reconocimientoMaterialOrigen;

	@Column(name = "reconocimiento_material_terminal_observaciones", length = 45)
	private String reconocimientoMaterialTerminalObservaciones;

	@Column(name = "reconocimiento_material_archiva_observaciones", length = 45)
	private String reconocimientoMaterialArchivaObservaciones;

	@Column(name = "reconocimiento_material_origen_observaciones", length = 45)
	private String reconocimientoMaterialOrigenObservaciones;

	@Column(name = "reconocimiento_posterior_terminal")
	private Boolean reconocimientoPosteriorTerminal;

	@Column(name = "reconocimiento_posterior_terminal_observaciones", length = 45)
	private String reconocimientoPosteriorTerminalObservaciones;

	@Column(name = "reconocimiento_posterior_modelo")
	private Boolean reconocimientoPosteriorModelo;

	@Column(name = "reconocimiento_posterior_modelo_observaciones", length = 45)
	private String reconocimientoPosteriorModeloObservaciones;

	@Column(name = "reconocimiento_visual_deposito")
	private Boolean reconocimientoVisualDeposito;

	@Column(name = "reconocimiento_visual_deposito_observaciones", length = 45)
	private String reconocimientoVisualDepositoObservaciones;

	@Column(name = "reconocimiento_visual_defectos")
	private Boolean reconocimientoVisualDefectos;

	@Column(name = "reconocimiento_visual_defectos_observaciones", length = 45)
	private String reconocimientoVisualDefectosObservaciones;

	@Column(name = "reconocimiento_visual_capacidad")
	private Boolean reconocimientoVisualCapacidad;

	@Column(name = "reconocimiento_visual_capacidad_observaciones", length = 45)
	private String reconocimientoVisualCapacidadObservaciones;

	@Column(name = "reconocimiento_observaciones", length = 400)
	private String reconocimientoObservaciones;

	@Column(name = "prescripcion_se_notifica")
	private Boolean prescripcionSeNotifica;

	@Column(name = "prescripcion_se_notifica_observaciones", length = 45)
	private String prescripcionSeNotificaObservaciones;

	@Column(name = "prescripcion_corresponde")
	private Boolean prescripcionCorresponde;

	@Column(name = "prescripcion_corresponde_observaciones", length = 45)
	private String prescripcionCorrespondeObservaciones;

	@Column(name = "medidas_cautelares", length = 400)
	private String medidasCautelares;

	@Column(name = "lista_documentos", length = 400)
	private String listaDocumentos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo78", foreignKey = @ForeignKey(name = "fk_ismp_evidencia78"))
	@ToString.Exclude
	private Evidencia evidencia78;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo80", foreignKey = @ForeignKey(name = "fk_ismp_evidencia80"))
	@ToString.Exclude
	private Evidencia evidencia80;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_inspector", foreignKey = @ForeignKey(name = "fk_ismp_firma_inspector"))
	@ToString.Exclude
	private Personal firmaInspector;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_inspector")
	private Date fechahoraFirmaInspector;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_responsable", foreignKey = @ForeignKey(name = "fk_ismp_firma_responsable"))
	@ToString.Exclude
	private Personal firmaResponsable;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_responsable")
	private Date fechahoraFirmaResponsable;
}
