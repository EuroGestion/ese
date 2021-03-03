package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.domain.CausaAccidente;
import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoAccidente;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorInvestigacionJSP;
import eu.eurogestion.ese.pojo.GenerarAccidenteJSP;
import eu.eurogestion.ese.repository.AccidenteDAO;
import eu.eurogestion.ese.repository.CausaAccidenteDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.EstadoAccidenteDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoAccidenteDAO;
import eu.eurogestion.ese.services.GenerarAccidenteService;
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
public class GenerarAccidenteController {

	/**
	 * Repositorio de la clase de dominio Accidente
	 */
	@Autowired
	public AccidenteDAO accidenteDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio TipoAccidente
	 */
	@Autowired
	public TipoAccidenteDAO tipoAccidenteDAO;

	/**
	 * Repositorio de la clase de dominio CausaAccidente
	 */
	@Autowired
	public CausaAccidenteDAO causaAccidenteDAO;

	/**
	 * Repositorio de la clase de dominio EstadoAccidente
	 */
	@Autowired
	public EstadoAccidenteDAO estadoAccidenteDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Servicio de la clase de CrearInvestigacion
	 */
	@Autowired
	public GenerarAccidenteService generarAccidenteService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de companias para un Select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("empresas")
	public List<Compania> listCompaniasAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de tipos de investigacion para un Select
	 * 
	 * @return lista de objetos TipoAccidente
	 */
	@ModelAttribute("tiposInvestigacion")
	public List<TipoAccidente> listTipoAccidenteAll() {
		List<TipoAccidente> lista = new ArrayList<>();
		TipoAccidente tipoAccidente = new TipoAccidente();
		tipoAccidente.setValor("Selecciona uno:");
		lista.add(tipoAccidente);
		lista.addAll(tipoAccidenteDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de causas de investigacion para un Select
	 * 
	 * @return lista de objetos CausaAccidente
	 */
	@ModelAttribute("causas")
	public List<CausaAccidente> listCausaAccidenteAll() {
		List<CausaAccidente> lista = new ArrayList<>();
		CausaAccidente causaAccidente = new CausaAccidente();
		causaAccidente.setValor("Selecciona uno:");
		lista.add(causaAccidente);
		lista.addAll(causaAccidenteDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("responsablesSeguridad")
	public List<Personal> listResponsablesSeguridadAll() {
		List<Personal> lista = new ArrayList<>();
		Personal personal = new Personal();
		personal.setNombre("Selecciona uno:");
		lista.add(personal);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("personal")
	public List<Personal> listPersonalAll() {
		List<Personal> lista = new ArrayList<>();
		Personal personal = new Personal();
		personal.setNombre("Selecciona uno:");
		lista.add(personal);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Personas para un Select
	 * 
	 * @return lista de objetos Personal
	 */
	@ModelAttribute("delegadosInvestigacion")
	public List<Personal> listDelegadosInvestigacionAll() {
		List<Personal> lista = new ArrayList<>();
		Personal personal = new Personal();
		personal.setNombre("Selecciona uno:");
		lista.add(personal);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/generarAccidente", method = RequestMethod.GET)
	public String generarAccidente(Model model) {

		GenerarAccidenteJSP generarAccidenteJSP = new GenerarAccidenteJSP();
		generarAccidenteJSP.setHeridosVictimaTren("0");
		generarAccidenteJSP.setHeridosVictimaAjenaTren("0");
		generarAccidenteJSP.setMuertesVictimaTren("0");
		generarAccidenteJSP.setMuertesVictimaAjenaTren("0");
		generarAccidenteJSP.setLectura(Boolean.FALSE);
		generarAccidenteJSP.setConfirmadoIntervieneCIAF(Boolean.FALSE);
		generarAccidenteJSP.setConfirmadoDelegaInvestigacion(Boolean.FALSE);
		generarAccidenteJSP.setFirmadoFichaNotificacion(Boolean.FALSE);
		generarAccidenteJSP.setFirmadoInformeFinal(Boolean.FALSE);
		generarAccidenteJSP.setPagina("1");
		model.addAttribute("generarAccidente", generarAccidenteJSP);

		return "generarAccidente";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verAccidente", method = RequestMethod.POST)
	public String verAccidente(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model) {

		GenerarAccidenteJSP generarAccidenteJSP = convertIdAccidenteToGenerarAccidenteJSP(
				Integer.parseInt(buscadorInvestigacionJSP.getIdInvestigacion()));
		generarAccidenteJSP.setLectura(Boolean.TRUE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarAccidente", method = RequestMethod.POST)
	public String modificarAccidente(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model) {

		GenerarAccidenteJSP generarAccidenteJSP = convertIdAccidenteToGenerarAccidenteJSP(
				Integer.parseInt(buscadorInvestigacionJSP.getIdInvestigacion()));
		generarAccidenteJSP.setLectura(Boolean.FALSE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	/**
	 * Metodo que guarda una investigacion con los parametros de pantalla
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarAccidente", method = RequestMethod.POST)
	public String guardarAccidente(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		Accidente accidente;
		try {
			accidente = generarAccidenteService.guardarAccidente(generarAccidenteJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("generarAccidente", generarAccidenteJSP);
			return "generarAccidente";
		}

		generarAccidenteJSP.setIdAccidente(accidente.getIdAccidente());
		generarAccidenteJSP.setIdEstadoAccidente(accidente.getEstadoAccidente().getIdEstadoAccidente());
		generarAccidenteJSP.setIdTipoAccidente(accidente.getTipoAccidente().getIdTipoAccidente().toString());

		model.addAttribute("generarAccidente", generarAccidenteJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "generarAccidente";

	}

	@RequestMapping(value = "/confirmarIntervieneCIAF", method = RequestMethod.POST)
	public String confirmarIntervieneCIAF(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		try {
			generarAccidenteService.confirmarIntervieneCIAF(generarAccidenteJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIntervieneCIAF(Boolean.FALSE);
			model.addAttribute("generarAccidente", generarAccidenteJSP);
			return "generarAccidente";
		}
		generarAccidenteJSP.setConfirmadoIntervieneCIAF(Boolean.TRUE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		model.addAttribute("info", "Se ha confirmado correctamente");
		return "generarAccidente";

	}

	@RequestMapping(value = "/confirmarDelegaInvestigacion", method = RequestMethod.POST)
	public String confirmarDelegaInvestigacion(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		try {
			generarAccidenteService.confirmarDelegaInvestigacion(generarAccidenteJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setDelegaInvestigacion(Boolean.FALSE);
			model.addAttribute("generarAccidente", generarAccidenteJSP);
			return "generarAccidente";
		}

		generarAccidenteJSP.setConfirmadoDelegaInvestigacion(Boolean.TRUE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		model.addAttribute("info", "Se ha confirmado correctamente");
		return "generarAccidente";

	}

	@RequestMapping(value = "/firmaAccidenteFichaAccidente", method = RequestMethod.POST)
	public String firmaAccidenteFichaAccidente(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaFichaAccidente())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaFichaAccidente())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaFichaAccidente(),
				generarAccidenteJSP.getPasswordFirmaFichaAccidente());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaFichaAccidente(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteFichaAccidente(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaFichaAccidente(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/firmaAccidenteFichaEstructura", method = RequestMethod.POST)
	public String firmaAccidenteFichaEstructura(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaFichaEstructura())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaFichaEstructura())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaFichaEstructura(),
				generarAccidenteJSP.getPasswordFirmaFichaEstructura());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaFichaEstructura(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteFichaEstructura(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaFichaEstructura(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}

		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/firmaAccidenteFichaNotificacionResponsableSeguridad", method = RequestMethod.POST)
	public String firmaAccidenteFichaNotificacionResponsableSeguridad(GenerarAccidenteJSP generarAccidenteJSP,
			Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaFichaNotificacionResponsableSeguridad())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaFichaNotificacionResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaFichaNotificacionResponsableSeguridad(),
				generarAccidenteJSP.getPasswordFirmaFichaNotificacionResponsableSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaFichaNotificacionResponsableSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteFichaNotificacionResponsableSeguridad(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaFichaNotificacionResponsableSeguridad(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}

		generarAccidenteJSP.setFirmadoFichaNotificacion(Boolean.TRUE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/firmaAccidenteFichaNotificacionDelegadoSeguridad", method = RequestMethod.POST)
	public String firmaAccidenteFichaNotificacionDelegadoSeguridad(GenerarAccidenteJSP generarAccidenteJSP,
			Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaFichaNotificacionDelegadoSeguridad())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaFichaNotificacionDelegadoSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del delegado de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaFichaNotificacionDelegadoSeguridad(),
				generarAccidenteJSP.getPasswordFirmaFichaNotificacionDelegadoSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del delegado de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaFichaNotificacionDelegadoSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteFichaNotificacionDelegadoSeguridad(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaFichaNotificacionDelegadoSeguridad(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}

		generarAccidenteJSP.setFirmadoFichaNotificacion(Boolean.TRUE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/firmaAccidenteInformeFinalResponsableSeguridad", method = RequestMethod.POST)
	public String firmaAccidenteInformeFinalResponsableSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaInformeFinalResponsableSeguridad())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaInformeFinalResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaInformeFinalResponsableSeguridad(),
				generarAccidenteJSP.getPasswordFirmaInformeFinalResponsableSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaInformeFinalResponsableSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteInformeFinalResponsableSeguridad(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaInformeFinalResponsableSeguridad(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}

		generarAccidenteJSP.setFirmadoInformeFinal(Boolean.TRUE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/firmaAccidenteInformeFinalDelegadoSeguridad", method = RequestMethod.POST)
	public String firmaAccidenteInformeFinalDelegadoSeguridad(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaInformeFinalDelegadoSeguridad())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaInformeFinalDelegadoSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del delegado de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaInformeFinalDelegadoSeguridad(),
				generarAccidenteJSP.getPasswordFirmaInformeFinalDelegadoSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del delegado de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaInformeFinalDelegadoSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteInformeFinalDelegadoSeguridad(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaInformeFinalDelegadoSeguridad(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}

		generarAccidenteJSP.setFirmadoInformeFinal(Boolean.TRUE);
		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/anadirDocumentoInformeProvisionalCIAF", method = RequestMethod.POST)
	public String anadirDocumentoInformeProvisionalCIAF(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		try {
			generarAccidenteService.anadirDocumentoInformeProvisionalCIAF(generarAccidenteJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("generarAccidente", generarAccidenteJSP);
			return "generarAccidente";
		}

		model.addAttribute("generarAccidente", generarAccidenteJSP);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "generarAccidente";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaInformeRecibidoCIAFAccidente", method = RequestMethod.POST)
	public void verEvidenciaSFEviadoCIAF(GenerarAccidenteJSP generarAccidenteJSP, Model model,
			HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(generarAccidenteJSP.getIdAccidente());
		Documento documento = accidente.getEvidenciaTipo63().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}

	}

	@RequestMapping(value = "/firmaAccidenteFichaComentarios", method = RequestMethod.POST)
	public String firmaAccidenteFichaComentarios(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaFichaComentarios())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaFichaComentarios())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaFichaComentarios(),
				generarAccidenteJSP.getPasswordFirmaFichaComentarios());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaFichaComentarios(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteFichaComentarios(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaFichaComentarios(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}

		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/firmaAccidenteFichaMedidas", method = RequestMethod.POST)
	public String firmaAccidenteFichaMedidas(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		if (StringUtils.isBlank(generarAccidenteJSP.getNombreFirmaFichaMedidas())
				|| StringUtils.isBlank(generarAccidenteJSP.getPasswordFirmaFichaMedidas())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				generarAccidenteJSP.getNombreFirmaFichaMedidas(), generarAccidenteJSP.getPasswordFirmaFichaMedidas());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		} else {
			generarAccidenteJSP.setIdFirmaFichaMedidas(inspectorSeguridad.getIdPersonal());
		}

		try {
			generarAccidenteService.firmaAccidenteFichaMedidas(generarAccidenteJSP, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			generarAccidenteJSP.setIdFirmaFichaMedidas(null);
			model.addAttribute("generarAccidente", generarAccidenteJSP);

			return "generarAccidente";
		}

		model.addAttribute("generarAccidente", generarAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/paginaSiguienteAccidente", method = RequestMethod.POST)
	public String paginaSiguienteAccidente(GenerarAccidenteJSP generarInformeAccidenteJSP, Model model) {
		generarInformeAccidenteJSP
				.setPagina(String.valueOf((Integer.parseInt(generarInformeAccidenteJSP.getPagina()) + 1)));
		model.addAttribute("generarAccidente", generarInformeAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/paginaAnteriorAccidente", method = RequestMethod.POST)
	public String paginaAnteriorAccidente(GenerarAccidenteJSP generarInformeAccidenteJSP, Model model) {
		generarInformeAccidenteJSP
				.setPagina(String.valueOf((Integer.parseInt(generarInformeAccidenteJSP.getPagina()) - 1)));
		model.addAttribute("generarAccidente", generarInformeAccidenteJSP);
		return "generarAccidente";
	}

	@RequestMapping(value = "/volverAccidente", method = RequestMethod.POST)
	public String volverInvestigacion(GenerarAccidenteJSP generarAccidenteJSP, Model model) {

		return "redirect:/buscadorInvestigacion";

	}

	private GenerarAccidenteJSP convertIdAccidenteToGenerarAccidenteJSP(Integer idAccidente) {
		GenerarAccidenteJSP generarAccidenteJSP = new GenerarAccidenteJSP();
		Accidente accidente = accidenteDAO.getOne(idAccidente);

		// GLOBALES
		generarAccidenteJSP.setPagina("1");
		generarAccidenteJSP.setIdAccidente(accidente.getIdAccidente());
		generarAccidenteJSP.setIdEstadoAccidente(accidente.getEstadoAccidente().getIdEstadoAccidente());
		generarAccidenteJSP.setFirmadoFichaNotificacion(accidente.getFirmaFichaDelegacionResponsable() != null
				|| accidente.getFirmaFichaDelegacionDelegado() != null);
		generarAccidenteJSP.setFirmadoInformeFinal(accidente.getFirmaInformeFinalResponsable() != null
				|| accidente.getFirmaInformeFinalDelegado() != null);

		generarAccidenteJSP.setConfirmadoIntervieneCIAF(accidente.getIntervieneCiaf() != null);
		generarAccidenteJSP.setConfirmadoDelegaInvestigacion(accidente.getSeDelegaInvestigacion() != null);

		// PAGINA 1
		generarAccidenteJSP.setNumeroIdentificacion(accidente.getNumeroSuceso());
		generarAccidenteJSP.setIdEmpresa(accidente.getCompania().getIdCompania().toString());
		generarAccidenteJSP.setIdTipoAccidente(accidente.getTipoAccidente().getIdTipoAccidente().toString());
		generarAccidenteJSP.setIdCausa(accidente.getCausaAccidente().getIdCausaAccidente().toString());
		if (accidente.getFechaAccidente() != null) {
			generarAccidenteJSP.setFecha(
					Utiles.convertDateToString(accidente.getFechaAccidente(), Constantes.FORMATO_FECHA_PANTALLA));
		}
		generarAccidenteJSP.setHora(Utiles.formatStringHorasPantalla(accidente.getHoraAccidente()));
		generarAccidenteJSP.setLugar(accidente.getLugarAccidente());
		generarAccidenteJSP.setDescripcionSucesoInvestigacion(accidente.getDescripcion());
		generarAccidenteJSP.setCondicionesAtmosfericasInvestigacion(accidente.getCondicionesAtmosfericas());
		generarAccidenteJSP.setCirculacionesImplicadasInvestigacion(accidente.getCirculacionesImplicadas());
		generarAccidenteJSP.setComposicionTrenesInvestigacion(accidente.getComposicionTrenes());
		generarAccidenteJSP.setDanhiosMaterialRodanteInvestigacion(accidente.getDanosMaterialesMaterial());
		generarAccidenteJSP.setDanhiosInfraestructuraInvestigacion(accidente.getDanosMaterialesInfraestructura());
		generarAccidenteJSP.setPerturbacionesServicio(accidente.getPerturbacionesServicio());
		generarAccidenteJSP.setPrevisionesRestablecimiento(accidente.getPrevisionesRestablecimiento());
		generarAccidenteJSP.setPrimerasMedidasAdoptadas(accidente.getPrimerasMedidas());
		generarAccidenteJSP.setNotificacionesEfectuadas(accidente.getNotificacionesEfectuadas());
		generarAccidenteJSP.setObservacionesFichaAccidente(accidente.getObservacionesFichaAccidente());
		if (accidente.getHeridosTren() != null) {
			generarAccidenteJSP.setHeridosVictimaTren(accidente.getHeridosTren().toString());
		}
		if (accidente.getFallecidosTren() != null) {
			generarAccidenteJSP.setMuertesVictimaTren(accidente.getFallecidosTren().toString());
		}
		if (accidente.getHeridosAjenos() != null) {
			generarAccidenteJSP.setHeridosVictimaAjenaTren(accidente.getHeridosAjenos().toString());
		}
		if (accidente.getFallecidosAjenos() != null) {
			generarAccidenteJSP.setMuertesVictimaAjenaTren(accidente.getFallecidosAjenos().toString());
		}

		if (accidente.getFirmaFichaAccidente() != null) {
			generarAccidenteJSP.setIdFirmaFichaAccidente(accidente.getFirmaFichaAccidente().getIdPersonal());
		}
		// PAGINA 2
		generarAccidenteJSP.setIntervieneCIAF(accidente.getIntervieneCiaf());
		generarAccidenteJSP.setNombreResponsableCIAF(accidente.getInvestigadorCiafNombre());
		generarAccidenteJSP.setTelefonoResponsableCIAF(accidente.getInvestigadorCiafTelefono());
		generarAccidenteJSP.setCorreoResponsableCIAF(accidente.getInvestigadorCiafEmail());
		if (accidente.getResponsableSeguridad() != null) {
			generarAccidenteJSP
					.setIdResponsableSeguridad(accidente.getResponsableSeguridad().getIdPersonal().toString());
		}
		generarAccidenteJSP.setObservacionesIntervieneCIAF(accidente.getObservacionesIntervieneCiaf());
		if (accidente.getFirmaFichaEstructura() != null) {
			generarAccidenteJSP.setIdFirmaFichaEstructura(accidente.getFirmaFichaEstructura().getIdPersonal());
		}
		generarAccidenteJSP.setDelegaInvestigacion(accidente.getSeDelegaInvestigacion());
		if (accidente.getDelegado() != null) {
			generarAccidenteJSP.setIdDelegadoInvestigacion(accidente.getDelegado().getIdPersonal().toString());
		}
		if (accidente.getFirmaFichaDelegacionResponsable() != null) {
			generarAccidenteJSP.setIdFirmaFichaNotificacionResponsableSeguridad(
					accidente.getFirmaFichaDelegacionResponsable().getIdPersonal());
		}
		if (accidente.getFirmaFichaDelegacionDelegado() != null) {
			generarAccidenteJSP.setIdFirmaFichaNotificacionDelegadoSeguridad(
					accidente.getFirmaFichaDelegacionDelegado().getIdPersonal());
		}

		// Pagina 3
		generarAccidenteJSP.setNumeroInformeFinal(accidente.getNumeroReferenciaFinal());
		if (accidente.getFechaInformeFinal() != null) {
			generarAccidenteJSP.setFechaInformeFinal(
					Utiles.convertDateToString(accidente.getFechaInformeFinal(), Constantes.FORMATO_FECHA_PANTALLA));
		}
		generarAccidenteJSP.setSucesoInformeFinal(accidente.getHechosSuceso());
		generarAccidenteJSP.setCircunstanciasSucesoInformeFinal(accidente.getHechosCircunstancias());
		generarAccidenteJSP.setDanosInformeFinal(accidente.getHechosDanos());
		generarAccidenteJSP.setCircunstanciasExternasInformeFinal(accidente.getHechosCircunstanciasExternas());
		generarAccidenteJSP.setResumenDeclaracionesTestigosInformeFinal(accidente.getResumenDeclaraciones());
		generarAccidenteJSP.setSistemaGestionSeguridadInformeFinal(accidente.getResumenSgs());
		generarAccidenteJSP.setNormativaInformeFinal(accidente.getResumenNormativa());
		generarAccidenteJSP.setFuncionamientoMaterialRodanteInformeFinal(accidente.getResumenFuncionamiento());
		generarAccidenteJSP.setDocumentacionGestionCirculacionInformeFinal(accidente.getResumenDocumentacion());
		generarAccidenteJSP.setInterfazHombreMaquinaInformeFinal(accidente.getResumenInterfaz());
		generarAccidenteJSP.setOtrosSucesosAnterioresInformeFinal(accidente.getResumenOtros());
		generarAccidenteJSP.setDescripcionDefinitivaInformeFinal(accidente.getAnalisisDescripcion());
		generarAccidenteJSP.setDeliberacionInformeFinal(accidente.getAnalisisDeliberacion());
		generarAccidenteJSP.setConclusionesInformeFinal(accidente.getAnalisisConclusiones());
		generarAccidenteJSP.setObservacionesAdicionalesInformeFinal(accidente.getAnalisisObservaciones());
		generarAccidenteJSP.setMedidasAdoptadasInformeFinal(accidente.getMedidasAdoptadas());
		generarAccidenteJSP.setRecomendacionesInformeFinal(accidente.getRecomendaciones());
		generarAccidenteJSP.setDatosComplementariosInformeFinal(accidente.getDatosComplementarios());

		if (accidente.getFirmaInformeFinalResponsable() != null) {
			generarAccidenteJSP.setIdFirmaInformeFinalResponsableSeguridad(
					accidente.getFirmaInformeFinalResponsable().getIdPersonal());
		}
		if (accidente.getFirmaInformeFinalDelegado() != null) {
			generarAccidenteJSP
					.setIdFirmaInformeFinalDelegadoSeguridad(accidente.getFirmaInformeFinalDelegado().getIdPersonal());
		}

		// PAGINA 4
		generarAccidenteJSP.setNumIdentificacion(accidente.getNumIdInfCiaf());
		if (accidente.getFechaInfCiaf() != null) {
			generarAccidenteJSP.setFechaCIAF(
					Utiles.convertDateToString(accidente.getFechaInfCiaf(), Constantes.FORMATO_FECHA_PANTALLA));
		}
		generarAccidenteJSP.setComentariosInformeCIAF(accidente.getComentariosAlInfCiaf());
		generarAccidenteJSP.setObservacionesIntervencionCIAF(accidente.getObservacionesAlInfCiaf());
		if (accidente.getFirmaFichaComentariosCiaf() != null) {
			generarAccidenteJSP.setIdFirmaFichaComentarios(accidente.getFirmaFichaComentariosCiaf().getIdPersonal());
		}

		generarAccidenteJSP.setNumRefInformeFinalCIAF(accidente.getFichaMedidasRefInforme());
		generarAccidenteJSP.setAnnoReferencia(accidente.getFichaMedidasAno());
		generarAccidenteJSP.setMedidasAdoptadas(accidente.getFichaMedidasAdoptadas());
		generarAccidenteJSP.setMedidasProyectadas(accidente.getFichaMedidasProyectadas());
		generarAccidenteJSP.setObservaciones(accidente.getFichaMedidasObservaciones());
		if (accidente.getFirmaFichaMedidas() != null) {
			generarAccidenteJSP.setIdFirmaFichaMedidas(accidente.getFirmaFichaMedidas().getIdPersonal());
		}

		return generarAccidenteJSP;
	}

}