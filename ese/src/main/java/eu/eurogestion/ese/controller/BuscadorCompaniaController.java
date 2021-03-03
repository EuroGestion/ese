package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.TipoCompania;
import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.TipoCompaniaDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorCompaniaController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public TipoCompaniaDAO tipoCompaniaDAO;

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("tiposCompania")
	public List<TipoCompania> listTipoCompania() {
		List<TipoCompania> lista = new ArrayList<>();
		TipoCompania tipoCompania = new TipoCompania();
		tipoCompania.setValor("Selecciona uno:");
		lista.add(tipoCompania);
		lista.addAll(tipoCompaniaDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorCompania", method = RequestMethod.GET)
	public String buscadorCompania(Model model) {
		model.addAttribute("buscadorCompania", new BuscadorCompaniaJSP());
		cargarListaValores(new BuscadorCompaniaJSP(), model, 0, 10);
		return "buscadorCompania";
	}

	@RequestMapping(value = "/buscadorCompaniaRegisterCompania", method = RequestMethod.GET)
	public String buscadorCompaniaRegisterCompania(Model model) {
		model.addAttribute("buscadorCompania", new BuscadorCompaniaJSP());
		cargarListaValores(new BuscadorCompaniaJSP(), model, 0, 10);
		model.addAttribute("info", " Se ha creado correctamente");
		return "buscadorCompania";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorCompaniaJSP objeto con los campos de filtro de la pantalla
	 * @param model               objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorCompania", method = RequestMethod.POST)
	public String filtrarBuscadorModificarRevision(BuscadorCompaniaJSP buscadorCompaniaJSP, Model model) {

		model.addAttribute("buscadorCompania", buscadorCompaniaJSP);
		cargarListaValores(buscadorCompaniaJSP, model, 0, 10);
		return "buscadorCompania";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorCompania", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorModificarRevision(BuscadorCompaniaJSP buscadorCompaniaJSP, Model model) {
		model.addAttribute("buscadorCompania", new BuscadorCompaniaJSP());
		cargarListaValores(new BuscadorCompaniaJSP(), model, 0, 10);
		return "buscadorCompania";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorCompania", method = RequestMethod.POST)
	public String paginacionTablaBuscadorCompania(BuscadorCompaniaJSP buscadorCompaniaJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorCompaniaJSP.getPage())) {
			buscadorCompaniaJSP.setPage(buscadorCompaniaJSP.getPage().substring(
					buscadorCompaniaJSP.getPage().indexOf(",") + 1, buscadorCompaniaJSP.getPage().length()));
			page = Integer.parseInt(buscadorCompaniaJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorCompania", buscadorCompaniaJSP);
		cargarListaValores(buscadorCompaniaJSP, model, page, size);
		return "buscadorCompania";
	}

	@RequestMapping(value = "/ordenTablaBuscadorCompania", method = RequestMethod.POST)
	public String ordenTablaBuscadorCompania(BuscadorCompaniaJSP buscadorCompaniaJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorCompaniaJSP.getPage())) {
			page = Integer.parseInt(buscadorCompaniaJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorCompania", buscadorCompaniaJSP);
		cargarListaValores(buscadorCompaniaJSP, model, page, size);
		return "buscadorCompania";
	}

	private void cargarListaValores(BuscadorCompaniaJSP buscadorCompaniaJSP, Model model, int page, int size) {

		Order order = Order.desc("idCompania");
		if (StringUtils.isNotBlank(buscadorCompaniaJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorCompaniaJSP.getDireccion())) {

			if ("asc".equals(buscadorCompaniaJSP.getDireccion())) {
				order = Order.asc(buscadorCompaniaJSP.getOrder());
			} else {
				order = Order.desc(buscadorCompaniaJSP.getOrder());
			}

		}

		Page<Compania> result = companiaDAO.findAllCompaniaAltaByFilters(buscadorCompaniaJSP.getDocumento(),
				buscadorCompaniaJSP.getNombre(), buscadorCompaniaJSP.getIdTipoCompania(),
				PageRequest.of(page, size, Sort.by(order)));
		model.addAttribute("companias", result);
	}

}