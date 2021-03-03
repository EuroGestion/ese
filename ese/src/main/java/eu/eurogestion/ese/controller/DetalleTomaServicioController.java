package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.TomaServicio;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorTomaServicioJSP;
import eu.eurogestion.ese.pojo.DetalleTomaServicioJSP;
import eu.eurogestion.ese.repository.TomaServicioDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.services.DetalleTomaServicioService;
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
public class DetalleTomaServicioController {

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public TrenDAO trenDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TomaServicioDAO tomaServicioDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public DetalleTomaServicioService detalleTomaServicioService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	/**
	 * Metodo que devuelve una lista de trenes para una tabla
	 * 
	 * @return lista de objetos Tren
	 */
	@ModelAttribute("trenes")
	public List<Tren> listTrenesAll() {
		List<Tren> lista = new ArrayList<>();
		Tren personal = new Tren();
		personal.setNumero("Selecciona uno:");
		lista.add(personal);
		lista.addAll(trenDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleTomaServicio", method = RequestMethod.POST)
	public String verTomaServicio(BuscadorTomaServicioJSP buscadorTomaServicioJSP, Model model) {

		model.addAttribute("detalleTomaServicio", convertTomaServicioToDetalleTomaServicioJSP(
				tomaServicioDAO.getOne(Integer.parseInt(buscadorTomaServicioJSP.getIdTomaServicio())), Boolean.TRUE));

		return "detalleTomaServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarDetalleTomaServicio", method = RequestMethod.POST)
	public String modificarTomaServicio(BuscadorTomaServicioJSP buscadorTomaServicioJSP, Model model) {

		model.addAttribute("detalleTomaServicio", convertTomaServicioToDetalleTomaServicioJSP(
				tomaServicioDAO.getOne(Integer.parseInt(buscadorTomaServicioJSP.getIdTomaServicio())), Boolean.FALSE));

		return "detalleTomaServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/nuevoDetalleTomaServicio", method = RequestMethod.GET)
	public String nuevoTomaServicio(Model model) {

		DetalleTomaServicioJSP detalleTomaServicioJSP = new DetalleTomaServicioJSP();
		detalleTomaServicioJSP.setLectura(Boolean.FALSE);
		model.addAttribute("detalleTomaServicio", detalleTomaServicioJSP);

		return "detalleTomaServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarDetalleTomaServicio", method = RequestMethod.POST)
	public String guardarDetalleTomaServicio(DetalleTomaServicioJSP detalleTomaServicioJSP, Model model,
			HttpSession sesion) {

		try {
			detalleTomaServicioService.guardarDetalleTomaServicio(detalleTomaServicioJSP, sesion);
		} catch (EseException e) {
			// TODO falta añadir error
			log.error(e.getMessage());
		}

		model.addAttribute("detalleTomaServicio", detalleTomaServicioJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "detalleTomaServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/generarFichaDetalleTomaServicio", method = RequestMethod.POST)
	public String generarFichaDetalleTomaServicio(DetalleTomaServicioJSP detalleTomaServicioJSP, Model model,
			HttpSession sesion) {

		try {
			detalleTomaServicioService.generarFichaDetalleTomaServicio(detalleTomaServicioJSP, sesion);
		} catch (EseException e) {
			// TODO falta añadir error
			log.error(e.getMessage());
		}

		model.addAttribute("detalleTomaServicio", detalleTomaServicioJSP);
		model.addAttribute("info", "Se ha generado la evidencia correctamente");
		return "detalleTomaServicio";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verFichaDetalleTomaServicio", method = RequestMethod.POST)
	public void verFichaDetalleTomaServicio(DetalleTomaServicioJSP destalleTomaServicioJSP, Model model,
			HttpServletResponse response) {
		try {
			TomaServicio tomaServicio = tomaServicioDAO
					.getOne(Integer.parseInt(destalleTomaServicioJSP.getIdTomaServicio()));

			Documento documento = tomaServicio.getEvidencia77().getDocumento();
			utilesPDFService.descargarEvidencia(documento, response);
		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}
	}

	private DetalleTomaServicioJSP convertTomaServicioToDetalleTomaServicioJSP(TomaServicio tomaServicio,
			Boolean lectura) {

		DetalleTomaServicioJSP destalleTomaServicioJSP = new DetalleTomaServicioJSP();
		destalleTomaServicioJSP.setIdTomaServicio(tomaServicio.getIdTomaServicio().toString());
		destalleTomaServicioJSP.setLectura(lectura);
		destalleTomaServicioJSP.setIdEstadoTomaServicio(tomaServicio.getEstadoToma().getIdEstadoHistorico());
		destalleTomaServicioJSP
				.setFecha(Utiles.convertDateToString(tomaServicio.getFecha(), Constantes.FORMATO_FECHA_PANTALLA));
		destalleTomaServicioJSP
				.setHora(Utiles.convertDateToString(tomaServicio.getHora(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		destalleTomaServicioJSP.setNveLocomotora(tomaServicio.getNve());
		destalleTomaServicioJSP.setIdTren(tomaServicio.getTren().getIdTren().toString());
		destalleTomaServicioJSP.setLugar(tomaServicio.getLugarInspeccion());
		destalleTomaServicioJSP.setMotivo(tomaServicio.getMotivo());
		destalleTomaServicioJSP.setRealizarAccion(tomaServicio.getRealizarAccion());
		destalleTomaServicioJSP.setDocumentacionReglamentaria(tomaServicio.getDocumentacionReglamentaria());
		destalleTomaServicioJSP.setLibroTelefonemas(tomaServicio.getLibroTelefonemas());
		destalleTomaServicioJSP.setLibroAverias(tomaServicio.getLibroAverias());
		destalleTomaServicioJSP.setDotacionUtilesServicio(tomaServicio.getDotacionUtilesServicio());
		destalleTomaServicioJSP.setSenalizacionCabezaCola(tomaServicio.getSenalizacionCabezaCola());
		destalleTomaServicioJSP.setVisibilidadAdecuada(tomaServicio.getVisibilidadAdecuada());
		destalleTomaServicioJSP.setAnomaliasRodajeCaja(tomaServicio.getAnomaliasRodajeCaja());
		destalleTomaServicioJSP.setAnomaliasSuspension(tomaServicio.getAnomaliasSuspension());
		destalleTomaServicioJSP.setAnomaliasChoqueTraccion(tomaServicio.getAnomaliasChoqueTraccion());
		destalleTomaServicioJSP.setEstadoPrecintos(tomaServicio.getEstadoPrecintos());
		destalleTomaServicioJSP.setPosicionPalancaCambiador(tomaServicio.getPosicionPalancaCambiador());
		destalleTomaServicioJSP.setFrenosEstacionamiento(tomaServicio.getFrenosEstacionamiento());
		destalleTomaServicioJSP.setConfiguracionFrenado(tomaServicio.getConfiguracionFrenado());
		destalleTomaServicioJSP.setDispositivoVigilanciaHM(tomaServicio.getDispositivoVigilanciaHm());
		destalleTomaServicioJSP.setValvulaEmergenciaSeta(tomaServicio.getValvulaEmergenciaSeta());
		destalleTomaServicioJSP.setPruebasFreno(tomaServicio.getPruebasFreno());
		destalleTomaServicioJSP.setPruebaInversionMarcha(tomaServicio.getPruebaInversionMarcha());
		destalleTomaServicioJSP.setAsfaCorrecto(tomaServicio.getAsfaCorrecto());
		destalleTomaServicioJSP.setEquipoRadioTelefonia(tomaServicio.getEquipoRadiotelefonia());
		destalleTomaServicioJSP.setInspeccionVisual(tomaServicio.getInspeccionVisual());
		destalleTomaServicioJSP.setDatosDocumentoTren(tomaServicio.getDatosDocumentoTren());
		destalleTomaServicioJSP.setLibroTelefonemasRelevo(tomaServicio.getLibroTelefonemasRelevo());
		destalleTomaServicioJSP.setNoExistenNotificaciones(tomaServicio.getNoExistenNotificaciones());
		destalleTomaServicioJSP.setObservaciones(tomaServicio.getObservaciones());
		return destalleTomaServicioJSP;
	}

}