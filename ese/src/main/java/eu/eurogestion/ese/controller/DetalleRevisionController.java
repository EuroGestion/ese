package eu.eurogestion.ese.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.RevisionPsico;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.DetalleRevisionJSP;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rmerino, alvaro
 *
 */
@Slf4j
@Controller
public class DetalleRevisionController {

	/**
	 * Repositorio de la clase de dominio RevisionPsico
	 */
	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que inicializa el formulario de detalleRevision
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleRevision", method = RequestMethod.POST)
	public String verDetalleRevision(DetallePersonalJSP detallePersonalJSP, Model model) {

		model.addAttribute("detalleRevision", convertRevisionPsicoToDetalleRevisionJSP(
				revisionPsicoDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdRevision()))));

		return "detalleRevision";
	}

	/**
	 * Metodo que descarga la evidencia
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/descargarEvidenciaResultadoPrueba", method = RequestMethod.POST)
	public void descargarEvidenciaResultadoPrueba(DetalleRevisionJSP detalleRevisionJSP, Model model,
			HttpServletResponse response) {
		try {
			RevisionPsico revision = revisionPsicoDAO.getOne(Integer.parseInt(detalleRevisionJSP.getIdRevision()));
			Documento documento = revision.getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	private DetalleRevisionJSP convertRevisionPsicoToDetalleRevisionJSP(RevisionPsico revisionPsico) {
		DetalleRevisionJSP detalleRevisionJSP = new DetalleRevisionJSP();
		Personal personal = revisionPsico.getPersonal();

		detalleRevisionJSP.setIdPersonal(personal.getIdPersonal().toString());
		detalleRevisionJSP.setIdRevision(revisionPsico.getIdRevisionPsico().toString());

		String nombre = personal.getNombre() + " " + personal.getApellido1();
		if (StringUtils.isNotBlank(personal.getApellido2())) {
			nombre += " " + personal.getApellido2();
		}

		detalleRevisionJSP.setNombre(nombre);
		detalleRevisionJSP.setCargo(personal.getCargo().getNombre());
		detalleRevisionJSP.setCausa(revisionPsico.getCausa());
		detalleRevisionJSP.setResultado(revisionPsico.getEstadoRevision().getValor());
		detalleRevisionJSP.setValidez(revisionPsico.getValidoDesde());
		detalleRevisionJSP.setFechaCaducidad(revisionPsico.getFechaCaducidad());
		detalleRevisionJSP.setCentroMedico(revisionPsico.getCompania().getNombre());
		detalleRevisionJSP.setFechaRealizacion(revisionPsico.getFechaRealizacion());
		detalleRevisionJSP.setObservaciones(revisionPsico.getObservaciones());
		if (revisionPsico.getEvidencia() != null) {
			detalleRevisionJSP.setEvidenciaResultadoPrueba(Boolean.TRUE);
		}

		return detalleRevisionJSP;
	}
}