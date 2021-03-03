package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.CausaSuspension;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.SuspenderTituloJSP;
import eu.eurogestion.ese.repository.CausaSuspensionDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.services.SuspenderTituloService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rmerino, alvaro
 *
 */
@Slf4j
@Controller
public class SuspenderTituloController {

	/**
	 * Repositorio de la clase de dominio Titulo
	 */
	@Autowired
	public TituloDAO tituloDAO;

	@Autowired
	public CausaSuspensionDAO causaSuspensionDAO;

	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Servicio de la suspension de un titulo
	 */
	@Autowired
	public SuspenderTituloService suspenderTituloService;

	@RequestMapping(value = "/firmarSuspenderTitulo", method = RequestMethod.POST)
	public String aceptarfirmarSuspenderTitulo(SuspenderTituloJSP suspenderTituloJSP, Model model, HttpSession sesion) {

		if (StringUtils.isBlank(suspenderTituloJSP.getNombreResponsableSeguridad())
				|| StringUtils.isBlank(suspenderTituloJSP.getPasswordResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("suspenderTitulo", suspenderTituloJSP);
			return "suspenderTitulo";
		}

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				suspenderTituloJSP.getNombreResponsableSeguridad(),
				suspenderTituloJSP.getPasswordResponsableSeguridad());
		if (responsableSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("suspenderTitulo", suspenderTituloJSP);
			return "suspenderTitulo";
		}

		try {
			suspenderTituloService.suspenderTitulo(suspenderTituloJSP, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("suspenderTitulo", suspenderTituloJSP);
			return "suspenderTitulo";
		}

		return "forward:/volverSuspenderTitulo";
	}

	@ModelAttribute("causas")
	public List<CausaSuspension> listCausaSuspensionAll() {
		List<CausaSuspension> lista = new ArrayList<>();
		CausaSuspension causa = new CausaSuspension();
		causa.setValor("Selecciona uno:");
		lista.add(causa);
		lista.addAll(causaSuspensionDAO.findAll());
		return lista;
	}

	@RequestMapping(value = "/suspenderTitulo", method = RequestMethod.POST)
	public String suspenderTitulo(DetallePersonalJSP detallePersonalJSP, Model model) {

		model.addAttribute("suspenderTitulo", convertSuspenderTituloToSuspenderTituloJSP(
				tituloDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdTitulo()))));

		return "suspenderTitulo";
	}

	private SuspenderTituloJSP convertSuspenderTituloToSuspenderTituloJSP(Titulo titulo) {
		SuspenderTituloJSP suspenderTituloJSP = new SuspenderTituloJSP();

		suspenderTituloJSP.setIdPersonal(titulo.getPersonal().getIdPersonal().toString());
		suspenderTituloJSP.setIdTitulo(titulo.getIdTitulo().toString());

		String nombre = titulo.getPersonal().getNombre() + " " + titulo.getPersonal().getApellido1();
		if (StringUtils.isNotBlank(titulo.getPersonal().getApellido2())) {
			nombre += " " + titulo.getPersonal().getApellido2();
		}
		suspenderTituloJSP.setNombre(nombre);

		suspenderTituloJSP.setTitulo(titulo.getNombre());
		suspenderTituloJSP.setFechaCaducidad(Utiles.convertDateToString(titulo.getFechaCaducidad(),Constantes.FORMATO_FECHA_PANTALLA));

		return suspenderTituloJSP;
	}
}