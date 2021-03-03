package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Composicion;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.PuntoInfraestructura;
import eu.eurogestion.ese.domain.Tramo;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorTrenJSP;
import eu.eurogestion.ese.pojo.CrearComposicionJSP;
import eu.eurogestion.ese.pojo.DetalleTrenJSP;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TramoDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.ComposicionService;
import eu.eurogestion.ese.services.TrenService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class DetalleTrenController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public TramoDAO tramoDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TrenService trenService;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public ComposicionDAO composicionDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public ComposicionService composicionService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@ModelAttribute("tramos")
	public List<Tramo> listTramosAll() {
		List<Tramo> lista = new ArrayList<>();
		Tramo tramo = new Tramo();
		tramo.setNombre("Selecciona uno:");
		lista.add(tramo);
		lista.addAll(tramoDAO.findByEsEspecialFalse(Sort.by(Order.asc("nombre"))));
		return lista;
	}

	@ModelAttribute("puntosInfraestructura")
	public List<PuntoInfraestructura> listPuntoInfraestructuraAll() {
		List<PuntoInfraestructura> lista = new ArrayList<>();
		PuntoInfraestructura tramo = new PuntoInfraestructura();
		tramo.setNombre("Selecciona uno:");
		lista.add(tramo);
		lista.addAll(puntoInfraestructuraDAO.findAll(Sort.by(Order.asc("nombreLargo"))));
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleTren", method = RequestMethod.POST)
	public String verDetalleTren(BuscadorTrenJSP buscadorTrenJSP, Model model) {

		model.addAttribute("detalleTren", convertTrenToDetalleTrenJSP(
				trenDAO.getOne(Integer.parseInt(buscadorTrenJSP.getIdTren())), Boolean.TRUE));
		cargarComposiciones(buscadorTrenJSP.getIdTren(), model, 0, 5);
		return "detalleTren";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarDetalleTren", method = RequestMethod.POST)
	public String modificarDetalleTren(BuscadorTrenJSP buscadorTrenJSP, Model model) {

		model.addAttribute("detalleTren", convertTrenToDetalleTrenJSP(
				trenDAO.getOne(Integer.parseInt(buscadorTrenJSP.getIdTren())), Boolean.FALSE));
		cargarComposiciones(buscadorTrenJSP.getIdTren(), model, 0, 5);
		return "detalleTren";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/nuevoTren", method = RequestMethod.GET)
	public String nuevoTren(Model model) {

		DetalleTrenJSP detalleTrenJSP = new DetalleTrenJSP();
		detalleTrenJSP.setLectura(Boolean.FALSE);
		detalleTrenJSP.setEsEspecial(Boolean.FALSE);
		detalleTrenJSP.setTieneDocumento(Boolean.FALSE);
		model.addAttribute("detalleTren", detalleTrenJSP);

		return "detalleTren";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/seleccionarEspecialTren", method = RequestMethod.POST)
	public String seleccionarEspecialTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		if (StringUtils.isBlank(detalleTrenJSP.getIdTren())) {
			detalleTrenJSP.setIdTren(null);
		}

		cargarComposiciones(detalleTrenJSP.getIdTren(), model, 0, 5);
		model.addAttribute("detalleTren", detalleTrenJSP);
		return "detalleTren";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/crearTren", method = RequestMethod.POST)
	public String crearTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		Tren tren;
		try {
			tren = trenService.crearTren(detalleTrenJSP);
		} catch (EseException e) {
			// TODO falta añadir error
			detalleTrenJSP.setIdTren(null);
			log.error(e.getMessage());
			model.addAttribute("detalleTren", detalleTrenJSP);
			return "detalleTren";
		}
		detalleTrenJSP.setIdTren(tren.getIdTren().toString());
		cargarComposiciones(detalleTrenJSP.getIdTren(), model, 0, 5);
		model.addAttribute("detalleTren", convertTrenToDetalleTrenJSP(
				trenDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTren())), detalleTrenJSP.isLectura()));
		model.addAttribute("info", " Se ha creado correctamente");
		return "detalleTren";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarDetalleTren", method = RequestMethod.POST)
	public String guardarTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		trenService.guardarTren(detalleTrenJSP);

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleTrenJSP.getPage())) {
			page = Integer.parseInt(detalleTrenJSP.getPage()) - 1;
		}

		model.addAttribute("detalleTren", convertTrenToDetalleTrenJSP(
				trenDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTren())), detalleTrenJSP.isLectura()));
		cargarComposiciones(detalleTrenJSP.getIdTren(), model, page, size);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "detalleTren";
	}

	@RequestMapping(value = "/anadirDocumentoTren", method = RequestMethod.POST)
	public String anadirDocumentoTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleTrenJSP.getPage())) {
			page = Integer.parseInt(detalleTrenJSP.getPage()) - 1;
		}
		try {
			trenService.anadirDocumentoTren(detalleTrenJSP);
		} catch (EseException e) {
			model.addAttribute("detalleTren", convertTrenToDetalleTrenJSP(
					trenDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTren())), detalleTrenJSP.isLectura()));
			cargarComposiciones(detalleTrenJSP.getIdTren(), model, page, size);
			return "detalleTren";
		}

		model.addAttribute("detalleTren", convertTrenToDetalleTrenJSP(
				trenDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTren())), detalleTrenJSP.isLectura()));
		cargarComposiciones(detalleTrenJSP.getIdTren(), model, page, size);
		model.addAttribute("info", " Se añadido correctamente");
		return "detalleTren";
	}

	@RequestMapping(value = "/verDocumentoTren", method = RequestMethod.POST)
	public void verDocumentoTren(DetalleTrenJSP detalleTrenJSP, Model model, HttpServletResponse response) {
		Tren tren = trenDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTren()));
		Documento documento = tren.getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorComposicionJSP objeto con los campos de filtro de la pantalla
	 * @param model                  objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/elimiarComposicionTren", method = RequestMethod.POST)
	public String elimiarComposicionTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		Composicion composicion = composicionDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdComposicion()));
		if (!CollectionUtils.isEmpty(composicion.getListHistoricoMaquinista())) {
			int page = 0; // default page number is 0 (yes it is weird)
			int size = 5; // default page size is 10

			if (StringUtils.isNotBlank(detalleTrenJSP.getPage())) {
				page = Integer.parseInt(detalleTrenJSP.getPage()) - 1;
			}

			model.addAttribute("detalleTren", detalleTrenJSP);
			cargarComposiciones(detalleTrenJSP.getIdTren(), model, page, size);
			model.addAttribute("error",
					"No se puede eliminar la composición porque se hace referencia en un reporte fin de servicio");
			return "detalleTren";
		}

		try {
			composicionService.eliminarComposicion(Integer.parseInt(detalleTrenJSP.getIdComposicion()));
		} catch (NumberFormatException | EseException e) {
			log.error(e.getMessage());
		}

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleTrenJSP.getPage())) {
			Long elementosTotales = composicionDAO.countListComposicionesByFilters(detalleTrenJSP.getIdTren(), null);

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(detalleTrenJSP.getPage());
			if (totalPaginas < pageJSP) {
				detalleTrenJSP.setPage(String.valueOf(pageJSP - 1));
			}
			page = Integer.parseInt(detalleTrenJSP.getPage()) - 1;
		}

		model.addAttribute("detalleTren", detalleTrenJSP);
		cargarComposiciones(detalleTrenJSP.getIdTren(), model, page, size);
		model.addAttribute("info", "Se ha eliminado correctamente");
		return "detalleTren";
	}

	@RequestMapping(value = "/paginacionTablaDetalleTren", method = RequestMethod.POST)
	public String paginacionTablaDetalleTren(DetalleTrenJSP detalleTrenJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleTrenJSP.getPage())) {
			detalleTrenJSP.setPage(detalleTrenJSP.getPage().substring(detalleTrenJSP.getPage().indexOf(",") + 1,
					detalleTrenJSP.getPage().length()));
			page = Integer.parseInt(detalleTrenJSP.getPage()) - 1;
		}

		model.addAttribute("detalleTren", detalleTrenJSP);
		cargarComposiciones(detalleTrenJSP.getIdTren(), model, page, size);
		return "detalleTren";
	}

	private DetalleTrenJSP convertTrenToDetalleTrenJSP(Tren tren, Boolean lectura) {

		DetalleTrenJSP detalleTrenJSP = new DetalleTrenJSP();
		detalleTrenJSP.setIdTren(tren.getIdTren().toString());
		detalleTrenJSP.setLectura(lectura);
		detalleTrenJSP.setNumeroTren(tren.getNumero());
		detalleTrenJSP.setIdTramo(tren.getTramo().getIdTramo().toString());
		detalleTrenJSP.setIdOrigen(tren.getTramo().getPuntoOrigen().getIdPuntoInfraestructura().toString());
		detalleTrenJSP.setIdDestino(tren.getTramo().getPuntoDestino().getIdPuntoInfraestructura().toString());
		detalleTrenJSP.setNombre(tren.getTramo().getNombre());
		detalleTrenJSP.setEsEspecial(tren.getTramo().getEsEspecial());
		detalleTrenJSP.setHoraInicio(Utiles.formatStringHorasPantalla(tren.getHoraInicio()));
		detalleTrenJSP.setHoraFin(Utiles.formatStringHorasPantalla(tren.getHoraFin()));
		detalleTrenJSP.setTieneDocumento(tren.getDocumento() != null);
		return detalleTrenJSP;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverDetalleTren", method = RequestMethod.POST)
	public String volverDetalleTren(CrearComposicionJSP crearComposicionJSP, Model model) {

		model.addAttribute("detalleTren", convertTrenToDetalleTrenJSP(
				trenDAO.getOne(Integer.parseInt(crearComposicionJSP.getIdTren())), Boolean.FALSE));
		cargarComposiciones(crearComposicionJSP.getIdTren(), model, 0, 5);
		return "detalleTren";
	}

	private void cargarComposiciones(String idTren, Model model, int page, int size) {
		model.addAttribute("composiciones",
				composicionDAO.findListComposicionesByFilters(idTren, null, PageRequest.of(page, size)));
	}

}