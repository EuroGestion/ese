package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.ModeloMaterial;
import eu.eurogestion.ese.domain.TipoCurso;
import eu.eurogestion.ese.pojo.ProgramarFormacionJSP;
import eu.eurogestion.ese.pojo.SelectPersonalFormRevJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoCursoDAO;
import eu.eurogestion.ese.services.ProgramarFormacionService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class ProgramarFornacionController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Repositorio de la clase de dominio ModeloMaterial
	 */
	@Autowired
	public ModeloMaterialDAO modeloMaterialDAO;

	/**
	 * Repositorio de la clase de dominio TipoCurso
	 */
	@Autowired
	public TipoCursoDAO tipoCursoDAO;

	/**
	 * Repositorio de la clase de dominio Personal
	 */
	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Servicio de la programacion de la formacion
	 */
	@Autowired
	public ProgramarFormacionService programarFormacionService;

	/**
	 * Metodo que devuelve una lista de Personas para una tabla
	 * 
	 * @return lista de objetos PersonalTablaJSP
	 */
	@ModelAttribute("centrosFormacion")
	public List<Compania> listCentroFormacionAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAllCompaniaFormacion());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Cargos para un select
	 * 
	 * @return lista de objetos Cargo
	 */
	@ModelAttribute("tiposCursos")
	public List<TipoCurso> listTipoCargoAll() {
		List<TipoCurso> lista = new ArrayList<>();
		TipoCurso tipoCurso = new TipoCurso();
		tipoCurso.setValor("Selecciona uno:");
		lista.add(tipoCurso);
		lista.addAll(tipoCursoDAO.findAll());
		return lista;
	}

	/**
	 * Metodo que devuelve una lista de Cargos para un select
	 * 
	 * @return lista de objetos Cargo
	 */
	@ModelAttribute("materiales")
	public List<ModeloMaterial> listMaterialesAll() {
		List<ModeloMaterial> lista = new ArrayList<>();
		ModeloMaterial modeloMaterial = new ModeloMaterial();
		modeloMaterial.setSerie("Selecciona uno:");
		lista.add(modeloMaterial);
		lista.addAll(modeloMaterialDAO.findAll());
		return lista;
	}

	@RequestMapping(value = "/selectPersonalOptionForm", method = RequestMethod.POST)
	public String selectPersonalOptionForm(SelectPersonalFormRevJSP selectPersonalFormRev, Model model,
			HttpSession session) {

		if (CollectionUtils.isEmpty(selectPersonalFormRev.getListaPersonalTotal())) {
			model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
			return "forward:/errorValidacionFormacionRevision";
		}

		ProgramarFormacionJSP programarFormacionJSP = convertSelectPersonalFormRevJSPToProgramarFormacionJSP(
				selectPersonalFormRev);

		programarFormacionJSP.setNAsistentes(String.valueOf(selectPersonalFormRev.getListaPersonalTotal().size()));
		programarFormacionJSP.setIsSelectUser(Boolean.TRUE);
		model.addAttribute("programarFormacion", programarFormacionJSP);

		return "programarFormacion";
	}

	/**
	 * Metodo que filtra la tabla de personal de la pantalla dados los filtros
	 * ingresados desde la pantalla
	 * 
	 * @param selectPersonalFormRev objeto con los campos de filtro de la pantalla
	 * @param model                 objeto model de la pantalla
	 * @param session               objeto de la sesion
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/programarFormacion", method = RequestMethod.POST)
	public String programarFormacion(ProgramarFormacionJSP programarFormacion, Model model, HttpSession session) {

		if (StringUtils.isBlank(programarFormacion.getFechaFin())
				^ StringUtils.isBlank(programarFormacion.getFechaInicio())) {
			model.addAttribute("programarFormacion", programarFormacion);
			return "programarFormacion";
		}

		try {
			programarFormacionService.crearCurso(programarFormacion);

		} catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("programarFormacion", programarFormacion);
			return "programarFormacion";
		}

		model.addAttribute("programarFormacion", programarFormacion);
		return "redirect:/buscadorModificarFormacionProgramarFormacion";
	}

	@RequestMapping(value = "/seleccionarCurso", method = RequestMethod.POST)
	public String seleccionarCurso(ProgramarFormacionJSP programarFormacion, Model model, HttpSession session) {

		programarFormacion.setIsInfraestructura(Boolean.FALSE);
		programarFormacion.setInfraestructura(null);
		programarFormacion.setIsNormativa(Boolean.FALSE);
		programarFormacion.setNormativa(null);
		programarFormacion.setIsMaterial(Boolean.FALSE);
		programarFormacion.setIdMaterial(null);

		switch (programarFormacion.getIdTipoCurso()) {
		case "1":
			programarFormacion.setIsMaterial(Boolean.TRUE);
			break;
		case "2":
			programarFormacion.setIsInfraestructura(Boolean.TRUE);
			break;
		case "3":
			programarFormacion.setIsNormativa(Boolean.TRUE);
			break;
		}

		model.addAttribute("programarFormacion", programarFormacion);
		return "programarFormacion";
	}

	private ProgramarFormacionJSP convertSelectPersonalFormRevJSPToProgramarFormacionJSP(
			SelectPersonalFormRevJSP selectPersonalFormRevJSP) {

		ProgramarFormacionJSP programarFormacion = new ProgramarFormacionJSP();
		programarFormacion.setIdCargoSelect(selectPersonalFormRevJSP.getIdCargo());
		programarFormacion.setNombreSelect(selectPersonalFormRevJSP.getNombre());
		programarFormacion.setApellidoSelect(selectPersonalFormRevJSP.getApellido());
		programarFormacion.setDniSelect(selectPersonalFormRevJSP.getDni());
		programarFormacion.setListaPersonalTotalSelect(selectPersonalFormRevJSP.getListaPersonalTotal());
		programarFormacion.setIsFormacionSelect(selectPersonalFormRevJSP.getIsFormacion());
		programarFormacion.setIsRevisionSelect(selectPersonalFormRevJSP.getIsRevision());
		return programarFormacion;

	}

}