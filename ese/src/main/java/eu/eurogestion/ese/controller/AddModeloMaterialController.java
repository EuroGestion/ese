package eu.eurogestion.ese.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.ModeloMaterial;
import eu.eurogestion.ese.domain.TipoMaterial;
import eu.eurogestion.ese.pojo.AddModeloMaterialJSP;
import eu.eurogestion.ese.pojo.BuscadorModeloMaterialJSP;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;
import eu.eurogestion.ese.repository.TipoMaterialDAO;
import eu.eurogestion.ese.services.AddModeloMaterialService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class AddModeloMaterialController {

	/**
	 * Repositorio de la clase de dominio TipoCompania
	 */
	@Autowired
	public AddModeloMaterialService addModeloMaterialService;

	@Autowired
	public TipoMaterialDAO tipoMaterialDAO;

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

	@RequestMapping(value = "/verDetalleModeloMaterial", method = RequestMethod.POST)
	public String verDetalleModeloMaterial(BuscadorModeloMaterialJSP buscadorModeloMaterialJSP, Model model) {

		AddModeloMaterialJSP addModeloMaterialJSP = rellenarJSP(buscadorModeloMaterialJSP, true);

		model.addAttribute("addModeloMaterial", addModeloMaterialJSP);
		return "addModeloMaterial";
	}

	@RequestMapping(value = "/modificarDetalleModeloMaterial", method = RequestMethod.POST)
	public String modificarDetalleModeloMaterial(BuscadorModeloMaterialJSP buscadorModeloMaterialJSP, Model model) {

		AddModeloMaterialJSP addModeloMaterialJSP = rellenarJSP(buscadorModeloMaterialJSP, false);

		model.addAttribute("addModeloMaterial", addModeloMaterialJSP);
		return "addModeloMaterial";
	}

	@RequestMapping(value = "/nuevoModeloMaterial", method = RequestMethod.GET)
	public String nuevoModeloMaterial(Model model) {

		AddModeloMaterialJSP addModeloMaterialJSP = new AddModeloMaterialJSP();

		model.addAttribute("addModeloMaterial", addModeloMaterialJSP);
		return "addModeloMaterial";

	}

	@RequestMapping(value = "/crearModeloMaterial", method = RequestMethod.POST)
	public String crearModeloMaterial(AddModeloMaterialJSP addModeloMaterialJSP, Model model) {

		ModeloMaterial modeloMaterial = addModeloMaterialService.crearModeloMaterial(addModeloMaterialJSP);
		addModeloMaterialJSP.setIdModeloMaterial(modeloMaterial.getIdModeloMaterial().toString());

		model.addAttribute("addModeloMaterial", addModeloMaterialJSP);
		model.addAttribute("info", " Se ha creado correctamente");

		return "addModeloMaterial";
	}

	@RequestMapping(value = "/guardarModeloMaterial", method = RequestMethod.POST)
	public String guardarModeloMaterial(AddModeloMaterialJSP addModeloMaterialJSP, Model model) {

		addModeloMaterialService.guardarModeloMaterial(addModeloMaterialJSP);

		model.addAttribute("addModeloMaterial", addModeloMaterialJSP);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "addModeloMaterial";

	}

	private AddModeloMaterialJSP rellenarJSP(BuscadorModeloMaterialJSP buscadorModeloMaterialJSP, boolean lectura) {

		ModeloMaterial modeloMaterial = modeloMaterialDAO
				.getOne(Integer.parseInt(buscadorModeloMaterialJSP.getIdModeloMaterial()));

		AddModeloMaterialJSP addModeloMaterialJSP = new AddModeloMaterialJSP();
		addModeloMaterialJSP.setIdModeloMaterial(buscadorModeloMaterialJSP.getIdModeloMaterial());
		addModeloMaterialJSP.setLectura(lectura);
		addModeloMaterialJSP.setSerie(modeloMaterial.getSerie());
		addModeloMaterialJSP.setSubserie(modeloMaterial.getSubserie());
		addModeloMaterialJSP.setNotas(modeloMaterial.getNotas());
		addModeloMaterialJSP.setIdTipoMaterial(modeloMaterial.getTipoMaterial().getIdTipoMaterial().toString());

		return addModeloMaterialJSP;
	}

}