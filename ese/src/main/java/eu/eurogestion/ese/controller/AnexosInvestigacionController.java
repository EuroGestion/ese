package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.domain.AnexoAccidente;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.MedidaAccidente;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AnexosInvestigacionJSP;
import eu.eurogestion.ese.pojo.BuscadorInvestigacionJSP;
import eu.eurogestion.ese.repository.AccidenteDAO;
import eu.eurogestion.ese.repository.AnexoAccidenteDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.MedidaAccidenteDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.services.AnexosInvestigacionService;
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
public class AnexosInvestigacionController {

	/** Repositories & Services **/

	@Autowired
	public AccidenteDAO accidenteDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public AnexoAccidenteDAO anexoAccidenteDAO;

	@Autowired
	public MedidaAccidenteDAO medidaAccidenteDAO;

	@Autowired
	public AnexosInvestigacionService anexosInvestigacionService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que inicializa el formulario de addAnomalia
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verAnexosInvestigacionBuscador", method = RequestMethod.POST)
	public String verAnexosInvestigacionBuscador(BuscadorInvestigacionJSP buscadorInvestigacionJSP, Model model) {

		AnexosInvestigacionJSP anexosInvestigacion = new AnexosInvestigacionJSP();

		Accidente accidente = accidenteDAO.getOne(Integer.parseInt(buscadorInvestigacionJSP.getIdInvestigacion()));
		anexosInvestigacion.setIdInvestigacion(accidente.getIdAccidente());
		anexosInvestigacion.setIdEstadoInvestigacion(accidente.getEstadoAccidente().getIdEstadoAccidente());
		anexosInvestigacion.setNumIdentificacion(accidente.getNumeroSuceso());
		anexosInvestigacion.setLugar(accidente.getLugarAccidente());
		anexosInvestigacion.setFechaInspeccion(
				Utiles.convertDateToString(accidente.getFechaAccidente(), Constantes.FORMATO_FECHA_PANTALLA));
		anexosInvestigacion.setHora(
				Utiles.convertDateToString(accidente.getHoraAccidente(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		anexosInvestigacion.setCausa(accidente.getCausaAccidente().getValor());

		model.addAttribute("anexosInvestigacion", anexosInvestigacion);

		cargarListasAnexosInvestigacion(model, accidente.getIdAccidente(), 0, 0, 5);

		return "anexosInvestigacion";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/anadirEvidenciaDocAnexo", method = RequestMethod.POST)
	public String anadirEvidenciaDocAnexo(AnexosInvestigacionJSP anexosInvestigacion, Model model, HttpSession sesion) {

		try {
			anexosInvestigacionService.guardarEvidenciaDocumentosAnexos(anexosInvestigacion, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
			// TODO falta añadir error

		}

		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageMedidasAdoptadas = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(anexosInvestigacion.getPageMedidasAdoptadas())) {
			pageMedidasAdoptadas = Integer.parseInt(anexosInvestigacion.getPageMedidasAdoptadas()) - 1;
		}

		if (StringUtils.isNotBlank(anexosInvestigacion.getPageAnexos())) {
			pageAnexos = Integer.parseInt(anexosInvestigacion.getPageAnexos()) - 1;
		}

		model.addAttribute("anexosInvestigacion", anexosInvestigacion);
		model.addAttribute("info", " Se ha añadido correctamente");
		cargarListasAnexosInvestigacion(model, anexosInvestigacion.getIdInvestigacion(), pageAnexos,
				pageMedidasAdoptadas, size);
		return "anexosInvestigacion";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/anadirEvidenciaMedidaAdoptadas", method = RequestMethod.POST)
	public String anadirEvidenciaMedidaAdoptadas(AnexosInvestigacionJSP anexosInvestigacion, Model model,
			HttpSession sesion) {

		try {
			anexosInvestigacionService.guardarEvidenciaMedidasAdoptadas(anexosInvestigacion, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
			// TODO falta añadir error

		}
		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageMedidasAdoptadas = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(anexosInvestigacion.getPageMedidasAdoptadas())) {
			pageMedidasAdoptadas = Integer.parseInt(anexosInvestigacion.getPageMedidasAdoptadas()) - 1;
		}

		if (StringUtils.isNotBlank(anexosInvestigacion.getPageAnexos())) {
			pageAnexos = Integer.parseInt(anexosInvestigacion.getPageAnexos()) - 1;
		}
		model.addAttribute("anexosInvestigacion", anexosInvestigacion);
		model.addAttribute("info", " Se ha añadido correctamente");
		cargarListasAnexosInvestigacion(model, anexosInvestigacion.getIdInvestigacion(), pageAnexos,
				pageMedidasAdoptadas, size);
		return "anexosInvestigacion";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaDocAnexo", method = RequestMethod.POST)
	public void verEvidenciaDocAnexo(AnexosInvestigacionJSP anexosInvestigacion, Model model,
			HttpServletResponse response) {

		try {
			AnexoAccidente docAnexoAccidente = anexoAccidenteDAO
					.getOne(Integer.parseInt(anexosInvestigacion.getIdAnexo()));
			Documento documento = docAnexoAccidente.getEvidencia().getDocumento();

			utilesPDFService.descargarEvidencia(documento, response);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaMedidasAdoptadas", method = RequestMethod.POST)
	public void verEvidenciaMedidasAdoptadas(AnexosInvestigacionJSP anexosInvestigacion, Model model,
			HttpServletResponse response) {

		try {
			MedidaAccidente medidaAccidente = medidaAccidenteDAO
					.getOne(Integer.parseInt(anexosInvestigacion.getIdMedidasAdoptadas()));
			Documento documento = medidaAccidente.getEvidencia().getDocumento();

			utilesPDFService.descargarEvidencia(documento, response);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	@RequestMapping(value = "/paginacionTablaAnexosInvestigacionAnexos", method = RequestMethod.POST)
	public String paginacionTablaAnexosInvestigacionAnexos(AnexosInvestigacionJSP anexosInvestigacionJSP, Model model,
			HttpSession session) {
		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageMedidasAdoptadas = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(anexosInvestigacionJSP.getPageAnexos())) {
			anexosInvestigacionJSP.setPageAnexos(anexosInvestigacionJSP.getPageAnexos().substring(
					anexosInvestigacionJSP.getPageAnexos().indexOf(",") + 1,
					anexosInvestigacionJSP.getPageAnexos().length()));
			pageAnexos = Integer.parseInt(anexosInvestigacionJSP.getPageAnexos()) - 1;
		}

		if (StringUtils.isNotBlank(anexosInvestigacionJSP.getPageMedidasAdoptadas())) {
			pageMedidasAdoptadas = Integer.parseInt(anexosInvestigacionJSP.getPageMedidasAdoptadas()) - 1;
		}

		model.addAttribute("anexosInvestigacion", anexosInvestigacionJSP);
		cargarListasAnexosInvestigacion(model, anexosInvestigacionJSP.getIdInvestigacion(), pageAnexos,
				pageMedidasAdoptadas, size);
		return "anexosInvestigacion";
	}

	@RequestMapping(value = "/paginacionTablaAnexosInvestigacionMediasAdoptadas", method = RequestMethod.POST)
	public String paginacionTablaAnexosInvestigacionMediasAdoptadas(AnexosInvestigacionJSP anexosInvestigacionJSP,
			Model model, HttpSession session) {
		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageMedidasAdoptadas = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(anexosInvestigacionJSP.getPageMedidasAdoptadas())) {
			anexosInvestigacionJSP.setPageMedidasAdoptadas(anexosInvestigacionJSP.getPageMedidasAdoptadas().substring(
					anexosInvestigacionJSP.getPageMedidasAdoptadas().indexOf(",") + 1,
					anexosInvestigacionJSP.getPageMedidasAdoptadas().length()));
			pageMedidasAdoptadas = Integer.parseInt(anexosInvestigacionJSP.getPageMedidasAdoptadas()) - 1;
		}

		if (StringUtils.isNotBlank(anexosInvestigacionJSP.getPageAnexos())) {
			pageAnexos = Integer.parseInt(anexosInvestigacionJSP.getPageAnexos()) - 1;
		}

		model.addAttribute("anexosInvestigacion", anexosInvestigacionJSP);
		cargarListasAnexosInvestigacion(model, anexosInvestigacionJSP.getIdInvestigacion(), pageAnexos,
				pageMedidasAdoptadas, size);
		return "anexosInvestigacion";
	}

	private void cargarListasAnexosInvestigacion(Model model, Integer idAccidente, int pageAnexos,
			int pageMedidasAdoptadas, int size) {

		model.addAttribute("listAnexos",
				anexoAccidenteDAO.findByIdAccidente(idAccidente, PageRequest.of(pageAnexos, size)));
		model.addAttribute("listMedidasAdoptadas",
				medidaAccidenteDAO.findByIdAccidente(idAccidente, PageRequest.of(pageMedidasAdoptadas, size)));
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverAnexosInvestigacion", method = RequestMethod.POST)
	public String volverAnexosInvestigacion(AnexosInvestigacionJSP anexosInvestigacion, Model model,
			HttpSession sesion) {

		return "redirect:/buscadorInvestigacion";

	}

}