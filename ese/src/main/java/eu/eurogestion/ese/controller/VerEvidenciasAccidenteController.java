package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.pojo.BuscadorInvestigacionJSP;
import eu.eurogestion.ese.pojo.VerEvidenciasAccidenteJSP;
import eu.eurogestion.ese.repository.AccidenteDAO;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class VerEvidenciasAccidenteController {

	/**
	 * Repositorio de la clase de dominio Accidente
	 */
	@Autowired
	public AccidenteDAO accidenteDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciasAccidente", method = RequestMethod.POST)
	public String verDetalleTitulo(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model) {

		VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP = new VerEvidenciasAccidenteJSP();
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(buscadorInvestigacionJSP.getIdInvestigacion()));
		verEvidenciasAccidenteJSP.setTieneFichaEstructura(accidente.getEvidenciaTipo50() != null);
		verEvidenciasAccidenteJSP.setTieneFichaNotificacion(accidente.getEvidenciaTipo51() != null);
		verEvidenciasAccidenteJSP.setTieneFichaAccidentes(accidente.getEvidenciaTipo60() != null);
		verEvidenciasAccidenteJSP.setTieneInformeFinal(accidente.getEvidenciaTipo61() != null);
		verEvidenciasAccidenteJSP.setTieneEvidenciaRecibidoCIAF(accidente.getEvidenciaTipo63() != null);
		verEvidenciasAccidenteJSP.setTieneFichaComentarios(accidente.getEvidenciaTipo64() != null);
		verEvidenciasAccidenteJSP.setTieneFichaMedidas(accidente.getEvidenciaTipo65() != null);
		verEvidenciasAccidenteJSP.setIdAccidente(buscadorInvestigacionJSP.getIdInvestigacion());

		model.addAttribute("verEvidenciasAccidente", verEvidenciasAccidenteJSP);

		return "verEvidenciasAccidente";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSFFichaEstructuraSucesosFerroviarios", method = RequestMethod.POST)
	public void verEvidenciaSFFichaEstructuraSucesosFerroviarios(VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP,
			Model model, HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(verEvidenciasAccidenteJSP.getIdAccidente()));
		Documento documento = accidente.getEvidenciaTipo50().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSFFichaNotificacionDelegacionSucesosFerroviarios", method = RequestMethod.POST)
	public void verEvidenciaSFFichaNotificacionDelegacionSucesosFerroviarios(
			VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP, Model model, HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(verEvidenciasAccidenteJSP.getIdAccidente()));
		Documento documento = accidente.getEvidenciaTipo51().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSFFichaSucesosFerroviarios", method = RequestMethod.POST)
	public void verEvidenciaSFFichaSucesosFerroviarios(VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP, Model model,
			HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(verEvidenciasAccidenteJSP.getIdAccidente()));
		Documento documento = accidente.getEvidenciaTipo60().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSFInformeFinalSucesosFerroviarios", method = RequestMethod.POST)
	public void verEvidenciaSFInformeFinalSucesosFerroviarios(VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP,
			Model model, HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(verEvidenciasAccidenteJSP.getIdAccidente()));
		Documento documento = accidente.getEvidenciaTipo61().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSFEviadoCIAF", method = RequestMethod.POST)
	public void verEvidenciaSFEviadoCIAF(VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP, Model model,
			HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(verEvidenciasAccidenteJSP.getIdAccidente()));
		Documento documento = accidente.getEvidenciaTipo63().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSFFichaComentariosSugerenciasInformeProvisional", method = RequestMethod.POST)
	public void verEvidenciaSFFichaComentariosSugerenciasInformeProvisional(
			VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP, Model model, HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(verEvidenciasAccidenteJSP.getIdAccidente()));
		Documento documento = accidente.getEvidenciaTipo64().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSFFichaMedidasAdoptadas", method = RequestMethod.POST)
	public void verEvidenciaSFFichaMedidasAdoptadas(VerEvidenciasAccidenteJSP verEvidenciasAccidenteJSP, Model model,
			HttpServletResponse response) {
		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(verEvidenciasAccidenteJSP.getIdAccidente()));
		Documento documento = accidente.getEvidenciaTipo65().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

}