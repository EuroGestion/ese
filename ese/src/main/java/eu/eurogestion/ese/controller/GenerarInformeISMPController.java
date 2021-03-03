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
import eu.eurogestion.ese.domain.Ismp;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.ResultadoInspeccion;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorInspeccionJSP;
import eu.eurogestion.ese.pojo.GenerarInformeISMPJSP;
import eu.eurogestion.ese.pojo.TareasPendientesJSP;
import eu.eurogestion.ese.repository.IsmpDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResultadoInspeccionDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.GenerarInformeISMPService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class GenerarInformeISMPController {

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
	public IsmpDAO ismpDAO;

	/**
	 * Servicio de la generacion del informe ISO
	 */
	@Autowired
	public GenerarInformeISMPService generarInformeISMPService;

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
	@RequestMapping(value = "/verGenerarInformeISMPTareaPendiente", method = RequestMethod.POST)
	public String verGenerarInformeISMPTareaPendiente(TareasPendientesJSP tareasPendientesJSP, Model model) {

		GenerarInformeISMPJSP generarInformeISMPJSP = generarInformeISMPService
				.createGenerarInformeISMPJSPToIdISMP(Integer.parseInt(tareasPendientesJSP.getIdTarea()));
		generarInformeISMPJSP.setPagina("1");
		generarInformeISMPJSP.setPaginaVuelta("tareasPendientes");
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		return "generarInformeISMP";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verInspeccionISMP", method = RequestMethod.POST)
	public String verInspeccionISMP(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISMPJSP generarInformeISMPJSP = generarInformeISMPService
				.createGenerarInformeISMPJSPToIdISMP(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISMPJSP.setPagina("1");
		generarInformeISMPJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISMPJSP.setIsLectura(Boolean.TRUE);
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		return "generarInformeISMP";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaGenerarInformeISMP", method = RequestMethod.POST)
	public void verEvidenciaGenerarInformeISMP(GenerarInformeISMPJSP generarInformeISMPJSP, Model model,
			HttpServletResponse response) {

		try {
			Ismp ismp = ismpDAO.getOne(Integer.parseInt(generarInformeISMPJSP.getIdISMP()));
			Documento documento = ismp.getEvidencia80().getDocumento();
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
	@RequestMapping(value = "/verEvidenciaPlanificarInformeISMP", method = RequestMethod.POST)
	public void verEvidenciaPlanificarInformeISMP(GenerarInformeISMPJSP generarInformeISMPJSP, Model model,
			HttpServletResponse response) {

		try {
			Ismp ismp = ismpDAO.getOne(Integer.parseInt(generarInformeISMPJSP.getIdISMP()));
			Documento documento = ismp.getEvidencia78().getDocumento();
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
	@RequestMapping(value = "/modificarInspeccionISMP", method = RequestMethod.POST)
	public String modificarInspeccionISMP(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISMPJSP generarInformeISMPJSP = generarInformeISMPService
				.createGenerarInformeISMPJSPToIdISMP(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISMPJSP.setPagina("1");
		generarInformeISMPJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISMPJSP.setIsLectura(Boolean.FALSE);
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		return "generarInformeISMP";
	}

	@RequestMapping(value = "/paginaSiguienteISMP", method = RequestMethod.POST)
	public String paginaSiguienteISMP(GenerarInformeISMPJSP generarInformeISMPJSP, Model model) {
		generarInformeISMPJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISMPJSP.getPagina()) + 1)));
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		return "generarInformeISMP";
	}

	@RequestMapping(value = "/paginaAnteriorISMP", method = RequestMethod.POST)
	public String paginaAnteriorISMP(GenerarInformeISMPJSP generarInformeISMPJSP, Model model) {
		generarInformeISMPJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISMPJSP.getPagina()) - 1)));
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		return "generarInformeISMP";
	}

	@RequestMapping(value = "/volverGenerarInformeISMP", method = RequestMethod.POST)
	public String volverGenerarInformeISMP(GenerarInformeISMPJSP generarInformeISMPJSP, Model model,
			HttpSession sesion) {

		return "redirect:/" + generarInformeISMPJSP.getPaginaVuelta();
	}

	@RequestMapping(value = "/guardarGenerarInformeISMP", method = RequestMethod.POST)
	public String guardarGenerarInformeISMP(GenerarInformeISMPJSP generarInformeISMPJSP, Model model,
			HttpSession sesion) {

		try {
			generarInformeISMPService.guardarInformeISMP(generarInformeISMPJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("generarInformeISO", generarInformeISMPJSP);

			return "generarInformeISMP";
		}

		if (Constantes.ESTADO_INSPECCION_PLANIFICADA.equals(generarInformeISMPJSP.getIdEstadoISMP())) {
			generarInformeISMPJSP.setIdEstadoISMP(Constantes.ESTADO_INSPECCION_REALIZADA);
		}
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "generarInformeISMP";
	}

	@RequestMapping(value = "/firmaISMPInspectorSeguridad", method = RequestMethod.POST)
	public String firmaISMPInspectorSeguridad(GenerarInformeISMPJSP generarInformeISMPJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISMPJSP.getNombreInspectorSeguridad())
				|| StringUtils.isBlank(generarInformeISMPJSP.getPasswordInspectorSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISMP", generarInformeISMPJSP);

			return "generarInformeISMP";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISMPJSP.getNombreInspectorSeguridad(),
				generarInformeISMPJSP.getPasswordInspectorSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISMP", generarInformeISMPJSP);

			return "generarInformeISMP";
		} else {
			generarInformeISMPJSP.setIdFirmaInspectorSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarInformeISMPService.firmaISMPInspectorSeguridad(generarInformeISMPJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISMPJSP.setIdFirmaInspectorSeguridad(null);
			model.addAttribute("generarInformeISMP", generarInformeISMPJSP);

			return "generarInformeISMP";
		}

		generarInformeISMPJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		return "generarInformeISMP";
	}

	@RequestMapping(value = "/firmaISMPResponsableSeguridad", method = RequestMethod.POST)
	public String firmaISMPResponsableSeguridad(GenerarInformeISMPJSP generarInformeISMPJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISMPJSP.getNombreResponsableSeguridad())
				|| StringUtils.isBlank(generarInformeISMPJSP.getPasswordResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISMP", generarInformeISMPJSP);

			return "generarInformeISMP";
		}

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISMPJSP.getNombreResponsableSeguridad(),
				generarInformeISMPJSP.getPasswordResponsableSeguridad());
		if (responsableSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISMP", generarInformeISMPJSP);

			return "generarInformeISMP";
		} else {
			generarInformeISMPJSP.setIdFirmaResponsableSeguridad(responsableSeguridad.getIdPersonal());
		}
		try {
			generarInformeISMPService.firmaISMPResponsableSeguridad(generarInformeISMPJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISMPJSP.setIdFirmaResponsableSeguridad(null);
			model.addAttribute("generarInformeISMP", generarInformeISMPJSP);

			return "generarInformeISMP";
		}

		generarInformeISMPJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISMP", generarInformeISMPJSP);
		return "generarInformeISMP";
	}

}