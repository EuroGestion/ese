package eu.eurogestion.ese.utils;

/**
 * @author Rmerino
 *
 */

public class Constantes {

	public static final String ZERO = "0";

	public static final Integer MAXIMA_PREVISION_PASF = 5;

	public static final String FORMATO_FECHA_BLOCKCHAIN = "yyyyMMdd";
	public static final String FORMATO_FECHA_LARGO = "dd/MM/yyyy";
	public static final String FORMATO_FECHA_CORTO = "dd/MM/yy";
	public static final String FORMATO_FECHA_PANTALLA = "yyyy-MM-dd";
	public static final String FORMATO_FECHA_HORAS_PANTALLA = "HH:mm";
	public static final String FORMATO_FECHA_ANNO = "YYYY";
	public static final String FORMATO_FECHA_COMPLETA_PANTALLA = "yyyy-MM-dd'T'HH:mm";
	public static final String FORMATO_FECHA_COMPLETA_FIRMA = "dd-MM-yyyy HH:mm";

	/** BLOCKCHAIN PARAMS **/
	public static final String BC_URL_SERVER = "HTTP://5.153.57.74:8545";
	public static final String BC_CREDENTIALS = "a190815b83566e45323ccb4d855cfd855d278a9dc3e6f4ab4021fecd60fbe41b";
	public static final String BC_CONTRACT_ADRESS = "0x46A79626F5febC1675E043274f565C49e9A4D0fC";

	/** REST CALL PARAMS **/
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String MIME_TYPE = "Mime-Type";
	public static final String TYPE_JSON = "application/json";
	public static final String TYPE_OCTET_STREAM = "application/octet-stream";
	public static final String TYPE_PDF = "application/pdf";
	public static final String TYPE_JPEG = "image/jpeg";

	/** PDF **/
	public static final String TITULO_AOT = "AUXILIAR DE OPERACIONES DEL TREN SIN ALCANCE DE MANIOBRAS";
	public static final String TITULO_AOTM = "AUXILIAR DE OPERACIONES DEL TREN CON ALCANCE DE MANIOBRAS";
	public static final String TITULO_OVM = "OPERADOR DE VEHÍCULOS DE MANIOBRAS";
	public static final String EMPRESA_FERROVIARIA = "Empresa Ferroviaria";
	public static final String TIPO_CURSO_MATERIAL_NOMBRE = "Material";
	public static final String TIPO_CURSO_INFRAESTRUCTURA_NOMBRE = "Infraestructura";
	public static final String FORMATO_FECHA_DOCUMENTAL = "dd 'de' MMMMMMMMM 'de' yyyy";

	/** ESTADOS CURSOS **/
	public static final Integer ESTADO_CURSO_CREADO = 1;
	public static final Integer ESTADO_CURSO_PLANIFICADO = 2;
	public static final Integer ESTADO_CURSO_PENDIENTE_APROVACION = 3;
	public static final Integer ESTADO_CURSO_APROBADO = 4;
	public static final Integer ESTADO_CURSO_NO_APROBADO = 5;
	public static final Integer ESTADO_CURSO_ANULADO = 6;

	/** ESTADOS CURSO ALUMNO **/
	public static final Integer ESTADO_CURSO_ALUMNO_INSCRITO = 1;
	public static final Integer ESTADO_CURSO_ALUMNO_EN_CURSO = 2;
	public static final Integer ESTADO_CURSO_ALUMNO_SUPERADO = 3;
	public static final Integer ESTADO_CURSO_ALUMNO_NO_SUPERADO = 4;
	public static final Integer ESTADO_CURSO_ALUMNO_CADUCADO = 5;

	/** ESTADOS REVISION **/
	public static final Integer ESTADO_REVISION_CREADO = 1;
	public static final Integer ESTADO_REVISION_PLANIFICADO = 2;
	public static final Integer ESTADO_REVISION_APTO = 3;
	public static final Integer ESTADO_REVISION_NO_APTO = 4;
	public static final Integer ESTADO_REVISION_CADUCADO = 5;
	public static final Integer ESTADO_REVISION_ANULADO = 6;

