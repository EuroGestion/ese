package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.Arrays;
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

import eu.eurogestion.ese.domain.Composicion;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorComposicionJSP;
import eu.eurogestion.ese.pojo.DetalleComposicionJSP;
import eu.eurogestion.ese.pojo.DetalleTrenJSP;
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
public class DetalleComposicionController {

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
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleComposicionTren", method = RequestMethod.POST)
	public String verDetalleComposicionTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		DetalleComposicionJSP detalleComposicionJSP = new DetalleComposicionJSP();
		detalleComposicionJSP.setVieneCrearTren(Boolean.TRUE);
		detalleComposicionJSP.setLectura(Boolean.TRUE);
		detalleComposicionJSP.setIdTren(detalleTrenJSP.getIdTren());
		Composicion composicion = composicionDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdComposicion()));
		detalleComposicionJSP.setIdTren(composicion.getTren().getIdTren().toString());
		detalleComposicionJSP
				.setFecha(Utiles.convertDateToString(composicion.getFecha(), Constantes.FORMATO_FECHA_PANTALLA));
		detalleComposicionJSP.setIdComposicion(composicion.getIdComposicion());
		generarListasMaterial(detalleComposicionJSP, model, 0, 0, 5);
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarDetalleComposicionTren", method = RequestMethod.POST)
	public String modificarDetalleComposicionTren(DetalleTrenJSP detalleTrenJSP, Model model) {

		DetalleComposicionJSP detalleComposicionJSP = new DetalleComposicionJSP();
		detalleComposicionJSP.setVieneCrearTren(Boolean.TRUE);
		detalleComposicionJSP.setLectura(Boolean.FALSE);
		detalleComposicionJSP.setIdTren(detalleTrenJSP.getIdTren());
		Composicion composicion = composicionDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdComposicion()));
		detalleComposicionJSP.setIdTren(composicion.getTren().getIdTren().toString());
		detalleComposicionJSP
				.setFecha(Utiles.convertDateToString(composicion.getFecha(), Constantes.FORMATO_FECHA_PANTALLA));
		detalleComposicionJSP.setIdComposicion(composicion.getIdComposicion());
		generarListasMaterial(detalleComposicionJSP, model, 0, 0, 5);
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleComposicion", method = RequestMethod.POST)
	public String verDetalleComposicion(BuscadorComposicionJSP buscadorComposicionJSP, Model model) {

		DetalleComposicionJSP detalleComposicionJSP = new DetalleComposicionJSP();
		detalleComposicionJSP.setVieneCrearTren(Boolean.FALSE);
		detalleComposicionJSP.setLectura(Boolean.TRUE);
		Composicion composicion = composicionDAO.getOne(Integer.parseInt(buscadorComposicionJSP.getIdComposicion()));
		detalleComposicionJSP.setIdTren(composicion.getTren().getIdTren().toString());
		detalleComposicionJSP
				.setFecha(Utiles.convertDateToString(composicion.getFecha(), Constantes.FORMATO_FECHA_PANTALLA));
		detalleComposicionJSP.setIdComposicion(composicion.getIdComposicion());
		generarListasMaterial(detalleComposicionJSP, model, 0, 0, 5);
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarDetalleComposicion", method = RequestMethod.POST)
	public String modificarDetalleComposicion(BuscadorComposicionJSP buscadorComposicionJSP, Model model) {

		DetalleComposicionJSP detalleComposicionJSP = new DetalleComposicionJSP();
		detalleComposicionJSP.setVieneCrearTren(Boolean.FALSE);
		detalleComposicionJSP.setLectura(Boolean.FALSE);
		Composicion composicion = composicionDAO.getOne(Integer.parseInt(buscadorComposicionJSP.getIdComposicion()));
		detalleComposicionJSP.setIdTren(composicion.getTren().getIdTren().toString());
		detalleComposicionJSP
				.setFecha(Utiles.convertDateToString(composicion.getFecha(), Constantes.FORMATO_FECHA_PANTALLA));
		detalleComposicionJSP.setIdComposicion(composicion.getIdComposicion());
		generarListasMaterial(detalleComposicionJSP, model, 0, 0, 5);
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	@RequestMapping(value = "/addMaterialComposicionDetalleComposicion", method = RequestMethod.POST)
	public String addMaterialComposicion(DetalleComposicionJSP detalleComposicionJSP, Model model) {

		try {
			crearComposicionService.addMaterialComposicion(Arrays.asList(detalleComposicionJSP.getIdComposicion()),
					Integer.parseInt(detalleComposicionJSP.getIdMaterial()));
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMateriales())) {
			Long elementosTotales = materialDAO
					.countByNotIdComposicion(Arrays.asList(detalleComposicionJSP.getIdComposicion()));

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(detalleComposicionJSP.getPageTotalMateriales());
			if (totalPaginas < pageJSP) {
				detalleComposicionJSP.setPageTotalMateriales(String.valueOf(pageJSP - 1));
			}
			pageTotalMateriales = Integer.parseInt(detalleComposicionJSP.getPageTotalMateriales()) - 1;
		}

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMaterialesComposicion())) {
			pageTotalMaterialesComposicion = Integer.parseInt(detalleComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}

		generarListasMaterial(detalleComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("info", " Se ha añadido correctamente");
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	@RequestMapping(value = "/eliminarMaterialComposicionDetalleComposicion", method = RequestMethod.POST)
	public String eliminarMaterialComposicion(DetalleComposicionJSP detalleComposicionJSP, Model model) {

		try {
			crearComposicionService.eliminarMaterialComposicion(Arrays.asList(detalleComposicionJSP.getIdComposicion()),
					Integer.parseInt(detalleComposicionJSP.getIdMaterial()));
		} catch (EseException e) {
			log.error(e.getMessage());
		}

		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMateriales())) {
			pageTotalMateriales = Integer.parseInt(detalleComposicionJSP.getPageTotalMateriales()) - 1;

		}

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMaterialesComposicion())) {
			Long elementosTotales = materialDAO
					.countByIdComposicion(Arrays.asList(detalleComposicionJSP.getIdComposicion()));

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(detalleComposicionJSP.getPageTotalMaterialesComposicion());
			if (totalPaginas < pageJSP) {
				detalleComposicionJSP.setPageTotalMaterialesComposicion(String.valueOf(pageJSP - 1));
			}
			pageTotalMaterialesComposicion = Integer.parseInt(detalleComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}

		generarListasMaterial(detalleComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("info", "Se ha eliminado correctamente");
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	@RequestMapping(value = "/paginacionTablaDetalleComposicionTotalMateriales", method = RequestMethod.POST)
	public String paginacionTablaDetalleComposicionTotalMateriales(DetalleComposicionJSP detalleComposicionJSP,
			Model model, HttpSession session) {
		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMateriales())) {
			detalleComposicionJSP.setPageTotalMateriales(detalleComposicionJSP.getPageTotalMateriales().substring(
					detalleComposicionJSP.getPageTotalMateriales().indexOf(",") + 1,
					detalleComposicionJSP.getPageTotalMateriales().length()));
			pageTotalMateriales = Integer.parseInt(detalleComposicionJSP.getPageTotalMateriales()) - 1;
		}

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMaterialesComposicion())) {
			pageTotalMaterialesComposicion = Integer.parseInt(detalleComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}

		generarListasMaterial(detalleComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	@RequestMapping(value = "/paginacionTablaDetalleComposicionTotalMaterialesComposicion", method = RequestMethod.POST)
	public String paginacionTablaDetalleComposicionTotalMaterialesComposicion(
			DetalleComposicionJSP detalleComposicionJSP, Model model, HttpSession session) {
		int pageTotalMateriales = 0; // default page number is 0 (yes it is weird)
		int pageTotalMaterialesComposicion = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMaterialesComposicion())) {
			detalleComposicionJSP
					.setPageTotalMaterialesComposicion(detalleComposicionJSP.getPageTotalMaterialesComposicion()
							.substring(detalleComposicionJSP.getPageTotalMaterialesComposicion().indexOf(",") + 1,
									detalleComposicionJSP.getPageTotalMaterialesComposicion().length()));
			pageTotalMaterialesComposicion = Integer.parseInt(detalleComposicionJSP.getPageTotalMaterialesComposicion())
					- 1;
		}

		if (StringUtils.isNotBlank(detalleComposicionJSP.getPageTotalMateriales())) {
			pageTotalMateriales = Integer.parseInt(detalleComposicionJSP.getPageTotalMateriales()) - 1;
		}

		generarListasMaterial(detalleComposicionJSP, model, pageTotalMateriales, pageTotalMaterialesComposicion, size);
		model.addAttribute("detalleComposicion", detalleComposicionJSP);
		return "detalleComposicion";
	}

	@RequestMapping(value = "/volverCrearComposicionDetalleComposicion", method = RequestMethod.POST)
	public String volverCrearComposicion(DetalleComposicionJSP detalleComposicionJSP, Model model) {

		if (detalleComposicionJSP.isVieneCrearTren()) {
			return "forward:/volverDetalleTren";
		} else {
			return "redirect:/buscadorComposicion";
		}

	}

	private void generarListasMaterial(DetalleComposicionJSP detalleComposicionJSP, Model model,
			int pageTotalMateriales, int pageTotalMaterialesComposicion, int size) {

		model.addAttribute("totalMaterialesComposicion",
				materialDAO.findByIdComposicion(Arrays.asList(detalleComposicionJSP.getIdComposicion()),
						PageRequest.of(pageTotalMaterialesComposicion, size)));
		model.addAttribute("totalMateriales", materialDAO.findByNotIdComposicion(
				Arrays.asList(detalleComposicionJSP.getIdComposicion()), PageRequest.of(pageTotalMateriales, size)));

	}
}