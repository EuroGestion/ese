package eu.eurogestion.ese.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CambiarPasswordJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.services.CambiarPasswordService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class CambiarPasswordController {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public CambiarPasswordService cambiarPasswordService;

	/** Functions **/

	@RequestMapping(value = "/cambiarPasswordPropia", method = RequestMethod.GET)
	public String cambiarPasswordPropia(Model model, HttpSession sesion) {

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) sesion.getAttribute("usuario");

		CambiarPasswordJSP cambiarPasswordJSP = new CambiarPasswordJSP();
		cambiarPasswordJSP.setEsPropia(Boolean.TRUE);
		cambiarPasswordJSP.setIdPersonal(usuarioRegistrado.getIdPersonal());

		model.addAttribute("cambiarPassword", cambiarPasswordJSP);
		return "cambiarPassword";

	}

	@RequestMapping(value = "/cambiarPasswordPersonal", method = RequestMethod.POST)
	public String cambiarPasswordPersonal(DetallePersonalJSP detallePersonalJSP, Model model) {

		CambiarPasswordJSP cambiarPasswordJSP = new CambiarPasswordJSP();
		cambiarPasswordJSP.setEsPropia(Boolean.FALSE);
		cambiarPasswordJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("cambiarPassword", cambiarPasswordJSP);
		return "cambiarPassword";

	}

	@RequestMapping(value = "/confirmacionCambiarPasswordPropia", method = RequestMethod.POST)
	public String confirmacionCambiarPasswordPropia(CambiarPasswordJSP cambiarPasswordJSP, Model model) {
		if (!cambiarPasswordService.mismaPasswordPropia(cambiarPasswordJSP)) {
			log.error("Tienes que proporcionar una contraseña antigua válida");
			model.addAttribute("error", "Tienes que proporcionar una contraseña antigua válida");
			model.addAttribute("cambiarPassword", cambiarPasswordJSP);
			return "cambiarPassword";
		}

		if (!cambiarPasswordJSP.getPasswordNueva()
				.equals(cambiarPasswordJSP.getPasswordNuevaConfirmacion())) {
			log.error("La confirmación de la contraseña no coincide con la contraseña nueva");
			model.addAttribute("error", "La confirmación de la contraseña no coincide con la contraseña nueva");
			model.addAttribute("cambiarPassword", cambiarPasswordJSP);
			return "cambiarPassword";
		}

		try {
			cambiarPasswordService.confirmacionCambiarPassword(cambiarPasswordJSP);
		} catch (EseException e) {
			model.addAttribute("cambiarPassword", cambiarPasswordJSP);
			return "cambiarPassword";
		}
		model.addAttribute("info", "Se ha cambiado la contraseña");
		model.addAttribute("cambiarPassword", cambiarPasswordJSP);
		return "cambiarPassword";

	}

	@RequestMapping(value = "/confirmacionCambiarPasswordPersonal", method = RequestMethod.POST)
	public String confirmacionCambiarPasswordPersonal(CambiarPasswordJSP cambiarPasswordJSP, Model model) {

		if (!cambiarPasswordJSP.getPasswordNueva()
				.equals(cambiarPasswordJSP.getPasswordNuevaConfirmacion())) {
			log.error("La confirmación de la contraseña no coincide con la contraseña nueva");
			model.addAttribute("error", "La confirmación de la contraseña no coincide con la contraseña nueva");
			model.addAttribute("cambiarPassword", cambiarPasswordJSP);
			return "cambiarPassword";
		}
		try {
			cambiarPasswordService.confirmacionCambiarPassword(cambiarPasswordJSP);
		} catch (EseException e) {
			model.addAttribute("cambiarPassword", cambiarPasswordJSP);
			return "cambiarPassword";
		}
		model.addAttribute("info", "Se ha cambiado la contraseña");
		model.addAttribute("cambiarPassword", cambiarPasswordJSP);
		return "cambiarPassword";
	}

	@RequestMapping(value = "/volverCambiarPassword", method = RequestMethod.POST)
	public String volverCambiarPassword(CambiarPasswordJSP cambiarPasswordJSP, Model model) {
		if (cambiarPasswordJSP.getEsPropia()) {
			return "redirect:/welcome";
		} else {
			return "forward:/volverPersonalCambiarPassword";
		}

	}

}