	/** ESTADOS TITULOS **/
	public static final Integer ESTADO_TITULO_CONCEDIDO = 1;
	public static final Integer ESTADO_TITULO_SUSPENDIDO = 2;
	public static final Integer ESTADO_TITULO_REVOCADO = 3;
	public static final Integer ESTADO_TITULO_CADUCADO = 4;

	/** ESTADOS PROVEEDOR **/
	public static final int ESTADO_PROVEEDOR_NO_EXISTE = 0;
	public static final int ESTADO_PROVEEDOR_CREADO = 1;
	public static final int ESTADO_PROVEEDOR_ENVIADO_INFORMACION = 2;
	public static final int ESTADO_PROVEEDOR_RECIBIDO_INFORMACION = 3;
	public static final int ESTADO_PROVEEDOR_HOMOLOGACION_SOLICITADA = 4;
	public static final int ESTADO_PROVEEDOR_ANALIZANDO = 5;
	public static final int ESTADO_PROVEEDOR_FIN_ANALISIS = 6;
	public static final int ESTADO_PROVEEDOR_HOMOLOGACION_RECHAZADA = 7;
	public static final int ESTADO_PROVEEDOR_HOMOLOGACION_ACEPTADA = 8;
	public static final int ESTADO_PROVEEDOR_FINALIZADO = 9;

	/** ESTADOS HISTORICO **/
	public static final Integer ESTADO_HISTORICO_CREADO = 1;
	public static final Integer ESTADO_HISTORICO_CERRADO = 2;

	/** TIPOS EVIDENCIA **/
	public static final int TIPO_EVIDENCIA_ENVIO_CF = 1;
	public static final int TIPO_EVIDENCIA_APROBAR_CURSO = 2;
	public static final int TIPO_EVIDENCIA_SUPERAR_CURSO = 3;
	public static final int TIPO_EVIDENCIA_GENERAR_TITULO_HABILITANTE = 4;
	public static final int TIPO_EVIDENCIA_SUSPENDER_TITULO = 5;
	public static final int TIPO_EVIDENCIA_REVOCAR_TITULO = 6;
	public static final int TIPO_EVIDENCIA_ISO_PLANIFICADA = 7;
	public static final int TIPO_EVIDENCIA_FICHA_ISO = 9;
	public static final int TIPO_EVIDENCIA_ISEET_PLANIFICADA = 15;
	public static final int TIPO_EVIDENCIA_FICHA_ISEET = 17;
	public static final int TIPO_EVIDENCIA_CIERRE_MEDIDAS_RA = 20;
	public static final int TIPO_EVIDENCIA_ISCC_PLANIFICADA = 30;
	public static final int TIPO_EVIDENCIA_FICHA_ISCC = 31;
	public static final int TIPO_EVIDENCIA_FICHA_CONSENTIMIENTO_CAD = 37;
	public static final int TIPO_EVIDENCIA_RESULTADO_PRUEBA_CAD = 38;
	public static final int TIPO_EVIDENCIA_FICHA_POSITIVO_COMUNICACION_RO_CAD = 39;
	public static final int TIPO_EVIDENCIA_RESULTADO_PRUEBA_PSICOFISICA = 41;
	public static final int TIPO_EVIDENCIA_PASF = 42;
	public static final int TIPO_EVIDENCIA_SF_ENVIO_CIAF = 50;
	public static final int TIPO_EVIDENCIA_SF_DESIGNACION_DELEGADO_SEGURIDAD = 51;
	public static final int TIPO_EVIDENCIA_SF_REGISTRO_DOCUMENTOS_ANEXOS = 55;
	public static final int TIPO_EVIDENCIA_SF_REGISTRO_MEDIDAS_ADOPTADAS = 56;
	public static final int TIPO_EVIDENCIA_SF_FICHA_SUCESOS_FERROVIARIOS = 60;
	public static final int TIPO_EVIDENCIA_SF_INFORME_INVESTIGACION = 61;
	public static final int TIPO_EVIDENCIA_SF_INFORME_CIAF = 63;
	public static final int TIPO_EVIDENCIA_SF_FICHA_COMENTARIOS_INFORME_CIAF = 64;
	public static final int TIPO_EVIDENCIA_SF_FICHA_MEDIDAS_ADOPTADAS = 65;
	public static final int TIPO_EVIDENCIA_PYC_DOCUMENTACION_ENVIADA = 67;
	public static final int TIPO_EVIDENCIA_PYC_ACUSE_DE_RECIBO = 68;
	public static final int TIPO_EVIDENCIA_PYC_SOLICITUD_DOCUMENTACION = 69;
	public static final int TIPO_EVIDENCIA_PYC_DOCUMENTACION_RECIBIDA = 70;
	public static final int TIPO_EVIDENCIA_PYC_FICHA_EVALUACION = 72;
	public static final int TIPO_EVIDENCIA_PYC_COMUNICACION_RS = 73;
	public static final int TIPO_EVIDENCIA_PYC_INFORME_NO_CONFORMIDAD = 74;
	public static final int TIPO_EVIDENCIA_ANEXO = 75;
	public static final int TIPO_EVIDENCIA_REPORTE_FIN_SERVICIO = 76;
	public static final int TIPO_EVIDENCIA_ISMP_PLANIFICADA = 78;
	public static final int TIPO_EVIDENCIA_FICHA_ISMP = 80;
	public static final int TIPO_EVIDENCIA_LICENCIA_CONDUCCION = 81;
	public static final int TIPO_EVIDENCIA_NIVEL_IDIOMA = 82;

