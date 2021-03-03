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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.EstadoPasf;
import eu.eurogestion.ese.domain.Pasf;
import eu.eurogestion.ese.pojo.BuscadorPasfJSP;
import eu.eurogestion.ese.repository.EstadoPasfDAO;
import eu.eurogestion.ese.repository.PasfDAO;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorPasfController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public PasfDAO pasfDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public EstadoPasfDAO estadoPasfDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("estadosPasf")
	public List<EstadoPasf> listEstadoPlan() {
		List<EstadoPasf> lista = new ArrayList<>();
		EstadoPasf estadoPasf = new EstadoPasf();
		estadoPasf.setValor("Selecciona uno:");
		lista.add(estadoPasf);
		lista.addAll(estadoPasfDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorPasf", method = RequestMethod.GET)
	public String buscadorPasf(Model model) {
		model.addAttribute("buscadorPasf", new BuscadorPasfJSP());
		cargarLista(new BuscadorPasfJSP(), model, 0, 10);
		return "buscadorPasf";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorHomologacionJSP objeto con los campos de filtro de la pantalla
	 * @param model                   objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorPasf", method = RequestMethod.POST)
	public String filtrarBuscadorPasf(BuscadorPasfJSP buscadorPasfJSP, Model model) {

		model.addAttribute("buscadorPasf", buscadorPasfJSP);
		cargarLista(buscadorPasfJSP, model, 0, 10);
		return "buscadorPasf";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorPasf", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorPasf(BuscadorPasfJSP buscadorPasfJSP, Model model) {
		model.addAttribute("buscadorPasf", new BuscadorPasfJSP());
		cargarLista(new BuscadorPasfJSP(), model, 0, 10);
		return "buscadorPasf";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorPASF", method = RequestMethod.POST)
	public String paginacionTablaBuscadorPASF(BuscadorPasfJSP buscadorPasfJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorPasfJSP.getPage())) {
			buscadorPasfJSP.setPage(buscadorPasfJSP.getPage().substring(buscadorPasfJSP.getPage().indexOf(",") + 1,
					buscadorPasfJSP.getPage().length()));
			page = Integer.parseInt(buscadorPasfJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorPasf", buscadorPasfJSP);
		cargarLista(buscadorPasfJSP, model, page, size);
		return "buscadorPasf";
	}

	@RequestMapping(value = "/ordenTablaBuscadorPasf", method = RequestMethod.POST)
	public String ordenTablaBuscadorPasf(BuscadorPasfJSP buscadorPasfJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorPasfJSP.getPage())) {
			page = Integer.parseInt(buscadorPasfJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorPasf", buscadorPasfJSP);
		cargarLista(buscadorPasfJSP, model, page, size);
		return "buscadorPasf";
	}

	private void cargarLista(BuscadorPasfJSP buscadorPasfJSP, Model model, int page, int size) {

		Order order = Order.desc("idPasf");
		if (StringUtils.isNotBlank(buscadorPasfJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorPasfJSP.getDireccion())) {

			if ("asc".equals(buscadorPasfJSP.getDireccion())) {
				order = Order.asc(buscadorPasfJSP.getOrder());
			} else {
				order = Order.desc(buscadorPasfJSP.getOrder());
			}

		}
		model.addAttribute("pasfs", pasfDAO.findPasfByFilters(buscadorPasfJSP.getAnno(), buscadorPasfJSP.getIdEstado(),
				PageRequest.of(page, size, Sort.by(order))));
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/descargarPasf", method = RequestMethod.POST)
	public void descargarPasf(BuscadorPasfJSP buscadorPasfJSP, Model model, HttpServletResponse response) {

		try {
			Pasf pasf = pasfDAO.getOne(Integer.valueOf(buscadorPasfJSP.getIdPasf()));
			Documento documento = pasf.getEvidencia().getDocumento();

			utilesPDFService.descargarEvidencia(documento, response);

		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

}