package eu.eurogestion.ese.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Pasf;
import eu.eurogestion.ese.pojo.BuscadorPasfJSP;
import eu.eurogestion.ese.pojo.InformePasfActualJSP;
import eu.eurogestion.ese.repository.AccidenteDAO;
import eu.eurogestion.ese.repository.CadDAO;
import eu.eurogestion.ese.repository.CursoAlumnoDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.CursoPasfDAO;
import eu.eurogestion.ese.repository.IseetDAO;
import eu.eurogestion.ese.repository.IsmpDAO;
import eu.eurogestion.ese.repository.IsoDAO;
import eu.eurogestion.ese.repository.PasfDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class InformePasfActualController {

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public PasfDAO pasfDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public CursoPasfDAO cursoPasfDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public AccidenteDAO accidenteDAO;
	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public CursoDAO cursoDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public CursoAlumnoDAO cursoAlumnoDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public IsoDAO isoDAO;

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public CadDAO cadDAO;
	
	@Autowired
	public IseetDAO iseetDAO;
	
	@Autowired
	public IsmpDAO ismpDAO;

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param buscadorHomologacionJSP objeto con los campos de filtro de la pantalla
	 * @param model                   objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/verInformePasfActual", method = RequestMethod.POST)
	public String verInformePasfActual(BuscadorPasfJSP buscadorPasfJSP, Model model) {

		InformePasfActualJSP pasfJSP = cargarPasf(buscadorPasfJSP, model);
		model.addAttribute("informePasfActual", pasfJSP);
		return "informePasfActual";
	}

	private InformePasfActualJSP cargarPasf(BuscadorPasfJSP buscadorPasfJSP, Model model) {

		InformePasfActualJSP pasfJSP = new InformePasfActualJSP();

		Pasf pasf = pasfDAO.getOne(Integer.parseInt(buscadorPasfJSP.getIdPasf()));

		pasfJSP.setIdPasf(pasf.getIdPasf());
		pasfJSP.setAnno(pasf.getAnno().toString());
		pasfJSP.setDescarrilamiento(pasf.getDescarrilamiento().toString());
		pasfJSP.setDescarrilamientoActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_DESCARRILAMIENTO).toString());
		pasfJSP.setColision(pasf.getColision().toString());
		pasfJSP.setColisionActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_COLISION).toString());
		pasfJSP.setAccidentePasoNivel(pasf.getAccidentePn().toString());
		pasfJSP.setAccidentePasoNivelActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_ACCIDENTE_PASO_NIVEL)
				.toString());
		pasfJSP.setIncendio(pasf.getIncendio().toString());
		pasfJSP.setIncendioActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_INCENDIO).toString());
		pasfJSP.setArrollamientoVia(pasf.getArrollamientoVia().toString());
		pasfJSP.setArrollamientoViaActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_ARROLLAMIENTO_VIA).toString());
		pasfJSP.setArrollamientoInterseccion(pasf.getArrollamientoInterseccion().toString());
		pasfJSP.setArrollamientoInterseccionActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_ARROLLAMIENTO_INTERSECCION)
				.toString());
		pasfJSP.setCaidaPersonas(pasf.getCaidaPersonas().toString());
		pasfJSP.setCaidaPersonasActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_CAIDA_PERSONAS).toString());
		pasfJSP.setSuicidio(pasf.getSuicidio().toString());
		pasfJSP.setSuicidioActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_SUICIDIO).toString());
		pasfJSP.setDescomposicion(pasf.getDescomposiciónCarga().toString());
		pasfJSP.setDescomposicionActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_DESCOMPOSICION_CARGAMENTO)
				.toString());
		pasfJSP.setDetencion(pasf.getDetencionTren().toString());
		pasfJSP.setDetencionActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_DETENCION_TREN).toString());
		pasfJSP.setInvasionVia(pasf.getInvasionVia().toString());
		pasfJSP.setInvasionViaActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_INVASION_VIA).toString());
		pasfJSP.setIncidentesTransportesExcepcionales(pasf.getIncidenteTe().toString());
		pasfJSP.setIncidentesTransportesExcepcionalesActual(accidenteDAO.countAccidenteByDateAndCausa(pasf.getAnno(),
				Constantes.CAUSA_ACCIDENTE_INCIDENTES_TRANSPORTES_EXCEPCIONALES).toString());
		pasfJSP.setRebaseSennal(pasf.getRebaseSenal().toString());
		pasfJSP.setRebaseSennalActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_REBASE_SENAL).toString());
		pasfJSP.setConatoColision(pasf.getConatoColision().toString());
		pasfJSP.setConatoColisionActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_CONATO_COLISION).toString());
		pasfJSP.setEnganchePantografo(pasf.getEnganche().toString());
		pasfJSP.setEnganchePantografoActual(accidenteDAO
				.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_ENGANCHE_PANTOGRAFO)
				.toString());
		pasfJSP.setOtros(pasf.getOtros().toString());
		pasfJSP.setOtrosActual(
				accidenteDAO.countAccidenteByDateAndCausa(pasf.getAnno(), Constantes.CAUSA_ACCIDENTE_OTROS).toString());

		pasfJSP.setCursosFormativos(pasf.getCursos().toString());
		pasfJSP.setCursosFormativosActual(cursoDAO.findCursoByYear(pasf.getAnno()).toString());
		pasfJSP.setCursosFormativosMaterial(
				cursoPasfDAO.sumByPasfAndCategory(pasf.getIdPasf(), Constantes.TIPO_CURSO_MATERIAL).toString());
		pasfJSP.setCursosFormativosMaterialActual(
				cursoAlumnoDAO.countByYearAndType(pasf.getAnno(), Constantes.TIPO_CURSO_MATERIAL).toString());
		pasfJSP.setCursosFormativosInfraestructura(
				cursoPasfDAO.sumByPasfAndCategory(pasf.getIdPasf(), Constantes.TIPO_CURSO_INFRAESTRUCTURA).toString());
		pasfJSP.setCursosFormativosInfraestructuraActual(
				cursoAlumnoDAO.countByYearAndType(pasf.getAnno(), Constantes.TIPO_CURSO_INFRAESTRUCTURA).toString());
		pasfJSP.setCursosFormativosOtros(
				cursoPasfDAO.sumByPasfAndCategory(pasf.getIdPasf(), Constantes.TIPO_CURSO_OTROS).toString());
		pasfJSP.setCursosFormativosOtrosActual(
				cursoAlumnoDAO.countByYearAndType(pasf.getAnno(), Constantes.TIPO_CURSO_OTROS).toString());

		pasfJSP.setRevisionesAptitudPsicofisica(pasf.getRevisiones().toString());
		pasfJSP.setRevisionesAptitudPsicofisicaActual(
				revisionPsicoDAO.findRevisionPsicoByYear(pasf.getAnno()).toString());

		pasfJSP.setIso(pasf.getIso().toString());
		pasfJSP.setIsoActual(isoDAO.countByYear(pasf.getAnno()).toString());
		pasfJSP.setCad(pasf.getCad().toString());
		pasfJSP.setCadActual(cadDAO.countByYear(pasf.getAnno()).toString());
		pasfJSP.setIssc(pasf.getIssc().toString());
		pasfJSP.setIsscActual("0");
		pasfJSP.setIsmp(pasf.getIsmp().toString());
		pasfJSP.setIsmpActual(ismpDAO.countByYear(pasf.getAnno()).toString());
		pasfJSP.setIseet(pasf.getIseet().toString());
		pasfJSP.setIseetActual(iseetDAO.countByYear(pasf.getAnno()).toString());

		pasfJSP.setAuditorias(pasf.getAuditorias().toString());
		pasfJSP.setAuditoriasActual("0");

		return pasfJSP;
	}

}