	/** TIPOS TITULO **/
	public static final int TIPO_TITULO_CERTIFICADO_COMPLEMENTARIO = 1;
	public static final int TIPO_TITULO_AUXILIAR_OPERACIONES_TREN_CON_MANIOBRAS = 2;
	public static final int TIPO_TITULO_RESPONSABLE_OPERACIONES_CARGA = 3;
	public static final int TIPO_TITULO_OPERADOR_VEHICULO_MANIOBRAS = 4;
	public static final int TIPO_TITULO_AUXILIAR_OPERACIONES_TREN = 5;

	/** TIPOS TAREA **/
	public static final int TIPO_TAREA_INSPECCION_ASIGNADA = 3;
	public static final int TIPO_TAREA_RECEPCION_DOCUMENTACION = 4;

	/** TIPOS COMPANIA **/
	public static final int TIPO_COMPANIA_EMPRESA_FERROVIARIA = 1;
	public static final int TIPO_COMPANIA_CENTRO_FORMACION = 2;
	public static final int TIPO_COMPANIA_CENTRO_MEDICO = 3;

	/** TIPOS INSPECCION **/
	public static final int TIPO_INSPECCION_ISO = 1;
	public static final int TIPO_INSPECCION_ISCC = 2;
	public static final int TIPO_INSPECCION_ISMP = 3;
	public static final int TIPO_INSPECCION_ISEER = 4;
	public static final int TIPO_INSPECCION_ISEET = 5;
	public static final int TIPO_INSPECCION_ISRC = 6;
	public static final int TIPO_INSPECCION_CAD = 7;

	/** TIPOS INSPECCION **/
	public static final int TIPO_CAD_ALEATORIO = 1;
	public static final int TIPO_CAD_POR_ACCIDENTE = 2;

	/** ESTADOS VERIFICACION INFORME **/
	public static final Integer ESTADO_VERIFICACION_INFORME_CORRECTO = 1;
	public static final Integer ESTADO_VERIFICACION_INFORME_INCORRECTO = 2;
	public static final Integer ESTADO_VERIFICACION_INFORME_NO_APLICA = 3;

	/** ESTADOS INSPECCION **/
	public static final Integer ESTADO_INSPECCION_PLANIFICADA = 1;
	public static final Integer ESTADO_INSPECCION_REALIZADA = 2;
	public static final Integer ESTADO_INSPECCION_ANOMALIAS_ABIERTAS = 3;
	public static final Integer ESTADO_INSPECCION_ANOMALIAS_CERRADAS = 4;
	public static final Integer ESTADO_INSPECCION_FINALIZADA = 5;
	public static final Integer ESTADO_INSPECCION_ACEPTADA = 6;
	public static final Integer ESTADO_INSPECCION_NO_ACEPTADA = 7;
	public static final Integer ESTADO_INSPECCION_EN_CURSO = 8;

