package eu.eurogestion.ese.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.OlvidarPasswordJSP;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.services.OlvidarPasswordService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class OlvidarPasswordController {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public OlvidarPasswordService olvidarPasswordService;

	/** Functions **/

	@RequestMapping(value = "/ovidarPassword", method = RequestMethod.GET)
	public String ovidarPassword(Model model, HttpSession sesion) {

		model.addAttribute("olvidarPassword", new OlvidarPasswordJSP());
		return "olvidarPassword";

	}

	@RequestMapping(value = "/confirmacionOlvidarPassword", method = RequestMethod.POST)
	public String confirmacionOlvidarPassword(OlvidarPasswordJSP olvidarPasswordJSP, Model model) {
		if (!olvidarPasswordService.emailValido(olvidarPasswordJSP)) {
			log.error("El email no existe");
			model.addAttribute("error", "El email no existe");
			model.addAttribute("olvidarPassword", olvidarPasswordJSP);
			return "olvidarPassword";
		}

		try {
			olvidarPasswordService.confirmacionOlvidarPassword(olvidarPasswordJSP);
		} catch (EseException e) {
			model.addAttribute("olvidarPassword", olvidarPasswordJSP);
			return "olvidarPassword";
		}
		return "redirect:/volverOlvidarPassword";

	}
}