package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CambiarPasswordJSP;
import eu.eurogestion.ese.pojo.CrearInformeAnomaliasJSP;
import eu.eurogestion.ese.pojo.DetalleFormacionJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.DetalleRevisionJSP;
import eu.eurogestion.ese.pojo.DetalleTituloJSP;
import eu.eurogestion.ese.pojo.GenerarInformeISEETJSP;
import eu.eurogestion.ese.pojo.GenerarInformeISOJSP;
import eu.eurogestion.ese.pojo.HabilitacionJSP;
import eu.eurogestion.ese.pojo.ModificarFormacionCursoAlumnoJSP;
import eu.eurogestion.ese.pojo.BuscadorPersonalJSP;
import eu.eurogestion.ese.pojo.RealizarCADJSP;
import eu.eurogestion.ese.pojo.ReporteFinServicioJSP;
import eu.eurogestion.ese.pojo.RevocarTituloJSP;
import eu.eurogestion.ese.pojo.SancionesJSP;
import eu.eurogestion.ese.pojo.SuspenderTituloJSP;
import eu.eurogestion.ese.pojo.SuspensionActivaTituloJSP;
import eu.eurogestion.ese.pojo.TareasPendientesJSP;
import eu.eurogestion.ese.repository.CursoAlumnoDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.HistoricoMaquinistaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.repository.ViewInspeccionDAO;
import eu.eurogestion.ese.services.ModificarPersonalService;
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
public class DetallePersonalController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Repositorio de la clase de dominio Titulo
	 */
	@Autowired
	public TituloDAO tituloDAO;

	/**
	 * Servicio de la modificacion del personal
	 */
	@Autowired
	public ModificarPersonalService modificarPersonalService;

	/**
	 * Repositorio de la clase de dominio RevisionPsico
	 */
	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public CursoDAO cursoDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public CursoAlumnoDAO cursoAlumnoDAO;

	@Autowired
	public TareaPendienteDAO tareaPendienteDAO;

	@Autowired
	public HistoricoMaquinistaDAO historicoMaquinistaDAO;

	@Autowired
	public ViewInspeccionDAO viewInspeccionDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetallePersonal", method = RequestMethod.POST)
	public String verDetallePersonal(BuscadorPersonalJSP formularioPersonal, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(formularioPersonal.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);

		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verPersonalTareaPendiente", method = RequestMethod.POST)
	public String verPersonalTareaPendiente(TareasPendientesJSP tareasPendientesJSP, Model model) {

		Personal personal = personalDAO.getOne(Integer.parseInt(tareasPendientesJSP.getIdTarea()));
		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(personal);
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);
		cerrarTareaPendiente(personal);

		return "detallePersonal";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/suspenderTituloCAD", method = RequestMethod.POST)
	public String suspenderTituloCAD(RealizarCADJSP realizarCAD, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(realizarCAD.getIdPersonalSometidoControl())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverVerDetallePersonal", method = RequestMethod.POST)
	public String volverVerDetallePersonal(DetallePersonalJSP detallePersonalJSP, Model model) {

		return "redirect:/buscadorPersonal";
	}

	@RequestMapping(value = "/volverVerDetalleFormacion", method = RequestMethod.POST)
	public String volverVerDetalleFormacion(DetalleFormacionJSP detalleFormacionJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(detalleFormacionJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverVerDetalleRevision", method = RequestMethod.POST)
	public String volverVerDetalleRevision(DetalleRevisionJSP detalleRevisionJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(detalleRevisionJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverVerDetalleTitulo", method = RequestMethod.POST)
	public String volverVerDetalleTitulo(DetalleTituloJSP detalleTituloJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(detalleTituloJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverSuspenderTitulo", method = RequestMethod.POST)
	public String volverSuspenderTitulo(SuspenderTituloJSP suspenderTituloJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(suspenderTituloJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);
		model.addAttribute("info", " Se ha suspendido el título correctamente");

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverVisorPDF", method = RequestMethod.POST)
	public String volverVisorPDF(@RequestParam String idPersonal, Model model, HttpServletResponse response) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(idPersonal)));
		model.addAttribute("detallePersonal", detallePersonalJSP);

		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";

	}

	@RequestMapping(value = "/volverSuspensionActivaCaducar", method = RequestMethod.POST)
	public String volverSuspensionActivaCaducar(SuspensionActivaTituloJSP suspensionActivaTituloJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(suspensionActivaTituloJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);
		model.addAttribute("info", " Se ha caducado el título correctamente");

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverSuspensionActivaRecuperar", method = RequestMethod.POST)
	public String volverSuspensionActivaRecuperar(SuspensionActivaTituloJSP suspensionActivaTituloJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(suspensionActivaTituloJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);
		model.addAttribute("info", " Se ha recuperado el título correctamente");

		return "detallePersonal";
	}

	@RequestMapping(value = "/errorVerDetalle", method = RequestMethod.POST)
	public String errorVerDetalleFormacion(DetallePersonalJSP detallePersonalJSP, Model model) {
		model.addAttribute("detallePersonal", detallePersonalJSP);
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {
			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {
			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {
			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {
			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {
			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);

		return "detallePersonal";
	}

	@RequestMapping(value = "/errorRevocarTitulo", method = RequestMethod.POST)
	public String errorRevocarTitulo(DetallePersonalJSP detallePersonalJSP, Model model) {
		model.addAttribute("detallePersonal", detallePersonalJSP);
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {
			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {
			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {
			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {
			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {
			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);
		return "detallePersonal";
	}

	@RequestMapping(value = "/volverRevocarTitulo", method = RequestMethod.POST)
	public String volverRevocarTitulo(RevocarTituloJSP revocarTituloJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(revocarTituloJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);
		model.addAttribute("info", " Se ha revocado el título correctamente");
		return "detallePersonal";
	}

	@RequestMapping(value = "/volverModificarSuperarCursoAlumno", method = RequestMethod.POST)
	public String volverModificarSuperarCursoAlumno(ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumnoJSP,
			Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(modificarFormacionCursoAlumnoJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverSuperarNoSuperarModificarFormacionCursoAlumno", method = RequestMethod.POST)
	public String volverSuperarNoSuperarModificarFormacionCursoAlumno(
			ModificarFormacionCursoAlumnoJSP modificarFormacionCursoAlumnoJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(modificarFormacionCursoAlumnoJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);
		model.addAttribute("info", "Se ha calificado correctamente");
		return "detallePersonal";
	}

	@RequestMapping(value = "/volverVerSanciones", method = RequestMethod.POST)
	public String volverVerSanciones(SancionesJSP sancionesJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(sancionesJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverHabilitarPersonal", method = RequestMethod.POST)
	public String volverHabilitarPersonal(HabilitacionJSP habilitacionJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(habilitacionJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverInformeAnomaliaPersonal", method = RequestMethod.POST)
	public String volverInformeAnomaliaPersonal(CrearInformeAnomaliasJSP crearInformeAnomaliasJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(crearInformeAnomaliasJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverRealizarCADPersonal", method = RequestMethod.POST)
	public String volverRealizarCADPersonal(RealizarCADJSP realizarCADJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(realizarCADJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverGenerarISEETPersonal", method = RequestMethod.POST)
	public String volverGenerarISEETPersonal(GenerarInformeISEETJSP generarISEETJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(generarISEETJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverGenerarISOPersonal", method = RequestMethod.POST)
	public String volverGenerarISOPersonal(GenerarInformeISOJSP generarISOJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(generarISOJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverReporteFinServicioPersonal", method = RequestMethod.POST)
	public String volverReporteFinServicioPersonal(ReporteFinServicioJSP reporteFinServicioJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(reporteFinServicioJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/volverPersonalCambiarPassword", method = RequestMethod.POST)
	public String volverPersonalCambiarPassword(CambiarPasswordJSP cambiarPasswordJSP, Model model) {

		DetallePersonalJSP detallePersonalJSP = convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(cambiarPasswordJSP.getIdPersonal())));
		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, 0, 0, 0, 0, 0, 5);

		return "detallePersonal";
	}

	@RequestMapping(value = "/bajaPersonal", method = RequestMethod.POST)
	public String bajaPersonal(DetallePersonalJSP detallePersonalJSP, Model model) {

		try {
			modificarPersonalService.bajaLogicaPersonal(detallePersonalJSP.getIdPersonal());
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("detallePersonal", detallePersonalJSP);
			int pageTitulos = 0;
			int pageCursos = 0;
			int pageRevisiones = 0;
			int pageInspecciones = 0;
			int pageReportes = 0;
			int size = 5;
			if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {
				pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
			}
			if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {
				pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
			}
			if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {
				pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
			}
			if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {
				pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
			}
			if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {
				pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
			}

			cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones,
					pageReportes, size);
			return "detallePersonal";
		}

		return "redirect:/buscadorPersonalDetallePersonalBaja";
	}

	@RequestMapping(value = "/caducarTitulo", method = RequestMethod.POST)
	public String caducarTitulo(DetallePersonalJSP detallePersonalJSP, Model model) {

		try {
			modificarPersonalService.caducarTitulo(detallePersonalJSP);

		} catch (EseException e) {
			log.error(e.getMessage());

		}

		model.addAttribute("detallePersonal", detallePersonalJSP);
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {
			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {
			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {
			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {
			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {
			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);
		model.addAttribute("info", " Se ha caducado el título correctamente");
		return "detallePersonal";
	}

	@RequestMapping(value = "/anadirLicenciaConduccionPersonal", method = RequestMethod.POST)
	public String anadirLicenciaConduccionPersonal(DetallePersonalJSP detallePersonalJSP, Model model) {

		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {
			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {
			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {
			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {
			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {
			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}

		try {
			modificarPersonalService.anadirLicenciaConduccionPersonal(detallePersonalJSP);

		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("detallePersonal", convertPersonalToDetallePersonalJSP(
					personalDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdPersonal()))));

			cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones,
					pageReportes, size);

			return "detallePersonal";
		}

		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);

		model.addAttribute("detallePersonal", convertPersonalToDetallePersonalJSP(
				personalDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdPersonal()))));
		model.addAttribute("info", " Se añadido correctamente");

		return "detallePersonal";
	}

	@RequestMapping(value = "/verLicenciaConduccionPersonal", method = RequestMethod.POST)
	public void verLicenciaConduccionPersonal(DetallePersonalJSP detallePersonalJSP, Model model,
			HttpServletResponse response) {
		Personal personal = personalDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdPersonal()));
		Documento documento = personal.getEvidencia81().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/paginacionTablaDetallePersonalTitulos", method = RequestMethod.POST)
	public String paginacionTablaDetallePersonalTitulos(DetallePersonalJSP detallePersonalJSP, Model model,
			HttpSession session) {
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {

			detallePersonalJSP.setPageTitulos(
					detallePersonalJSP.getPageTitulos().substring(detallePersonalJSP.getPageTitulos().indexOf(",") + 1,
							detallePersonalJSP.getPageTitulos().length()));
			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {

			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {

			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {

			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {

			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}

		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);
		return "detallePersonal";
	}

	@RequestMapping(value = "/paginacionTablaDetallePersonalCursos", method = RequestMethod.POST)
	public String paginacionTablaDetallePersonalCursos(DetallePersonalJSP detallePersonalJSP, Model model,
			HttpSession session) {
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {

			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {

			detallePersonalJSP.setPageCursos(detallePersonalJSP.getPageCursos().substring(
					detallePersonalJSP.getPageCursos().indexOf(",") + 1, detallePersonalJSP.getPageCursos().length()));
			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {

			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {

			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {

			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}

		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);
		return "detallePersonal";
	}

	@RequestMapping(value = "/paginacionTablaDetallePersonalRevisiones", method = RequestMethod.POST)
	public String paginacionTablaDetallePersonalRevisiones(DetallePersonalJSP detallePersonalJSP, Model model,
			HttpSession session) {
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {

			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {

			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {

			detallePersonalJSP.setPageRevisiones(detallePersonalJSP.getPageRevisiones().substring(
					detallePersonalJSP.getPageRevisiones().indexOf(",") + 1,
					detallePersonalJSP.getPageRevisiones().length()));
			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {

			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {

			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}

		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);
		return "detallePersonal";
	}

	@RequestMapping(value = "/paginacionTablaDetallePersonalInspecciones", method = RequestMethod.POST)
	public String paginacionTablaDetallePersonalInspecciones(DetallePersonalJSP detallePersonalJSP, Model model,
			HttpSession session) {
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {

			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {

			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {

			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {

			detallePersonalJSP.setPageInspecciones(detallePersonalJSP.getPageInspecciones().substring(
					detallePersonalJSP.getPageInspecciones().indexOf(",") + 1,
					detallePersonalJSP.getPageInspecciones().length()));
			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {

			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}

		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);
		return "detallePersonal";
	}

	@RequestMapping(value = "/paginacionTablaDetallePersonalReportes", method = RequestMethod.POST)
	public String paginacionTablaDetallePersonalReportes(DetallePersonalJSP detallePersonalJSP, Model model,
			HttpSession session) {
		int pageTitulos = 0;
		int pageCursos = 0;
		int pageRevisiones = 0;
		int pageInspecciones = 0;
		int pageReportes = 0;
		int size = 5;
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageTitulos())) {

			pageTitulos = Integer.parseInt(detallePersonalJSP.getPageTitulos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageCursos())) {

			pageCursos = Integer.parseInt(detallePersonalJSP.getPageCursos()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageRevisiones())) {

			pageRevisiones = Integer.parseInt(detallePersonalJSP.getPageRevisiones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageInspecciones())) {

			pageInspecciones = Integer.parseInt(detallePersonalJSP.getPageInspecciones()) - 1;
		}
		if (StringUtils.isNotBlank(detallePersonalJSP.getPageReportes())) {

			detallePersonalJSP.setPageReportes(detallePersonalJSP.getPageReportes().substring(
					detallePersonalJSP.getPageReportes().indexOf(",") + 1,
					detallePersonalJSP.getPageReportes().length()));
			pageReportes = Integer.parseInt(detallePersonalJSP.getPageReportes()) - 1;
		}

		model.addAttribute("detallePersonal", detallePersonalJSP);
		cargarListas(detallePersonalJSP, model, pageTitulos, pageCursos, pageRevisiones, pageInspecciones, pageReportes,
				size);
		return "detallePersonal";
	}

	private DetallePersonalJSP convertPersonalToDetallePersonalJSP(Personal personal) {

		DetallePersonalJSP detallePersonalJSP = new DetallePersonalJSP();
		detallePersonalJSP.setIdPersonal(personal.getIdPersonal().toString());
		String nombre = personal.getNombre() + " " + personal.getApellido1();
		if (StringUtils.isNotBlank(personal.getApellido2())) {
			nombre += " " + personal.getApellido2();
		}
		detallePersonalJSP.setNombre(nombre);

		detallePersonalJSP.setCargo(personal.getCargo().getNombre());

		if (personal.getFoto() != null) {
			detallePersonalJSP.setFoto(Base64.encodeBase64String(Utiles.convertBlobTobyteArray(personal.getFoto())));
		}
		detallePersonalJSP.setDni(personal.getDocumento());
		detallePersonalJSP.setNumeroEmpleado(personal.getDocEmpresa());
		detallePersonalJSP.setUsuario(personal.getNombreUsuario());
		detallePersonalJSP.setEmail(personal.getEmail());
		detallePersonalJSP
				.setFechaAlta(Utiles.convertDateToString(personal.getFechaAlta(), Constantes.FORMATO_FECHA_PANTALLA));
		detallePersonalJSP.setNacionalidad(personal.getNacionalidad());
		detallePersonalJSP.setDireccionCompleta(direccionCompletaPersonal(personal));
		detallePersonalJSP.setTieneLicenciaConduccion(personal.getEvidencia81() != null);

		// TODO Falta la implementacion cuando se creen los dominios de sanciones

		if (!CollectionUtils.isEmpty(cursoDAO.findCursoByPersonal(personal.getIdPersonal().toString()))
				&& revisionPsicoDAO.findLastRevisionPsico(personal.getIdPersonal().toString()) != null) {
			detallePersonalJSP.setHabilitar(Boolean.TRUE);
		} else {
			detallePersonalJSP.setHabilitar(Boolean.FALSE);
		}

		return detallePersonalJSP;

	}

	private String direccionCompletaPersonal(Personal personal) {
		String direccion = "";
		if (StringUtils.isNotBlank(personal.getTipoVia())) {
			direccion += personal.getTipoVia();
			direccion += " ";
		}
		if (StringUtils.isNotBlank(personal.getVia())) {
			direccion += personal.getVia();
			direccion += " ";
		}
		if (personal.getNumero() != null) {
			direccion += personal.getNumero().toString();
			direccion += " ";
		}
		if (StringUtils.isNotBlank(personal.getPuerta())) {
			direccion += personal.getPuerta();
			direccion += " ";
		}
		direccion += personal.getLocalidad().getNombre();
		direccion += " ";
		direccion += personal.getProvincia().getNombre();
		return direccion;
	}

	private void cargarListas(DetallePersonalJSP detallePersonalJSP, Model model, int pageTitulos, int pageCursos,
			int pageRevisiones, int pageInspecciones, int pageReportes, int size) {
		model.addAttribute("titulos",
				tituloDAO.findAllByPersonal(detallePersonalJSP.getIdPersonal(), PageRequest.of(pageTitulos, size)));
		model.addAttribute("cursoAlumnos",
				cursoAlumnoDAO.findAllByPersonal(detallePersonalJSP.getIdPersonal(), PageRequest.of(pageCursos, size)));
		model.addAttribute("revisiones", revisionPsicoDAO.findAllByPersonal(detallePersonalJSP.getIdPersonal(),
				PageRequest.of(pageRevisiones, size)));

		model.addAttribute("revisiones", revisionPsicoDAO.findAllByPersonal(detallePersonalJSP.getIdPersonal(),
				PageRequest.of(pageRevisiones, size)));
		model.addAttribute("inspecciones", viewInspeccionDAO.findByPersonal(detallePersonalJSP.getIdPersonal(),
				PageRequest.of(pageInspecciones, size)));
		model.addAttribute("reportes", historicoMaquinistaDAO.findAllReportesFinServicioByPersonal(
				detallePersonalJSP.getIdPersonal(), PageRequest.of(pageReportes, size)));
	}

	private void cerrarTareaPendiente(Personal personal) {

		TareaPendiente tareaPendiente = tareaPendienteDAO.findTareaPendienteByReference(personal.getIdPersonal(),
				Constantes.TAREAPTE_PERSONAL);

		if (tareaPendiente != null) {
			tareaPendiente.setLeido(Boolean.TRUE);
			tareaPendienteDAO.save(tareaPendiente);
		}
	}
}