	/** ESTADOS INVESTIGACION **/
	public static final int ESTADO_INVESTIGACION_CREADO = 1;
	public static final int ESTADO_INVESTIGACION_ESPERANDO_CIAF = 2;
	public static final int ESTADO_INVESTIGACION_ENVIADA_ESTRUCTURA = 3;
	public static final int ESTADO_INVESTIGACION_RECOGIENDO_DATOS = 4;
	public static final int ESTADO_INVESTIGACION_RECIBIENDO_CIAF = 5;
	public static final int ESTADO_INVESTIGACION_ENVIANDO_CIAF = 6;
	public static final int ESTADO_INVESTIGACION_APLICANDO_MEDIDAS = 7;
	public static final int ESTADO_INVESTIGACION_FINALIZADO = 8;

	/** ESTADOS PASF **/
	public static final Integer ESTADO_PASF_CREADO = 1;
	public static final Integer ESTADO_PASF_APROBADO = 2;

	/** ESTADOS ANOMALIA **/
	public static final int ESTADO_ANOMALIA_ABIERTA = 1;
	public static final int ESTADO_ANOMALIA_CERRADA = 2;

	/** RESULTADOS CAD **/
	public static final Integer RESULTADO_CAD_POSITIVO = 1;
	public static final Integer RESULTADO_CAD_NEGATIVO = 2;

	/** CARGOS PERSONAL **/
	public static final Integer CARGO_PERSONAL_RESPONSABLE_SEGURIDAD = 1;
	public static final Integer CARGO_PERSONAL_MAQUINISTA = 2;
	public static final Integer CARGO_PERSONAL_RESPONSABLE_OPERACIONES = 3;
	public static final Integer CARGO_PERSONAL_AUXILIAR_OPERACIONES = 4;
	public static final Integer CARGO_PERSONAL_RESPONSABLE_OPERACIONES_CARGA = 5;
	public static final Integer CARGO_PERSONAL_OPERADOR_VEHICULOS_MANIOBRAS = 6;
	public static final Integer CARGO_PERSONAL_AUXILIAR_CABINA = 7;
	public static final Integer CARGO_PERSONAL_ADMINISTRADOR = 8;

	/** OPCIONES DE PANTALLA **/
	public static final String OPCION_PANTALLA_MATERIALES = "materiales";
	public static final String OPCION_PANTALLA_MODELO_MATERIAL = "modelo material";
	public static final String OPCION_PANTALLA_PERMISOS = "permisos";
	public static final String OPCION_PANTALLA_TAREAS_PENDIENTES = "tareas pendientes";
	public static final String OPCION_PANTALLA_PERSONAL = "personal";
	public static final String OPCION_PANTALLA_COMPANIAS = "companias";
	public static final String OPCION_PANTALLA_CURSOS = "cursos";
	public static final String OPCION_PANTALLA_REVISIONES = "revisiones";
	public static final String OPCION_PANTALLA_TITULOS = "titulos";
	public static final String OPCION_PANTALLA_INSPECCIONES = "inspecciones";
	public static final String OPCION_PANTALLA_ACCIDENTES = "accidentes";
	public static final String OPCION_PANTALLA_HOMOLOGACION = "homologacion";
	public static final String OPCION_PANTALLA_PASF = "PASF";
	public static final String OPCION_PANTALLA_ADMINISTRACION = "administracion";
	public static final String OPCION_PANTALLA_FORMACION_Y_TITULO_HABILITANTE = "formacion y titulo habilitante";
	public static final String OPCION_PANTALLA_TRENES = "trenes";
	public static final String OPCION_PANTALLA_GESTION_PROCEDIMIENTOS = "gestion de procedimientos";
	public static final String OPCION_PANTALLA_GESTION_MATERIALES = "gestion de materiales";
	public static final String OPCION_PANTALLA_GESTION_DE_TURNOS = "gestion de turnos";
	public static final String OPCION_PANTALLA_TOMA_DE_SERVICIO = "toma de servicio";
	public static final String OPCION_PANTALLA_COMPOSICION = "composicion";
	public static final String OPCION_PANTALLA_LIBROS = "libros";
	public static final String OPCION_PANTALLA_TRAMOS = "tramos";

