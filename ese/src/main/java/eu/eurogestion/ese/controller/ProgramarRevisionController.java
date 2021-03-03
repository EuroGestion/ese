package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.pojo.ProgramarRevisionJSP;
import eu.eurogestion.ese.pojo.SelectPersonalFormRevJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.services.ProgramarRevisionService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class ProgramarRevisionController {

	/**
	 * Repositorio de la clase de dominio Compania
	 */
	@Autowired
	public CompaniaDAO companiaDAO;

	/**
	 * Servicio de la programacion de la formacion
	 */
	@Autowired
	public ProgramarRevisionService programarRevisionService;

	/**
	 * Metodo que devuelve una lista de Personas para una tabla
	 * 
	 * @return lista de objetos PersonalTablaJSP
	 */
	@ModelAttribute("centrosMedicos")
	public List<Compania> listCentroFormacionAll() {
		List<Compania> lista = new ArrayList<>();
		Compania compania = new Compania();
		compania.setNombre("Selecciona uno:");
		lista.add(compania);
		lista.addAll(companiaDAO.findAllCompaniaCentroMedico());
		return lista;
	}

	@RequestMapping(value = "/selectPersonalOptionRev", method = RequestMethod.POST)
	public String selectPersonalOptionRev(SelectPersonalFormRevJSP selectPersonalFormRev, Model model,
			HttpSession session) {

		if (CollectionUtils.isEmpty(selectPersonalFormRev.getListaPersonalTotal())) {
			model.addAttribute("selectPersonalFormRev", selectPersonalFormRev);
			return "forward:/errorValidacionFormacionRevision";
		}

		ProgramarRevisionJSP programarRevisionJSP = convertSelectPersonalFormRevJSPToProgramarRevisionJSP(
				selectPersonalFormRev);
		programarRevisionJSP.setNAsistentes(String.valueOf(selectPersonalFormRev.getListaPersonalTotal().size()));
		programarRevisionJSP.setIsSelectUser(Boolean.TRUE);
		model.addAttribute("programarRevision", programarRevisionJSP);

		return "programarRevision";
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
	@RequestMapping(value = "/programarRevision", method = RequestMethod.POST)
	public String programarRevision(ProgramarRevisionJSP programarRevision, Model model, HttpSession session) {

		try {
			programarRevisionService.crearRevision(programarRevision);

		} catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("programarRevision", programarRevision);
			return "programarRevision";
		}

		model.addAttribute("programarRevision", programarRevision);
		return "redirect:/buscadorModificarRevisionProgramarRevision";
	}

	private ProgramarRevisionJSP convertSelectPersonalFormRevJSPToProgramarRevisionJSP(
			SelectPersonalFormRevJSP selectPersonalFormRevJSP) {

		ProgramarRevisionJSP programarRevision = new ProgramarRevisionJSP();
		programarRevision.setIdCargoSelect(selectPersonalFormRevJSP.getIdCargo());
		programarRevision.setNombreSelect(selectPersonalFormRevJSP.getNombre());
		programarRevision.setApellidoSelect(selectPersonalFormRevJSP.getApellido());
		programarRevision.setDniSelect(selectPersonalFormRevJSP.getDni());
		programarRevision.setListaPersonalTotalSelect(selectPersonalFormRevJSP.getListaPersonalTotal());
		programarRevision.setIsFormacionSelect(selectPersonalFormRevJSP.getIsFormacion());
		programarRevision.setIsRevisionSelect(selectPersonalFormRevJSP.getIsRevision());
		return programarRevision;

	}

}