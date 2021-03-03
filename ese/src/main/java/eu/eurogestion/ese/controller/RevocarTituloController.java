package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.CausaRevocacion;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.pojo.RevocarTituloJSP;
import eu.eurogestion.ese.pojo.SuspensionActivaTituloJSP;
import eu.eurogestion.ese.repository.CausaRevocacionDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.services.RevocarTituloService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class RevocarTituloController {

	/**
	 * Repositorio de la clase de dominio Titulo
	 */
	@Autowired
	public TituloDAO tituloDAO;

	/**
	 * Repositorio de la clase de dominio CausaRevocacion
	 */
	@Autowired
	public CausaRevocacionDAO causaRevocacionDAO;

	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Servicio de la revocacion de un titulo
	 */
	@Autowired
	public RevocarTituloService revocarTituloService;

	/**
	 * Metodo que devuelve una lista de tipo de titulos para un select
	 * 
	 * @return lista de objetos TipoTitulo
	 */
	@ModelAttribute("causasRevocacion")
	public List<CausaRevocacion> listTiposTitulos() {
		List<CausaRevocacion> lista = new ArrayList<>();
		CausaRevocacion causaRevocacion = new CausaRevocacion();
		causaRevocacion.setValor("Selecciona uno:");
		lista.add(causaRevocacion);
		lista.addAll(causaRevocacionDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/revocarTituloSuspension", method = RequestMethod.POST)
	public String revocarTituloSuspension(SuspensionActivaTituloJSP suspensionActivaTituloJSP, Model model) {

		try {

			model.addAttribute("revocarTitulo",
					convertSuspensionActivaTituloJSPToRevocarTituloJSP(suspensionActivaTituloJSP));

		} catch (RuntimeException e) {
			log.error(e.getMessage());
			return "forward:/errorRevocarTituloSuspension";
		}
		return "revocarTitulo";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/revocarTitulo", method = RequestMethod.POST)
	public String revocarTitulo(DetallePersonalJSP detallePersonalJSP, Model model, HttpServletResponse response) {
		try {
			RevocarTituloJSP revocarTituloJSP = convertDetallePersonalJSPToRevocarTituloJSP(detallePersonalJSP);
			revocarTituloJSP.setIsSuspension(Boolean.FALSE);
			model.addAttribute("revocarTitulo", revocarTituloJSP);

		} catch (RuntimeException e) {
			log.error(e.getMessage());
			return "forward:/errorRevocarTitulo";
		}
		return "revocarTitulo";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/firmarRevocarTitulo", method = RequestMethod.POST)
	public String aceptarRevocarTitulo(RevocarTituloJSP revocarTituloJSP, Model model, HttpServletResponse response,
			HttpSession sesion) {
		if (StringUtils.isBlank(revocarTituloJSP.getNombreResponsableSeguridad())
				|| StringUtils.isBlank(revocarTituloJSP.getPasswordResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("revocarTitulo", revocarTituloJSP);
			return "revocarTitulo";
		}

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				revocarTituloJSP.getNombreResponsableSeguridad(), revocarTituloJSP.getPasswordResponsableSeguridad());
		if (responsableSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("revocarTitulo", revocarTituloJSP);
			return "revocarTitulo";
		}
		try {
			revocarTituloService.revocarTitulo(revocarTituloJSP, sesion);
		} catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("revocarTitulo", revocarTituloJSP);
			return "revocarTitulo";
		}
		return "forward:/volverRevocarTitulo";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/errorvolverRevocarTitulo", method = RequestMethod.POST)
	public String errorvolverRevocarTitulo(RevocarTituloJSP revocarTituloJSP, Model model, HttpServletResponse response,
			HttpSession sesion) {

		model.addAttribute("revocarTitulo", revocarTituloJSP);
		return "revocarTitulo";

	}

	private RevocarTituloJSP convertDetallePersonalJSPToRevocarTituloJSP(DetallePersonalJSP detallePersonalJSP) {
		RevocarTituloJSP revocarTituloJSP = new RevocarTituloJSP();
		Titulo titulo = tituloDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdTitulo()));
		revocarTituloJSP.setFechaCaducidad(
				Utiles.convertDateToString(titulo.getFechaCaducidad(), Constantes.FORMATO_FECHA_PANTALLA));
		revocarTituloJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());
		revocarTituloJSP.setIdTitulo(detallePersonalJSP.getIdTitulo());
		String nombre = titulo.getPersonal().getNombre() + " " + titulo.getPersonal().getApellido1();
		if (StringUtils.isNotBlank(titulo.getPersonal().getApellido2())) {
			nombre += " " + titulo.getPersonal().getApellido2();
		}
		revocarTituloJSP.setNombre(nombre);
		revocarTituloJSP.setTitulo(titulo.getNombre());

		return revocarTituloJSP;

	}

	private RevocarTituloJSP convertSuspensionActivaTituloJSPToRevocarTituloJSP(
			SuspensionActivaTituloJSP suspensionActivaTituloJSP) {
		RevocarTituloJSP revocarTituloJSP = new RevocarTituloJSP();
		Titulo titulo = tituloDAO.getOne(Integer.parseInt(suspensionActivaTituloJSP.getIdTitulo()));
		revocarTituloJSP.setFechaCaducidad(
				Utiles.convertDateToString(titulo.getFechaCaducidad(), Constantes.FORMATO_FECHA_PANTALLA));
		revocarTituloJSP.setIdPersonal(suspensionActivaTituloJSP.getIdPersonal());
		revocarTituloJSP.setIdTitulo(suspensionActivaTituloJSP.getIdTitulo());
		revocarTituloJSP.setNombre(suspensionActivaTituloJSP.getNombre());
		revocarTituloJSP.setTitulo(titulo.getNombre());
		revocarTituloJSP.setIdSuspension(suspensionActivaTituloJSP.getIdSuspension());
		revocarTituloJSP.setCausaResolucionSuspension(suspensionActivaTituloJSP.getCausaResolucion());
		revocarTituloJSP.setFechaResolucionSuspension(suspensionActivaTituloJSP.getFechaResolucion());
		revocarTituloJSP.setObservacionesSuspension(suspensionActivaTituloJSP.getObservaciones());
		revocarTituloJSP.setIsSuspension(Boolean.TRUE);
		return revocarTituloJSP;
	}
}