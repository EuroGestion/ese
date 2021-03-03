package eu.eurogestion.ese.domain;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * @author Rmerino, alvaro
 *
 */

@Data
@Entity
@Table(name = "iseet")
public class Iseet implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_iseet", unique = true, nullable = false)
	private Integer idIseet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_is", nullable = false, foreignKey = @ForeignKey(name = "fk_iseet_is"))
	@ToString.Exclude
	private Is is;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_maquinista", nullable = false, foreignKey = @ForeignKey(name = "iseet_maquinista"))
	@ToString.Exclude
	private Personal maquinista;

	@Column(name = "nve", nullable = false, length = 12)
	private String nve;

	@Column(name = "observaciones_circulacion", length = 200)
	private String observacionesCirculacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caja_1", foreignKey = @ForeignKey(name = "iseet_caja1"))
	@ToString.Exclude
	private ResultadoInspeccion caja1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caja_2", foreignKey = @ForeignKey(name = "iseet_caja2"))
	@ToString.Exclude
	private ResultadoInspeccion caja2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caja_3", foreignKey = @ForeignKey(name = "iseet_caja3"))
	@ToString.Exclude
	private ResultadoInspeccion caja3;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caja_4", foreignKey = @ForeignKey(name = "iseet_caja4"))
	@ToString.Exclude
	private ResultadoInspeccion caja4;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caja_5", foreignKey = @ForeignKey(name = "iseet_caja5"))
	@ToString.Exclude
	private ResultadoInspeccion caja5;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caja_6", foreignKey = @ForeignKey(name = "iseet_caja6"))
	@ToString.Exclude
	private ResultadoInspeccion caja6;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caja_7", foreignKey = @ForeignKey(name = "iseet_caja7"))
	@ToString.Exclude
	private ResultadoInspeccion caja7;

	@Column(name = "observaciones_caja", length = 300)
	private String observacionesCaja;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_8", foreignKey = @ForeignKey(name = "iseet_elementos_choque_8"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque8;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_9", foreignKey = @ForeignKey(name = "iseet_elementos_choque_9"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque9;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_10", foreignKey = @ForeignKey(name = "iseet_elementos_choque_10"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque10;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_11", foreignKey = @ForeignKey(name = "iseet_elementos_choque_11"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque11;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_12", foreignKey = @ForeignKey(name = "iseet_elementos_choque_12"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque12;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_13", foreignKey = @ForeignKey(name = "iseet_elementos_choque_13"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque13;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_14", foreignKey = @ForeignKey(name = "iseet_elementos_choque_14"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque14;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_15", foreignKey = @ForeignKey(name = "iseet_elementos_choque_15"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque15;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_16", foreignKey = @ForeignKey(name = "iseet_elementos_choque_16"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque16;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_17", foreignKey = @ForeignKey(name = "iseet_elementos_choque_17"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque17;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_18", foreignKey = @ForeignKey(name = "iseet_elementos_choque_18"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque18;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_19", foreignKey = @ForeignKey(name = "iseet_elementos_choque_19"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque19;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_20", foreignKey = @ForeignKey(name = "iseet_elementos_choque_20"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque20;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_21", foreignKey = @ForeignKey(name = "iseet_elementos_choque_21"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque21;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_22", foreignKey = @ForeignKey(name = "iseet_elementos_choque_22"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque22;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_23", foreignKey = @ForeignKey(name = "iseet_elementos_choque_23"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque23;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elementos_choque_24", foreignKey = @ForeignKey(name = "iseet_elementos_choque_24"))
	@ToString.Exclude
	private ResultadoInspeccion elementosChoque24;

	@Column(name = "observaciones_elementos_choque", length = 300)
	private String observacionesElementosChoque;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bogies_25", foreignKey = @ForeignKey(name = "iseet_bogies_25"))
	@ToString.Exclude
	private ResultadoInspeccion bogies25;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bogies_26", foreignKey = @ForeignKey(name = "iseet_bogies_26"))
	@ToString.Exclude
	private ResultadoInspeccion bogies26;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bogies_27", foreignKey = @ForeignKey(name = "iseet_bogies_27"))
	@ToString.Exclude
	private ResultadoInspeccion bogies27;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bogies_28", foreignKey = @ForeignKey(name = "iseet_bogies_28"))
	@ToString.Exclude
	private ResultadoInspeccion bogies28;

	@Column(name = "observaciones_bogies", length = 300)
	private String observacionesBogies;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suspension_29", foreignKey = @ForeignKey(name = "iseet_suspension_29"))
	@ToString.Exclude
	private ResultadoInspeccion suspension29;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suspension_30", foreignKey = @ForeignKey(name = "iseet_suspension_30"))
	@ToString.Exclude
	private ResultadoInspeccion suspension30;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suspension_31", foreignKey = @ForeignKey(name = "iseet_suspension_31"))
	@ToString.Exclude
	private ResultadoInspeccion suspension31;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suspension_32", foreignKey = @ForeignKey(name = "iseet_suspension_32"))
	@ToString.Exclude
	private ResultadoInspeccion suspension32;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suspension_33", foreignKey = @ForeignKey(name = "iseet_suspension_33"))
	@ToString.Exclude
	private ResultadoInspeccion suspension33;

	@Column(name = "observaciones_suspension", length = 300)
	private String observacionesSuspension;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_34", foreignKey = @ForeignKey(name = "iseet_rodaje_34"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje34;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_35", foreignKey = @ForeignKey(name = "iseet_rodaje_35"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje35;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_36", foreignKey = @ForeignKey(name = "iseet_rodaje_36"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje36;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_37", foreignKey = @ForeignKey(name = "iseet_rodaje_37"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje37;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_38", foreignKey = @ForeignKey(name = "iseet_rodaje_38"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje38;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_39", foreignKey = @ForeignKey(name = "iseet_rodaje_39"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje39;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_40", foreignKey = @ForeignKey(name = "iseet_rodaje_40"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje40;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_41", foreignKey = @ForeignKey(name = "iseet_rodaje_41"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje41;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_42", foreignKey = @ForeignKey(name = "iseet_rodaje_42"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje42;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_43", foreignKey = @ForeignKey(name = "iseet_rodaje_43"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje43;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_44", foreignKey = @ForeignKey(name = "iseet_rodaje_44"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje44;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_45", foreignKey = @ForeignKey(name = "iseet_rodaje_45"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje45;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_46", foreignKey = @ForeignKey(name = "iseet_rodaje_46"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje46;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_47", foreignKey = @ForeignKey(name = "iseet_rodaje_47"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje47;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_48", foreignKey = @ForeignKey(name = "iseet_rodaje_48"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje48;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_49", foreignKey = @ForeignKey(name = "iseet_rodaje_49"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje49;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_50", foreignKey = @ForeignKey(name = "iseet_rodaje_50"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje50;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rodaje_51", foreignKey = @ForeignKey(name = "iseet_rodaje_51"))
	@ToString.Exclude
	private ResultadoInspeccion rodaje51;

	@Column(name = "observaciones_rodaje", length = 300)
	private String observacionesRodaje;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_iseet_2", foreignKey = @ForeignKey(name = "iseet_iseet_2"))
	@ToString.Exclude
	private Iseet2 iseet2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_iseet_3", foreignKey = @ForeignKey(name = "iseet_iseet_3"))
	@ToString.Exclude
	private Iseet3 iseet3;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo15", foreignKey = @ForeignKey(name = "fk_iseet_evidencia_tipo15"))
	@ToString.Exclude
	private Evidencia evidencia15;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evidencia_tipo17", foreignKey = @ForeignKey(name = "fk_iseet_evidencia_tipo17"))
	@ToString.Exclude
	private Evidencia evidencia17;
}
