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
import eu.eurogestion.ese.domain.EstadoVerificacionInforme;
import eu.eurogestion.ese.domain.Iso;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorInspeccionJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.GenerarInformeISOJSP;
import eu.eurogestion.ese.pojo.TareasPendientesJSP;
import eu.eurogestion.ese.repository.EstadoVerificacionInformeDAO;
import eu.eurogestion.ese.repository.IsoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.services.GenerarInformeISOService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class GenerarInformeISOController {

	/**
	 * Repositorio de la clase de dominio EstadoVerificacionInforme
	 */
	@Autowired
	public EstadoVerificacionInformeDAO estadoVerificacionInformeDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public IsoDAO isoDAO;

	/**
	 * Servicio de la generacion del informe ISO
	 */
	@Autowired
	public GenerarInformeISOService generarInformeISOService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de Cursos para una tabla
	 * 
	 * @return lista de objetos Curso
	 */
	@ModelAttribute("verificaciones")
	public List<EstadoVerificacionInforme> listVerificacionesAll() {
		List<EstadoVerificacionInforme> lista = new ArrayList<>();
		EstadoVerificacionInforme estadoVerificacionInforme = new EstadoVerificacionInforme();
		estadoVerificacionInforme.setValor("Seleccione uno:");
		lista.add(estadoVerificacionInforme);
		lista.addAll(estadoVerificacionInformeDAO.findAll());
		return lista;
	}

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
	@RequestMapping(value = "/generarInformeISO", method = RequestMethod.GET)
	public String informeISO(Model model) {

		GenerarInformeISOJSP generarInformeISOJSP = new GenerarInformeISOJSP();
		generarInformeISOJSP.setPagina("1");
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verGenerarInformeISOTareaPendiente", method = RequestMethod.POST)
	public String verGenerarinformeISOTareaPendiente(TareasPendientesJSP tareasPendientesJSP, Model model) {

		GenerarInformeISOJSP generarInformeISOJSP = generarInformeISOService
				.createGenerarInformeISOJSPToIdISO(Integer.parseInt(tareasPendientesJSP.getIdTarea()));
		generarInformeISOJSP.setPagina("1");
		generarInformeISOJSP.setPaginaVuelta("tareasPendientes");
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verInspeccionISO", method = RequestMethod.POST)
	public String verInspeccionISO(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISOJSP generarInformeISOJSP = generarInformeISOService
				.createGenerarInformeISOJSPToIdISO(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISOJSP.setPagina("1");
		generarInformeISOJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISOJSP.setIsLectura(Boolean.TRUE);
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	@RequestMapping(value = "/verInspeccionPersonalISO", method = RequestMethod.POST)
	public String verInspeccionPersonalISO(DetallePersonalJSP detallePersonalJSP, Model model) {

		GenerarInformeISOJSP generarInformeISOJSP = generarInformeISOService
				.createGenerarInformeISOJSPToIdISO(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));
		generarInformeISOJSP.setPagina("1");
		generarInformeISOJSP.setPaginaVuelta("detallePersonal");
		generarInformeISOJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());
		generarInformeISOJSP.setIsLectura(Boolean.TRUE);
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaGenerarInformeISO", method = RequestMethod.POST)
	public void verEvidenciaGenerarInformeISO(GenerarInformeISOJSP generarInformeISOJSP, Model model,
			HttpServletResponse response) {

		try {
			Iso iso = isoDAO.getOne(Integer.parseInt(generarInformeISOJSP.getIdISO()));
			Documento documento = iso.getEvidencia9().getDocumento();
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
	@RequestMapping(value = "/verEvidenciaPlanificarInformeISO", method = RequestMethod.POST)
	public void verEvidenciaPlanificarInformeISO(GenerarInformeISOJSP generarInformeISOJSP, Model model,
			HttpServletResponse response) {

		try {
			Iso iso = isoDAO.getOne(Integer.parseInt(generarInformeISOJSP.getIdISO()));
			Documento documento = iso.getEvidencia7().getDocumento();
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
	@RequestMapping(value = "/modificarInspeccionISO", method = RequestMethod.POST)
	public String modificarInspeccionISO(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		GenerarInformeISOJSP generarInformeISOJSP = generarInformeISOService
				.createGenerarInformeISOJSPToIdISO(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		generarInformeISOJSP.setPagina("1");
		generarInformeISOJSP.setPaginaVuelta("buscadorInspeccion");
		generarInformeISOJSP.setIsLectura(Boolean.FALSE);
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	@RequestMapping(value = "/modificarInspeccionPersonalISO", method = RequestMethod.POST)
	public String modificarInspeccionPersonalISO(DetallePersonalJSP detallePersonalJSP, Model model) {

		GenerarInformeISOJSP generarInformeISOJSP = generarInformeISOService
				.createGenerarInformeISOJSPToIdISO(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));
		generarInformeISOJSP.setPagina("1");
		generarInformeISOJSP.setPaginaVuelta("detallePersonal");
		generarInformeISOJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());
		generarInformeISOJSP.setIsLectura(Boolean.FALSE);
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	@RequestMapping(value = "/paginaSiguiente", method = RequestMethod.POST)
	public String paginaSiguiente(GenerarInformeISOJSP generarInformeISOJSP, Model model) {
		generarInformeISOJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISOJSP.getPagina()) + 1)));
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	@RequestMapping(value = "/paginaAnterior", method = RequestMethod.POST)
	public String paginaAnterior(GenerarInformeISOJSP generarInformeISOJSP, Model model) {
		generarInformeISOJSP.setPagina(String.valueOf((Integer.parseInt(generarInformeISOJSP.getPagina()) - 1)));
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	@RequestMapping(value = "/volverGenerarInformeISO", method = RequestMethod.POST)
	public String volverGenerarInformeISO(GenerarInformeISOJSP generarInformeISOJSP, Model model, HttpSession sesion) {

		if ("detallePersonal".equalsIgnoreCase(generarInformeISOJSP.getPaginaVuelta())) {
			return "forward:/volverGenerarISOPersonal";
		}

		return "redirect:/" + generarInformeISOJSP.getPaginaVuelta();
	}

	@RequestMapping(value = "/guardarGenerarInformeISO", method = RequestMethod.POST)
	public String guardarGenerarInformeISO(GenerarInformeISOJSP generarInformeISOJSP, Model model, HttpSession sesion) {

		try {
			generarInformeISOService.guardarInformeISO(generarInformeISOJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("generarInformeISO", generarInformeISOJSP);

			return "generarInformeISO";
		}
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "generarInformeISO";
	}

	@RequestMapping(value = "/firmaISOInspectorSeguridad", method = RequestMethod.POST)
	public String firmaISOInspectorSeguridad(GenerarInformeISOJSP generarInformeISOJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISOJSP.getNombreInspectorSeguridad())
				|| StringUtils.isBlank(generarInformeISOJSP.getPasswordInspectorSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISO", generarInformeISOJSP);

			return "generarInformeISO";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISOJSP.getNombreInspectorSeguridad(),
				generarInformeISOJSP.getPasswordInspectorSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("generarInformeISO", generarInformeISOJSP);

			return "generarInformeISO";
		} else {
			generarInformeISOJSP.setIdFirmaInspectorSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarInformeISOService.firmaISOInspectorSeguridad(generarInformeISOJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISOJSP.setIdFirmaInspectorSeguridad(null);
			model.addAttribute("generarInformeISO", generarInformeISOJSP);

			return "generarInformeISO";
		}

		generarInformeISOJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

	@RequestMapping(value = "/firmaISOResponsableSeguridad", method = RequestMethod.POST)
	public String firmaISOResponsableSeguridad(GenerarInformeISOJSP generarInformeISOJSP, Model model,
			HttpSession sesion) {

		if (StringUtils.isBlank(generarInformeISOJSP.getNombreResponsableSeguridad())
				|| StringUtils.isBlank(generarInformeISOJSP.getPasswordResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISO", generarInformeISOJSP);

			return "generarInformeISO";
		}

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarInformeISOJSP.getNombreResponsableSeguridad(),
				generarInformeISOJSP.getPasswordResponsableSeguridad());
		if (responsableSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarInformeISO", generarInformeISOJSP);

			return "generarInformeISO";
		} else {
			generarInformeISOJSP.setIdFirmaResponsableSeguridad(responsableSeguridad.getIdPersonal());
		}
		try {
			generarInformeISOService.firmaISOResponsableSeguridad(generarInformeISOJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarInformeISOJSP.setIdFirmaResponsableSeguridad(null);
			model.addAttribute("generarInformeISO", generarInformeISOJSP);

			return "generarInformeISO";
		}

		generarInformeISOJSP.setIsFirmado(Boolean.TRUE);
		model.addAttribute("generarInformeISO", generarInformeISOJSP);
		return "generarInformeISO";
	}

}