package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.HistoricoMaquinista;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.PuntoInfraestructura;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorReportesFinServicioJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.ReporteFinServicioJSP;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.HistoricoMaquinistaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.ReporteFinServicioService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class ReporteFinServicioController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public HistoricoMaquinistaDAO historicoMaquinistaDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public ReporteFinServicioService reporteFinServicioService;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public ComposicionDAO composicionDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de maquinistas para una tabla
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("maquinistas")
	public List<Personal> listMaquinistasAll() {
		List<Personal> lista = new ArrayList<>();
		Personal personal = new Personal();
		personal.setNombre("Selecciona uno:");
		lista.add(personal);
		lista.addAll(personalDAO.findAllMaquinistas());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de trenes para una tabla
	 * 
	 * @return lista de objetos Tren
	 */
	@ModelAttribute("trenes")
	public List<Tren> listTrenesAll() {
		List<Tren> lista = new ArrayList<>();
		Tren personal = new Tren();
		personal.setNumero("Selecciona uno:");
		lista.add(personal);
		lista.addAll(trenDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de trenes para una tabla
	 * 
	 * @return lista de objetos Tren
	 */
	@ModelAttribute("puntosInfraestructura")
	public List<PuntoInfraestructura> listPuntosAll() {
		List<PuntoInfraestructura> lista = new ArrayList<>();
		PuntoInfraestructura personal = new PuntoInfraestructura();
		personal.setNombreLargo("Selecciona uno:");
		lista.add(personal);
		lista.addAll(puntoInfraestructuraDAO.findAll(Sort.by(Sort.Direction.ASC, "nombreLargo")));
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verReporte", method = RequestMethod.POST)
	public String verReporte(BuscadorReportesFinServicioJSP buscadorReportesFinServicioJSP, Model model) {

		model.addAttribute("reporteFinServicio",
				convertHistoricoMaquinistaToReporteFinServicioJSP(
						historicoMaquinistaDAO
								.getOne(Integer.parseInt(buscadorReportesFinServicioJSP.getIdHistoricoMaquinista())),
						Boolean.TRUE, "buscadorReportes"));

		return "reporteFinServicio";
	}

	@RequestMapping(value = "/verReportePersonal", method = RequestMethod.POST)
	public String verReportePersonal(DetallePersonalJSP detallePersonalJSP, Model model) {

		ReporteFinServicioJSP reporteFinServicioJSP = convertHistoricoMaquinistaToReporteFinServicioJSP(
				historicoMaquinistaDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdReporte())), Boolean.TRUE,
				"detallePersonal");

		reporteFinServicioJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("reporteFinServicio", reporteFinServicioJSP);

		return "reporteFinServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarReporte", method = RequestMethod.POST)
	public String modificarReporte(BuscadorReportesFinServicioJSP buscadorReportesFinServicioJSP, Model model) {

		model.addAttribute("reporteFinServicio",
				convertHistoricoMaquinistaToReporteFinServicioJSP(
						historicoMaquinistaDAO
								.getOne(Integer.parseInt(buscadorReportesFinServicioJSP.getIdHistoricoMaquinista())),
						Boolean.FALSE, "buscadorReportes"));

		return "reporteFinServicio";
	}

	@RequestMapping(value = "/modificarReportePersonal", method = RequestMethod.POST)
	public String modificarReportePersonal(DetallePersonalJSP detallePersonalJSP, Model model) {

		ReporteFinServicioJSP reporteFinServicioJSP = convertHistoricoMaquinistaToReporteFinServicioJSP(
				historicoMaquinistaDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdReporte())), Boolean.FALSE,
				"detallePersonal");

		reporteFinServicioJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("reporteFinServicio", reporteFinServicioJSP);

		return "reporteFinServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/nuevoReporte", method = RequestMethod.GET)
	public String nuevoReporte(Model model) {

		ReporteFinServicioJSP reporteFinServicioJSP = new ReporteFinServicioJSP();
		reporteFinServicioJSP.setLectura(Boolean.FALSE);
		model.addAttribute("reporteFinServicio", reporteFinServicioJSP);

		return "reporteFinServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarReporteFinServicio", method = RequestMethod.POST)
	public String guardarReporteFinServicio(ReporteFinServicioJSP reporteFinServicioJSP, Model model,
			HttpSession sesion) {

		try {
			composicionDAO.findComposicionByFilters(reporteFinServicioJSP.getIdTren(),
					reporteFinServicioJSP.getFecha());
		} catch (EseException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("reporteFinServicio", reporteFinServicioJSP);
			return "reporteFinServicio";
		}

		try {
			reporteFinServicioService.guardarReporteFinServicio(reporteFinServicioJSP, sesion);
		} catch (EseException e) {
			// TODO falta añadir error
			log.error(e.getMessage());
		}

		model.addAttribute("reporteFinServicio", reporteFinServicioJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "reporteFinServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/generarFichaReporteFinServicio", method = RequestMethod.POST)
	public String generarFichaReporteFinServicio(ReporteFinServicioJSP reporteFinServicioJSP, Model model,
			HttpSession sesion) {

		try {
			composicionDAO.findComposicionByFilters(reporteFinServicioJSP.getIdTren(),
					reporteFinServicioJSP.getFecha());
		} catch (EseException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("reporteFinServicio", reporteFinServicioJSP);
			return "reporteFinServicio";
		}

		try {
			reporteFinServicioService.generarFichaReporteFinServicio(reporteFinServicioJSP, sesion);
		} catch (EseException e) {
			// TODO falta añadir error
			log.error(e.getMessage());
		}

		model.addAttribute("reporteFinServicio", reporteFinServicioJSP);
		model.addAttribute("info", "Se ha generado la evidencia correctamente");
		return "reporteFinServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verFichaReporteFinServicio", method = RequestMethod.POST)
	public void descargarEvidenciaEnvioSolicitud(ReporteFinServicioJSP reporteFinServicioJSP, Model model,
			HttpServletResponse response) {
		try {
			HistoricoMaquinista historicoMaquinista = historicoMaquinistaDAO
					.getOne(Integer.parseInt(reporteFinServicioJSP.getIdReporteFinServicio()));
			Documento documento = historicoMaquinista.getEvidencia76().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	private ReporteFinServicioJSP convertHistoricoMaquinistaToReporteFinServicioJSP(
			HistoricoMaquinista historicoMaquinista, Boolean lectura, String paginaVuelta) {

		ReporteFinServicioJSP reporteFinServicioJSP = new ReporteFinServicioJSP();
		reporteFinServicioJSP.setIdReporteFinServicio(historicoMaquinista.getIdHistoricoMaquinista().toString());
		reporteFinServicioJSP.setLectura(lectura);
		reporteFinServicioJSP
				.setIdEstadoReporteFinServicio(historicoMaquinista.getEstadoHistorico().getIdEstadoHistorico());
		reporteFinServicioJSP.setFecha(
				Utiles.convertDateToString(historicoMaquinista.getFecha(), Constantes.FORMATO_FECHA_PANTALLA));
		reporteFinServicioJSP.setNveLocomotora(historicoMaquinista.getNve());
		reporteFinServicioJSP.setIdTren(historicoMaquinista.getTren().getIdTren().toString());
		reporteFinServicioJSP.setPractico(historicoMaquinista.getPractico());
		if (historicoMaquinista.getPredecesor() != null) {
			reporteFinServicioJSP
					.setIdMaquinistaPredecesor(historicoMaquinista.getPredecesor().getIdPersonal().toString());
		}
		if (historicoMaquinista.getSucesor() != null) {
			reporteFinServicioJSP.setIdMaquinistaSucesor(historicoMaquinista.getSucesor().getIdPersonal().toString());
		}
		reporteFinServicioJSP
				.setIdPuntoOrigen(historicoMaquinista.getPtoOrigen().getIdPuntoInfraestructura().toString());
		reporteFinServicioJSP.setIdPuntoFin(historicoMaquinista.getPtoFin().getIdPuntoInfraestructura().toString());
		reporteFinServicioJSP.setFechaToma(Utiles.convertDateToString(historicoMaquinista.getHoraToma(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		reporteFinServicioJSP.setFechaSalida(Utiles.convertDateToString(historicoMaquinista.getHoraSalida(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		reporteFinServicioJSP.setFechaLlegada(Utiles.convertDateToString(historicoMaquinista.getHoraLlegada(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		reporteFinServicioJSP.setFechaDeje(Utiles.convertDateToString(historicoMaquinista.getHoraDeje(),
				Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		reporteFinServicioJSP.setEstacionRefrigerio(historicoMaquinista.getEstacionRefrigerio());
		if (historicoMaquinista.getHoraIniRefrigerio() != null) {
			reporteFinServicioJSP.setInicioRefrigerio(Utiles.convertDateToString(
					historicoMaquinista.getHoraIniRefrigerio(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (historicoMaquinista.getHoraFinRefrigerio() != null) {
			reporteFinServicioJSP.setFinRefrigerio(Utiles.convertDateToString(
					historicoMaquinista.getHoraFinRefrigerio(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (historicoMaquinista.getInicioViajeAntes() != null) {
			reporteFinServicioJSP.setViajeAntesServicioInicio(Utiles.convertDateToString(
					historicoMaquinista.getInicioViajeAntes(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (historicoMaquinista.getFinViajeAntes() != null) {
			reporteFinServicioJSP.setViajeAntesServicioFin(Utiles.convertDateToString(
					historicoMaquinista.getFinViajeAntes(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (historicoMaquinista.getInicioViajeDespues() != null) {
			reporteFinServicioJSP.setViajeDespuesServicioInicio(Utiles.convertDateToString(
					historicoMaquinista.getInicioViajeDespues(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		if (historicoMaquinista.getFinViajeDespues() != null) {
			reporteFinServicioJSP.setViajeDespuesServicioFin(Utiles.convertDateToString(
					historicoMaquinista.getFinViajeDespues(), Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		reporteFinServicioJSP.setEm2000Inicio(historicoMaquinista.getInicioEm2000());
		reporteFinServicioJSP.setEm2000Fin(historicoMaquinista.getFinEm2000());
		reporteFinServicioJSP.setTelocInicio(historicoMaquinista.getInicioTeloc());
		reporteFinServicioJSP.setTelocFin(historicoMaquinista.getFinTeloc());
		reporteFinServicioJSP.setNivelCombustibleInicio(historicoMaquinista.getNivelCombustibleInicio());
		reporteFinServicioJSP.setNivelCombustibleFin(historicoMaquinista.getNivelCombustibleFin());
		reporteFinServicioJSP.setEstacionamiento(historicoMaquinista.getEstacionamientoLoco());
		reporteFinServicioJSP.setKwHFin(historicoMaquinista.getKwHFin());
		reporteFinServicioJSP.setSenhalesColaReflectantes(historicoMaquinista.getSenalesColaReflectante());
		reporteFinServicioJSP.setSenhalesColaLuminosa(historicoMaquinista.getSenalesColaLuminosas());
		reporteFinServicioJSP.setLinternas(historicoMaquinista.getLinternas());
		reporteFinServicioJSP.setBanderinesRojos(historicoMaquinista.getBanderiniesRojos());
		reporteFinServicioJSP.setCalcesAntideriva(historicoMaquinista.getCalcesAntideriva());
		reporteFinServicioJSP.setBarraCortocircuito(historicoMaquinista.getBarrasCortocircuito());
		reporteFinServicioJSP.setLlaveTrinquete(historicoMaquinista.getLlaveTrinquete());
		reporteFinServicioJSP.setDotacionOtros(historicoMaquinista.getOtros());
		reporteFinServicioJSP.setEstacionRepostaje(historicoMaquinista.getEstacionRepostaje());
		reporteFinServicioJSP.setLitrosRepostaje(historicoMaquinista.getLitrosRepostados());
		reporteFinServicioJSP.setAveriasLocomotora(historicoMaquinista.getAveriasLocomotora());
		reporteFinServicioJSP.setAveriasMaterialRemolcado(historicoMaquinista.getAveriasRemolcado());
		reporteFinServicioJSP.setOtraInformacionRelevante(historicoMaquinista.getOtraInformacion());

		reporteFinServicioJSP.setPaginaVuelta(paginaVuelta);

		return reporteFinServicioJSP;
	}

	@RequestMapping(value = "/volverReporteFinServicio", method = RequestMethod.POST)
	public String volverReporteFinServicio(ReporteFinServicioJSP reporteFinServicioJSP, Model model,
			HttpSession sesion) {

		if ("detallePersonal".equalsIgnoreCase(reporteFinServicioJSP.getPaginaVuelta())) {
			return "forward:/volverReporteFinServicioPersonal";
		}

		return "redirect:/buscadorReportesFinServicio";
	}
}