package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.HistoricoMaquinista;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ReporteFinServicioJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoHistoricoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.HistoricoMaquinistaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ReporteFinServicioServiceImpl implements ReporteFinServicioService {

	/** Repositories & Services **/

	@Autowired
	public HistoricoMaquinistaDAO historicoMaquinistaDAO;

	@Autowired
	public EstadoHistoricoDAO estadoHistoricoDAO;

	@Autowired
	public ComposicionDAO composicionDAO;

	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	@Autowired
	public TrenDAO trenDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void guardarReporteFinServicio(ReporteFinServicioJSP reporteFinServicioJSP, HttpSession sesion)
			throws EseException {

		HistoricoMaquinista historicoMaquinista = convertReporteFinServicioJSPToHistoricoMaquinista(
				reporteFinServicioJSP, sesion);
		historicoMaquinistaDAO.save(historicoMaquinista);

		reporteFinServicioJSP.setIdReporteFinServicio(historicoMaquinista.getIdHistoricoMaquinista().toString());
		reporteFinServicioJSP
				.setIdEstadoReporteFinServicio(historicoMaquinista.getEstadoHistorico().getIdEstadoHistorico());
	}

	@Override
	public void generarFichaReporteFinServicio(ReporteFinServicioJSP reporteFinServicioJSP, HttpSession sesion)
			throws EseException {

		HistoricoMaquinista historicoMaquinista = convertReporteFinServicioJSPToHistoricoMaquinista(
				reporteFinServicioJSP, sesion);
		historicoMaquinista.setEstadoHistorico(estadoHistoricoDAO.getOne(Constantes.ESTADO_HISTORICO_CERRADO));

		Evidencia evidencia = crearEvidencia(historicoMaquinista);
		historicoMaquinista.setEvidencia76(evidencia);

		historicoMaquinistaDAO.save(historicoMaquinista);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		reporteFinServicioJSP
				.setIdEstadoReporteFinServicio(historicoMaquinista.getEstadoHistorico().getIdEstadoHistorico());

	}

	private Evidencia crearEvidencia(HistoricoMaquinista historicoMaquinista) throws EseException {

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoRepFinSer(historicoMaquinista);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_REPORTE_FIN_SERVICIO);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), Constantes.TYPE_PDF);

		return evidencia;
	}

	private HistoricoMaquinista convertReporteFinServicioJSPToHistoricoMaquinista(
			ReporteFinServicioJSP reporteFinServicioJSP, HttpSession sesion) throws EseException {

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) sesion.getAttribute("usuario");
		Personal personal = personalDAO.getOne(Integer.parseInt(usuarioRegistrado.getIdPersonal()));
		HistoricoMaquinista historicoMaquinista;
		if (StringUtils.isBlank(reporteFinServicioJSP.getIdReporteFinServicio())) {
			historicoMaquinista = new HistoricoMaquinista();
			historicoMaquinista.setEstadoHistorico(estadoHistoricoDAO.getOne(Constantes.ESTADO_HISTORICO_CREADO));
		} else {
			historicoMaquinista = historicoMaquinistaDAO
					.getOne(Integer.parseInt(reporteFinServicioJSP.getIdReporteFinServicio()));
		}

		historicoMaquinista.setFecha(Utiles.parseDatePantalla(reporteFinServicioJSP.getFecha()));

		historicoMaquinista.setPersonal(personal);
		historicoMaquinista.setComposicion(composicionDAO.findComposicionByFilters(reporteFinServicioJSP.getIdTren(),
				reporteFinServicioJSP.getFecha()));
		historicoMaquinista.setPtoOrigen(
				puntoInfraestructuraDAO.getOne(Integer.parseInt(reporteFinServicioJSP.getIdPuntoOrigen())));
		historicoMaquinista
				.setPtoFin(puntoInfraestructuraDAO.getOne(Integer.parseInt(reporteFinServicioJSP.getIdPuntoFin())));
		historicoMaquinista.setNve(reporteFinServicioJSP.getNveLocomotora());
		historicoMaquinista.setTren(trenDAO.getOne(Integer.parseInt(reporteFinServicioJSP.getIdTren())));
		historicoMaquinista.setPractico(reporteFinServicioJSP.getPractico());
		historicoMaquinista.setHoraToma(Utiles.convertStringToDate(reporteFinServicioJSP.getFechaToma(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		historicoMaquinista.setHoraSalida(Utiles.convertStringToDate(reporteFinServicioJSP.getFechaSalida(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		historicoMaquinista.setHoraLlegada(Utiles.convertStringToDate(reporteFinServicioJSP.getFechaLlegada(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		historicoMaquinista.setHoraDeje(Utiles.convertStringToDate(reporteFinServicioJSP.getFechaDeje(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));

		if (StringUtils.isNotBlank(reporteFinServicioJSP.getIdMaquinistaPredecesor())) {
			historicoMaquinista.setPredecesor(
					personalDAO.getOne(Integer.parseInt(reporteFinServicioJSP.getIdMaquinistaPredecesor())));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getIdMaquinistaSucesor())) {
			historicoMaquinista
					.setSucesor(personalDAO.getOne(Integer.parseInt(reporteFinServicioJSP.getIdMaquinistaSucesor())));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getEstacionRefrigerio())) {
			historicoMaquinista.setEstacionRefrigerio(reporteFinServicioJSP.getEstacionRefrigerio());

		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getInicioRefrigerio())) {
			historicoMaquinista.setHoraIniRefrigerio(Utiles.convertStringToDate(
					reporteFinServicioJSP.getInicioRefrigerio(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getFinRefrigerio())) {
			historicoMaquinista.setHoraFinRefrigerio(Utiles.convertStringToDate(
					reporteFinServicioJSP.getFinRefrigerio(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getViajeAntesServicioInicio())) {
			historicoMaquinista.setInicioViajeAntes(Utiles.convertStringToDate(
					reporteFinServicioJSP.getViajeAntesServicioInicio(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getViajeAntesServicioFin())) {
			historicoMaquinista.setFinViajeAntes(Utiles.convertStringToDate(
					reporteFinServicioJSP.getViajeAntesServicioFin(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getViajeDespuesServicioInicio())) {
			historicoMaquinista.setInicioViajeDespues(Utiles.convertStringToDate(
					reporteFinServicioJSP.getViajeDespuesServicioInicio(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getViajeDespuesServicioFin())) {
			historicoMaquinista.setFinViajeDespues(Utiles.convertStringToDate(
					reporteFinServicioJSP.getViajeDespuesServicioFin(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getEm2000Inicio())) {
			historicoMaquinista.setInicioEm2000(reporteFinServicioJSP.getEm2000Inicio());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getEm2000Fin())) {
			historicoMaquinista.setFinEm2000(reporteFinServicioJSP.getEm2000Fin());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getTelocInicio())) {
			historicoMaquinista.setInicioTeloc(reporteFinServicioJSP.getTelocInicio());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getTelocFin())) {
			historicoMaquinista.setFinTeloc(reporteFinServicioJSP.getTelocFin());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getNivelCombustibleInicio())) {
			historicoMaquinista.setNivelCombustibleInicio(reporteFinServicioJSP.getNivelCombustibleInicio());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getNivelCombustibleFin())) {
			historicoMaquinista.setNivelCombustibleFin(reporteFinServicioJSP.getNivelCombustibleFin());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getEstacionamiento())) {
			historicoMaquinista.setEstacionamientoLoco(reporteFinServicioJSP.getEstacionamiento());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getKwHFin())) {
			historicoMaquinista.setKwHFin(reporteFinServicioJSP.getKwHFin());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getSenhalesColaReflectantes())) {
			historicoMaquinista.setSenalesColaReflectante(reporteFinServicioJSP.getSenhalesColaReflectantes());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getSenhalesColaLuminosa())) {
			historicoMaquinista.setSenalesColaLuminosas(reporteFinServicioJSP.getSenhalesColaLuminosa());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getLinternas())) {
			historicoMaquinista.setLinternas(reporteFinServicioJSP.getLinternas());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getBanderinesRojos())) {
			historicoMaquinista.setBanderiniesRojos(reporteFinServicioJSP.getBanderinesRojos());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getCalcesAntideriva())) {
			historicoMaquinista.setCalcesAntideriva(reporteFinServicioJSP.getCalcesAntideriva());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getBarraCortocircuito())) {
			historicoMaquinista.setBarrasCortocircuito(reporteFinServicioJSP.getBarraCortocircuito());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getLlaveTrinquete())) {
			historicoMaquinista.setLlaveTrinquete(reporteFinServicioJSP.getLlaveTrinquete());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getDotacionOtros())) {
			historicoMaquinista.setOtros(reporteFinServicioJSP.getDotacionOtros());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getEstacionRepostaje())) {
			historicoMaquinista.setEstacionRepostaje(reporteFinServicioJSP.getEstacionRepostaje());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getLitrosRepostaje())) {
			historicoMaquinista.setLitrosRepostados(reporteFinServicioJSP.getLitrosRepostaje());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getAveriasLocomotora())) {
			historicoMaquinista.setAveriasLocomotora(reporteFinServicioJSP.getAveriasLocomotora());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getAveriasMaterialRemolcado())) {
			historicoMaquinista.setAveriasRemolcado(reporteFinServicioJSP.getAveriasMaterialRemolcado());
		}
		if (StringUtils.isNotBlank(reporteFinServicioJSP.getOtraInformacionRelevante())) {
			historicoMaquinista.setOtraInformacion(reporteFinServicioJSP.getOtraInformacionRelevante());
		}

		return historicoMaquinista;
	}
}