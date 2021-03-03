package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.ModeloMaterial;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorMaterialJSP;
import eu.eurogestion.ese.repository.MaterialDAO;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;
import eu.eurogestion.ese.repository.TipoMaterialDAO;
import eu.eurogestion.ese.services.MaterialService;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rmerino
 *
 */
@Slf4j
@Controller
public class BuscadorMaterialController {

	@Autowired
	public TipoMaterialDAO tipoMaterialDAO;

	@Autowired
	public ModeloMaterialDAO modeloMaterialDAO;

	@Autowired
	public MaterialDAO materialDAO;

	@Autowired
	public MaterialService materialService;

	@ModelAttribute("modelosMaterial")
	public List<ModeloMaterial> listModeloMaterialAll() {
		List<ModeloMaterial> lista = new ArrayList<>();
		ModeloMaterial modeloMaterial = new ModeloMaterial();
		modeloMaterial.setSerie("Selecciona uno:");
		modeloMaterial.setSubserie("");
		lista.add(modeloMaterial);
		lista.addAll(modeloMaterialDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorMaterial", method = RequestMethod.GET)
	public String buscadorMaterial(Model model) {

		model.addAttribute("buscadorMaterial", new BuscadorMaterialJSP());
		cargarLista(new BuscadorMaterialJSP(), model, 0, 10);
		return "buscadorMaterial";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorMaterialJSP objeto con los campos de filtro de la pantalla
	 * @param model               objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorMaterial", method = RequestMethod.POST)
	public String filtrarBuscadorMaterial(BuscadorMaterialJSP buscadorMaterialJSP, Model model) {

		cargarLista(buscadorMaterialJSP, model, 0, 10);
		model.addAttribute("buscadorMaterial", buscadorMaterialJSP);
		return "buscadorMaterial";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorMaterialJSP objeto con los campos de filtro de la pantalla
	 * @param model               objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorMaterial", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorMaterial(Model model) {
		model.addAttribute("buscadorMaterial", new BuscadorMaterialJSP());
		cargarLista(new BuscadorMaterialJSP(), model, 0, 10);
		return "buscadorMaterial";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorMaterialJSP objeto con los campos de filtro de la pantalla
	 * @param model               objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/bajaMaterial", method = RequestMethod.POST)
	public String bajaMaterial(BuscadorMaterialJSP buscadorMaterialJSP, Model model) {

		try {
			materialService.bajaMaterial(Integer.parseInt(buscadorMaterialJSP.getIdMaterial()));
		} catch (NumberFormatException | EseException e) {
			log.error(e.getMessage());
		}

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorMaterialJSP.getPage())) {
			Long elementosTotales = materialDAO.countByFilters(buscadorMaterialJSP.getIdModeloMaterial(),
					buscadorMaterialJSP.getNve());

			Long totalPaginas = Utiles.obtenerPaginasTotales(elementosTotales, size);

			Integer pageJSP = Integer.parseInt(buscadorMaterialJSP.getPage());
			if (totalPaginas < pageJSP) {
				buscadorMaterialJSP.setPage(String.valueOf(pageJSP - 1));
			}
			page = Integer.parseInt(buscadorMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorMaterial", buscadorMaterialJSP);
		model.addAttribute("info", "Se ha dado de baja correctamente");
		cargarLista(buscadorMaterialJSP, model, page, size);
		return "buscadorMaterial";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorMaterial", method = RequestMethod.POST)
	public String paginacionTablaBuscadorMaterial(BuscadorMaterialJSP buscadorMaterialJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorMaterialJSP.getPage())) {
			buscadorMaterialJSP.setPage(buscadorMaterialJSP.getPage()
					.substring(buscadorMaterialJSP.getPage().indexOf(",") + 1, buscadorMaterialJSP.getPage().length()));
			page = Integer.parseInt(buscadorMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorMaterial", buscadorMaterialJSP);
		cargarLista(buscadorMaterialJSP, model, page, size);
		return "buscadorMaterial";
	}

	@RequestMapping(value = "/ordenTablaBuscadorMaterial", method = RequestMethod.POST)
	public String ordenTablaBuscadorMaterial(BuscadorMaterialJSP buscadorMaterialJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorMaterialJSP.getPage())) {
			page = Integer.parseInt(buscadorMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorMaterial", buscadorMaterialJSP);
		cargarLista(buscadorMaterialJSP, model, page, size);
		return "buscadorMaterial";
	}

	private void cargarLista(BuscadorMaterialJSP buscadorMaterialJSP, Model model, int page, int size) {

		if (StringUtils.isNotBlank(buscadorMaterialJSP.getOrder())
				&& "modeloMaterial".equals(buscadorMaterialJSP.getOrder())) {
			Direction direccion = Direction.DESC;
			if ("asc".equals(buscadorMaterialJSP.getDireccion())) {
				direccion = Direction.ASC;
			}

			model.addAttribute("materiales",
					materialDAO.findByFilters(buscadorMaterialJSP.getIdModeloMaterial(), buscadorMaterialJSP.getNve(),
							PageRequest.of(page, size, direccion, "modeloMaterial.serie", "modeloMaterial.subserie")));
			return;
		}
		Order order = Order.desc("idMaterial");
		if (StringUtils.isNotBlank(buscadorMaterialJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorMaterialJSP.getDireccion())) {

			if ("asc".equals(buscadorMaterialJSP.getDireccion())) {
				order = Order.asc(buscadorMaterialJSP.getOrder());
			} else {
				order = Order.desc(buscadorMaterialJSP.getOrder());
			}

		}

		model.addAttribute("materiales", materialDAO.findByFilters(buscadorMaterialJSP.getIdModeloMaterial(),
				buscadorMaterialJSP.getNve(), PageRequest.of(page, size, Sort.by(order))));
	}
}