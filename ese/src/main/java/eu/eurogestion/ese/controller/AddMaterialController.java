package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Material;
import eu.eurogestion.ese.domain.ModeloMaterial;
import eu.eurogestion.ese.domain.TipoMaterial;
import eu.eurogestion.ese.pojo.AddMaterialJSP;
import eu.eurogestion.ese.pojo.BuscadorMaterialJSP;
import eu.eurogestion.ese.repository.MaterialDAO;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;
import eu.eurogestion.ese.repository.TipoMaterialDAO;
import eu.eurogestion.ese.services.AddMaterialService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rmerino
 *
 */
@Slf4j
@Controller
public class AddMaterialController {

	@Autowired
	public AddMaterialService addMaterialService;

	@Autowired
	public TipoMaterialDAO tipoMaterialDAO;

	@Autowired
	public MaterialDAO materialDAO;

	@Autowired
	public ModeloMaterialDAO modeloMaterialDAO;

	@ModelAttribute("tiposMaterial")
	public List<TipoMaterial> listTipoMaterialAll() {
		List<TipoMaterial> lista = new ArrayList<>();
		TipoMaterial tipoMaterial = new TipoMaterial();
		tipoMaterial.setValor("Selecciona uno:");
		lista.add(tipoMaterial);
		lista.addAll(tipoMaterialDAO.findAll());
		return lista;
	}

	@RequestMapping(value = "/verDetalleMaterial", method = RequestMethod.POST)
	public String verDetalleMaterial(BuscadorMaterialJSP buscadorMaterialJSP, Model model) {

		AddMaterialJSP addMaterialJSP = rellenarJSP(buscadorMaterialJSP, true);

		model.addAttribute("addMaterial", addMaterialJSP);
		cargarLista(addMaterialJSP, model, 0, 5);
		return "addMaterial";
	}

	@RequestMapping(value = "/modificarDetalleMaterial", method = RequestMethod.POST)
	public String modificarDetalleMaterial(BuscadorMaterialJSP buscadorMaterialJSP, Model model) {

		AddMaterialJSP addMaterialJSP = rellenarJSP(buscadorMaterialJSP, false);

		model.addAttribute("addMaterial", addMaterialJSP);
		cargarLista(addMaterialJSP, model, 0, 5);
		return "addMaterial";
	}

	@RequestMapping(value = "/crearMaterial", method = RequestMethod.GET)
	public String crearMaterial(Model model) {

		AddMaterialJSP addMaterialJSP = new AddMaterialJSP();

		model.addAttribute("addMaterial", addMaterialJSP);
		cargarLista(addMaterialJSP, model, 0, 5);
		return "addMaterial";

	}

	@RequestMapping(value = "/filtrarAddMaterial", method = RequestMethod.POST)
	public String filtrarAddMaterial(AddMaterialJSP addMaterialJSP, Model model) {

		model.addAttribute("addMaterial", addMaterialJSP);
		cargarLista(addMaterialJSP, model, 0, 5);
		return "addMaterial";
	}

	@RequestMapping(value = "/borrarFiltrosAddMaterial", method = RequestMethod.POST)
	public String borrarFiltrosAddMaterial(AddMaterialJSP addMaterialJSP, Model model) {

		addMaterialJSP.setIdTipoMaterial(null);
		addMaterialJSP.setSerie(null);
		addMaterialJSP.setSubserie(null);

		model.addAttribute("addMaterial", addMaterialJSP);
		cargarLista(new AddMaterialJSP(), model, 0, 5);
		return "addMaterial";
	}

	@RequestMapping(value = "/addModeloMaterialToMaterial", method = RequestMethod.POST)
	public String addModeloMaterialToMaterial(AddMaterialJSP addMaterialJSP, Model model) {

		ModeloMaterial modeloMaterial = modeloMaterialDAO
				.getOne(Integer.parseInt(addMaterialJSP.getIdModeloMaterial()));

		addMaterialJSP.setIdModeloMaterialGuardar(modeloMaterial.getIdModeloMaterial().toString());
		addMaterialJSP.setModelo(modeloMaterial.getSerie() + "-" + modeloMaterial.getSubserie());

		if (StringUtils.isBlank(addMaterialJSP.getIdMaterial())) {
			addMaterialJSP.setIdMaterial(null);
		}
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(addMaterialJSP.getPage())) {
			page = Integer.parseInt(addMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("addMaterial", addMaterialJSP);
		cargarLista(addMaterialJSP, model, page, size);
		return "addMaterial";
	}

	@RequestMapping(value = "/crearMaterial", method = RequestMethod.POST)
	public String crearMaterial(AddMaterialJSP addMaterialJSP, Model model) {

		Material material = addMaterialService.crearMaterial(addMaterialJSP);
		addMaterialJSP.setIdMaterial(material.getIdMaterial().toString());

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(addMaterialJSP.getPage())) {
			page = Integer.parseInt(addMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("addMaterial", addMaterialJSP);
		model.addAttribute("info", " Se ha creado correctamente");
		cargarLista(addMaterialJSP, model, page, size);
		return "addMaterial";
	}

	@RequestMapping(value = "/guardarMaterial", method = RequestMethod.POST)
	public String guardarMaterial(AddMaterialJSP addMaterialJSP, Model model) {

		addMaterialService.guardarMaterial(addMaterialJSP);

		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(addMaterialJSP.getPage())) {
			page = Integer.parseInt(addMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("addMaterial", addMaterialJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		cargarLista(addMaterialJSP, model, page, size);
		return "addMaterial";
	}

	@RequestMapping(value = "/paginacionTablaAddMaterial", method = RequestMethod.POST)
	public String paginacionTablaAddMaterial(AddMaterialJSP addMaterialJSP, Model model, HttpSession session) {
		int page = 0; // default page number is 0 (yes it is weird)
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(addMaterialJSP.getPage())) {
			addMaterialJSP.setPage(addMaterialJSP.getPage().substring(addMaterialJSP.getPage().indexOf(",") + 1,
					addMaterialJSP.getPage().length()));
			page = Integer.parseInt(addMaterialJSP.getPage()) - 1;
		}

		model.addAttribute("addMaterial", addMaterialJSP);
		cargarLista(addMaterialJSP, model, page, size);
		return "addMaterial";
	}

	private void cargarLista(AddMaterialJSP addMaterialJSP, Model model, int page, int size) {

		model.addAttribute("modelosMaterial", modeloMaterialDAO.findByFilters(addMaterialJSP.getIdTipoMaterial(),
				addMaterialJSP.getSerie(), addMaterialJSP.getSubserie(), PageRequest.of(page, size)));
	}

	private AddMaterialJSP rellenarJSP(BuscadorMaterialJSP buscadorMaterialJSP, boolean lectura) {

		Material material = materialDAO.getOne(Integer.parseInt(buscadorMaterialJSP.getIdMaterial()));

		AddMaterialJSP addMaterialJSP = new AddMaterialJSP();
		addMaterialJSP.setIdMaterial(material.getIdMaterial().toString());
		addMaterialJSP.setLectura(lectura);
		addMaterialJSP.setIdModeloMaterialGuardar(material.getModeloMaterial().getIdModeloMaterial().toString());
		addMaterialJSP
				.setModelo(material.getModeloMaterial().getSerie() + "-" + material.getModeloMaterial().getSubserie());
		addMaterialJSP.setNve(material.getNve());

		return addMaterialJSP;
	}
}