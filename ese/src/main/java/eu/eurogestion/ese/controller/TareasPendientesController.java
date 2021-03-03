package eu.eurogestion.ese.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.TareasPendientesJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class TareasPendientesController {

	/**
	 * Repositorio de la clase de dominio TareaPendiente
	 */
	@Autowired
	public TareaPendienteDAO tareaPendienteDAO;

	@RequestMapping(value = "/tareasPendientes", method = RequestMethod.GET)
	public String login(Model model, HttpSession session) {

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) session.getAttribute("usuario");
		TareasPendientesJSP tareasPendientesJSP = new TareasPendientesJSP();
		tareasPendientesJSP.setIdUsuario(usuarioRegistrado.getIdPersonal());
		model.addAttribute("tareasPendientes", tareasPendientesJSP);
		cargarLista(tareasPendientesJSP, model, 0, 10);
		return "tareasPendientes";
	}

	@RequestMapping(value = "/paginacionTablaBuscadorTereasPendientes", method = RequestMethod.POST)
	public String paginacionTablaBuscadorTereasPendientes(TareasPendientesJSP tareasPendientesJSP, Model model,
			HttpSession session) {

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10;
		if (StringUtils.isNotBlank(tareasPendientesJSP.getPage())) {
			tareasPendientesJSP.setPage(tareasPendientesJSP.getPage()
					.substring(tareasPendientesJSP.getPage().indexOf(",") + 1, tareasPendientesJSP.getPage().length()));
			page = Integer.parseInt(tareasPendientesJSP.getPage()) - 1;
		}
		cargarLista(tareasPendientesJSP, model, page, size);

		model.addAttribute("tareasPendientes", tareasPendientesJSP);
		return "tareasPendientes";
	}

	@RequestMapping(value = "/ordenTablaTareasPendientes", method = RequestMethod.POST)
	public String ordenTablaTareasPendientes(TareasPendientesJSP tareasPendientesJSP, Model model,
			HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 10; // default page size is 10

		if (StringUtils.isNotBlank(tareasPendientesJSP.getPage())) {
			page = Integer.parseInt(tareasPendientesJSP.getPage()) - 1;
		}

		model.addAttribute("tareasPendientes", tareasPendientesJSP);
		cargarLista(tareasPendientesJSP, model, page, size);
		return "tareasPendientes";
	}

	
	/**
	 * Metodo que inicializa el formulario de busqueda para la visualizacion de
	 * tareas pendientes
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */

	private void cargarLista(TareasPendientesJSP tareasPendientesJSP, Model model, int page, int size) {

		if (StringUtils.isNotBlank(tareasPendientesJSP.getOrder())
				&& "de".equals(tareasPendientesJSP.getOrder())) {
			Direction direccion = Direction.DESC;
			if ("asc".equals(tareasPendientesJSP.getDireccion())) {
				direccion = Direction.ASC;
			}

			model.addAttribute("tareas",
					tareaPendienteDAO.findTareaPendienteByIdDestinatario(
							Integer.valueOf(tareasPendientesJSP.getIdUsuario()), PageRequest.of(page, size, direccion,
									"origen.apellido1", "origen.apellido2", "origen.nombre", "origen.cargo.nombre")));
			return;
		}

		Order order = Order.desc("idTareaPendiente");
		if (StringUtils.isNotBlank(tareasPendientesJSP.getOrder())
				&& StringUtils.isNotBlank(tareasPendientesJSP.getDireccion())) {

			if ("asc".equals(tareasPendientesJSP.getDireccion())) {
				order = Order.asc(tareasPendientesJSP.getOrder());
			} else {
				order = Order.desc(tareasPendientesJSP.getOrder());
			}

		}
		model.addAttribute("tareas", tareaPendienteDAO.findTareaPendienteByIdDestinatario(
				Integer.valueOf(tareasPendientesJSP.getIdUsuario()), PageRequest.of(page, size, Sort.by(order))));
	}

}