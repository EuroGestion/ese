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

import eu.eurogestion.ese.domain.Cad;
import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.ResultadoCad;
import eu.eurogestion.ese.domain.TipoCad;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorInspeccionJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.RealizarCADJSP;
import eu.eurogestion.ese.pojo.TareasPendientesJSP;
import eu.eurogestion.ese.repository.CadDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResultadoCadDAO;
import eu.eurogestion.ese.repository.TipoCadDAO;
import eu.eurogestion.ese.services.EmailService;
import eu.eurogestion.ese.services.RealizarCADService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class RealizarCADController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public CadDAO cadDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public TipoCadDAO tipoCadDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public ResultadoCadDAO resultadoCadDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Servicio de la modificacion del personal
	 */
	@Autowired
	public RealizarCADService realizarCADService;
	/**
	 * Servicio de la modificacion del personal
	 */
	@Autowired
	public EmailService emailService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de compañias para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("personales")
	public List<Personal> listPersonalesAll() {
		List<Personal> lista = new ArrayList<>();
		Personal compania = new Personal();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de compañias para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("delegadosSeguridad")
	// TODO mirar si hay que filtrar x cargo o no
	public List<Personal> listDelegadosSeguridadAll() {
		List<Personal> lista = new ArrayList<>();
		Personal compania = new Personal();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de compañias para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("tipoControles")
	public List<TipoCad> listTipoControlesAll() {
		List<TipoCad> lista = new ArrayList<>();
		TipoCad compania = new TipoCad();
		compania.setValor("Selecciona uno:");
		lista.add(compania);
		lista.addAll(tipoCadDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de compañias para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("centrosMedicos")
	public List<Compania> listCentrosMedicosAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAllCompaniaCentroMedico());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de compañias para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("resultadosCAD")
	public List<ResultadoCad> listResultadosCadAll() {
		List<ResultadoCad> lista = new ArrayList<>();
		ResultadoCad compania = new ResultadoCad();
		compania.setValor("Selecciona uno:");
		lista.add(compania);
		lista.addAll(resultadoCadDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verRealizarCADTareaPendiente", method = RequestMethod.POST)
	public String verRealizarCADTareaPendiente(TareasPendientesJSP tareasPendientesJSP, Model model) {

		RealizarCADJSP realizarCAD = realizarCADService
				.convertIdCADToRealizarCADJSP(Integer.parseInt(tareasPendientesJSP.getIdTarea()));

		realizarCAD.setLectura(Boolean.FALSE);
		realizarCAD.setPaginaAnterior("tareasPendientes");
		model.addAttribute("realizarCAD", realizarCAD);
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verInspeccionCAD", method = RequestMethod.POST)
	public String verInspeccionCAD(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		RealizarCADJSP realizarCAD = realizarCADService
				.convertIdCADToRealizarCADJSP(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));

		realizarCAD.setLectura(Boolean.TRUE);
		realizarCAD.setPaginaAnterior("buscadorInspeccion");
		model.addAttribute("realizarCAD", realizarCAD);
		return "realizarCAD";
	}

	@RequestMapping(value = "/verInspeccionPersonalCAD", method = RequestMethod.POST)
	public String verInspeccionPersonalCAD(DetallePersonalJSP detallePersonalJSP, Model model) {

		RealizarCADJSP realizarCAD = realizarCADService
				.convertIdCADToRealizarCADJSP(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));

		realizarCAD.setLectura(Boolean.TRUE);
		realizarCAD.setPaginaAnterior("detallePersonal");
		realizarCAD.setIdPersonal(detallePersonalJSP.getIdPersonal());
		model.addAttribute("realizarCAD", realizarCAD);
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarInspeccionCAD", method = RequestMethod.POST)
	public String modificarInspeccionCAD(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		RealizarCADJSP realizarCAD = realizarCADService
				.convertIdCADToRealizarCADJSP(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));

		realizarCAD.setLectura(Boolean.FALSE);
		realizarCAD.setPaginaAnterior("buscadorInspeccion");
		model.addAttribute("realizarCAD", realizarCAD);
		return "realizarCAD";
	}

	@RequestMapping(value = "/modificarInspeccionPersonalCAD", method = RequestMethod.POST)
	public String modificarInspeccionPersonalCAD(DetallePersonalJSP detallePersonalJSP, Model model) {

		RealizarCADJSP realizarCAD = realizarCADService
				.convertIdCADToRealizarCADJSP(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));

		realizarCAD.setLectura(Boolean.FALSE);
		realizarCAD.setPaginaAnterior("detallePersonal");
		realizarCAD.setIdPersonal(detallePersonalJSP.getIdPersonal());
		model.addAttribute("realizarCAD", realizarCAD);
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/autorizaCAD", method = RequestMethod.POST)
	public String autorizaCAD(RealizarCADJSP realizarCAD, Model model, HttpSession session) {

		if (StringUtils.isBlank(realizarCAD.getNombrePersonalSometidoControl())
				|| StringUtils.isBlank(realizarCAD.getPasswordPersonalSometidoControl())) {
			realizarCAD.setIdPersonalSometidoControl(null);
			model.addAttribute("error", "El usuario o la contraseña del personal sometido al control no son correctos");
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}

		Personal personalSometidoControl = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				realizarCAD.getNombrePersonalSometidoControl(), realizarCAD.getPasswordPersonalSometidoControl());
		if (personalSometidoControl == null) {
			realizarCAD.setIdPersonalSometidoControl(null);
			model.addAttribute("error", "El usuario o la contraseña del personal sometido al control no son correctos");
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}
		realizarCAD.setIdPersonalSometidoControl(personalSometidoControl.getIdPersonal().toString());
		try {
			realizarCADService.autorizarCAD(realizarCAD, session);
		} catch (EseException e) {
			log.error(e.getMessage());
			// TODO falta añadir error
			realizarCAD.setIdPersonalSometidoControl(null);
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}
		realizarCAD.setConsentimiento(Boolean.TRUE);
		realizarCAD.setIdEstadoInspeccion(Constantes.ESTADO_INSPECCION_EN_CURSO);
		model.addAttribute("realizarCAD", realizarCAD);
		model.addAttribute("info", "Se ha realizado la autorización correctamente");
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/noAutorizaCAD", method = RequestMethod.POST)
	public String noAutorizaCAD(RealizarCADJSP realizarCAD, Model model, HttpSession session) {

		if (StringUtils.isBlank(realizarCAD.getNombrePersonalSometidoControl())
				|| StringUtils.isBlank(realizarCAD.getPasswordPersonalSometidoControl())) {
			realizarCAD.setIdPersonalSometidoControl(null);
			model.addAttribute("error", "El usuario o la contraseña del personal sometido al control no son correctos");
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}

		Personal personalSometidoControl = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				realizarCAD.getNombrePersonalSometidoControl(), realizarCAD.getPasswordPersonalSometidoControl());
		if (personalSometidoControl == null) {
			realizarCAD.setIdPersonalSometidoControl(null);
			model.addAttribute("error", "El usuario o la contraseña del personal sometido al control no son correctos");
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}
		realizarCAD.setIdPersonalSometidoControl(personalSometidoControl.getIdPersonal().toString());
		try {
			realizarCADService.noAutorizarCAD(realizarCAD, session);
		} catch (EseException e) {
			log.error(e.getMessage());
			// TODO falta añadir error
			realizarCAD.setIdPersonalSometidoControl(null);
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}
		realizarCAD.setIdEstadoInspeccion(Constantes.ESTADO_INSPECCION_FINALIZADA);
		realizarCAD.setConsentimiento(Boolean.FALSE);
		model.addAttribute("realizarCAD", realizarCAD);
		model.addAttribute("info", "Se ha realizado la autorización correctamente");
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarCAD", method = RequestMethod.POST)
	public String guardarCAD(RealizarCADJSP realizarCAD, Model model) {
		Boolean dniCorrecto = true;
		List<String> errores = new ArrayList<>();

		if (StringUtils.isNotBlank(realizarCAD.getDniPersonalMedico())
				&& !Utiles.dniValido(realizarCAD.getDniPersonalMedico())) {
			errores.add("El DNI del personal médico no es correcto");
			dniCorrecto = false;
		}

		if (StringUtils.isNotBlank(realizarCAD.getDniDelegadoSeguridad())
				&& !Utiles.dniValido(realizarCAD.getDniDelegadoSeguridad())) {
			errores.add("El DNI del delegado de seguridad no es correcto");
			dniCorrecto = false;
		}

		if (StringUtils.isNotBlank(realizarCAD.getDniDelegadoADIF())
				&& !Utiles.dniValido(realizarCAD.getDniDelegadoADIF())) {
			errores.add("El DNI del delegado de ADIF no es correcto");
			dniCorrecto = false;
		}

		if (StringUtils.isNotBlank(realizarCAD.getDniTecnicoCIAF())
				&& !Utiles.dniValido(realizarCAD.getDniTecnicoCIAF())) {
			errores.add("El DNI del técnico de la CIAF no es correcto");
			dniCorrecto = false;
		}

		if (!dniCorrecto) {
			model.addAttribute("error", errores);
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}

		realizarCADService.guardarCAD(realizarCAD);
		model.addAttribute("realizarCAD", realizarCAD);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/crearFichaCAD", method = RequestMethod.POST)
	public String crearFichaCAD(RealizarCADJSP realizarCAD, Model model, HttpSession session) {

		try {
			realizarCADService.crearFichaCAD(realizarCAD, session);
		} catch (EseException e) {
			log.error(e.getMessage());
			// TODO falta añadir error
			model.addAttribute("realizarCAD", realizarCAD);
			return "realizarCAD";
		}
		realizarCAD.setIdEstadoInspeccion(Constantes.ESTADO_INSPECCION_FINALIZADA);
		model.addAttribute("realizarCAD", realizarCAD);
		model.addAttribute("info", "Se ha generado la evidencia correctamente");
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/errorVerDetallePersonalSuspenderTituloCAD", method = RequestMethod.POST)
	public String errorVerDetallePersonalSuspenderTituloCAD(RealizarCADJSP realizarCAD, Model model) {

		model.addAttribute("realizarCAD", realizarCAD);
		return "realizarCAD";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDocumentoCAD", method = RequestMethod.POST)
	public void verDocumentoCAD(RealizarCADJSP realizarCAD, Model model, HttpServletResponse response) {

		try {
			Cad cad = cadDAO.getOne(realizarCAD.getIdInformeCAD());
			Documento documento = cad.getEvidenciaTipo37().getDocumento();
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
	@RequestMapping(value = "/verFichaCAD", method = RequestMethod.POST)
	public void verFichaCAD(RealizarCADJSP realizarCAD, Model model, HttpServletResponse response) {
		try {
			Cad cad = cadDAO.getOne(realizarCAD.getIdInformeCAD());
			Documento documento = cad.getEvidenciaTipo38().getDocumento();
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
	@RequestMapping(value = "/volverCAD", method = RequestMethod.POST)
	public String volverCAD(RealizarCADJSP realizarCAD, Model model) {

		if ("tareasPendientes".equalsIgnoreCase(realizarCAD.getPaginaAnterior())) {
			return "redirect:/tareasPendientes";
		}

		if ("detallePersonal".equalsIgnoreCase(realizarCAD.getPaginaAnterior())) {
			return "forward:/volverRealizarCADPersonal";
		}

		return "redirect:/buscadorInspeccion";
	}

}