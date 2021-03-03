package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Localidad;
import eu.eurogestion.ese.domain.Pais;
import eu.eurogestion.ese.domain.Provincia;
import eu.eurogestion.ese.domain.TipoCompania;
import eu.eurogestion.ese.pojo.CompaniaJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.LocalidadDAO;
import eu.eurogestion.ese.repository.PaisDAO;
import eu.eurogestion.ese.repository.ProvinciaDAO;
import eu.eurogestion.ese.repository.TipoCompaniaDAO;
import eu.eurogestion.ese.services.RegistroService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class RegisterCompaniaController {

	/** Repositories & Services **/

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public TipoCompaniaDAO tipoCompaniaDAO;

	@Autowired
	public PaisDAO paisDAO;

	@Autowired
	public ProvinciaDAO provinciaDAO;

	@Autowired
	public LocalidadDAO localidadDAO;

	@Autowired
	public RegistroService registroService;

	/** Functions **/

	@ModelAttribute("tiposCompania")
	public List<TipoCompania> listTiposCompanias() {
		List<TipoCompania> lista = new ArrayList<>();
		TipoCompania tipoCompania = new TipoCompania();
		tipoCompania.setValor("Selecciona uno:");
		lista.add(tipoCompania);
		lista.addAll(tipoCompaniaDAO.findAll());
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

	private List<Provincia> listProvinciaByPais(Integer idPais) {
		List<Provincia> lista = new ArrayList<>();
		Provincia provincia = new Provincia();
		provincia.setNombre("Selecciona uno:");
		lista.add(provincia);
		lista.addAll(provinciaDAO.findProvinciaByPais(idPais));
		return lista;
	}

	private List<Localidad> listLocalidadByPaisAndProvincia(Integer idPais, Integer idProvincia) {
		List<Localidad> lista = new ArrayList<>();
		Localidad localidad = new Localidad();
		localidad.setNombre("Selecciona uno:");
		lista.add(localidad);
		lista.addAll(localidadDAO.findLocalidadByPaisAndProvincia(idPais, idProvincia));
		return lista;
	}

	@RequestMapping(value = "/cargarProvinciasRegisterCompania", method = RequestMethod.POST)
	public String cargarProvinciaRegisterCompania(final CompaniaJSP formularioCompania, BindingResult result,
			Model model) {

		log.info("se cargan las provincias");
		formularioCompania.setIdLocalidad("");
		formularioCompania.setIdProvincia("");
		model.addAttribute("formularioCompania", formularioCompania);
		if (StringUtils.isBlank(formularioCompania.getIdPais())) {
			return "registerCompania";
		}
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(formularioCompania.getIdPais())));
		return "registerCompania";

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

	@RequestMapping(value = "/cargarLocalidadesRegisterCompania", method = RequestMethod.POST)
	public String cargarLocalidadRegisterCompania(final CompaniaJSP formularioCompania, BindingResult result,
			Model model) {

		log.info("se cargan las provincias");
		formularioCompania.setIdLocalidad("");
		model.addAttribute("formularioCompania", formularioCompania);
		model.addAttribute("provincias", listProvinciaByPais(Integer.parseInt(formularioCompania.getIdPais())));
		if (StringUtils.isBlank(formularioCompania.getIdProvincia())) {
			return "registerCompania";
		}
		model.addAttribute("localidades",
				listLocalidadByPaisAndProvincia(Integer.parseInt(formularioCompania.getIdPais()),
						Integer.parseInt(formularioCompania.getIdProvincia())));
		return "registerCompania";

	}

	@RequestMapping(value = "/registerCompania", method = RequestMethod.GET)
	public String registerCompania(Model model) {
		model.addAttribute("formularioCompania", new CompaniaJSP());

		return "registerCompania";
	}

	@RequestMapping(value = "/registerCompania", method = RequestMethod.POST)
	public String registerCompania(final CompaniaJSP formularioCompania, BindingResult result, Model model) {

		if (!formRegCompaniaValido(formularioCompania, model, result)) {
			model.addAttribute("formularioCompania", formularioCompania);
			cargarListaValores(formularioCompania.getIdPais(), formularioCompania.getIdProvincia(), model);
			return "registerCompania";
		}
		try {
			Compania compania = crearCompaniaByForm(formularioCompania);

			companiaDAO.save(compania);
		} catch (Exception e) {
			log.error(e.getMessage());
			cargarListaValores(formularioCompania.getIdPais(), formularioCompania.getIdProvincia(), model);
			model.addAttribute("formularioCompania", formularioCompania);
			return "registerCompania";
		}

		return "redirect:/buscadorCompaniaRegisterCompania";

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

	private Compania crearCompaniaByForm(CompaniaJSP newCompania) throws Exception {
		Compania compania = new Compania();
		compania.setFechaAlta(new Date());

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

}