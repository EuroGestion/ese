package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.domain.Cad;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.HistoricoMaquinista;
import eu.eurogestion.ese.domain.InformeAnomalias;
import eu.eurogestion.ese.domain.Iscc;
import eu.eurogestion.ese.domain.Iseet;
import eu.eurogestion.ese.domain.Ismp;
import eu.eurogestion.ese.domain.Iso;
import eu.eurogestion.ese.domain.LibroPersonal;
import eu.eurogestion.ese.domain.Pasf;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.domain.TomaServicio;
import eu.eurogestion.ese.exception.EseException;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface UtilesPDFService {

	/**
	 * CERTIFICADO COMPLEMENTARIO DE MAQUINISTA DE TREN
	 * 
	 * @param response
	 * @param idTitulo
	 * @return
	 * @throws IOException
	 */
	byte[] generarCertificadoComplementarioHabilitacion(Titulo titulo) throws IOException, SQLException;

	/**
	 * DOCUMENTO DE SUSPENSIÓN DE TÍTULO HABILITANTE DE PERSONAL FERROVIARIO
	 * 
	 * @param titulo
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoSuspensionTituloHabilitantePersonalFerroviario(Titulo titulo, String nombreUsuario,
			String companiaUsuario) throws IOException, SQLException;

	/**
	 * DOCUMENTO DE REVOCACIÓN DE TÍTULO HABILITANTE DE PERSONAL FERROVIARIO
	 * 
	 * @param titulo
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoRevocacionTituloHabilitantePersonalFerroviario(Titulo titulo, String nombreUsuario,
			String companiaUsuario) throws IOException, SQLException;

	/**
	 * HABILITACIÓN DE PERSONAL DE OPERACIONES DEL TREN RESPONSABLE DE LAS
	 * OPERACIONES DE CARGA
	 * 
	 * @param titulo
	 * @param listaCursos
	 * @param usuarioNombre
	 * @param usuarioCargo
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoHabPerOpeTrenROC(Titulo titulo, String usuarioNombre, String usuarioCargo)
			throws IOException, SQLException;

	/**
	 * HABILITACIÓN DE PERSONAL DE OPERACIONES DEL TREN
	 * 
	 * @param titulo
	 * @param listaCursos
	 * @param usuarioNombre
	 * @param usuarioCargo
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoHabPerOpeTrenAOT_AOTM_OVM(Titulo titulo, String usuarioNombre, String usuarioCargo,
			Integer tipoTitulo) throws IOException, SQLException;

	/**
	 * FICHA DE ISO DEL MATERIAL RODANTE
	 * 
	 * @param iso
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFicInsSegOpe(Iso iso) throws IOException, SQLException;

	/**
	 * INFORME DE INSPECCIÓN DE SEGURIDAD Y DE RESOLUCIÓN DE ANOMALÍAS
	 * 
	 * @param informeAnomalias
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoInfInsSegResAno(InformeAnomalias informeAnomalias) throws IOException, SQLException;

	/**
	 * FICHA DE NOTIFICACIÓN DE LA DELEGACIÓN DE INTERVENCIÓN EN ACCIDENTES E
	 * INCIDENTES FERROVIARIOS
	 * 
	 * @param accidente
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFicNotDelIntAccIncFer(Accidente accidente) throws IOException, SQLException;

	/**
	 * FICHA DE ACCIDENTE O INCIDENTE FERROVIARIO
	 * 
	 * @param accidente
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFicAccIncFer(Accidente accidente) throws IOException, SQLException;

	/**
	 * FICHA DE LA ESTRUCTURA DE INVESTIGACIÓN, RECOGIDA DE DATOS Y COORDINACIÓN EN
	 * CASO DE ACCIDENTES E INCIDENTES FERROVIARIOS
	 * 
	 * @param accidente
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFicEstInvRecDatCooCasAccIncFerr(Accidente accidente) throws IOException, SQLException;

	/**
	 * FICHA DE COMENTARIOS Y SUGERENCIAS AL INFORME PROVISIONAL DE ACCIDENTES E
	 * INCIDENTES FERROVIARIOS DE LA CIAF
	 * 
	 * @param accidente
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFicComSugInfProAccIncFerrCIAF(Accidente accidente) throws IOException, SQLException;

	/**
	 * FICHA DE MEDIDAS ADOPTADAS
	 * 
	 * @param accidente
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFicMedAdop(Accidente accidente) throws IOException, SQLException;

	/**
	 * INFORME FINAL DE LA INVESTIGACIÓN DE ACCIDENTES E INCIDENTES FERROVIARIOS
	 * 
	 * @param accidente
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoInfInvAccIncFerr(Accidente accidente) throws IOException, SQLException;

	/**
	 * Autorización de detección de CAD
	 * 
	 * @param cad
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	byte[] generarDocumentoAutDetCAD(Cad cad) throws IOException, SQLException, SQLException;

	/**
	 * Ficha de Control de detección de CAD
	 * 
	 * @param cad
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFichContDetCAD(Cad cad) throws IOException, SQLException;

	/**
	 * REPORTE DE FIN DE SERVICIO
	 * 
	 * @param historicoMaquinista
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoRepFinSer(HistoricoMaquinista historicoMaquinista) throws IOException, SQLException;

	/**
	 * LISTA DE CHEQUEO DEL PERSONAL DE CONDUCCIÓN A LA TOMA O INICIO DEL SERVICIO
	 * 
	 * @param tomaServicio
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoTomSer(TomaServicio tomaServicio) throws IOException, SQLException;

	/**
	 * FICHA DE INSPECCIÓN DE SEGURIDAD DE EQUIPOS Y ELEMENTOS DE SEGURIDAD DEL
	 * MATERIAL RODANTE DE TRACCIÓN
	 * 
	 * @param iseet
	 * @return
	 * @throws IOException
	 */
	byte[] generarDocumentoFicInsSegEquEleSegMatRodTra(Iseet iseet) throws IOException, SQLException;

	/**
	 * ACUSE DE RECIBO DE DOCUMENTACIÓN DE SERVICIO
	 * 
	 * @param libroPersonal
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	byte[] generarDocumentoAcuRecDocSer(LibroPersonal libroPersonal) throws IOException, SQLException;

	/**
	 * FICHA DE ISMP DE MERCANCÍAS PELIGROSAS
	 * 
	 * @param ismp
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	byte[] generarDocumentoFicISMPMerPel(Ismp ismp) throws IOException, SQLException;

	/**
	 * Para pruebas en las que no haya pdf
	 * 
	 * @param texto
	 * @return
	 * @throws IOException
	 */
	byte[] crearPDF(String texto) throws IOException;

	/**
	 * FICHA DE INSPECCIÓN DE SEGURIDAD DEL ESTADO DEL CARGAMENTO DE LAS
	 * COMPOSICIONES FERROVIARIAS
	 * 
	 * @param iscc
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	byte[] generarDocumentoFicInsSegEstCarComFer(Iscc iscc) throws IOException, SQLException;

	/**
	 * PLAN ANUAL DE SEGURIDAD FERROVIARIA
	 * 
	 * @param pasf
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	byte[] generarDocumentoPlaAnuSegFer(Pasf pasf) throws IOException, SQLException;

	public Evidencia crearEvidencia(String titulo, byte[] fichero, String md5, TipoEvidencia tipoEvidencia,
			Date fechaDocumento, String tipoFichero) throws EseException;

	Documento crearDocumento(String titulo, byte[] fichero, String md5, Date fechaDocumento, String tipoFichero,
			String observaciones) throws EseException;

	void descargarEvidencia(Documento fichero, HttpServletResponse response) throws IOException;

	void descargarDocumento(Documento fichero, HttpServletResponse response) throws IOException;
}
