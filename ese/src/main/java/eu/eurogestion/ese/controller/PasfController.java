package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.domain.Pasf;
import eu.eurogestion.ese.domain.TipoCurso;
import eu.eurogestion.ese.domain.TipoInspeccion;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorPasfJSP;
import eu.eurogestion.ese.pojo.PasfJSP;
import eu.eurogestion.ese.repository.AuditoriaPasfDAO;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CursoPasfDAO;
import eu.eurogestion.ese.repository.EstadoPasfDAO;
import eu.eurogestion.ese.repository.InspeccionPasfDAO;
import eu.eurogestion.ese.repository.PasfDAO;
import eu.eurogestion.ese.repository.TipoCursoDAO;
import eu.eurogestion.ese.repository.TipoInspeccionDAO;
import eu.eurogestion.ese.services.PasfService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class PasfController {

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public PasfDAO pasfDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public EstadoPasfDAO estadoPasfDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public CursoPasfDAO cursoPasfDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public InspeccionPasfDAO inspeccionPasfDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public AuditoriaPasfDAO auditoriaPasfDAO;

	@Autowired
	public TipoCursoDAO tipoCursoDAO;

	@Autowired
	public CargoDAO cargoDAO;

	@Autowired
	public TipoInspeccionDAO tipoInspeccionDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public PasfService pasfService;

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("categorias")
	public List<TipoCurso> listCategorias() {
		List<TipoCurso> lista = new ArrayList<>();
		TipoCurso categoria = new TipoCurso();
		categoria.setValor("Selecciona uno:");
		lista.add(categoria);
		lista.addAll(tipoCursoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("cargos")
	public List<Cargo> listCargos() {
		List<Cargo> lista = new ArrayList<>();
		Cargo cargo = new Cargo();
		cargo.setNombre("Selecciona uno:");
		lista.add(cargo);
		lista.addAll(cargoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("tipos")
	public List<TipoInspeccion> listTipos() {
		List<TipoInspeccion> lista = new ArrayList<>();
		TipoInspeccion tipo = new TipoInspeccion();
		tipo.setValor("Selecciona uno:");
		lista.add(tipo);
		lista.addAll(tipoInspeccionDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/addPasf", method = RequestMethod.GET)
	public String addPasf(Model model) {

		PasfJSP pasfJSP = new PasfJSP();
		pasfJSP.setLectura(Boolean.FALSE);
		model.addAttribute("pasf", pasfJSP);
		return "pasf";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorHomologacionJSP objeto con los campos de filtro de la pantalla
	 * @param model                   objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verPasf", method = RequestMethod.POST)
	public String verPasf(BuscadorPasfJSP buscadorPasfJSP, Model model) {

		PasfJSP pasfJSP = cargarPasf(buscadorPasfJSP, model);
		pasfJSP.setLectura(Boolean.TRUE);
		model.addAttribute("pasf", pasfJSP);
		return "pasf";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarPasf", method = RequestMethod.POST)
	public String modificarPasf(BuscadorPasfJSP buscadorPasfJSP, Model model) {

		PasfJSP pasfJSP = cargarPasf(buscadorPasfJSP, model);
		pasfJSP.setLectura(Boolean.FALSE);
		model.addAttribute("pasf", pasfJSP);
		return "pasf";
	}

	private PasfJSP cargarPasf(BuscadorPasfJSP buscadorPasfJSP, Model model) {

		PasfJSP pasfJSP = new PasfJSP();

		Pasf pasf = pasfDAO.getOne(Integer.parseInt(buscadorPasfJSP.getIdPasf()));

		pasfJSP.setIdPasf(pasf.getIdPasf());
		pasfJSP.setIdEstadoPasf(pasf.getEstadoPasf().getIdEstadoPasf());
		pasfJSP.setAnno(pasf.getAnno().toString());
		pasfJSP.setDescarrilamiento(pasf.getDescarrilamiento().toString());
		pasfJSP.setColision(pasf.getColision().toString());
		pasfJSP.setAccidentePasoNivel(pasf.getAccidentePn().toString());
		pasfJSP.setIncendio(pasf.getIncendio().toString());
		pasfJSP.setArrollamientoVia(pasf.getArrollamientoVia().toString());
		pasfJSP.setArrollamientoInterseccion(pasf.getArrollamientoInterseccion().toString());
		pasfJSP.setCaidaPersonas(pasf.getCaidaPersonas().toString());
		pasfJSP.setSuicidio(pasf.getSuicidio().toString());
		pasfJSP.setDescomposicion(pasf.getDescomposiciónCarga().toString());
		pasfJSP.setDetencion(pasf.getDetencionTren().toString());
		pasfJSP.setInvasionVia(pasf.getInvasionVia().toString());
		pasfJSP.setIncidentesTransportesExcepcionales(pasf.getIncidenteTe().toString());
		pasfJSP.setRebaseSennal(pasf.getRebaseSenal().toString());
		pasfJSP.setConatoColision(pasf.getConatoColision().toString());
		pasfJSP.setEnganchePantografo(pasf.getEnganche().toString());
		pasfJSP.setOtros(pasf.getOtros().toString());

		pasfJSP.setCursosFormativos(pasf.getCursos().toString());

		pasfJSP.setRevisionesAptitudPsicofisica(pasf.getRevisiones().toString());

		pasfJSP.setIso(pasf.getIso().toString());
		pasfJSP.setIssc(pasf.getIssc().toString());
		pasfJSP.setIsmp(pasf.getIsmp().toString());
		pasfJSP.setIseet(pasf.getIseet().toString());
		pasfJSP.setCad(pasf.getCad().toString());

		pasfJSP.setAuditorias(pasf.getAuditorias().toString());

		cargarListas(pasf.getIdPasf().toString(), model, 0, 0, 0, 5);

		return pasfJSP;
	}

	private void cargarListas(String idPasf, Model model, int pageCursos, int pageInspecciones, int pageAuditorias,
			int size) {

		model.addAttribute("cursosPasf", cursoPasfDAO.findCursoPasfByPasf(idPasf, PageRequest.of(pageCursos, size)));
		model.addAttribute("inspeccionesPasf",
				inspeccionPasfDAO.findInspeccionPasfByPasf(idPasf, PageRequest.of(pageInspecciones, size)));
		model.addAttribute("auditoriasPasf",
				auditoriaPasfDAO.findAuditoriaPasfByPasf(idPasf, PageRequest.of(pageAuditorias, size)));
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarPasf", method = RequestMethod.POST)
	public String guardarPasf(PasfJSP pasfJSP, Model model, HttpSession sesion) {

		try {
			pasfService.guardarPasf(pasfJSP, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("error", e.getMessage());
			model.addAttribute("pasf", pasfJSP);
			return "pasf";
		}

		model.addAttribute("pasf", pasfJSP);
		if (pasfJSP.getIdPasf() != null) {
			int pageCursos = 0; // default page number is 0 (yes it is weird)
			int pageInspecciones = 0; // default page number is 0 (yes it is weird)
			int pageAuditorias = 0; // default page number is 0 (yes it is weird)
			int size = 5; // default page size is 10

			if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
				pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
			}
			if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
				pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
			}
			if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
				pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
			}
			cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		}
		model.addAttribute("info", "Se ha guardado correctamente");
		return "pasf";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarCursoPasf", method = RequestMethod.POST)
	public String guardarCursoPasf(PasfJSP pasfJSP, Model model, HttpSession sesion) {

		try {
			pasfService.guardarCursoPasf(pasfJSP, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		model.addAttribute("pasf", pasfJSP);
		int pageCursos = 0; // default page number is 0 (yes it is weird)
		int pageInspecciones = 0; // default page number is 0 (yes it is weird)
		int pageAuditorias = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
			pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
			pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
			pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
		}
		cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "pasf";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarInspeccionPasf", method = RequestMethod.POST)
	public String guardarInspeccionPasf(PasfJSP pasfJSP, Model model, HttpSession sesion) {

		try {
			pasfService.guardarInspeccionPasf(pasfJSP, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		model.addAttribute("pasf", pasfJSP);
		int pageCursos = 0; // default page number is 0 (yes it is weird)
		int pageInspecciones = 0; // default page number is 0 (yes it is weird)
		int pageAuditorias = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
			pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
			pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
			pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
		}
		cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "pasf";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarAuditoriaPasf", method = RequestMethod.POST)
	public String guardarAuditoriaPasf(PasfJSP pasfJSP, Model model, HttpSession sesion) {

		try {
			pasfService.guardarAuditoriaPasf(pasfJSP, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		model.addAttribute("pasf", pasfJSP);
		int pageCursos = 0; // default page number is 0 (yes it is weird)
		int pageInspecciones = 0; // default page number is 0 (yes it is weird)
		int pageAuditorias = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
			pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
			pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
			pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
		}
		cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "pasf";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/aprobarPasf", method = RequestMethod.POST)
	public String aprobarPasf(PasfJSP pasfJSP, Model model, HttpSession sesion) {

		try {
			pasfService.aprobarPasf(pasfJSP, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		model.addAttribute("pasf", pasfJSP);
		int pageCursos = 0; // default page number is 0 (yes it is weird)
		int pageInspecciones = 0; // default page number is 0 (yes it is weird)
		int pageAuditorias = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
			pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
			pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
			pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
		}
		cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		model.addAttribute("info", "Se ha aprobado correctamente");
		return "pasf";
	}

	@RequestMapping(value = "/paginacionTablaPASFAccionesFormativas", method = RequestMethod.POST)
	public String paginacionTablaPASFAccionesFormativas(PasfJSP pasfJSP, Model model) {
		int pageCursos = 0; // default page number is 0 (yes it is weird)
		int pageInspecciones = 0; // default page number is 0 (yes it is weird)
		int pageAuditorias = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
			pasfJSP.setPageAccionesFormativas(
					pasfJSP.getPageAccionesFormativas().substring(pasfJSP.getPageAccionesFormativas().indexOf(",") + 1,
							pasfJSP.getPageAccionesFormativas().length()));
			pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
			pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
			pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
		}
		cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		model.addAttribute("pasf", pasfJSP);
		return "pasf";
	}

	@RequestMapping(value = "/paginacionTablaPASFAccionesSeguridad", method = RequestMethod.POST)
	public String paginacionTablaPASFAccionesSeguridad(PasfJSP pasfJSP, Model model) {
		int pageCursos = 0; // default page number is 0 (yes it is weird)
		int pageInspecciones = 0; // default page number is 0 (yes it is weird)
		int pageAuditorias = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
			pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
			pasfJSP.setPageAccionesSeguridad(pasfJSP.getPageAccionesSeguridad().substring(
					pasfJSP.getPageAccionesSeguridad().indexOf(",") + 1, pasfJSP.getPageAccionesSeguridad().length()));
			pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
			pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
		}
		cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		model.addAttribute("pasf", pasfJSP);
		return "pasf";
	}

	@RequestMapping(value = "/paginacionTablaPASFIniciativasSeguridad", method = RequestMethod.POST)
	public String paginacionTablaPASFIniciativasSeguridad(PasfJSP pasfJSP, Model model) {
		int pageCursos = 0; // default page number is 0 (yes it is weird)
		int pageInspecciones = 0; // default page number is 0 (yes it is weird)
		int pageAuditorias = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesFormativas())) {
			pageCursos = Integer.parseInt(pasfJSP.getPageAccionesFormativas()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageAccionesSeguridad())) {
			pageInspecciones = Integer.parseInt(pasfJSP.getPageAccionesSeguridad()) - 1;
		}
		if (StringUtils.isNotBlank(pasfJSP.getPageIniciativasSeguridad())) {
			pasfJSP.setPageIniciativasSeguridad(pasfJSP.getPageIniciativasSeguridad().substring(
					pasfJSP.getPageIniciativasSeguridad().indexOf(",") + 1,
					pasfJSP.getPageIniciativasSeguridad().length()));
			pageAuditorias = Integer.parseInt(pasfJSP.getPageIniciativasSeguridad()) - 1;
		}
		cargarListas(pasfJSP.getIdPasf().toString(), model, pageCursos, pageInspecciones, pageAuditorias, size);
		model.addAttribute("pasf", pasfJSP);
		return "pasf";
	}

}