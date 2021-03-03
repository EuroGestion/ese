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
import eu.eurogestion.ese.domain.Iscc;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.ResultadoInspeccion;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorInspeccionJSP;
import eu.eurogestion.ese.pojo.GenerarInformeISCCJSP;
import eu.eurogestion.ese.pojo.TareasPendientesJSP;
import eu.eurogestion.ese.repository.IsccDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResultadoInspeccionDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.GenerarInformeISCCService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class GenerarInformeISCCController {

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
	public IsccDAO isccDAO;

	/**
	 * Servicio de la generacion del informe ISO
	 */
	@Autowired
	public GenerarInformeISCCService generarInformeISCCService;

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
	@RequestMapping(value = "/verGenerarInformeISCCTareaPendiente", method = RequestMethod.POST)
	public String verGenerarInformeISCCTareaPendiente(TareasPendientesJSP tareasPendientesJSP, Model model) {

		GenerarInformeISCCJSP generarInformeISCCJSP = generarInformeISCCService
				.createGenerarInformeISCCJSPToIdISCC(Integer.parseInt(tareasPendientesJSP.getIdTarea()));
		generarInformeISCCJSP.setPagina("1");
		generarInformeISCCJSP.setPaginaVuelta("tareasPendientes");
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		return "generarInformeISCC";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verInspeccionISCC", method = RequestMethod.POST)
	public String verInspeccionISCC(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISCCJSP generarInformeISCCJSP = generarInformeISCCService
				.createGenerarInformeISCCJSPToIdISCC(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISCCJSP.setPagina("1");
		generarInformeISCCJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISCCJSP.setIsLectura(Boolean.TRUE);
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		return "generarInformeISCC";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaGenerarInformeISCC", method = RequestMethod.POST)
	public void verEvidenciaGenerarInformeISCC(GenerarInformeISCCJSP generarInformeISCCJSP, Model model,
			HttpServletResponse response) {

		try {
			Iscc iscc = isccDAO.getOne(Integer.parseInt(generarInformeISCCJSP.getIdISCC()));
			Documento documento = iscc.getEvidencia31().getDocumento();
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
	@RequestMapping(value = "/verEvidenciaPlanificarInformeISCC", method = RequestMethod.POST)
	public void verEvidenciaPlanificarInformeISCC(GenerarInformeISCCJSP generarInformeISCCJSP, Model model,
			HttpServletResponse response) {

		try {
			Iscc iscc = isccDAO.getOne(Integer.parseInt(generarInformeISCCJSP.getIdISCC()));
			Documento documento = iscc.getEvidencia30().getDocumento();
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
	@RequestMapping(value = "/modificarInspeccionISCC", method = RequestMethod.POST)
	public String modificarInspeccionISCC(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISCCJSP generarInformeISCCJSP = generarInformeISCCService
				.createGenerarInformeISCCJSPToIdISCC(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISCCJSP.setPagina("1");
		generarInformeISCCJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISCCJSP.setIsLectura(Boolean.FALSE);
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		return "generarInformeISCC";
	}

	@RequestMapping(value = "/paginaSiguienteISCC", method = RequestMethod.POST)
	public String paginaSiguienteISCC(GenerarInformeISCCJSP generarInformeISCCJSP, Model model) {
		generarInformeISCCJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISCCJSP.getPagina()) + 1)));
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		return "generarInformeISCC";
	}

	@RequestMapping(value = "/paginaAnteriorISCC", method = RequestMethod.POST)
	public String paginaAnteriorISCC(GenerarInformeISCCJSP generarInformeISCCJSP, Model model) {
		generarInformeISCCJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISCCJSP.getPagina()) - 1)));
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		return "generarInformeISCC";
	}

	@RequestMapping(value = "/volverGenerarInformeISCC", method = RequestMethod.POST)
	public String volverGenerarInformeISCC(GenerarInformeISCCJSP generarInformeISCCJSP, Model model,
			HttpSession sesion) {

		return "redirect:/" + generarInformeISCCJSP.getPaginaVuelta();
	}

	@RequestMapping(value = "/guardarGenerarInformeISCC", method = RequestMethod.POST)
	public String guardarGenerarInformeISCC(GenerarInformeISCCJSP generarInformeISCCJSP, Model model,
			HttpSession sesion) {

		try {
			generarInformeISCCService.guardarInformeISCC(generarInformeISCCJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("generarInformeISO", generarInformeISCCJSP);

			return "generarInformeISCC";
		}

		if (Constantes.ESTADO_INSPECCION_PLANIFICADA.equals(generarInformeISCCJSP.getIdEstadoISCC())) {
			generarInformeISCCJSP.setIdEstadoISCC(Constantes.ESTADO_INSPECCION_REALIZADA);
		}
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "generarInformeISCC";
	}

	@RequestMapping(value = "/firmaISCCInspectorSeguridad", method = RequestMethod.POST)
	public String firmaISCCInspectorSeguridad(GenerarInformeISCCJSP generarInformeISCCJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISCCJSP.getNombreInspectorSeguridad())
				|| StringUtils.isBlank(generarInformeISCCJSP.getPasswordInspectorSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISCC", generarInformeISCCJSP);

			return "generarInformeISCC";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISCCJSP.getNombreInspectorSeguridad(),
				generarInformeISCCJSP.getPasswordInspectorSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISCC", generarInformeISCCJSP);

			return "generarInformeISCC";
		} else {
			generarInformeISCCJSP.setIdFirmaInspectorSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarInformeISCCService.firmaISCCInspectorSeguridad(generarInformeISCCJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISCCJSP.setIdFirmaInspectorSeguridad(null);
			model.addAttribute("generarInformeISCC", generarInformeISCCJSP);

			return "generarInformeISCC";
		}

		generarInformeISCCJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		return "generarInformeISCC";
	}

	@RequestMapping(value = "/firmaISCCResponsableSeguridad", method = RequestMethod.POST)
	public String firmaISCCResponsableSeguridad(GenerarInformeISCCJSP generarInformeISCCJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISCCJSP.getNombreResponsableSeguridad())
				|| StringUtils.isBlank(generarInformeISCCJSP.getPasswordResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISCC", generarInformeISCCJSP);

			return "generarInformeISCC";
		}

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISCCJSP.getNombreResponsableSeguridad(),
				generarInformeISCCJSP.getPasswordResponsableSeguridad());
		if (responsableSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISCC", generarInformeISCCJSP);

			return "generarInformeISCC";
		} else {
			generarInformeISCCJSP.setIdFirmaResponsableSeguridad(responsableSeguridad.getIdPersonal());
		}
		try {
			generarInformeISCCService.firmaISCCResponsableSeguridad(generarInformeISCCJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISCCJSP.setIdFirmaResponsableSeguridad(null);
			model.addAttribute("generarInformeISCC", generarInformeISCCJSP);

			return "generarInformeISCC";
		}

		generarInformeISCCJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISCC", generarInformeISCCJSP);
		return "generarInformeISCC";
	}

}