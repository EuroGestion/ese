package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.TipoMaterial;
import eu.eurogestion.ese.pojo.BuscadorModeloMaterialJSP;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;
import eu.eurogestion.ese.repository.TipoMaterialDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rmerino
 *
 */
@Slf4j
@Controller
public class BuscadorModeloMaterialController {

	@Autowired
	public TipoMaterialDAO tipoMaterialDAO;

	@Autowired
	public ModeloMaterialDAO modeloMaterialDAO;

	@ModelAttribute("tiposMaterial")
	public List<TipoMaterial> listTipoMaterialAll() {
		List<TipoMaterial> lista = new ArrayList<>();
		TipoMaterial tipoMaterial = new TipoMaterial();
		tipoMaterial.setValor("Selecciona uno:");
		lista.add(tipoMaterial);
		lista.addAll(tipoMaterialDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorModeloMaterial", method = RequestMethod.GET)
	public String buscadorModeloMaterial(Model model) {

		model.addAttribute("buscadorModeloMaterial", new BuscadorModeloMaterialJSP());
		cargarLista(new BuscadorModeloMaterialJSP(), model, 0, 10);
		return "buscadorModeloMaterial";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorModeloMaterialJSP objeto con los campos de filtro de la
	 *                                  pantalla
	 * @param model                     objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorModeloMaterial", method = RequestMethod.POST)
	public String filtrarBuscadorModeloMaterial(BuscadorModeloMaterialJSP buscadorModeloMaterialJSP, Model model) {

		model.addAttribute("buscadorModeloMaterial", buscadorModeloMaterialJSP);
		cargarLista(buscadorModeloMaterialJSP, model, 0, 10);
		return "buscadorModeloMaterial";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorMaterialJSP objeto con los campos de filtro de la pantalla
	 * @param model               objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorModeloMaterial", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorModeloMaterial(Model model) {
		model.addAttribute("buscadorModeloMaterial", new BuscadorModeloMaterialJSP());
		cargarLista(new BuscadorModeloMaterialJSP(), model, 0, 10);
		return "buscadorModeloMaterial";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorModeloMaterial", method = RequestMethod.POST)
	public String paginacionTablaBuscadorModeloMaterial(BuscadorModeloMaterialJSP buscadorModeloMaterialJSP,
			Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorModeloMaterialJSP.getPage())) {
			buscadorModeloMaterialJSP.setPage(buscadorModeloMaterialJSP.getPage().substring(
					buscadorModeloMaterialJSP.getPage().indexOf(",") + 1, buscadorModeloMaterialJSP.getPage().length()));
			page = Integer.parseInt(buscadorModeloMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorModeloMaterial", buscadorModeloMaterialJSP);
		cargarLista(buscadorModeloMaterialJSP, model, page, size);
		return "buscadorModeloMaterial";
	}
	
	@RequestMapping(value = "/ordenTablaBuscadorModeloMaterial", method = RequestMethod.POST)
	public String ordenTablaBuscadorModeloMaterial(BuscadorModeloMaterialJSP buscadorModeloMaterialJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorModeloMaterialJSP.getPage())) {
			page = Integer.parseInt(buscadorModeloMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorModeloMaterial", buscadorModeloMaterialJSP);
		cargarLista(buscadorModeloMaterialJSP, model, page, size);
		return "buscadorModeloMaterial";
	}

	private void cargarLista(BuscadorModeloMaterialJSP buscadorModeloMaterialJSP, Model model, int page, int size) {

		Order order = Order.desc("idModeloMaterial");
		if (StringUtils.isNotBlank(buscadorModeloMaterialJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorModeloMaterialJSP.getDireccion())) {

			if ("asc".equals(buscadorModeloMaterialJSP.getDireccion())) {
				order = Order.asc(buscadorModeloMaterialJSP.getOrder());
			} else {
				order = Order.desc(buscadorModeloMaterialJSP.getOrder());
			}

		}
		model.addAttribute("modelosMaterial",
				modeloMaterialDAO.findByFilters(buscadorModeloMaterialJSP.getIdTipoMaterial(),
						buscadorModeloMaterialJSP.getSerie(), buscadorModeloMaterialJSP.getSubserie(),
						PageRequest.of(page, size, Sort.by(order))));
	}
}