package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CrearComposicionJSP;
import eu.eurogestion.ese.pojo.DetalleTrenJSP;
import eu.eurogestion.ese.pojo.DiaJSP;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.MaterialDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.CrearComposicionService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class CrearComposicionController {

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public CrearComposicionService crearComposicionService;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public MaterialDAO materialDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public ComposicionDAO composicionDAO;

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("trenes")
	public List<Tren> listTrenes() {
		List<Tren> lista = new ArrayList<>();
		Tren tren = new Tren();
		tren.setNumero("Selecciona uno:");
		lista.add(tren);
		lista.addAll(trenDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("dias")
	public List<DiaJSP> listDias() {
		List<DiaJSP> lista = new ArrayList<>();
		DiaJSP lunes = new DiaJSP();
		lunes.setIdDia(Calendar.MONDAY);
		lunes.setNombre(Constantes.DIA_LUNES);
		lista.add(lunes);
		DiaJSP martes = new DiaJSP();
		martes.setIdDia(Calendar.TUESDAY);
		martes.setNombre(Constantes.DIA_MARTES);
		lista.add(martes);
		DiaJSP miercoles = new DiaJSP();
		miercoles.setIdDia(Calendar.WEDNESDAY);
		miercoles.setNombre(Constantes.DIA_MIERCOLES);
		lista.add(miercoles);
		DiaJSP jueves = new DiaJSP();
		jueves.setIdDia(Calendar.THURSDAY);
		jueves.setNombre(Constantes.DIA_JUEVES);
		lista.add(jueves);
		DiaJSP viernes = new DiaJSP();
		viernes.setIdDia(Calendar.FRIDAY);
		viernes.setNombre(Constantes.DIA_VIERNES);
		lista.add(viernes);
		DiaJSP sabado = new DiaJSP();
		sabado.setIdDia(Calendar.SATURDAY);
		sabado.setNombre(Constantes.DIA_SABADO);
		lista.add(sabado);
		DiaJSP domingo = new DiaJSP();
		domingo.setIdDia(Calendar.SUNDAY);
		domingo.setNombre(Constantes.DIA_DOMINGO);
		lista.add(domingo);

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
	@RequestMapping(value = "/crearComposicionTren", method = RequestMethod.POST)
	public String crearComposicionTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		CrearComposicionJSP crearComposicionJSP = new CrearComposicionJSP();
		crearComposicionJSP.setVieneCrearTren(Boolean.TRUE);
		crearComposicionJSP.setIdTren(detalleTrenJSP.getIdTren());
		crearComposicionJSP.setListaDias(generarListaDias());
		model.addAttribute("crearComposicion", crearComposicionJSP);
		return "crearComposicion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/crearComposicion", method = RequestMethod.GET)
	public String crearComposicion(Model model) {

		CrearComposicionJSP crearComposicionJSP = new CrearComposicionJSP();
		crearComposicionJSP.setVieneCrearTren(Boolean.FALSE);
		crearComposicionJSP.setListaDias(generarListaDias());
		model.addAttribute("crearComposicion", crearComposicionJSP);
		return "crearComposicion";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorHomologacionJSP objeto con los campos de filtro de la pantalla
	 * @param model                   objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/generarComposiciones", method = RequestMethod.POST)
	public String generarComposiciones(CrearComposicionJSP crearComposicionJSP, Model model) {

		if (crearComposicionJSP.getListaDias().isEmpty()) {
			model.addAttribute("error", "Tienes que seleccionar mínimo un día.");
			model.addAttribute("crearComposicion", crearComposicionJSP);
			return "crearComposicion";
		}
		GregorianCalendar fechaInicio = new GregorianCalendar();
		fechaInicio.setTime(Utiles.parseDatePantalla(crearComposicionJSP.getFechaInicio()));
		GregorianCalendar fechaFin = new GregorianCalendar();
		fechaFin.setTime(Utiles.parseDatePantalla(crearComposicionJSP.getFechaFin()));
		if (fechaFin.before(fechaInicio)) {
			model.addAttribute("error", "La fecha inicio tiene que ser menor o igual que la fecha fin.");
			model.addAttribute("crearComposicion", crearComposicionJSP);
			return "crearComposicion";
		}
		if (composicionDAO.existeComposicionesByFilters(crearComposicionJSP.getIdTren(),
				crearComposicionJSP.getFechaInicio(), crearComposicionJSP.getFechaFin())) {
			model.addAttribute("error", "Ya existe alguna composición para ese tren incluida en ese rango de fechas.");
			model.addAttribute("crearComposicion", crearComposicionJSP);
			return "crearComposicion";
		}
		List<Integer> listIdComposicion;
		try {
			listIdComposicion = crearComposicionService.generarComposiciones(crearComposicionJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("crearComposicion", crearComposicionJSP);
			return "crearComposicion";
		}
		crearComposicionJSP.setListaIdsComposiciones(listIdComposicion);
		generarListasMaterial(crearComposicionJSP, model, 0, 0, 5);
		model.addAttribute("info", " Se ha creado correctamente");
		model.addAttribute("crearComposicion", crearComposicionJSP);
		return "crearComposicion";
	}

	@RequestMapping(value = "/addMaterialComposicion", method = RequestMethod.POST)
	public String addMaterialComposicion(CrearComposicionJSP crearComposicionJSP, Model model) {

		try {
			crearComposicionService.addMaterialComposicion(crearComposicionJSP.getListaIdsComposiciones(),
					Integer.parseInt(crearComposicionJSP.getIdMaterial()));
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMateriales())) {
			Long elementosTotales = materialDAO.countByNotIdComposicion(crearComposicionJSP.getListaIdsComposiciones());

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(crearComposicionJSP.getPageTotalMateriales());
			if (totalPaginas < pageJSP) {
				crearComposicionJSP.setPageTotalMateriales(String.valueOf(pageJSP - 1));
			}
			pageTotalMateriales = Integer.parseInt(crearComposicionJSP.getPageTotalMateriales()) - 1;
		}

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMaterialesComposicion())) {
			pageTotalMaterialesComposicion = Integer.parseInt(crearComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}

		generarListasMaterial(crearComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("crearComposicion", crearComposicionJSP);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "crearComposicion";
	}

	@RequestMapping(value = "/eliminarMaterialComposicion", method = RequestMethod.POST)
	public String eliminarMaterialComposicion(CrearComposicionJSP crearComposicionJSP, Model model) {

		try {
			crearComposicionService.eliminarMaterialComposicion(crearComposicionJSP.getListaIdsComposiciones(),
					Integer.parseInt(crearComposicionJSP.getIdMaterial()));
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMateriales())) {
			pageTotalMateriales = Integer.parseInt(crearComposicionJSP.getPageTotalMateriales()) - 1;

		}

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMaterialesComposicion())) {
			Long elementosTotales = materialDAO.countByIdComposicion(crearComposicionJSP.getListaIdsComposiciones());

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(crearComposicionJSP.getPageTotalMaterialesComposicion());
			if (totalPaginas < pageJSP) {
				crearComposicionJSP.setPageTotalMaterialesComposicion(String.valueOf(pageJSP - 1));
			}
			pageTotalMaterialesComposicion = Integer.parseInt(crearComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}
		generarListasMaterial(crearComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("crearComposicion", crearComposicionJSP);
		model.addAttribute("info", "Se ha eliminado correctamente");
		return "crearComposicion";
	}

	@RequestMapping(value = "/paginacionTablaCrearComposicionTotalMateriales", method = RequestMethod.POST)
	public String paginacionTablaCrearComposicionTotalMateriales(CrearComposicionJSP crearComposicionJSP, Model model,
			HttpSession session) {
		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMateriales())) {
			crearComposicionJSP.setPageTotalMateriales(crearComposicionJSP.getPageTotalMateriales().substring(
					crearComposicionJSP.getPageTotalMateriales().indexOf(",") + 1,
					crearComposicionJSP.getPageTotalMateriales().length()));
			pageTotalMateriales = Integer.parseInt(crearComposicionJSP.getPageTotalMateriales()) - 1;
		}

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMaterialesComposicion())) {
			pageTotalMaterialesComposicion = Integer.parseInt(crearComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}

		generarListasMaterial(crearComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("crearComposicion", crearComposicionJSP);
		return "crearComposicion";
	}

	@RequestMapping(value = "/paginacionTablaCrearComposicionTotalMaterialesComposicion", method = RequestMethod.POST)
	public String paginacionTablaCrearComposicionTotalMaterialesComposicion(CrearComposicionJSP crearComposicionJSP,
			Model model, HttpSession session) {
		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMaterialesComposicion())) {
			crearComposicionJSP
					.setPageTotalMaterialesComposicion(crearComposicionJSP.getPageTotalMaterialesComposicion()
							.substring(crearComposicionJSP.getPageTotalMaterialesComposicion().indexOf(",") + 1,
									crearComposicionJSP.getPageTotalMaterialesComposicion().length()));
			pageTotalMaterialesComposicion = Integer.parseInt(crearComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}

		if (StringUtils.isNotBlank(crearComposicionJSP.getPageTotalMateriales())) {
			pageTotalMateriales = Integer.parseInt(crearComposicionJSP.getPageTotalMateriales()) - 1;
		}

		generarListasMaterial(crearComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("crearComposicion", crearComposicionJSP);
		return "crearComposicion";
	}

	@RequestMapping(value = "/volverCrearComposicion", method = RequestMethod.POST)
	public String volverCrearComposicion(CrearComposicionJSP crearComposicionJSP, Model model) {

		if (crearComposicionJSP.isVieneCrearTren()) {
			return "forward:/volverDetalleTren";
		} else {
			return "redirect:/buscadorComposicion";
		}

	}

	private List<String> generarListaDias() {
		List<String> lista = new ArrayList<>();
		lista.add(String.valueOf(Calendar.MONDAY));
		lista.add(String.valueOf(Calendar.TUESDAY));
		lista.add(String.valueOf(Calendar.WEDNESDAY));
		lista.add(String.valueOf(Calendar.THURSDAY));
		lista.add(String.valueOf(Calendar.FRIDAY));
		lista.add(String.valueOf(Calendar.SATURDAY));
		lista.add(String.valueOf(Calendar.SUNDAY));
		return lista;
	}

	private void generarListasMaterial(CrearComposicionJSP crearComposicionJSP, Model model, int pageTotalMateriales,
			int pageTotalMaterialesComposicion, int size) {

		model.addAttribute("totalMaterialesComposicion", materialDAO.findByIdComposicion(
				crearComposicionJSP.getListaIdsComposiciones(), PageRequest.of(pageTotalMaterialesComposicion, size)));
		model.addAttribute("totalMateriales", materialDAO.findByNotIdComposicion(
				crearComposicionJSP.getListaIdsComposiciones(), PageRequest.of(pageTotalMateriales, size)));

	}
}