package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Iseet;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.ResultadoInspeccion;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorInspeccionJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.GenerarInformeISEETJSP;
import eu.eurogestion.ese.pojo.TareasPendientesJSP;
import eu.eurogestion.ese.repository.IseetDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResultadoInspeccionDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.GenerarInformeISEETService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class GenerarInformeISEETController {

	/**
	 * Repositorio de la clase de dominio ResultadoInspeccion
	 */
	@Autowired
	public ResultadoInspeccionDAO resultadoInspeccionDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public IseetDAO iseetDAO;

	/**
	 * Servicio de la generacion del informe ISO
	 */
	@Autowired
	public GenerarInformeISEETService generarInformeISEETService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de Cursos para una tabla
	 * 
	 * @return lista de objetos Curso
	 */
	@ModelAttribute("maquinistas")
	public List<Personal> listMaquinistasAll() {
		List<Personal> lista = new ArrayList<>();
		Personal estadoVerificacionInforme = new Personal();
		estadoVerificacionInforme.setNombre("Seleccione uno:");
		lista.add(estadoVerificacionInforme);
		lista.addAll(
				personalDAO.findPersonalByFilters(null, Constantes.CARGO_PERSONAL_MAQUINISTA.toString(), null, null));
		return lista;
	}

	@ModelAttribute("trenes")
	public List<Tren> listTrenesAll() {
		List<Tren> lista = new ArrayList<>();
		Tren estadoVerificacionInforme = new Tren();
		estadoVerificacionInforme.setNumero("Seleccione uno:");
		lista.add(estadoVerificacionInforme);
		lista.addAll(trenDAO.findByFechaBajaIsNull());
		return lista;
	}

	@ModelAttribute("resultadoInspecciones")
	public List<ResultadoInspeccion> listVerificacionesAll() {
		List<ResultadoInspeccion> lista = new ArrayList<>();
		ResultadoInspeccion estadoVerificacionInforme = new ResultadoInspeccion();
		estadoVerificacionInforme.setValor("Seleccione uno:");
		lista.add(estadoVerificacionInforme);
		lista.addAll(resultadoInspeccionDAO.findAll());
		return lista;
	}

	@ModelAttribute("personal")
	public List<Personal> listPersonalAll() {
		List<Personal> lista = new ArrayList<>();
		Personal estadoVerificacionInforme = new Personal();
		estadoVerificacionInforme.setNombre("Seleccione uno:");
		lista.add(estadoVerificacionInforme);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verGenerarInformeISEETTareaPendiente", method = RequestMethod.POST)
	public String verGenerarInformeISEETTareaPendiente(TareasPendientesJSP tareasPendientesJSP, Model model) {

		GenerarInformeISEETJSP generarInformeISEETJSP = generarInformeISEETService
				.createGenerarInformeISEETJSPToIdISEET(Integer.parseInt(tareasPendientesJSP.getIdTarea()));
		generarInformeISEETJSP.setPagina("1");
		generarInformeISEETJSP.setPaginaVuelta("tareasPendientes");
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verInspeccionISEET", method = RequestMethod.POST)
	public String verInspeccionISEET(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISEETJSP generarInformeISEETJSP = generarInformeISEETService
				.createGenerarInformeISEETJSPToIdISEET(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISEETJSP.setPagina("1");
		generarInformeISEETJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISEETJSP.setIsLectura(Boolean.TRUE);
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	@RequestMapping(value = "/verInspeccionPersonalISEET", method = RequestMethod.POST)
	public String verInspeccionPersonalISEET(DetallePersonalJSP detallePersonalJSP, Model model) {

		GenerarInformeISEETJSP generarInformeISEETJSP = generarInformeISEETService
				.createGenerarInformeISEETJSPToIdISEET(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));
		generarInformeISEETJSP.setPagina("1");
		generarInformeISEETJSP.setPaginaVuelta("detallePersonal");
		generarInformeISEETJSP.setIsLectura(Boolean.TRUE);
		generarInformeISEETJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaGenerarInformeISEET", method = RequestMethod.POST)
	public void verEvidenciaGenerarInformeISEET(GenerarInformeISEETJSP generarInformeISEETJSP, Model model,
			HttpServletResponse response) {

		try {
			Iseet iseet = iseetDAO.getOne(Integer.parseInt(generarInformeISEETJSP.getIdISEET()));
			Documento documento = iseet.getEvidencia17().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaPlanificarInformeISEET", method = RequestMethod.POST)
	public void verEvidenciaPlanificarInformeISEET(GenerarInformeISEETJSP generarInformeISEETJSP, Model model,
			HttpServletResponse response) {

		try {
			Iseet iseet = iseetDAO.getOne(Integer.parseInt(generarInformeISEETJSP.getIdISEET()));
			Documento documento = iseet.getEvidencia15().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarInspeccionISEET", method = RequestMethod.POST)
	public String modificarInspeccionISEET(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISEETJSP generarInformeISEETJSP = generarInformeISEETService
				.createGenerarInformeISEETJSPToIdISEET(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISEETJSP.setPagina("1");
		generarInformeISEETJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISEETJSP.setIsLectura(Boolean.FALSE);
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	@RequestMapping(value = "/modificarInspeccionPersonalISEET", method = RequestMethod.POST)
	public String modificarInspeccionPersonalISEET(DetallePersonalJSP detallePersonalJSP, Model model) {

		GenerarInformeISEETJSP generarInformeISEETJSP = generarInformeISEETService
				.createGenerarInformeISEETJSPToIdISEET(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));
		generarInformeISEETJSP.setPagina("1");
		generarInformeISEETJSP.setPaginaVuelta("detallePersonal");
		generarInformeISEETJSP.setIsLectura(Boolean.FALSE);
		generarInformeISEETJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	@RequestMapping(value = "/paginaSiguienteISEET", method = RequestMethod.POST)
	public String paginaSiguienteISEET(GenerarInformeISEETJSP generarInformeISEETJSP, Model model) {
		generarInformeISEETJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISEETJSP.getPagina()) + 1)));
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	@RequestMapping(value = "/paginaAnteriorISEET", method = RequestMethod.POST)
	public String paginaAnteriorISEET(GenerarInformeISEETJSP generarInformeISEETJSP, Model model) {
		generarInformeISEETJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISEETJSP.getPagina()) - 1)));
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	@RequestMapping(value = "/volverGenerarInformeISEET", method = RequestMethod.POST)
	public String volverGenerarInformeISEET(GenerarInformeISEETJSP generarInformeISEETJSP, Model model,
			HttpSession sesion) {

		if ("detallePersonal".equalsIgnoreCase(generarInformeISEETJSP.getPaginaVuelta())) {
			return "forward:/volverGenerarISEETPersonal";
		}

		return "redirect:/" + generarInformeISEETJSP.getPaginaVuelta();
	}

	@RequestMapping(value = "/guardarGenerarInformeISEET", method = RequestMethod.POST)
	public String guardarGenerarInformeISEET(GenerarInformeISEETJSP generarInformeISEETJSP, Model model,
			HttpSession sesion) {

		try {
			generarInformeISEETService.guardarInformeISEET(generarInformeISEETJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("generarInformeISO", generarInformeISEETJSP);

			return "generarInformeISEET";
		}

		if (Constantes.ESTADO_INSPECCION_PLANIFICADA.equals(generarInformeISEETJSP.getIdEstadoISEET())) {
			generarInformeISEETJSP.setIdEstadoISEET(Constantes.ESTADO_INSPECCION_REALIZADA);
		}
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "generarInformeISEET";
	}

	@RequestMapping(value = "/firmaISEETInspectorSeguridad", method = RequestMethod.POST)
	public String firmaISEETInspectorSeguridad(GenerarInformeISEETJSP generarInformeISEETJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISEETJSP.getNombreInspectorSeguridad())
				|| StringUtils.isBlank(generarInformeISEETJSP.getPasswordInspectorSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISEET", generarInformeISEETJSP);

			return "generarInformeISEET";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISEETJSP.getNombreInspectorSeguridad(),
				generarInformeISEETJSP.getPasswordInspectorSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISEET", generarInformeISEETJSP);

			return "generarInformeISEET";
		} else {
			generarInformeISEETJSP.setIdFirmaInspectorSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarInformeISEETService.firmaISEETInspectorSeguridad(generarInformeISEETJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISEETJSP.setIdFirmaInspectorSeguridad(null);
			model.addAttribute("generarInformeISEET", generarInformeISEETJSP);

			return "generarInformeISEET";
		}

		generarInformeISEETJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

	@RequestMapping(value = "/firmaISEETResponsableSeguridad", method = RequestMethod.POST)
	public String firmaISEETResponsableSeguridad(GenerarInformeISEETJSP generarInformeISEETJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISEETJSP.getNombreResponsableSeguridad())
				|| StringUtils.isBlank(generarInformeISEETJSP.getPasswordResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISEET", generarInformeISEETJSP);

			return "generarInformeISEET";
		}

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISEETJSP.getNombreResponsableSeguridad(),
				generarInformeISEETJSP.getPasswordResponsableSeguridad());
		if (responsableSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISEET", generarInformeISEETJSP);

			return "generarInformeISEET";
		} else {
			generarInformeISEETJSP.setIdFirmaResponsableSeguridad(responsableSeguridad.getIdPersonal());
		}
		try {
			generarInformeISEETService.firmaISEETResponsableSeguridad(generarInformeISEETJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISEETJSP.setIdFirmaResponsableSeguridad(null);
			model.addAttribute("generarInformeISEET", generarInformeISEETJSP);

			return "generarInformeISEET";
		}

		generarInformeISEETJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISEET", generarInformeISEETJSP);
		return "generarInformeISEET";
	}

}