	/** DEFINICION TAREAS PENDIENTES **/
	public static final String TAREAPTE_GENERAR_ISO = "GenerarInformeISO";
	public static final String TAREAPTE_REALIZAR_CAD = "RealizarCAD";
	public static final String TAREAPTE_GENERAR_ISEET = "GenerarInformeISEET";
	public static final String TAREAPTE_GENERAR_ISMP = "GenerarInformeISMP";
	public static final String TAREAPTE_GENERAR_ISCC = "GenerarInformeISCC";
	public static final String TAREAPTE_FIRMA_DOCUMENTACION = "FirmaDocumentacion";
	public static final String TAREAPTE_PERSONAL = "Personal";

	/** TIPO RESPONSABLE **/
	public static final Integer TIPO_RESPONSABLE_SEGURIDAD = 1;
	public static final Integer TIPO_RESPONSABLE_OPERACIONES = 2;

	/** IMAGENES **/
	public static final Integer IMAGENES_LOGO_APLICACION = 1;
	public static final Integer IMAGENES_LOGO_COMPANIA = 2;
	public static final Integer IMAGENES_TEMPLATE_CERT_COMP_HABI_C = 3;
	public static final Integer IMAGENES_TEMPLATE_MOD_DOC_SUS_TIT_HAB = 4;
	public static final Integer IMAGENES_TEMPLATE_MOD_DOC_REV_TIT_HAB = 5;
	public static final Integer IMAGENES_TEMPLATE_MOD_DOC_HAB_PER_OPE_TREN_ROC = 6;
	public static final Integer IMAGENES_TEMPLATE_MOD_DOC_HAB_PER_OPE_TREN_AOT_AOTM_OVM = 7;
	public static final Integer IMAGENES_TEMPLATE_FIC_INS_SEG_OPE = 8;
	public static final Integer IMAGENES_TEMPLATE_INF_INS_SEG_RES_ANO = 9;
	public static final Integer IMAGENES_TEMPLATE_INF_INS_SEG_RES_ANO_2 = 10;
	public static final Integer IMAGENES_TEMPLATE_FIC_NOT_DEL_INT_ACC_INC_FER = 11;
	public static final Integer IMAGENES_TEMPLATE_FIC_ACC_INC_FER = 12;
	public static final Integer IMAGENES_TEMPLATE_FIC_EST_INV_REC_DAT_COO_CAS_ACC_INC_FERR = 13;
	public static final Integer IMAGENES_TEMPLATE_FIC_COM_SUG_INF_PRO_ACC_INC_FERR_CIAF = 14;
	public static final Integer IMAGENES_TEMPLATE_FIC_MED_ADOP = 15;
	public static final Integer IMAGENES_TEMPLATE_INF_INV_ACC_INC_FERR = 16;
	public static final Integer IMAGENES_TEMPLATE_AUT_DET_CAD = 17;
	public static final Integer IMAGENES_TEMPLATE_FICH_CONT_DET_CAD = 18;
	public static final Integer IMAGENES_TEMPLATE_REP_FIN_SER = 19;
	public static final Integer IMAGENES_TEMPLATE_TOM_SER = 20;
	public static final Integer IMAGENES_FOTO_PERSONAL_DEFAULT = 21;
	public static final Integer IMAGENES_TEMPLATE_FIC_INS_SEG_EQU_ELE_SEG_MAT_ROD_TRA = 22;
	public static final Integer IMAGENES_TEMPLATE_ACU_REC_DOC_SER = 23;
	public static final Integer IMAGENES_TEMPLATE_FIC_ISMP_MER_PEL = 24;
	public static final Integer IMAGENES_TEMPLATE_FIC_INS_SEG_EST_CAR_COM_FER = 25;
	public static final Integer IMAGENES_TEMPLATE_PLA_ANU_SEG_FER = 26;
	public static final Integer IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_AS = 27;
	public static final Integer IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_IS = 28;
	public static final Integer IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_AF = 29;
	public static final Integer IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_ACRO = 30;


