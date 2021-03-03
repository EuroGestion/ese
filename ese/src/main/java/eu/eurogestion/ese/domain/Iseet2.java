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
@Table(name = "iseet_2")
public class Iseet2 implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id_iseet_2", unique = true, nullable = false)
	private Integer idIseet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_52", foreignKey = @ForeignKey(name = "fk_iseet_freno_52"))
	@ToString.Exclude
	private ResultadoInspeccion freno52;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_53", foreignKey = @ForeignKey(name = "fk_iseet_freno_53"))
	@ToString.Exclude
	private ResultadoInspeccion freno53;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_54", foreignKey = @ForeignKey(name = "fk_iseet_freno_54"))
	@ToString.Exclude
	private ResultadoInspeccion freno54;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_55", foreignKey = @ForeignKey(name = "fk_iseet_freno_55"))
	@ToString.Exclude
	private ResultadoInspeccion freno55;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_56", foreignKey = @ForeignKey(name = "fk_iseet_freno_56"))
	@ToString.Exclude
	private ResultadoInspeccion freno56;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_57", foreignKey = @ForeignKey(name = "fk_iseet_freno_57"))
	@ToString.Exclude
	private ResultadoInspeccion freno57;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_58", foreignKey = @ForeignKey(name = "fk_iseet_freno_58"))
	@ToString.Exclude
	private ResultadoInspeccion freno58;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_59", foreignKey = @ForeignKey(name = "fk_iseet_freno_59"))
	@ToString.Exclude
	private ResultadoInspeccion freno59;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_60", foreignKey = @ForeignKey(name = "fk_iseet_freno_60"))
	@ToString.Exclude
	private ResultadoInspeccion freno60;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_61", foreignKey = @ForeignKey(name = "fk_iseet_freno_61"))
	@ToString.Exclude
	private ResultadoInspeccion freno61;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_62", foreignKey = @ForeignKey(name = "fk_iseet_freno_62"))
	@ToString.Exclude
	private ResultadoInspeccion freno62;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_63", foreignKey = @ForeignKey(name = "fk_iseet_freno_63"))
	@ToString.Exclude
	private ResultadoInspeccion freno63;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_64", foreignKey = @ForeignKey(name = "fk_iseet_freno_64"))
	@ToString.Exclude
	private ResultadoInspeccion freno64;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_65", foreignKey = @ForeignKey(name = "fk_iseet_freno_65"))
	@ToString.Exclude
	private ResultadoInspeccion freno65;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_66", foreignKey = @ForeignKey(name = "fk_iseet_freno_66"))
	@ToString.Exclude
	private ResultadoInspeccion freno66;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_67", foreignKey = @ForeignKey(name = "fk_iseet_freno_67"))
	@ToString.Exclude
	private ResultadoInspeccion freno67;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_68", foreignKey = @ForeignKey(name = "fk_iseet_freno_68"))
	@ToString.Exclude
	private ResultadoInspeccion freno68;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_69", foreignKey = @ForeignKey(name = "fk_iseet_freno_69"))
	@ToString.Exclude
	private ResultadoInspeccion freno69;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_70", foreignKey = @ForeignKey(name = "fk_iseet_freno_70"))
	@ToString.Exclude
	private ResultadoInspeccion freno70;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_71", foreignKey = @ForeignKey(name = "fk_iseet_freno_71"))
	@ToString.Exclude
	private ResultadoInspeccion freno71;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_72", foreignKey = @ForeignKey(name = "fk_iseet_freno_72"))
	@ToString.Exclude
	private ResultadoInspeccion freno72;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_73", foreignKey = @ForeignKey(name = "fk_iseet_freno_73"))
	@ToString.Exclude
	private ResultadoInspeccion freno73;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_74", foreignKey = @ForeignKey(name = "fk_iseet_freno_74"))
	@ToString.Exclude
	private ResultadoInspeccion freno74;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_75", foreignKey = @ForeignKey(name = "fk_iseet_freno_75"))
	@ToString.Exclude
	private ResultadoInspeccion freno75;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_76", foreignKey = @ForeignKey(name = "fk_iseet_freno_76"))
	@ToString.Exclude
	private ResultadoInspeccion freno76;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_77", foreignKey = @ForeignKey(name = "fk_iseet_freno_77"))
	@ToString.Exclude
	private ResultadoInspeccion freno77;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_78", foreignKey = @ForeignKey(name = "fk_iseet_freno_78"))
	@ToString.Exclude
	private ResultadoInspeccion freno78;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_79", foreignKey = @ForeignKey(name = "fk_iseet_freno_79"))
	@ToString.Exclude
	private ResultadoInspeccion freno79;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_80", foreignKey = @ForeignKey(name = "fk_iseet_freno_80"))
	@ToString.Exclude
	private ResultadoInspeccion freno80;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_81", foreignKey = @ForeignKey(name = "fk_iseet_freno_81"))
	@ToString.Exclude
	private ResultadoInspeccion freno81;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_82", foreignKey = @ForeignKey(name = "fk_iseet_freno_82"))
	@ToString.Exclude
	private ResultadoInspeccion freno82;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_83", foreignKey = @ForeignKey(name = "fk_iseet_freno_83"))
	@ToString.Exclude
	private ResultadoInspeccion freno83;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_84", foreignKey = @ForeignKey(name = "fk_iseet_freno_84"))
	@ToString.Exclude
	private ResultadoInspeccion freno84;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_85", foreignKey = @ForeignKey(name = "fk_iseet_freno_85"))
	@ToString.Exclude
	private ResultadoInspeccion freno85;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_86", foreignKey = @ForeignKey(name = "fk_iseet_freno_86"))
	@ToString.Exclude
	private ResultadoInspeccion freno86;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_87", foreignKey = @ForeignKey(name = "fk_iseet_freno_87"))
	@ToString.Exclude
	private ResultadoInspeccion freno87;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_88", foreignKey = @ForeignKey(name = "fk_iseet_freno_88"))
	@ToString.Exclude
	private ResultadoInspeccion freno88;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_89", foreignKey = @ForeignKey(name = "fk_iseet_freno_89"))
	@ToString.Exclude
	private ResultadoInspeccion freno89;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_90", foreignKey = @ForeignKey(name = "fk_iseet_freno_90"))
	@ToString.Exclude
	private ResultadoInspeccion freno90;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_91", foreignKey = @ForeignKey(name = "fk_iseet_freno_91"))
	@ToString.Exclude
	private ResultadoInspeccion freno91;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freno_92", foreignKey = @ForeignKey(name = "fk_iseet_freno_92"))
	@ToString.Exclude
	private ResultadoInspeccion freno92;

	@Column(name = "observaciones_freno", length = 300)
	private String observacionesFreno;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_93", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_93"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia93;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_94", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_94"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia94;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_95", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_95"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia95;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_96", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_96"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia96;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_97", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_97"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia97;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_98", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_98"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia98;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_99", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_99"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia99;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_100", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_100"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia100;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_101", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_101"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia101;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_102", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_102"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia102;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vigilancia_103", foreignKey = @ForeignKey(name = "fk_iseet_vigilancia_103"))
	@ToString.Exclude
	private ResultadoInspeccion vigilancia103;

	@Column(name = "observaciones_vigilancia", length = 300)
	private String observacionesVigilancia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alumbrado_104", foreignKey = @ForeignKey(name = "fk_iseet_alumbrado_104"))
	@ToString.Exclude
	private ResultadoInspeccion alumbrado104;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alumbrado_105", foreignKey = @ForeignKey(name = "fk_iseet_alumbrado_105"))
	@ToString.Exclude
	private ResultadoInspeccion alumbrado105;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alumbrado_106", foreignKey = @ForeignKey(name = "fk_iseet_alumbrado_106"))
	@ToString.Exclude
	private ResultadoInspeccion alumbrado106;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alumbrado_107", foreignKey = @ForeignKey(name = "fk_iseet_alumbrado_107"))
	@ToString.Exclude
	private ResultadoInspeccion alumbrado107;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alumbrado_108", foreignKey = @ForeignKey(name = "fk_iseet_alumbrado_108"))
	@ToString.Exclude
	private ResultadoInspeccion alumbrado108;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alumbrado_109", foreignKey = @ForeignKey(name = "fk_iseet_alumbrado_109"))
	@ToString.Exclude
	private ResultadoInspeccion alumbrado109;

	@Column(name = "observaciones_alumbrado", length = 300)
	private String observacionesAlumbrado;

}
