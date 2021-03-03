package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.AnexoProveedor;
import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Proveedor;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorHomologacionJSP;
import eu.eurogestion.ese.pojo.HomologacionJSP;
import eu.eurogestion.ese.repository.AnexoProveedorDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.EstadoProveedorDAO;
import eu.eurogestion.ese.repository.ProveedorDAO;
import eu.eurogestion.ese.services.HomologacionService;
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
public class HomologacionController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public AnexoProveedorDAO anexoProveedorDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public ProveedorDAO proveedorDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public EstadoProveedorDAO estadoProveedorDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public HomologacionService homologacionService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de localidades para un select
	 * 
	 * @return lista de objetos Localidad
	 */
	@ModelAttribute("companias")
	public List<Compania> listCompanias() {
		List<Compania> lista = new ArrayList<>();
		Compania estadoProveedor = new Compania();
		estadoProveedor.setNombre("Selecciona uno:");
		lista.add(estadoProveedor);
		lista.addAll(companiaDAO.findAllCompaniaAlta());
		return lista;
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorHomologacionJSP objeto con los campos de filtro de la pantalla
	 * @param model                   objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verProveedor", method = RequestMethod.POST)
	public String verProveedor(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model) {

		HomologacionJSP homologacionJSP = cargarHomologacion(buscadorHomologacionJSP, model);
		homologacionJSP.setLectura(Boolean.TRUE);
		model.addAttribute("homologacion", homologacionJSP);
		return "homologacion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarProveedor", method = RequestMethod.POST)
	public String modificarProveedor(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model) {

		HomologacionJSP homologacionJSP = cargarHomologacion(buscadorHomologacionJSP, model);
		homologacionJSP.setLectura(Boolean.FALSE);
		model.addAttribute("homologacion", homologacionJSP);
		return "homologacion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/addProveedor", method = RequestMethod.GET)
	public String addProveedor(Model model) {

		HomologacionJSP homologacionJSP = new HomologacionJSP();
		homologacionJSP.setLectura(Boolean.FALSE);
		homologacionJSP.setIdEstadoProveedor(0);
		model.addAttribute("homologacion", homologacionJSP);
		return "homologacion";
	}

	private HomologacionJSP cargarHomologacion(BuscadorHomologacionJSP buscadorHomologacionJSP, Model model) {

		HomologacionJSP homologacionJSP = new HomologacionJSP();

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(buscadorHomologacionJSP.getIdProveedor()));

		homologacionJSP.setIdProveedor(proveedor.getIdProveedor().toString());
		homologacionJSP.setIdEstadoProveedor(proveedor.getEstadoProveedor().getIdEstadoProveedor());
		homologacionJSP.setIdCompaniaHomologacion(proveedor.getCompania().getIdCompania().toString());

		if (proveedor.getFechaResolucion() != null) {
			homologacionJSP.setFechaHomologacion(
					Utiles.convertDateToString(proveedor.getFechaResolucion(), Constantes.FORMATO_FECHA_PANTALLA));
		}

		if (proveedor.getEvidencia67() != null) {
			homologacionJSP
					.setDescripcionEvidenciaEnvioInformacion(proveedor.getEvidencia67().getDocumento().getTitulo());
		}

		if (proveedor.getEvidencia68() != null) {
			homologacionJSP
					.setDescripcionEvidenciaInformacionRecibida(proveedor.getEvidencia68().getDocumento().getTitulo());
		}

		if (proveedor.getEvidencia69() != null) {
			homologacionJSP.setDescripcionEvidenciaSolicitudDocumentacion(
					proveedor.getEvidencia69().getDocumento().getTitulo());
		}

		if (proveedor.getEvidencia70() != null) {
			homologacionJSP.setDescripcionEvidenciaDocumentacionRecibida(
					proveedor.getEvidencia70().getDocumento().getTitulo());
		}

		if (proveedor.getEvidencia73() != null) {
			homologacionJSP.setDescripcionEvidenciaComunicacion(proveedor.getEvidencia73().getDocumento().getTitulo());
		}

		if (proveedor.getEvidencia72() != null) {
			homologacionJSP.setResultadoHomologacion(true);
			homologacionJSP.setDescripcionEvidenciaResultadoHomologacion(
					proveedor.getEvidencia72().getDocumento().getTitulo());
		}

		if (proveedor.getEvidencia74() != null) {
			homologacionJSP.setResultadoHomologacion(false);
			homologacionJSP.setDescripcionEvidenciaResultadoHomologacion(
					proveedor.getEvidencia74().getDocumento().getTitulo());
		}

		// if anexos -> cargarListaAnexos
		if (proveedor.getEstadoProveedor().getIdEstadoProveedor()
				.intValue() >= Constantes.ESTADO_PROVEEDOR_ANALIZANDO) {
			int page = 0; // default page number is 0 (yes it is weird)
			int size = 5; // default page size is 10

			if (StringUtils.isNotBlank(homologacionJSP.getPage())) {
				page = Integer.parseInt(homologacionJSP.getPage()) - 1;
			}
			cargarListaAnexos(proveedor.getIdProveedor().toString(), model, page, size);
		}

		return homologacionJSP;
	}

	private void cargarListaAnexos(String idProveedor, Model model, int page, int size) {

		model.addAttribute("anexosProveedor",
				anexoProveedorDAO.findByIdProveedor(Integer.parseInt(idProveedor), PageRequest.of(page, size)));
	}

	@RequestMapping(value = "/paginacionTablaHomologacion", method = RequestMethod.POST)
	public String paginacionTablaHomologacion(HomologacionJSP homologacionJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(homologacionJSP.getPage())) {
			homologacionJSP.setPage(homologacionJSP.getPage().substring(homologacionJSP.getPage().indexOf(",") + 1,
					homologacionJSP.getPage().length()));
			page = Integer.parseInt(homologacionJSP.getPage()) - 1;
		}

		cargarListaAnexos(homologacionJSP.getIdProveedor(), model, page, size);
		model.addAttribute("homologacion", homologacionJSP);
		return "homologacion";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/anadirEvidenciaAnexoHomologacion", method = RequestMethod.POST)
	public String anadirEvidenciaAnexoHomologacion(HomologacionJSP homologacionJSP, Model model, HttpSession sesion) {

		try {
			homologacionService.guardarEvidenciaDocumentosAnexos(homologacionJSP, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
			// TODO falta añadir error
		}

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(homologacionJSP.getPage())) {
			page = Integer.parseInt(homologacionJSP.getPage()) - 1;
		}
		cargarListaAnexos(homologacionJSP.getIdProveedor(), model, page, size);
		model.addAttribute("homologacion", homologacionJSP);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "homologacion";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/pasoSiguienteHomologacion", method = RequestMethod.POST)
	public String pasoSiguienteHomologacion(HomologacionJSP homologacionJSP, Model model, HttpSession sesion) {

		try {
			homologacionService.guardarProveedor(homologacionJSP, sesion, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			// TODO falta añadir error
		}

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(homologacionJSP.getPage())) {
			page = Integer.parseInt(homologacionJSP.getPage()) - 1;
		}
		cargarListaAnexos(homologacionJSP.getIdProveedor(), model, page, size);
		model.addAttribute("homologacion", homologacionJSP);
		return "homologacion";
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaEnvioInformacion", method = RequestMethod.POST)
	public void verEvidenciaEnvioInformacion(HomologacionJSP homologacionJSP, Model model,
			HttpServletResponse response) {

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));
		descargarEvidenciaHomologacion(proveedor.getIdProveedor().toString(), proveedor.getEvidencia67().getDocumento(),
				Constantes.ESTADO_PROVEEDOR_ENVIADO_INFORMACION, model, response);
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaInformacionRecibida", method = RequestMethod.POST)
	public void verEvidenciaInformacionRecibida(HomologacionJSP homologacionJSP, Model model,
			HttpServletResponse response) {

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));
		descargarEvidenciaHomologacion(proveedor.getIdProveedor().toString(), proveedor.getEvidencia68().getDocumento(),
				Constantes.ESTADO_PROVEEDOR_RECIBIDO_INFORMACION, model, response);
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaSolicitudDocumentacion", method = RequestMethod.POST)
	public void verEvidenciaSolicitudDocumentacion(HomologacionJSP homologacionJSP, Model model,
			HttpServletResponse response) {

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));
		descargarEvidenciaHomologacion(proveedor.getIdProveedor().toString(), proveedor.getEvidencia69().getDocumento(),
				Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_SOLICITADA, model, response);
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaDocumentacionRecibida", method = RequestMethod.POST)
	public void verEvidenciaDocumentacionRecibida(HomologacionJSP homologacionJSP, Model model,
			HttpServletResponse response) {

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));
		descargarEvidenciaHomologacion(proveedor.getIdProveedor().toString(), proveedor.getEvidencia70().getDocumento(),
				Constantes.ESTADO_PROVEEDOR_ANALIZANDO, model, response);
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaResultadoHomologacion", method = RequestMethod.POST)
	public void verEvidenciaResultadoHomologacion(HomologacionJSP homologacionJSP, Model model,
			HttpServletResponse response) {

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));

		Documento evidencia;
		int idEstado;
		if (homologacionJSP.getResultadoHomologacion()) {
			evidencia = proveedor.getEvidencia72().getDocumento();
			idEstado = Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_ACEPTADA;
		} else {
			evidencia = proveedor.getEvidencia74().getDocumento();
			idEstado = Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_RECHAZADA;
		}

		descargarEvidenciaHomologacion(proveedor.getIdProveedor().toString(), evidencia, idEstado, model, response);
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaComunicacion", method = RequestMethod.POST)
	public void verEvidenciaComunicacion(HomologacionJSP homologacionJSP, Model model, HttpServletResponse response) {

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));
		descargarEvidenciaHomologacion(proveedor.getIdProveedor().toString(), proveedor.getEvidencia73().getDocumento(),
				Constantes.ESTADO_PROVEEDOR_FINALIZADO, model, response);
	}

	/**
	 * Metodo que resetea los filtros de la pantalla
	 * 
	 * @param buscadorModificarRevisionJSP objeto con los campos de filtro de la
	 *                                     pantalla
	 * @param model                        objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verEvidenciaAnexoProveedor", method = RequestMethod.POST)
	public void verEvidenciaAnexoProveedor(HomologacionJSP homologacionJSP, Model model, HttpServletResponse response) {

		try {
			AnexoProveedor anexoProveedor = anexoProveedorDAO
					.getOne(Integer.parseInt(homologacionJSP.getIdAnexoProveedor()));
			Documento documento = anexoProveedor.getEvidencia().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	private void descargarEvidenciaHomologacion(String idProveedor, Documento documento, int idEstado, Model model,
			HttpServletResponse response) {
		try {
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}
}