	/** DIAS DE LA SEMANA **/
	public static final String DIA_LUNES = "Lunes";
	public static final String DIA_MARTES = "Martes";
	public static final String DIA_MIERCOLES = "Miércoles";
	public static final String DIA_JUEVES = "Jueves";
	public static final String DIA_VIERNES = "Viernes";
	public static final String DIA_SABADO = "Sábado";
	public static final String DIA_DOMINGO = "Domingo";

	/** CAUSA ACCIDENTE **/
	public static final Integer CAUSA_ACCIDENTE_DESCARRILAMIENTO = 1;
	public static final Integer CAUSA_ACCIDENTE_COLISION = 2;
	public static final Integer CAUSA_ACCIDENTE_ACCIDENTE_PASO_NIVEL = 3;
	public static final Integer CAUSA_ACCIDENTE_INCENDIO = 4;
	public static final Integer CAUSA_ACCIDENTE_ARROLLAMIENTO_VIA = 5;
	public static final Integer CAUSA_ACCIDENTE_ARROLLAMIENTO_INTERSECCION = 6;
	public static final Integer CAUSA_ACCIDENTE_CAIDA_PERSONAS = 7;
	public static final Integer CAUSA_ACCIDENTE_SUICIDIO = 8;
	public static final Integer CAUSA_ACCIDENTE_DESCOMPOSICION_CARGAMENTO = 9;
	public static final Integer CAUSA_ACCIDENTE_DETENCION_TREN = 10;
	public static final Integer CAUSA_ACCIDENTE_INVASION_VIA = 11;
	public static final Integer CAUSA_ACCIDENTE_INCIDENTES_TRANSPORTES_EXCEPCIONALES = 12;
	public static final Integer CAUSA_ACCIDENTE_REBASE_SENAL = 13;
	public static final Integer CAUSA_ACCIDENTE_CONATO_COLISION = 14;
	public static final Integer CAUSA_ACCIDENTE_ENGANCHE_PANTOGRAFO = 15;
	public static final Integer CAUSA_ACCIDENTE_OTROS = 16;

	/** TIPOS DE CURSO **/
	public static final Integer TIPO_CURSO_MATERIAL = 1;
	public static final Integer TIPO_CURSO_INFRAESTRUCTURA = 2;
	public static final Integer TIPO_CURSO_OTROS = 3;

	/** TIPO PERMISO **/

	public static final Integer TIPO_PERMISO_LECTURA = 1;
	public static final Integer TIPO_PERMISO_ESCRITURA = 2;

	/** TIPOS DE ACCIDENTE **/

	public static final Integer TIPO_ACCIDENTE_INCIDENTE = 1;
	public static final Integer TIPO_ACCIDENTE_ACCIDENTE = 2;
	public static final Integer TIPO_ACCIDENTE_ACCIDENTE_LEVE = 3;
	public static final Integer TIPO_ACCIDENTE_ACCIDENTE_GRAVE = 4;

	/** RESULTADO INSPECCION **/

	public static final Integer RESULTADO_INSPECCION_CRITICO = 1;
	public static final Integer RESULTADO_INSPECCION_IMPORTANTE = 2;
	public static final Integer RESULTADO_INSPECCION_LEVE = 3;
	public static final Integer RESULTADO_INSPECCION_SATISFACTORIO = 4;
	public static final Integer RESULTADO_INSPECCION_NO_APLICA = 5;

	/** NIVEL IDIOMA **/
	public static final Integer NIVEL_IDIOMA_NATIVO = 1;

	/** TITULO DOCUMENTO TREN **/
	public static final String TITULO_DOCUMENTO_TREN = "Hoja de ruta";

	/** ENVIO CORREOS **/
	public static final String EMAIL_ENVIO_CORREOS = "noreply@eurogestion.eu";

}
