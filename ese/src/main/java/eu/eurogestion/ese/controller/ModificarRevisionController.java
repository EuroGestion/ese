package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.EstadoRevision;
import eu.eurogestion.ese.domain.RevisionPsico;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorModificarRevisionJSP;
import eu.eurogestion.ese.pojo.ModificarRevisionJSP;
import eu.eurogestion.ese.repository.EstadoRevisionDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.services.ModificarRevisionService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class ModificarRevisionController {

	/**
	 * Repositorio de la clase de dominio RevisionPsico
	 */
	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	/**
	 * Repositorio de la clase de dominio EstadoRevision
	 */
	@Autowired
	public EstadoRevisionDAO estadoRevisionDAO;

	/**
	 * Servicio de la modificacion de la formacion
	 */
	@Autowired
	public ModificarRevisionService modificarRevisionService;

	/**
	 * Metodo que devuelve una lista de Cursos para una tabla
	 * 
	 * @return lista de objetos Curso
	 */
	@ModelAttribute("resoluciones")
	public List<EstadoRevision> listMaterialesAll() {
		List<EstadoRevision> lista = new ArrayList<>();
		lista.addAll(estadoRevisionDAO
				.findAllById(Arrays.asList(Constantes.ESTADO_REVISION_APTO, Constantes.ESTADO_REVISION_NO_APTO)));
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de modificacion de formacion
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarRevision", method = RequestMethod.POST)
	public String modificarRevision(BuscadorModificarRevisionJSP buscadorModificarRevisionJSP, Model model) {
		RevisionPsico revision = revisionPsicoDAO
				.getOne(Integer.parseInt(buscadorModificarRevisionJSP.getIdRevision()));
		model.addAttribute("modificarRevision", convertRevisionToModificarRevisionJSP(revision));
		return "modificarRevision";
	}

	@RequestMapping(value = "/seleccionarResolucion", method = RequestMethod.POST)
	public String aceptarRevision(ModificarRevisionJSP modificarRevisionJSP, Model model, ModelMap modelMap) {
		modelMap.addAttribute("evidencia", modificarRevisionJSP.getEvidencia());
		model.addAttribute("modificarRevision", modificarRevisionJSP);
		return "modificarRevision";
	}

	/**
	 * Metodo que se encarga de cambiar el estado de la revision psicofisica a
	 * "APTO" o a "NO APTO"
	 * 
	 * @param modificarRevisionJSP objeto con los campos de la pantalla
	 * @param model
	 * @param sesion
	 * @return
	 */
	@RequestMapping(value = "/aceptarModificarRevision", method = RequestMethod.POST)
	public String aceptarModificarRevision(ModificarRevisionJSP modificarRevisionJSP, Model model, HttpSession sesion) {

		try {
			modificarRevisionService.modificarRevision(modificarRevisionJSP, sesion);
		} catch (EseException e) {
			model.addAttribute("modificarRevision", modificarRevisionJSP);
			log.error(e.getMessage());
			return "modificarRevision";
		}

		return "redirect:/buscadorModificarRevision";
	}

	/**
	 * Metodo que mapea el objeto de dominio Curso en el objeto
	 * ModificarFormacionJSP
	 * 
	 * @param revision
	 * @return Objeto ModificarFormacionJSP mapeado
	 */
	private ModificarRevisionJSP convertRevisionToModificarRevisionJSP(RevisionPsico revision) {
		ModificarRevisionJSP modificarRevision = new ModificarRevisionJSP();
		modificarRevision.setIdRevision(revision.getIdRevisionPsico().toString());
		modificarRevision.setCausa(revision.getCausa());
		modificarRevision.setFechaRevision(
				Utiles.convertDateToString(revision.getFechaRealizacion(), Constantes.FORMATO_FECHA_PANTALLA));
		modificarRevision.setObservaciones(modificarRevision.getObservaciones());

		return modificarRevision;
	}
}