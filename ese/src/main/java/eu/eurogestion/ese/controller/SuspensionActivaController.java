package eu.eurogestion.ese.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Suspension;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.RevocarTituloJSP;
import eu.eurogestion.ese.pojo.SuspensionActivaTituloJSP;
import eu.eurogestion.ese.repository.EstadoTituloDAO;
import eu.eurogestion.ese.repository.SuspensionDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.services.SuspensionActivaTituloService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rmerino, alvaro
 *
 */
@Slf4j
@Controller
public class SuspensionActivaController {

	/**
	 * Repositorio de la clase de dominio Titulo
	 */
	@Autowired
	public TituloDAO tituloDAO;

	/**
	 * Repositorio de la clase de dominio EstadoTitulo
	 */
	@Autowired
	public EstadoTituloDAO estadoTituloDAO;

	/**
	 * Repositorio de la clase de dominio Suspension
	 */
	@Autowired
	public SuspensionDAO suspensionDAO;

	/**
	 * Servicio de la suspension de un titulo
	 */
	@Autowired
	public SuspensionActivaTituloService suspensionActivaTituloService;

	@RequestMapping(value = "/recuperarSuspensionActivaTitulo", method = RequestMethod.POST)
	public String aceptarSuspenderTitulo(SuspensionActivaTituloJSP suspensionActivaTituloJSP, Model model,
			HttpSession sesion) {

		try {

			suspensionActivaTituloService.recuperarTitulo(suspensionActivaTituloJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("suspensionActiva", suspensionActivaTituloJSP);
			return "suspensionActiva";
		}

		return "forward:/volverSuspensionActivaRecuperar";
	}

	@RequestMapping(value = "/caducarSuspensionActivaTitulo", method = RequestMethod.POST)
	public String caducarSuspenderTitulo(SuspensionActivaTituloJSP suspensionActivaTituloJSP, Model model,
			HttpSession sesion) {

		try {

			suspensionActivaTituloService.caducarTitulo(suspensionActivaTituloJSP);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("suspensionActiva", suspensionActivaTituloJSP);
			return "suspensionActiva";
		}

		return "forward:/volverSuspensionActivaCaducar";
	}

	@RequestMapping(value = "/suspensionActivaTitulo", method = RequestMethod.POST)
	public String suspenderTitulo(DetallePersonalJSP detallePersonalJSP, Model model) {

		model.addAttribute("suspensionActiva", convertSuspenderTituloToSuspensionActivaTituloJSP(
				tituloDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdTitulo()))));

		return "suspensionActiva";

	}

	@RequestMapping(value = "/errorRevocarTituloSuspension", method = RequestMethod.POST)
	public String errorRevocarTituloSuspension(SuspensionActivaTituloJSP suspensionActivaTituloJSP, Model model) {

		model.addAttribute("suspensionActiva", suspensionActivaTituloJSP);

		return "suspensionActiva";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverRevocarTituloSuspensionActiva", method = RequestMethod.POST)
	public String volverRevocarTituloSuspensionActiva(RevocarTituloJSP revocarTituloJSP, Model model) {

		try {

			model.addAttribute("suspensionActiva", convertRevocarTituloToSuspensionActivaTituloJSP(revocarTituloJSP));

		} catch (RuntimeException e) {
			log.error(e.getMessage());
			return "forward:/errorvolverRevocarTitulo";
		}
		return "suspensionActiva";
	}

	private SuspensionActivaTituloJSP convertRevocarTituloToSuspensionActivaTituloJSP(
			RevocarTituloJSP revocarTituloJSP) {

		Titulo titulo = tituloDAO.getOne(Integer.parseInt(revocarTituloJSP.getIdTitulo()));
		SuspensionActivaTituloJSP suspensionActivaTituloJSP = new SuspensionActivaTituloJSP();

		suspensionActivaTituloJSP.setIdPersonal(titulo.getPersonal().getIdPersonal().toString());
		suspensionActivaTituloJSP.setIdTitulo(titulo.getIdTitulo().toString());
		Suspension suspensionActiva = null;
		for (Suspension suspension : titulo.getListSuspension()) {
			if (suspension.getFechaResolucion() == null) {
				suspensionActiva = suspension;
			}

		}
		suspensionActivaTituloJSP.setIdSuspension(suspensionActiva.getIdSuspension().toString());

		String nombre = titulo.getPersonal().getNombre() + " " + titulo.getPersonal().getApellido1();
		if (StringUtils.isNotBlank(titulo.getPersonal().getApellido2())) {
			nombre += " " + titulo.getPersonal().getApellido2();
		}
		suspensionActivaTituloJSP.setNombre(nombre);

		suspensionActivaTituloJSP.setTitulo(titulo.getNombre());
		suspensionActivaTituloJSP.setFechaCaducidad(
				Utiles.convertDateToString(titulo.getFechaCaducidad(), Constantes.FORMATO_FECHA_PANTALLA));
		suspensionActivaTituloJSP.setCausa(suspensionActiva.getCausaSuspension().getValor());
		suspensionActivaTituloJSP.setFechaSuspension(
				Utiles.convertDateToString(suspensionActiva.getFechaSuspension(), Constantes.FORMATO_FECHA_PANTALLA));

		suspensionActivaTituloJSP.setCausaResolucion(revocarTituloJSP.getCausaResolucionSuspension());
		suspensionActivaTituloJSP.setFechaResolucion(revocarTituloJSP.getFechaResolucionSuspension());
		suspensionActivaTituloJSP.setObservaciones(revocarTituloJSP.getObservacionesSuspension());

		return suspensionActivaTituloJSP;

	}

	private SuspensionActivaTituloJSP convertSuspenderTituloToSuspensionActivaTituloJSP(Titulo titulo) {
		SuspensionActivaTituloJSP suspensionActivaTituloJSP = new SuspensionActivaTituloJSP();

		suspensionActivaTituloJSP.setIdPersonal(titulo.getPersonal().getIdPersonal().toString());
		suspensionActivaTituloJSP.setIdTitulo(titulo.getIdTitulo().toString());
		Suspension suspensionActiva = null;
		for (Suspension suspension : titulo.getListSuspension()) {
			if (suspension.getFechaResolucion() == null) {
				suspensionActiva = suspension;
			}

		}
		suspensionActivaTituloJSP.setIdSuspension(suspensionActiva.getIdSuspension().toString());

		String nombre = titulo.getPersonal().getNombre() + " " + titulo.getPersonal().getApellido1();
		if (StringUtils.isNotBlank(titulo.getPersonal().getApellido2())) {
			nombre += " " + titulo.getPersonal().getApellido2();
		}
		suspensionActivaTituloJSP.setNombre(nombre);

		suspensionActivaTituloJSP.setTitulo(titulo.getNombre());
		suspensionActivaTituloJSP.setFechaCaducidad(
				Utiles.convertDateToString(titulo.getFechaCaducidad(), Constantes.FORMATO_FECHA_PANTALLA));
		suspensionActivaTituloJSP.setCausa(suspensionActiva.getCausaSuspension().getValor());
		suspensionActivaTituloJSP.setFechaSuspension(
				Utiles.convertDateToString(suspensionActiva.getFechaSuspension(), Constantes.FORMATO_FECHA_PANTALLA));

		return suspensionActivaTituloJSP;
	}

}