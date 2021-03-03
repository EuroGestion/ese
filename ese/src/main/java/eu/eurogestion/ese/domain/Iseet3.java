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
@Table(name = "iseet_3")
public class Iseet3 implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_iseet_3", unique = true, nullable = false)
	private Integer idIseet3;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pantografo_110", foreignKey = @ForeignKey(name = "fk_iseet_pantografo_110"))
	@ToString.Exclude
	private ResultadoInspeccion pantografo110;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pantografo_111", foreignKey = @ForeignKey(name = "fk_iseet_pantografo_111"))
	@ToString.Exclude
	private ResultadoInspeccion pantografo111;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pantografo_112", foreignKey = @ForeignKey(name = "fk_iseet_pantografo_112"))
	@ToString.Exclude
	private ResultadoInspeccion pantografo112;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pantografo_113", foreignKey = @ForeignKey(name = "fk_iseet_pantografo_113"))
	@ToString.Exclude
	private ResultadoInspeccion pantografo113;

	@Column(name = "observaciones_pantografo", length = 300)
	private String observacionesPantografo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_114", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_114"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento114;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_115", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_115"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento115;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_116", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_116"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento116;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_117", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_117"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento117;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_118", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_118"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento118;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_119", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_119"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento119;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_120", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_120"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento120;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compartimento_121", foreignKey = @ForeignKey(name = "fk_iseet_compartimento_121"))
	@ToString.Exclude
	private ResultadoInspeccion compartimento121;

	@Column(name = "observaciones_compartimento", length = 300)
	private String observacionesCompartimento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funcionalidad_122", foreignKey = @ForeignKey(name = "fk_iseet_funcionalidad_122"))
	@ToString.Exclude
	private ResultadoInspeccion funcionalidad122;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funcionalidad_123", foreignKey = @ForeignKey(name = "fk_iseet_funcionalidad_123"))
	@ToString.Exclude
	private ResultadoInspeccion funcionalidad123;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funcionalidad_124", foreignKey = @ForeignKey(name = "fk_iseet_funcionalidad_124"))
	@ToString.Exclude
	private ResultadoInspeccion funcionalidad124;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funcionalidad_125", foreignKey = @ForeignKey(name = "fk_iseet_funcionalidad_125"))
	@ToString.Exclude
	private ResultadoInspeccion funcionalidad125;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funcionalidad_126", foreignKey = @ForeignKey(name = "fk_iseet_funcionalidad_126"))
	@ToString.Exclude
	private ResultadoInspeccion funcionalidad126;

	@Column(name = "observaciones_funcionalidad", length = 300)
	private String observacionesFuncionalidad;

	@Column(name = "medidas_cautelares", length = 300)
	private String medidasCautelares;

	@Column(name = "documentos_anexos", length = 300)
	private String documentosAnexos;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_inspector", foreignKey = @ForeignKey(name = "fk_iseet_firma_inspector"))
	@ToString.Exclude
	private Personal firmaInspector;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_inspector")
	private Date fechahoraFirmaInspector;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_firma_responsable", foreignKey = @ForeignKey(name = "fk_iseet_firma_responsable"))
	@ToString.Exclude
	private Personal firmaResponsable;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechahora_firma_responsable")
	private Date fechahoraFirmaResponsable;
}
