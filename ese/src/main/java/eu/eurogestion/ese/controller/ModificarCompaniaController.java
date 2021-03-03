package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Idioma;
import eu.eurogestion.ese.domain.Localidad;
import eu.eurogestion.ese.domain.Pais;
import eu.eurogestion.ese.domain.Provincia;
import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.domain.TipoCompania;
import eu.eurogestion.ese.pojo.BuscadorCompaniaJSP;
import eu.eurogestion.ese.pojo.CompaniaJSP;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.IdiomaDAO;
import eu.eurogestion.ese.repository.IdiomaPersonaDAO;
import eu.eurogestion.ese.repository.LocalidadDAO;
import eu.eurogestion.ese.repository.PaisDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ProvinciaDAO;
import eu.eurogestion.ese.repository.RolDAO;
import eu.eurogestion.ese.repository.TipoCompaniaDAO;
import eu.eurogestion.ese.services.RegistroService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class ModificarCompaniaController {

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

	/**
	 * Metodo que devuelve una lista de idiomas para una tabla
	 * 
	 * @return lista de objetos Idioma
	 */
	@ModelAttribute("idiomas")
	public List<Idioma> listIdiomaAll() {
		List<Idioma> lista = new ArrayList<>();
		lista.addAll(idiomaDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de tipo de compañias para un select
	 * 
	 * @return lista de objetos TipoCompania
	 */
	@ModelAttribute("tiposCompania")
	public List<TipoCompania> listTiposCompanias() {
		List<TipoCompania> lista = new ArrayList<>();
		TipoCompania tipoCompania = new TipoCompania();
		tipoCompania.setValor("Selecciona uno:");
		lista.add(tipoCompania);
		lista.addAll(tipoCompaniaDAO.findAll());
		return lista;
	}

	@RequestMapping(value = "/cargarProvinciasModificarRegisterCompania", method = RequestMethod.POST)
	public String cargarProvinciaRegister(final CompaniaJSP modificarCompania, BindingResult result, Model model) {

		log.info("se cargan las provincias");
		modificarCompania.setIdLocalidad("");
		modificarCompania.setIdProvincia("");
		model.addAttribute("modificarCompania", modificarCompania);
		if (StringUtils.isBlank(modificarCompania.getIdPais())) {
			return "modificarCompania";
		}
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(modificarCompania.getIdPais())));
		return "modificarCompania";

	}

	@RequestMapping(value = "/cargarLocalidadesModificarRegisterCompania", method = RequestMethod.POST)
	public String cargarLocalidadRegister(final CompaniaJSP modificarCompania, BindingResult result, Model model) {

		log.info("se cargan las provincias");
		modificarCompania.setIdLocalidad("");
		model.addAttribute("modificarCompania", modificarCompania);
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(modificarCompania.getIdPais())));
		if (StringUtils.isBlank(modificarCompania.getIdProvincia())) {
			return "modificarCompania";
		}
		model.addAttribute("localidades", listLocalidadByPaisAndProvincia(
				Integer.parseInt(modificarCompania.getIdPais()), Integer.parseInt(modificarCompania.getIdProvincia())));
		return "modificarCompania";

	}

	private void cargarListaValores(String idPais, String idProvincia, Model model) {
		if (StringUtils.isNotBlank(idPais)) {
			model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(idPais)));
		}
		if (StringUtils.isNotBlank(idPais) && StringUtils.isNotBlank(idProvincia)) {

			model.addAttribute("localidades",
					listLocalidadByPaisAndProvincia(Integer.parseInt(idPais), Integer.parseInt(idProvincia)));
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
	@RequestMapping(value = "/cargarModificarCompania", method = RequestMethod.POST)
	public String cargarModificarCompania(final BuscadorCompaniaJSP buscadorCompaniaJSP, Model model,
			HttpSession session) {

		Compania compania = companiaDAO.getOne(Integer.parseInt(buscadorCompaniaJSP.getIdCompania()));

		CompaniaJSP companiaJSP = convertCompaniaToCompaniaJSP(compania);
		companiaJSP.setLectura(Boolean.FALSE);
		cargarListaValores(companiaJSP.getIdPais(), companiaJSP.getIdProvincia(), model);
		model.addAttribute("modificarCompania", companiaJSP);
		return "modificarCompania";
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
	@RequestMapping(value = "/cargarVerCompania", method = RequestMethod.POST)
	public String cargarVerCompania(final BuscadorCompaniaJSP buscadorCompaniaJSP, Model model, HttpSession session) {

		Compania compania = companiaDAO.getOne(Integer.parseInt(buscadorCompaniaJSP.getIdCompania()));

		CompaniaJSP companiaJSP = convertCompaniaToCompaniaJSP(compania);
		companiaJSP.setLectura(Boolean.TRUE);
		cargarListaValores(companiaJSP.getIdPais(), companiaJSP.getIdProvincia(), model);
		model.addAttribute("modificarCompania", companiaJSP);
		return "modificarCompania";
	}

	private CompaniaJSP convertCompaniaToCompaniaJSP(Compania compania) {

		CompaniaJSP companiaJSP = new CompaniaJSP();

		companiaJSP.setIdCompania(compania.getIdCompania().toString());
		companiaJSP.setNombre(compania.getNombre());
		companiaJSP.setTelefono(compania.getTelefono());
		companiaJSP.setTipoCompania(compania.getTipoCompania().getIdTipoCompania().toString());
		companiaJSP.setDocumento(compania.getDocumento());
		companiaJSP.setLicencia(compania.getLicencia());
		companiaJSP.setTipoVia(compania.getTipoVia());
		companiaJSP.setVia(compania.getVia());
		companiaJSP.setNumero(compania.getNumero());
		companiaJSP.setPlanta(compania.getPlanta());
		companiaJSP.setPuerta(compania.getPuerta());
		companiaJSP.setIdPais(compania.getPais().getIdPais().toString());
		companiaJSP.setIdProvincia(compania.getProvincia().getIdProvincia().toString());
		companiaJSP.setIdLocalidad(compania.getLocalidad().getIdLocalidad().toString());
		companiaJSP.setEmail(compania.getEmail());
		companiaJSP.setObservaciones(compania.getObservaciones());

		return companiaJSP;
	}

	/**
	 * Metodo que inserta un usuario en base de datos con los datos introducidos en
	 * la pantalla despues de validar dichos datos
	 * 
	 * @param companiaJSP objeto con los campos de la pantalla
	 * @param result      objeto BindingResult de la pantalla
	 * @param model       objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarModificarCompania", method = RequestMethod.POST)
	public String guardarModificarCompania(final CompaniaJSP companiaJSP, BindingResult result, Model model) {

		if (!formRegCompaniaValido(companiaJSP, model, result)) {
			model.addAttribute("modificarCompania", companiaJSP);
			cargarListaValores(companiaJSP.getIdPais(), companiaJSP.getIdProvincia(), model);
			return "modificarCompania";
		}
		try {
			Compania compania = crearCompaniaByForm(companiaJSP);

			companiaDAO.save(compania);
		} catch (Exception e) {
			log.error(e.getMessage());

		}

		cargarListaValores(companiaJSP.getIdPais(), companiaJSP.getIdProvincia(), model);
		model.addAttribute("modificarCompania", companiaJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "modificarCompania";
	}

	/**
	 * @param newCompania
	 * @return
	 * @throws Exception
	 */
	private Compania crearCompaniaByForm(CompaniaJSP newCompania) throws Exception {
		Compania compania = companiaDAO.getOne(Integer.parseInt(newCompania.getIdCompania()));

		compania.setNombre(newCompania.getNombre());

		if (StringUtils.isNotBlank(newCompania.getLicencia())) {
			compania.setLicencia(newCompania.getLicencia());
		}
		if (StringUtils.isNotBlank(newCompania.getTipoCompania())) {
			TipoCompania tipoCompania = tipoCompaniaDAO.getOne(Integer.parseInt(newCompania.getTipoCompania()));
			compania.setTipoCompania(tipoCompania);
		}
		if (StringUtils.isNotBlank(newCompania.getTelefono())) {
			compania.setTelefono(newCompania.getTelefono());
		}
		compania.setDocumento(newCompania.getDocumento());
		if (StringUtils.isNotBlank(newCompania.getTipoVia())) {
			compania.setTipoVia(newCompania.getTipoVia());
		}
		if (StringUtils.isNotBlank(newCompania.getVia())) {
			compania.setVia(newCompania.getVia());
		}
		if (StringUtils.isNotBlank(newCompania.getNumero())) {
			compania.setNumero(newCompania.getNumero());
		}
		if (StringUtils.isNotBlank(newCompania.getPlanta())) {
			compania.setPlanta(newCompania.getPlanta());
		}
		if (StringUtils.isNotBlank(newCompania.getPuerta())) {
			compania.setPuerta(newCompania.getPuerta());
		}

		if (StringUtils.isNotBlank(newCompania.getIdLocalidad())) {
			compania.setLocalidad(localidadDAO.getOne(Integer.parseInt(newCompania.getIdLocalidad())));
		}
		if (StringUtils.isNotBlank(newCompania.getIdProvincia())) {
			compania.setProvincia(provinciaDAO.getOne(Integer.parseInt(newCompania.getIdProvincia())));
		}
		if (StringUtils.isNotBlank(newCompania.getIdPais())) {
			compania.setPais(paisDAO.getOne(Integer.parseInt(newCompania.getIdPais())));
		}
		if (StringUtils.isNotBlank(newCompania.getEmail())) {
			compania.setEmail(newCompania.getEmail());
		}
		if (StringUtils.isNotBlank(newCompania.getObservaciones())) {
			compania.setObservaciones(newCompania.getObservaciones());
		}

		return compania;
	}

	private boolean formRegCompaniaValido(CompaniaJSP newCompania, Model model, BindingResult result) {
		boolean valido = Boolean.TRUE;
		if (StringUtils.isBlank(newCompania.getNombre())) {
			valido = Boolean.FALSE;
			log.error("El Nombre no puede estar vacio");
//			result.rejectValue("nombre", "error.vacio.nombre");
			model.addAttribute("errorNombre", "El Nombre no puede estar vacio");
		}
		if (StringUtils.isBlank(newCompania.getDocumento())) {
			valido = Boolean.FALSE;
			log.error("El documento no puede estar vacio");
//			result.rejectValue("documento", "error.vacio.documento");
			model.addAttribute("errorDocumento", "El documento no puede estar vacio");
		}
		return valido;
	}

	/**
	 * Metodo que devuelve a la pantalla inicial
	 * 
	 * @param session objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverModificarCompania", method = { RequestMethod.GET, RequestMethod.POST })
	public String cancelar(HttpSession session) {

		return "redirect:/buscadorCompania";
	}

}