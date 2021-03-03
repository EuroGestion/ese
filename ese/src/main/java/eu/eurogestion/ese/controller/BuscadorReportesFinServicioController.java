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

import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.pojo.BuscadorReportesFinServicioJSP;
import eu.eurogestion.ese.repository.HistoricoMaquinistaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class BuscadorReportesFinServicioController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public HistoricoMaquinistaDAO historicoMaquinistaDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Metodo que devuelve una lista de maquinistas para una tabla
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("maquinistas")
	public List<Personal> listMaquinistasAll() {
		List<Personal> lista = new ArrayList<>();
		Personal personal = new Personal();
		personal.setNombre("Selecciona uno:");
		lista.add(personal);
		lista.addAll(personalDAO.findAllMaquinistas());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de busqueda para la modificacion de la
	 * formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/buscadorReportesFinServicio", method = RequestMethod.GET)
	public String buscadorCompania(Model model) {
		model.addAttribute("buscadorReportesFinServicio", new BuscadorReportesFinServicioJSP());
		cargarListaValores(new BuscadorReportesFinServicioJSP(), model, 0, 10);
		return "buscadorReportesFinServicio";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorTrenJSP objeto con los campos de filtro de la pantalla
	 * @param model           objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/filtrarBuscadorReportesFinServicio", method = RequestMethod.POST)
	public String filtrarBuscadorReportesFinServicio(BuscadorReportesFinServicioJSP buscadorReportesFinServicioJSP,
			Model model) {

		model.addAttribute("buscadorReportesFinServicio", buscadorReportesFinServicioJSP);
		cargarListaValores(buscadorReportesFinServicioJSP, model, 0, 10);
		return "buscadorReportesFinServicio";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/borrarFiltrosBuscadorReportesFinServicio", method = RequestMethod.POST)
	public String borrarFiltrosBuscadorReportesFinServicio(
			BuscadorReportesFinServicioJSP buscadorReportesFinServicioJSP, Model model) {
		model.addAttribute("buscadorReportesFinServicio", new BuscadorReportesFinServicioJSP());
		cargarListaValores(new BuscadorReportesFinServicioJSP(), model, 0, 10);
		return "buscadorReportesFinServicio";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorReportesFinServicio", method = RequestMethod.POST)
	public String paginacionTablaBuscadorReportesFinServicio(
			BuscadorReportesFinServicioJSP buscadorReportesFinServicioJSP, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorReportesFinServicioJSP.getPage())) {
			buscadorReportesFinServicioJSP.setPage(buscadorReportesFinServicioJSP.getPage().substring(
					buscadorReportesFinServicioJSP.getPage().indexOf(",") + 1,
					buscadorReportesFinServicioJSP.getPage().length()));
			page = Integer.parseInt(buscadorReportesFinServicioJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorReportesFinServicio", buscadorReportesFinServicioJSP);
		cargarListaValores(buscadorReportesFinServicioJSP, model, page, size);
		return "buscadorReportesFinServicio";
	}
	
	@RequestMapping(value = "/ordenTablaBuscadorReportesFinServicio", method = RequestMethod.POST)
	public String ordenTablaBuscadorReportesFinServicio(BuscadorReportesFinServicioJSP buscadorReportesFinServicioJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(buscadorReportesFinServicioJSP.getPage())) {
			page = Integer.parseInt(buscadorReportesFinServicioJSP.getPage()) - 1;
		}

		model.addAttribute("buscadorReportesFinServicio", buscadorReportesFinServicioJSP);
		cargarListaValores(buscadorReportesFinServicioJSP, model, page, size);
		return "buscadorReportesFinServicio";
	}

	private void cargarListaValores(BuscadorReportesFinServicioJSP buscadorReportesFinServicioJSP, Model model,
			int page, int size) {

		if (StringUtils.isNotBlank(buscadorReportesFinServicioJSP.getOrder())
				&& "nombreCompleto".equals(buscadorReportesFinServicioJSP.getOrder())) {
			Direction direccion = Direction.DESC;
			if ("asc".equals(buscadorReportesFinServicioJSP.getDireccion())) {
				direccion = Direction.ASC;
			}

			model.addAttribute("reportes",
					historicoMaquinistaDAO.findAllReportesFinServicioByFilters(
							buscadorReportesFinServicioJSP.getIdMaquinista(),
							buscadorReportesFinServicioJSP.getNumeroTren(), buscadorReportesFinServicioJSP.getFecha(),
							PageRequest.of(page, size, direccion,  "personal.apellido1", "personal.nombre")));
			return;
		}

		Order order = Order.desc("idHistoricoMaquinista");
		if (StringUtils.isNotBlank(buscadorReportesFinServicioJSP.getOrder())
				&& StringUtils.isNotBlank(buscadorReportesFinServicioJSP.getDireccion())) {

			if ("asc".equals(buscadorReportesFinServicioJSP.getDireccion())) {
				order = Order.asc(buscadorReportesFinServicioJSP.getOrder());
			} else {
				order = Order.desc(buscadorReportesFinServicioJSP.getOrder());
			}

		}

		model.addAttribute("reportes", historicoMaquinistaDAO.findAllReportesFinServicioByFilters(
				buscadorReportesFinServicioJSP.getIdMaquinista(), buscadorReportesFinServicioJSP.getNumeroTren(),
				buscadorReportesFinServicioJSP.getFecha(), PageRequest.of(page, size, Sort.by(order))));

	}

}