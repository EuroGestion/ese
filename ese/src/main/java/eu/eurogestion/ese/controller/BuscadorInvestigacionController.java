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

import eu.eurogestion.ese.domain.CausaAccidente;
import eu.eurogestion.ese.domain.TipoAccidente;
import eu.eurogestion.ese.pojo.BuscadorInvestigacionJSP;
import eu.eurogestion.ese.repository.AccidenteDAO;
import eu.eurogestion.ese.repository.CausaAccidenteDAO;
import eu.eurogestion.ese.repository.TipoAccidenteDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorInvestigacionController {

	/**
	 * Repositorio de la clase de dominio ViewInspeccion
	 */
	@Autowired
	public AccidenteDAO accidenteDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public TipoAccidenteDAO tipoAccidenteDAO;

	/**
	 * Repositorio de la clase de dominio TipoInspeccion
	 */
	@Autowired
	public CausaAccidenteDAO causaAccidenteDAO;

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("tiposInvestigacion")
	public List<TipoAccidente> listPersonalAll() {
		List<TipoAccidente> lista = new ArrayList<>();
		TipoAccidente tipoAccidente = new TipoAccidente();
		tipoAccidente.setValor("Selecciona uno:");
		lista.add(tipoAccidente);
		lista.addAll(tipoAccidenteDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("causas")
	public List<CausaAccidente> listTipoInspeccionAll() {
		List<CausaAccidente> lista = new ArrayList<>();
		CausaAccidente causaAccidente = new CausaAccidente();
		causaAccidente.setValor("Selecciona uno:");
		lista.add(causaAccidente);
		lista.addAll(causaAccidenteDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorInvestigacion", method = RequestMethod.GET)
	public String buscadorInvestigacion(Model model) {

		model.addAttribute("buscadorInvestigacion", new BuscadorInvestigacionJSP());
		cargarListaValores(new BuscadorInvestigacionJSP(), model, 0, 10);
		return "buscadorInvestigacion";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorInvestigacion", method = RequestMethod.POST)
	public String filtrarBuscadorInvestigacion(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model) {

		model.addAttribute("buscadorInvestigacion", buscadorInvestigacionJSP);
		cargarListaValores(buscadorInvestigacionJSP, model, 0, 10);
		return "buscadorInvestigacion";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorInvestigacion", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorInvestigacion(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model) {

		model.addAttribute("buscadorInvestigacion", new BuscadorInvestigacionJSP());
		cargarListaValores(new BuscadorInvestigacionJSP(), model, 0, 10);
		return "buscadorInvestigacion";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorInvestigacion", method = RequestMethod.POST)
	public String paginacionTablaBuscadorInvestigacion(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorInvestigacionJSP.getPage())) {
			buscadorInvestigacionJSP.setPage(buscadorInvestigacionJSP.getPage().substring(
					buscadorInvestigacionJSP.getPage().indexOf(",") + 1, buscadorInvestigacionJSP.getPage().length()));
			page = Integer.parseInt(buscadorInvestigacionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorInvestigacion", buscadorInvestigacionJSP);
		cargarListaValores(buscadorInvestigacionJSP, model, page, size);
		return "buscadorInvestigacion";
	}

	@RequestMapping(value = "/ordenTablaBuscadorInvestigacion", method = RequestMethod.POST)
	public String ordenTablaBuscadorInvestigacion(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorInvestigacionJSP.getPage())) {
			page = Integer.parseInt(buscadorInvestigacionJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorInvestigacion", buscadorInvestigacionJSP);
		cargarListaValores(buscadorInvestigacionJSP, model, page, size);
		return "buscadorInvestigacion";
	}

	private void cargarListaValores(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model, int page,
			int size) {

		Order order = Order.desc("idAccidente");
		if (StringUtils.isNotBlank(buscadorInvestigacionJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorInvestigacionJSP.getDireccion())) {

			if ("asc".equals(buscadorInvestigacionJSP.getDireccion())) {
				order = Order.asc(buscadorInvestigacionJSP.getOrder());
			} else {
				order = Order.desc(buscadorInvestigacionJSP.getOrder());
			}

		}

		model.addAttribute("investigaciones",
				accidenteDAO.findAccidenteByFilters(buscadorInvestigacionJSP.getIdTipoInvestigacion(),
						buscadorInvestigacionJSP.getIdCausa(), buscadorInvestigacionJSP.getNIdentificacion(),
						buscadorInvestigacionJSP.getFecha(), buscadorInvestigacionJSP.getLugar(),
						PageRequest.of(page, size, Sort.by(order))));

	}

}