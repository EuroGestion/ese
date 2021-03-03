package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.PuntoInfraestructura;
import eu.eurogestion.ese.domain.Tramo;
import eu.eurogestion.ese.pojo.BuscadorTramoJSP;
import eu.eurogestion.ese.pojo.DetalleTramoJSP;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TramoDAO;
import eu.eurogestion.ese.services.TramoService;
import eu.eurogestion.ese.services.UtilesPDFService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class DetalleTramoController {

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public TramoDAO tramoDAO;

	/**
	 * Repositorio de la clase de dominio Curso
	 */
	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public TramoService tramoService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@ModelAttribute("tramos")
	public List<Tramo> listTramosAll() {
		List<Tramo> lista = new ArrayList<>();
		Tramo tramo = new Tramo();
		tramo.setNombre("Selecciona uno:");
		lista.add(tramo);
		lista.addAll(tramoDAO.findByEsEspecialFalse(Sort.by(Order.asc("nombre"))));
		return lista;
	}

	@ModelAttribute("puntosInfraestructura")
	public List<PuntoInfraestructura> listPuntoInfraestructuraAll() {
		List<PuntoInfraestructura> lista = new ArrayList<>();
		PuntoInfraestructura tramo = new PuntoInfraestructura();
		tramo.setNombre("Selecciona uno:");
		lista.add(tramo);
		lista.addAll(puntoInfraestructuraDAO.findAll(Sort.by(Order.asc("nombreLargo"))));
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verDetalleTramo", method = RequestMethod.POST)
	public String verDetalleTramo(BuscadorTramoJSP buscadorTramoJSP, Model model) {

		model.addAttribute("detalleTramo", convertTramoToDetalleTramoJSP(
				tramoDAO.getOne(Integer.parseInt(buscadorTramoJSP.getIdTramo())), Boolean.TRUE));
		return "detalleTramo";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/modificarDetalleTramo", method = RequestMethod.POST)
	public String modificarDetalleTramo(BuscadorTramoJSP buscadorTramoJSP, Model model) {

		model.addAttribute("detalleTramo", convertTramoToDetalleTramoJSP(
				tramoDAO.getOne(Integer.parseInt(buscadorTramoJSP.getIdTramo())), Boolean.FALSE));
		return "detalleTramo";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/nuevoTramo", method = RequestMethod.GET)
	public String nuevoTramo(Model model) {

		DetalleTramoJSP detalleTramoJSP = new DetalleTramoJSP();
		detalleTramoJSP.setLectura(Boolean.FALSE);
		model.addAttribute("detalleTramo", detalleTramoJSP);

		return "detalleTramo";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarDetalleTramo", method = RequestMethod.POST)
	public String guardarDetalleTramo(DetalleTramoJSP detalleTramoJSP, Model model) {

		tramoService.guardarTramo(detalleTramoJSP);
		model.addAttribute("detalleTramo", convertTramoToDetalleTramoJSP(
				tramoDAO.getOne(Integer.parseInt(detalleTramoJSP.getIdTramo())), detalleTramoJSP.isLectura()));
		model.addAttribute("info", "Se ha guardado correctamente");
		return "detalleTramo";
	}

	private DetalleTramoJSP convertTramoToDetalleTramoJSP(Tramo tramo, Boolean lectura) {

		DetalleTramoJSP detalleTramoJSP = new DetalleTramoJSP();
		detalleTramoJSP.setLectura(lectura);
		detalleTramoJSP.setIdTramo(tramo.getIdTramo().toString());
		detalleTramoJSP.setNombre(tramo.getNombre());
		detalleTramoJSP.setIdOrigen(tramo.getPuntoOrigen().getIdPuntoInfraestructura().toString());
		detalleTramoJSP.setIdDestino(tramo.getPuntoDestino().getIdPuntoInfraestructura().toString());
		return detalleTramoJSP;
	}

}