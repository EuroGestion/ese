package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

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
import eu.eurogestion.ese.domain.Localidad;
import eu.eurogestion.ese.domain.Pais;
import eu.eurogestion.ese.domain.Provincia;
import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.UsuarioJSP;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.LocalidadDAO;
import eu.eurogestion.ese.repository.PaisDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ProvinciaDAO;
import eu.eurogestion.ese.repository.RolDAO;
import eu.eurogestion.ese.services.RegistroService;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class RegisterUserController {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public CargoDAO cargoDAO;

	@Autowired
	public RolDAO rolDAO;

	@Autowired
	public PaisDAO paisDAO;

	@Autowired
	public ProvinciaDAO provinciaDAO;

	@Autowired
	public LocalidadDAO localidadDAO;

	@Autowired
	public RegistroService registroService;

	/** Functions **/

	@ModelAttribute("companias")
	public List<Compania> listCompaniasAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAllCompaniaAlta());
		return lista;
	}

	@ModelAttribute("cargos")
	public List<Cargo> listCargoAll() {
		List<Cargo> lista = new ArrayList<>();
		Cargo cargo = new Cargo();
		cargo.setNombre("Selecciona uno:");
		lista.add(cargo);
		lista.addAll(cargoDAO.findAll());
		return lista;
	}

	@ModelAttribute("roles")
	public List<Rol> listRoles() {
		List<Rol> lista = new ArrayList<>();
		Rol rol = new Rol();
		rol.setNombre("Selecciona uno:");
		lista.add(rol);
		lista.addAll(rolDAO.findAll());
		return lista;
	}

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

	@RequestMapping(value = "/cargarProvinciasRegister", method = RequestMethod.POST)
	public String cargarProvinciaRegister(final UsuarioJSP formularioUser, BindingResult result, Model model) {

		log.info("se cargan las provincias");
		formularioUser.setIdLocalidad("");
		formularioUser.setIdProvincia("");
		model.addAttribute("formularioUser", formularioUser);
		if (StringUtils.isBlank(formularioUser.getIdPais())) {
			return "register";
		}
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(formularioUser.getIdPais())));
		return "register";

	}

	@RequestMapping(value = "/cargarLocalidadesRegister", method = RequestMethod.POST)
	public String cargarLocalidadRegister(final UsuarioJSP formularioUser, BindingResult result, Model model) {

		log.info("se cargan las provincias");
		formularioUser.setIdLocalidad("");
		model.addAttribute("formularioUser", formularioUser);
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(formularioUser.getIdPais())));
		if (StringUtils.isBlank(formularioUser.getIdProvincia())) {
			return "register";
		}
		model.addAttribute("localidades", listLocalidadByPaisAndProvincia(Integer.parseInt(formularioUser.getIdPais()),
				Integer.parseInt(formularioUser.getIdProvincia())));
		return "register";

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

	@RequestMapping(value = "/registerUsuario", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("formularioUser", new UsuarioJSP());

		return "register";
	}

	@RequestMapping(value = "/registerUsuario", method = RequestMethod.POST)
	public String register(final UsuarioJSP formularioUser, BindingResult result, Model model) {

		List<String> errores = new ArrayList<>();
		if (!formRegUsuarioValido(formularioUser, model, errores)) {
			model.addAttribute("error", errores);
			model.addAttribute("formularioUser", formularioUser);
			cargarListaValores(formularioUser.getIdPais(), formularioUser.getIdProvincia(), model);
			return "register";
		}

		try {
			registroService.registroUsuario(formularioUser);
		} catch (EseException e) {
			log.error(e.getMessage());
			cargarListaValores(formularioUser.getIdPais(), formularioUser.getIdProvincia(), model);
			model.addAttribute("formularioUser", formularioUser);
			return "register";
		}
		return "redirect:/buscadorPersonalRegisterPersonal";
	}

	private boolean usuarioCorrecto(String usuario) {
		return personalDAO.findByNombreUsuario(usuario) == null;
	}
	
	private boolean emailCorrecto(String email) {
		return personalDAO.findByEmailAndFechaBajaIsNull(email) == null;
	}

	private boolean formRegUsuarioValido(UsuarioJSP newUser, Model model, List<String> errores) {
		boolean valido = Boolean.TRUE;
		if (StringUtils.isNotBlank(newUser.getUsuario()) && !usuarioCorrecto(newUser.getUsuario())) {
			valido = Boolean.FALSE;
			log.error("El usuario ya esta registrado");
			errores.add("El usuario ya esta registrado");
		}

		if (StringUtils.isBlank(newUser.getPassword()) ^ StringUtils.isBlank(newUser.getUsuario())) {
			if (StringUtils.isBlank(newUser.getPassword())) {
				valido = Boolean.FALSE;
				log.error("El usuario no puede estar vacio");
				errores.add("El usuario no puede estar vacio");
			} else {
				valido = Boolean.FALSE;
				log.error("La contraseña no puede estar vacia");
				errores.add("La contraseña no puede estar vacia");
			}
		}

		if (!newUser.getPassword().equals(newUser.getPasswordConfirm())) {
			valido = Boolean.FALSE;
			log.error("Las contraseñas no son iguales");
			errores.add("Las contraseñas no son iguales");
		}
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
		
		if( StringUtils.isBlank(newUser.getEmail()) && !emailCorrecto(newUser.getEmail())) {
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