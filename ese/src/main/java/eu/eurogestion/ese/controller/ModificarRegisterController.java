package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.DocumentoPersonal;
import eu.eurogestion.ese.domain.Idioma;
import eu.eurogestion.ese.domain.IdiomaPersona;
import eu.eurogestion.ese.domain.Localidad;
import eu.eurogestion.ese.domain.NivelIdioma;
import eu.eurogestion.ese.domain.Pais;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.Provincia;
import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorPersonalJSP;
import eu.eurogestion.ese.pojo.UsuarioJSP;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.DocumentoPersonalDAO;
import eu.eurogestion.ese.repository.IdiomaDAO;
import eu.eurogestion.ese.repository.IdiomaPersonaDAO;
import eu.eurogestion.ese.repository.LocalidadDAO;
import eu.eurogestion.ese.repository.NivelIdiomaDAO;
import eu.eurogestion.ese.repository.PaisDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ProvinciaDAO;
import eu.eurogestion.ese.repository.RolDAO;
import eu.eurogestion.ese.repository.TipoCompaniaDAO;
import eu.eurogestion.ese.services.RegistroService;
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
public class ModificarRegisterController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio Cargo
	 */
	@Autowired
	public CargoDAO cargoDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public TipoCompaniaDAO tipoCompaniaDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public RolDAO rolDAO;

	/**
	 * Repositorio de la clase de dominio Pais
	 */
	@Autowired
	public PaisDAO paisDAO;

	/**
	 * Repositorio de la clase de dominio Provincia
	 */
	@Autowired
	public ProvinciaDAO provinciaDAO;

	/**
	 * Repositorio de la clase de dominio Localidad
	 */
	@Autowired
	public LocalidadDAO localidadDAO;

	/**
	 * Repositorio de la clase de dominio Idioma
	 */
	@Autowired
	public IdiomaDAO idiomaDAO;

	/**
	 * Repositorio de la clase de dominio Idioma
	 */
	@Autowired
	public DocumentoPersonalDAO documentoPersonalDAO;

	/**
	 * Repositorio de la clase de dominio Idioma
	 */
	@Autowired
	public NivelIdiomaDAO nivelIdiomaDAO;

	/**
	 * Repositorio de la clase de dominio IdiomaPersona
	 */
	@Autowired
	public IdiomaPersonaDAO idiomaPersonaDAO;

	/**
	 * Servicio para registrar
	 */
	@Autowired
	public RegistroService registroService;

	/**
	 * Servicio para registrar
	 */
	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de compañias para un select
	 * 
	 * @return lista de objetos Compania
	 */
	@ModelAttribute("companias")
	public List<Compania> listCompaniasAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAllCompaniaAlta());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de cargos para un select
	 * 
	 * @return lista de objetos Cargo
	 */
	@ModelAttribute("cargos")
	public List<Cargo> listCargoAll() {
		List<Cargo> lista = new ArrayList<>();
		Cargo cargo = new Cargo();
		cargo.setNombre("Selecciona uno:");
		lista.add(cargo);
		lista.addAll(cargoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de roles para un select
	 * 
	 * @return lista de objetos Rol
	 */
	@ModelAttribute("roles")
	public List<Rol> listCompaniasAlta() {
		List<Rol> lista = new ArrayList<>();
		Rol rol = new Rol();
		rol.setNombre("Selecciona uno:");
		lista.add(rol);
		lista.addAll(rolDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de paises para un select
	 * 
	 * @return lista de objetos Pais
	 */
	@ModelAttribute("paises")
	public List<Pais> listPaisAll() {
		List<Pais> lista = new ArrayList<>();
		Pais pais = new Pais();
		pais.setNombre("Selecciona uno:");
		lista.add(pais);
		lista.addAll(paisDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de idiomas para una tabla
	 * 
	 * @return lista de objetos Idioma
	 */
	@ModelAttribute("idiomas")
	public List<Idioma> listIdiomaAll() {
		List<Idioma> lista = new ArrayList<>();
		Idioma idioma = new Idioma();
		idioma.setNombre("Selecciona uno:");
		lista.add(idioma);
		lista.addAll(idiomaDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de idiomas para una tabla
	 * 
	 * @return lista de objetos Idioma
	 */
	@ModelAttribute("nivelIdiomas")
	public List<NivelIdioma> listNivelIdiomaAll() {
		List<NivelIdioma> lista = new ArrayList<>();
		NivelIdioma nivelIdioma = new NivelIdioma();
		nivelIdioma.setNombre("Selecciona uno:");
		lista.add(nivelIdioma);
		lista.addAll(nivelIdiomaDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de provincias para un select
	 * 
	 * @return lista de objetos Provincia
	 */
	private List<Provincia> listProvinciaByPais(Integer idPais) {
		List<Provincia> lista = new ArrayList<>();
		Provincia provincia = new Provincia();
		provincia.setNombre("Selecciona uno:");
		lista.add(provincia);
		lista.addAll(provinciaDAO.findProvinciaByPais(idPais));
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	private List<Localidad> listLocalidadByPaisAndProvincia(Integer idPais, Integer idProvincia) {
		List<Localidad> lista = new ArrayList<>();
		Localidad localidad = new Localidad();
		localidad.setNombre("Selecciona uno:");
		lista.add(localidad);
		lista.addAll(localidadDAO.findLocalidadByPaisAndProvincia(idPais, idProvincia));
		return lista;
	}

	@RequestMapping(value = "/cargarProvinciasModificarRegister", method = RequestMethod.POST)
	public String cargarProvinciaRegister(final UsuarioJSP formularioUser, BindingResult result, Model model) {

		log.info("se cargan las provincias");
		formularioUser.setIdLocalidad("");
		formularioUser.setIdProvincia("");
		model.addAttribute("modificarRegister", formularioUser);
		if (StringUtils.isBlank(formularioUser.getIdPais())) {
			return "modificarRegister";
		}
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(formularioUser.getIdPais())));
		return "modificarRegister";

	}

	@RequestMapping(value = "/cargarLocalidadesModificarRegister", method = RequestMethod.POST)
	public String cargarLocalidadRegister(final UsuarioJSP formularioUser, BindingResult result, Model model) {

		log.info("se cargan las provincias");
		formularioUser.setIdLocalidad("");
		model.addAttribute("modificarRegister", formularioUser);
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(formularioUser.getIdPais())));
		if (StringUtils.isBlank(formularioUser.getIdProvincia())) {
			return "modificarRegister";
		}
		model.addAttribute("localidades", listLocalidadByPaisAndProvincia(Integer.parseInt(formularioUser.getIdPais()),
				Integer.parseInt(formularioUser.getIdProvincia())));
		return "modificarRegister";

	}

	private void cargarListaValores(String idPais, String idProvincia, Model model, String idPersonal, int page,
			int size, int pageDocumentos) {
		if (StringUtils.isNotBlank(idPais)) {
			model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(idPais)));
		}
		if (StringUtils.isNotBlank(idPais) && StringUtils.isNotBlank(idProvincia)) {

			model.addAttribute("localidades",
					listLocalidadByPaisAndProvincia(Integer.parseInt(idPais), Integer.parseInt(idProvincia)));
		}

		if (StringUtils.isNotBlank(idPersonal)) {
			model.addAttribute("listIdiomasPersona",
					idiomaPersonaDAO.findIdiomaPersonaByPersonal(idPersonal, PageRequest.of(page, size)));
			model.addAttribute("listDocumentosPersona",
					documentoPersonalDAO.findAllByIdPersonal(idPersonal, PageRequest.of(pageDocumentos, size)));
		}
	}

	/**
	 * Metodo que busca en base de datos el usuario y la contraseña introducidos en
	 * pantalla para ver si existe en el sistema y rellena el objeto usuario de la
	 * sesion
	 * 
	 * @param usuarioLogin objeto con los campos de la pantalla
	 * @param model        objeto model de la pantalla
	 * @param session      objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/cargarModificarRegister", method = RequestMethod.POST)
	public String cargarModificarRegister(final BuscadorPersonalJSP personalJSP, Model model, HttpSession session) {

		Personal personal = personalDAO.getOne(Integer.parseInt(personalJSP.getIdPersonal()));

		UsuarioJSP usuarioJSP = convertPersonalToUsuarioJSP(personal);
		usuarioJSP.setLectura(Boolean.FALSE);
		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), 0, 5,
				0);
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	/**
	 * Metodo que busca en base de datos el usuario y la contraseña introducidos en
	 * pantalla para ver si existe en el sistema y rellena el objeto usuario de la
	 * sesion
	 * 
	 * @param usuarioLogin objeto con los campos de la pantalla
	 * @param model        objeto model de la pantalla
	 * @param session      objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/cargarVerRegister", method = RequestMethod.POST)
	public String cargarVerRegister(final BuscadorPersonalJSP personalJSP, Model model, HttpSession session) {

		Personal personal = personalDAO.getOne(Integer.parseInt(personalJSP.getIdPersonal()));

		UsuarioJSP usuarioJSP = convertPersonalToUsuarioJSP(personal);
		usuarioJSP.setLectura(Boolean.TRUE);
		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), 0, 5,
				0);
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	@RequestMapping(value = "/anadirEvidenciaIdiomaPersona", method = RequestMethod.POST)
	public String anadirEvidenciaIdiomaPersona(UsuarioJSP usuarioJSP, Model model, HttpSession sesion) {

		int page = 0; // default page number is 0 (yes it is weird)
		int pageDocumento = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(usuarioJSP.getPage())) {
			page = Integer.parseInt(usuarioJSP.getPage()) - 1;
		}
		if (StringUtils.isNotBlank(usuarioJSP.getPageDocumento())) {
			pageDocumento = Integer.parseInt(usuarioJSP.getPageDocumento()) - 1;
		}
		boolean errorValidacion = false;
		List<String> errores = new ArrayList<>();
		if (StringUtils.isBlank(usuarioJSP.getIdIdioma())) {
			String error = "El idioma no puede estar vacio";
			errores.add(error);
			errorValidacion = true;
		}

		if (StringUtils.isBlank(usuarioJSP.getIdNivelIdioma())) {
			String error = "El nivel del idioma no puede estar vacio";
			errores.add(error);
			errorValidacion = true;
		}

		if (StringUtils.isNotBlank(usuarioJSP.getIdNivelIdioma())
				&& Constantes.NIVEL_IDIOMA_NATIVO.toString().equals(usuarioJSP.getIdNivelIdioma())
				&& StringUtils.isNotBlank(usuarioJSP.getEvidencia().getOriginalFilename())) {
			String error = "Un idioma nativo no necesita tener una evidencia";
			errores.add(error);
			errorValidacion = true;
		}

		if (StringUtils.isNotBlank(usuarioJSP.getIdNivelIdioma())
				&& !Constantes.NIVEL_IDIOMA_NATIVO.toString().equals(usuarioJSP.getIdNivelIdioma())
				&& StringUtils.isBlank(usuarioJSP.getEvidencia().getOriginalFilename())) {
			String error = "Un idioma no nativo tiene que tener una evidencia";
			errores.add(error);
			errorValidacion = true;
		}

		if (StringUtils.isNotBlank(usuarioJSP.getIdNivelIdioma())
				&& Constantes.NIVEL_IDIOMA_NATIVO.toString().equals(usuarioJSP.getIdNivelIdioma())
				&& StringUtils.isNotBlank(usuarioJSP.getFechaIdioma())) {
			String error = "Un idioma nativo no necesita tener una fecha para el idioma";
			errores.add(error);
			errorValidacion = true;
		}

		if (StringUtils.isNotBlank(usuarioJSP.getIdNivelIdioma())
				&& !Constantes.NIVEL_IDIOMA_NATIVO.toString().equals(usuarioJSP.getIdNivelIdioma())
				&& StringUtils.isBlank(usuarioJSP.getFechaIdioma())) {
			String error = "Un idioma no nativo tiene que tener una fecha para el idioma";
			errores.add(error);
			errorValidacion = true;
		}

		if (!StringUtils.isBlank(usuarioJSP.getIdIdioma())
				&& idiomaPersonaDAO.existIdiomaPersonaByPersonalAndIdiomaAndFechaBajaNull(usuarioJSP.getIdPersonal(),
						usuarioJSP.getIdIdioma())) {
			String error = "Ya tiene este idioma activo. Debe desactivarlo para añadir un nuevo nivel.";
			errores.add(error);
			errorValidacion = true;
		}

		if (errorValidacion) {
			model.addAttribute("error", errores);
			cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(),
					page, size, pageDocumento);
			model.addAttribute("modificarRegister", usuarioJSP);
			return "modificarRegister";
		}
		try {
			registroService.anadirEvidenciaIdiomaPersona(usuarioJSP, sesion);

		} catch (EseException e) {
			log.error(e.getMessage());
			cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(),
					page, size, pageDocumento);
			model.addAttribute("modificarRegister", usuarioJSP);
			return "modificarRegister";
		}

		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), page,
				size, pageDocumento);
		model.addAttribute("info", " Se ha añadido correctamente");
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	@RequestMapping(value = "/anadirDocumentoPersonal", method = RequestMethod.POST)
	public String anadirDocumentoPersonal(UsuarioJSP usuarioJSP, Model model, HttpSession sesion) {

		int page = 0; // default page number is 0 (yes it is weird)
		int pageDocumento = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(usuarioJSP.getPage())) {
			page = Integer.parseInt(usuarioJSP.getPage()) - 1;
		}
		if (StringUtils.isNotBlank(usuarioJSP.getPageDocumento())) {
			pageDocumento = Integer.parseInt(usuarioJSP.getPageDocumento()) - 1;
		}
		boolean errorValidacion = false;
		List<String> errores = new ArrayList<>();
		if (StringUtils.isBlank(usuarioJSP.getTituloDocumento())) {
			String error = "El titulo del documento no puede estar vacio";
			errores.add(error);
			errorValidacion = true;
		}

		if (StringUtils.isBlank(usuarioJSP.getFechaDocumento())) {
			String error = "La fecha del documento no puede estar vacia";
			errores.add(error);
			errorValidacion = true;
		}

		if (StringUtils.isBlank(usuarioJSP.getObservacionesDocumento())) {
			String error = "Las observaciones del documento no pueden estar vacias";
			errores.add(error);
			errorValidacion = true;
		}

		if (errorValidacion) {
			model.addAttribute("error", errores);
			cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(),
					page, size, pageDocumento);
			model.addAttribute("modificarRegister", usuarioJSP);
			return "modificarRegister";
		}
		try {
			registroService.anadirDocumentoPersonal(usuarioJSP, sesion);

		} catch (EseException e) {
			log.error(e.getMessage());
			cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(),
					page, size, pageDocumento);
			model.addAttribute("modificarRegister", usuarioJSP);
			return "modificarRegister";
		}

		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), page,
				size, pageDocumento);
		model.addAttribute("info", " Se ha añadido correctamente");
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	@RequestMapping(value = "/verEvidenciaIdiomaPersona", method = RequestMethod.POST)
	public void verEvidenciaIdiomaPersona(UsuarioJSP usuarioJSP, Model model, HttpServletResponse response) {

		IdiomaPersona idiomaPersona = idiomaPersonaDAO.getOne(Integer.parseInt(usuarioJSP.getIdIdiomaPersona()));
		Documento documento = idiomaPersona.getEvidencia82().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/verDocumentoPersonal", method = RequestMethod.POST)
	public void verDocumentoPersonal(UsuarioJSP usuarioJSP, Model model, HttpServletResponse response) {

		DocumentoPersonal documentoPersonal = documentoPersonalDAO
				.getOne(Integer.parseInt(usuarioJSP.getIdDocumentoPersona()));
		Documento documento = documentoPersonal.getDocumento();
		try {
			utilesPDFService.descargarDocumento(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/activarIdiomaPersona", method = RequestMethod.POST)
	public String activarIdiomaPersona(UsuarioJSP usuarioJSP, Model model, HttpServletResponse response) {

		int page = 0; // default page number is 0 (yes it is weird)
		int pageDocumento = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(usuarioJSP.getPage())) {
			page = Integer.parseInt(usuarioJSP.getPage()) - 1;
		}
		if (StringUtils.isNotBlank(usuarioJSP.getPageDocumento())) {
			pageDocumento = Integer.parseInt(usuarioJSP.getPageDocumento()) - 1;
		}
		IdiomaPersona idiomaPersona = idiomaPersonaDAO.getOne(Integer.parseInt(usuarioJSP.getIdIdiomaPersona()));
		if (idiomaPersonaDAO.existIdiomaPersonaByPersonalAndIdiomaAndFechaBajaNull(usuarioJSP.getIdPersonal(),
				idiomaPersona.getIdioma().getIdIdioma().toString())) {
			model.addAttribute("error",
					"Ya tiene este idioma activo. Debe desactivarlo para poder activar otro nivel.");
			cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(),
					page, size, pageDocumento);
			model.addAttribute("modificarRegister", usuarioJSP);
			return "modificarRegister";
		}

		try {
			registroService.activarIdiomaPersona(usuarioJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
		}
		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), page,
				size, pageDocumento);
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";

	}

	@RequestMapping(value = "/desactivarIdiomaPersona", method = RequestMethod.POST)
	public String desactivarIdiomaPersona(UsuarioJSP usuarioJSP, Model model, HttpServletResponse response) {

		int page = 0; // default page number is 0 (yes it is weird)
		int pageDocumento = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(usuarioJSP.getPage())) {
			page = Integer.parseInt(usuarioJSP.getPage()) - 1;
		}
		if (StringUtils.isNotBlank(usuarioJSP.getPageDocumento())) {
			pageDocumento = Integer.parseInt(usuarioJSP.getPageDocumento()) - 1;
		}
		try {
			registroService.desactivarIdiomaPersona(usuarioJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
		}
		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), page,
				size, pageDocumento);
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	private UsuarioJSP convertPersonalToUsuarioJSP(Personal personal) {

		UsuarioJSP usuarioJSP = new UsuarioJSP();

		usuarioJSP.setIdPersonal(personal.getIdPersonal().toString());
		usuarioJSP.setNombre(personal.getNombre());
		usuarioJSP.setApellido(personal.getApellido1());
		usuarioJSP.setApellido2(personal.getApellido2());
		usuarioJSP.setDocumento(personal.getDocumento());
		usuarioJSP.setFechaNacimiento(
				Utiles.convertDateToString(personal.getFechaNac(), Constantes.FORMATO_FECHA_PANTALLA));
		usuarioJSP.setTipoVia(personal.getTipoVia());
		usuarioJSP.setVia(personal.getVia());
		usuarioJSP.setNumero(personal.getNumero());
		usuarioJSP.setPlanta(personal.getPlanta());
		usuarioJSP.setPuerta(personal.getPuerta());
		usuarioJSP.setCp(personal.getCp());
		usuarioJSP.setIdCompania(personal.getCompania().getIdCompania().toString());
		usuarioJSP.setIdCargo(personal.getCargo().getIdCargo().toString());
		usuarioJSP.setIdRol(personal.getRol().getIdRol().toString());
		usuarioJSP.setLicencia(personal.getLicencia());
		usuarioJSP.setDocEmpresa(personal.getDocEmpresa());
		usuarioJSP.setEmail(personal.getEmail());
		usuarioJSP.setTelefono(personal.getTelefono());

		usuarioJSP.setNacionalidad(personal.getNacionalidad());
		usuarioJSP.setLugarNacimiento(personal.getLugarNacimiento());
		usuarioJSP.setIdPais(personal.getPais().getIdPais().toString());
		usuarioJSP.setIdProvincia(personal.getProvincia().getIdProvincia().toString());
		usuarioJSP.setIdLocalidad(personal.getLocalidad().getIdLocalidad().toString());

		return usuarioJSP;
	}

	/**
	 * Metodo que inserta un usuario en base de datos con los datos introducidos en
	 * la pantalla despues de validar dichos datos
	 * 
	 * @param usuarioJSP objeto con los campos de la pantalla
	 * @param result     objeto BindingResult de la pantalla
	 * @param model      objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarModificarRegister", method = RequestMethod.POST)
	public String guardarModificarRegister(final UsuarioJSP usuarioJSP, BindingResult result, Model model) {
		int page = 0; // default page number is 0 (yes it is weird)
		int pageDocumento = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(usuarioJSP.getPage())) {
			page = Integer.parseInt(usuarioJSP.getPage()) - 1;
		}
		if (StringUtils.isNotBlank(usuarioJSP.getPageDocumento())) {
			pageDocumento = Integer.parseInt(usuarioJSP.getPageDocumento()) - 1;
		}

		List<String> errores = new ArrayList<>();
		if (!formRegUsuarioValido(usuarioJSP, model, errores)) {
			model.addAttribute("error", errores);
			model.addAttribute("modificarRegister", usuarioJSP);
			cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(),
					page, size, pageDocumento);
			return "modificarRegister";
		}

		try {
			registroService.modificarRegistroUsuario(usuarioJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(),
					page, size, pageDocumento);
			model.addAttribute("modificarRegister", usuarioJSP);
			return "modificarRegister";
		}

		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), page,
				size, pageDocumento);
		model.addAttribute("info", "Se ha guardado correctamente");
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	/**
	 * Metodo que devuelve a la pantalla inicial
	 * 
	 * @param session objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverModificarRegister", method = { RequestMethod.GET, RequestMethod.POST })
	public String cancelar(HttpSession session) {

		return "redirect:/buscadorPersonal";
	}

	@RequestMapping(value = "/paginacionTablaModificarRegister", method = RequestMethod.POST)
	public String paginacionTablaModificarRegister(UsuarioJSP usuarioJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int pageDocumento = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(usuarioJSP.getPage())) {
			usuarioJSP.setPage(usuarioJSP.getPage().substring(usuarioJSP.getPage().indexOf(",") + 1,
					usuarioJSP.getPage().length()));
			page = Integer.parseInt(usuarioJSP.getPage()) - 1;
		}

		if (StringUtils.isNotBlank(usuarioJSP.getPageDocumento())) {
			pageDocumento = Integer.parseInt(usuarioJSP.getPageDocumento()) - 1;
		}

		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), page,
				size, pageDocumento);
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	@RequestMapping(value = "/paginacionTablaDocumentosModificarRegister", method = RequestMethod.POST)
	public String paginacionTablaDocumentosModificarRegister(UsuarioJSP usuarioJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int pageDocumento = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(usuarioJSP.getPage())) {
			page = Integer.parseInt(usuarioJSP.getPage()) - 1;
		}

		if (StringUtils.isNotBlank(usuarioJSP.getPageDocumento())) {
			usuarioJSP.setPageDocumento(usuarioJSP.getPageDocumento()
					.substring(usuarioJSP.getPageDocumento().indexOf(",") + 1, usuarioJSP.getPageDocumento().length()));
			pageDocumento = Integer.parseInt(usuarioJSP.getPageDocumento()) - 1;
		}

		cargarListaValores(usuarioJSP.getIdPais(), usuarioJSP.getIdProvincia(), model, usuarioJSP.getIdPersonal(), page,
				size, pageDocumento);
		model.addAttribute("modificarRegister", usuarioJSP);
		return "modificarRegister";
	}

	private boolean emailCorrecto(String email) {
		return personalDAO.findByEmailAndFechaBajaIsNull(email) == null;
	}

	/**
	 * Metodo que valida los campos del formulario del registro de usuarios
	 * 
	 * @param newUser objeto con los campos de la pantalla
	 * @param model   modelo de la pantala
	 * @param error   BindingResult de la pantalla
	 */
	private boolean formRegUsuarioValido(UsuarioJSP newUser, Model model, List<String> errores) {
		boolean valido = Boolean.TRUE;

		if (StringUtils.isBlank(newUser.getNombre())) {
			valido = Boolean.FALSE;
			log.error("El nombre no puede estar vacio");
			errores.add("El nombre no puede estar vacio");
		}
		if (StringUtils.isBlank(newUser.getApellido())) {
			valido = Boolean.FALSE;
			log.error("El apellido no puede estar vacio");
			errores.add("El apellido no puede estar vacio");
		}
		if (StringUtils.isBlank(newUser.getDocumento())) {
			valido = Boolean.FALSE;
			log.error("El DNI no puede estar vacio");
			errores.add("El DNI no puede estar vacio");
		} else if (!Utiles.dniValido(newUser.getDocumento())) {
			valido = Boolean.FALSE;
			log.error("El DNI es incorrecto");
			errores.add("El DNI es incorrecto");
		}
		if (StringUtils.isBlank(newUser.getEmail())) {
			valido = Boolean.FALSE;
			log.error("El email no puede estar vacio");
			errores.add("El email no puede estar vacio");
		}

		if (StringUtils.isBlank(newUser.getEmail()) && !emailCorrecto(newUser.getEmail())) {
			valido = Boolean.FALSE;
			log.error("El email ya esta registrado");
			errores.add("El email ya esta registrado");
		}

		if (StringUtils.isBlank(newUser.getFechaNacimiento())) {
			valido = Boolean.FALSE;
			log.error("La fecha no puede estar vacia");
			errores.add("La fecha no puede estar vacia");
		} else {

			if (Utiles.parseDatePantalla(newUser.getFechaNacimiento()) == null) {
				valido = Boolean.FALSE;
				log.error("La fecha introducida es incorrecta");
				errores.add("La fecha introducida es incorrecta");
			}
		}

		return valido;
	}

}