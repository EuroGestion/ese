package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.RevisionPsico;
import eu.eurogestion.ese.domain.TipoTitulo;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.HabilitacionJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.repository.TipoTituloDAO;
import eu.eurogestion.ese.repository.TituloCursoDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.services.HabilitarService;
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
public class HabilitarController {

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public CursoDAO cursoDAO;

	/**
	 * Repositorio de la clase de dominio TituloCurso
	 */
	@Autowired
	public TituloCursoDAO tituloCursoDAO;

	/**
	 * Repositorio de la clase de dominio TipoTitulo
	 */
	@Autowired
	public TipoTituloDAO tipoTituloDAO;

	/**
	 * Repositorio de la clase de dominio TipoTitulo
	 */
	@Autowired
	public TituloDAO tituloDAO;

	/**
	 * Repositorio de la clase de dominio RevisionPsico
	 */
	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;
	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;
	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public DocumentoDAO documentoDAO;

	/**
	 * Servicio para habilitar
	 */
	@Autowired
	public HabilitarService habilitarService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de tipo de titulos para un select
	 * 
	 * @return lista de objetos TipoTitulo
	 */
	@ModelAttribute("tiposTitulos")
	public List<TipoTitulo> listTiposTitulos() {
		List<TipoTitulo> lista = new ArrayList<>();
		TipoTitulo tipoTitulo = new TipoTitulo();
		tipoTitulo.setModeloDocumento("Selecciona uno:");
		lista.add(tipoTitulo);

		lista.addAll(tipoTituloDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de habilitar rellenando valores por
	 * defecto del peronal que vamos a habilitar
	 * 
	 * @param id    del personal que se va a habilitar
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/habilitar", method = RequestMethod.POST)
	public String habilitar(DetallePersonalJSP formularioPersonal, Model model) {

		HabilitacionJSP habilitacion = new HabilitacionJSP();
		habilitacion.setIdPersonal(formularioPersonal.getIdPersonal());
		habilitacion.setFechaExpedicion(Utiles.convertDateToString(new Date(), Constantes.FORMATO_FECHA_PANTALLA));
		RevisionPsico revisionPsico = revisionPsicoDAO.findLastRevisionPsico(formularioPersonal.getIdPersonal());
		habilitacion.setIdRevPsicofisica(String.valueOf(revisionPsico.getIdRevisionPsico()));
		habilitacion.setRevPsicofisica(revisionPsico.getEstadoRevision().getValor());
		habilitacion.setFechaRevPsicofisica(
				Utiles.convertDateToString(revisionPsico.getFechaCaducidad(), Constantes.FORMATO_FECHA_PANTALLA));
		model.addAttribute("habilitacion", habilitacion);
		obtenerCursos(formularioPersonal.getIdPersonal(), model);
		return "habilitar";

	}

	/**
	 * Metodo que inicializa el formulario de habilitar rellenando valores por
	 * defecto del peronal que vamos a habilitar
	 * 
	 * @param id    del personal que se va a habilitar
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/seleccionarTipoTitulo", method = RequestMethod.POST)
	public String seleccionarTipoTitulo(HabilitacionJSP habilitacionJSP, Model model) {

		HabilitacionJSP habilitacion = new HabilitacionJSP();
		habilitacion.setIdPersonal(habilitacionJSP.getIdPersonal());
		habilitacion.setFechaExpedicion(Utiles.convertDateToString(new Date(), Constantes.FORMATO_FECHA_PANTALLA));
		RevisionPsico revisionPsico = revisionPsicoDAO.findLastRevisionPsico(habilitacionJSP.getIdPersonal());
		habilitacion.setIdRevPsicofisica(String.valueOf(revisionPsico.getIdRevisionPsico()));
		habilitacion.setRevPsicofisica(revisionPsico.getEstadoRevision().getValor());
		habilitacion.setIdTipoTitulo(habilitacionJSP.getIdTipoTitulo());
		habilitacion.setFechaRevPsicofisica(
				Utiles.convertDateToString(revisionPsico.getFechaCaducidad(), Constantes.FORMATO_FECHA_PANTALLA));
		model.addAttribute("habilitacion", habilitacion);
		obtenerCursos(habilitacionJSP.getIdPersonal(), model);
		return "habilitar";

	}

	/**
	 * Metodo que descarga un pdf a traves del id del titulo
	 * 
	 * @param idTitulo     id del titulo del que vamos a descargar la habilitacion
	 * @param model        objeto model de la pantalla
	 * @param httpResponse objeto de la respuesta
	 */
	@RequestMapping(value = "/descargarPDF", method = RequestMethod.POST)
	public void descargarPDF(@RequestParam String idTitulo, Model model, HttpServletResponse response) {

		Titulo titulo = tituloDAO.getOne(Integer.parseInt(idTitulo));
		Documento documento = titulo.getEvidencia().getDocumento();
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Metodo que se encarga de crear un titulo a partir de los datos de pantalla
	 * despues de validarlos.
	 * 
	 * @param habilitacion objeto con los campos de la pantalla
	 * @param model        objeto model de la pantalla
	 * @param session      objeto de la sesion
	 * @param httpResponse objeto de la respuesta
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/habilitarCertificadoComplementario", method = RequestMethod.POST)
	public String habilitar(final HabilitacionJSP habilitacion, Model model, HttpSession session,
			HttpServletResponse httpResponse) {
		if (CollectionUtils.isEmpty(habilitacion.getListaCursos())) {
			model.addAttribute("habilitacion", habilitacion);
			model.addAttribute("errorCheckbox", "Tienes que seleccionar un curso");
			obtenerCursos(habilitacion.getIdPersonal(), model);
			return "habilitar";
		}
		if (habilitacion.getListaCursos().size() > 1 && (String
				.valueOf(Constantes.TIPO_TITULO_OPERADOR_VEHICULO_MANIOBRAS).equals(habilitacion.getIdTipoTitulo())
				|| String.valueOf(Constantes.TIPO_TITULO_AUXILIAR_OPERACIONES_TREN_CON_MANIOBRAS)
						.equals(habilitacion.getIdTipoTitulo()))) {
			model.addAttribute("habilitacion", habilitacion);
			model.addAttribute("errorCheckbox", "Solo puede haber un curso seleccionado");
			obtenerCursos(habilitacion.getIdPersonal(), model);
			return "habilitar";
		}
		Titulo titulo;
		try {
			titulo = habilitarService.altaTitulo(habilitacion, session);
		} catch (EseException e) {
			model.addAttribute("habilitacion", habilitacion);
			obtenerCursos(habilitacion.getIdPersonal(), model);
			log.error(e.getMessage());
			return "habilitar";
		}
		model.addAttribute("idTitulo", titulo.getIdTitulo());
		model.addAttribute("idPersonal", habilitacion.getIdPersonal());
//		obtenerCursos(habilitacion.getIdPersonal(), model);
		model.addAttribute("info", " Se ha creado correctamente");
		return "visorPDF";
	}

	/**
	 * Metodo que obtiene una lista de cursos de un personal dado su id
	 * 
	 * @param id    del personal
	 * @param model objeto model de la pantalla
	 */
	private void obtenerCursos(String id, Model model) {
		model.addAttribute("cursos", cursoDAO.findCursoByPersonal(id));
	}

}