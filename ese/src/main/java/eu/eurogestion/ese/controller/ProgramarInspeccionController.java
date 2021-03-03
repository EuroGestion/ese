package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoInspeccion;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.pojo.ProgramarInspeccionJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoInspeccionDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.ProgramarInspeccionService;
import eu.eurogestion.ese.services.ProgramarRevisionService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class ProgramarInspeccionController {

	/**
	 * Repositorio de la clase de dominio TipoInspeccion
	 */
	@Autowired
	public TipoInspeccionDAO tipoInspeccionDAO;

	/**
	 * Repositorio de la clase de dominio Tren
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Servicio de la programacion de la formacion
	 */
	@Autowired
	public ProgramarRevisionService programarRevisionService;

	@Autowired
	public ProgramarInspeccionService programarInspeccionService;

	/**
	 * Metodo que devuelve una lista de TipoInspeccion para un select
	 * 
	 * @return lista de objetos TipoInspeccion
	 */
	@ModelAttribute("tipoInspecciones")
	public List<TipoInspeccion> listTipoInspeccionAll() {
		List<TipoInspeccion> lista = new ArrayList<>();
		TipoInspeccion tipoInspeccion = new TipoInspeccion();
		tipoInspeccion.setValor("Selecciona uno:");
		lista.add(tipoInspeccion);
		lista.addAll(tipoInspeccionDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Tren para un select
	 * 
	 * @return lista de objetos Tren
	 */
	@ModelAttribute("trenes")
	public List<Tren> listTrenAll() {
		List<Tren> lista = new ArrayList<>();
		Tren tipoInspeccion = new Tren();
		tipoInspeccion.setNumero("Selecciona uno:");
		lista.add(tipoInspeccion);
		lista.addAll(trenDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Personal para un select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("inspectores")
	public List<Personal> listInspectoresAll() {
		List<Personal> lista = new ArrayList<>();
		Personal tipoInspeccion = new Personal();
		tipoInspeccion.setNombre("Selecciona uno:");
		lista.add(tipoInspeccion);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de programar inspeccion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/programarInspeccion", method = RequestMethod.GET)
	public String programarInspeccion(Model model) {
		model.addAttribute("programarInspeccion", new ProgramarInspeccionJSP());

		return "programarInspeccion";
	}

	/**
	 * Metodo que inicializa el formulario de programar inspeccion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/aceptarProgramarInspeccion", method = RequestMethod.POST)
	public String aceptarProgramarInspeccion(ProgramarInspeccionJSP programarInspeccion, Model model,
			HttpSession session) {

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) session.getAttribute("usuario");

		programarInspeccion.setIdCreador(usuarioRegistrado.getIdPersonal());

		try {
			programarInspeccionService.registroInspeccionYTarea(programarInspeccion, session);
		} catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("programarInspeccion", programarInspeccion);
			return "programarInspeccion";
		}

		return "redirect:/buscadorInspeccionProgramarInspeccion";
	}

	/**
	 * Metodo que inicializa el formulario de programar inspeccion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/recargarProgramarInspeccion", method = RequestMethod.POST)
	public String recargarProgramarInspeccion(ProgramarInspeccionJSP programarInspeccion, Model model) {
		model.addAttribute("programarInspeccion", programarInspeccion);

		return "programarInspeccion";
	}

}