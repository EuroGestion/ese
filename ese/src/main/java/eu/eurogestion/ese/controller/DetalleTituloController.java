package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Suspension;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.DetalleTituloJSP;
import eu.eurogestion.ese.repository.RevocacionDAO;
import eu.eurogestion.ese.repository.SuspensionDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class DetalleTituloController {

	/**
	 * Repositorio de la clase de dominio Titulo
	 */
	@Autowired
	public TituloDAO tituloDAO;

	/**
	 * Repositorio de la clase de dominio Suspension
	 */
	@Autowired
	public SuspensionDAO suspensionDAO;

	/**
	 * Repositorio de la clase de dominio Revocacion
	 */
	@Autowired
	public RevocacionDAO revocacionDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleTitulo", method = RequestMethod.POST)
	public String verDetalleTitulo(DetallePersonalJSP detallePersonalJSP, Model model) {

		model.addAttribute("detalleTitulo",
				convertTituloToDetalleTituloJSP(tituloDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdTitulo()))));

		return "detalleTitulo";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/descargarEvidenciaDocumentoTitulo", method = RequestMethod.POST)
	public void descargarEvidenciaDocuemtnoTitulo(DetalleTituloJSP detalleTituloJSP, Model model,
			HttpServletResponse response) {
		try {
			Titulo titulo = tituloDAO.getOne(Integer.parseInt(detalleTituloJSP.getIdTitulo()));
			Documento documento = titulo.getEvidencia().getDocumento();
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
	@RequestMapping(value = "/descargarEvidenciaSuspensionTitulo", method = RequestMethod.POST)
	public void descargarEvidenciaSuspensionTitulo(DetalleTituloJSP detalleTituloJSP, Model model,
			HttpServletResponse response) {
		try {
			Titulo titulo = tituloDAO.getOne(Integer.parseInt(detalleTituloJSP.getIdTitulo()));
			Suspension suspensionTitulo = null;
			for (Suspension suspension : titulo.getListSuspension()) {
				if (suspension.getFechaResolucion() == null) {
					suspensionTitulo = suspension;
				}
			}

			Documento documento = suspensionTitulo.getEvidencia().getDocumento();
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
	@RequestMapping(value = "/descargarEvidenciaRevocacionTitulo", method = RequestMethod.POST)
	public void descargarEvidenciaRevocacionTitulo(DetalleTituloJSP detalleTituloJSP, Model model,
			HttpServletResponse response) {

		try {
			Titulo titulo = tituloDAO.getOne(Integer.parseInt(detalleTituloJSP.getIdTitulo()));
			Documento documento = titulo.getRevocacion().getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	private DetalleTituloJSP convertTituloToDetalleTituloJSP(Titulo titulo) {
		DetalleTituloJSP detalleFormacionJSP = new DetalleTituloJSP();
		detalleFormacionJSP.setFechaCaducidad(titulo.getFechaCaducidad());
		detalleFormacionJSP.setIdPersonal(titulo.getPersonal().getIdPersonal().toString());
		detalleFormacionJSP.setIdTitulo(titulo.getIdTitulo().toString());
		detalleFormacionJSP.setLugarTrabajo(titulo.getLugarTrabajo());
		detalleFormacionJSP.setRestricciones(titulo.getRestricciones());
		detalleFormacionJSP.setTipoTitulo(titulo.getTipoTitulo().getModeloDocumento());
		detalleFormacionJSP.setValidoDesde(titulo.getValidoDesde());
		detalleFormacionJSP.setEstadoTitulo(titulo.getEstadoTitulo().getValor());
		detalleFormacionJSP.setIsSuspension(Boolean.FALSE);
		for (Suspension suspension : titulo.getListSuspension()) {
			if (suspension.getFechaResolucion() == null) {
				detalleFormacionJSP.setIsSuspension(Boolean.TRUE);
				detalleFormacionJSP.setIdSuspensionTitulo(suspension.getIdSuspension().toString());
			}
		}

		if (titulo.getRevocacion() != null) {
			detalleFormacionJSP.setIdRevocacionTitulo(titulo.getRevocacion().getIdRevocacion().toString());
		}

		return detalleFormacionJSP;

	}

}