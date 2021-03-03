package eu.eurogestion.ese.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.ModeloMaterial;
import eu.eurogestion.ese.domain.TipoMaterial;
import eu.eurogestion.ese.pojo.AddModeloMaterialJSP;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;
import eu.eurogestion.ese.repository.TipoMaterialDAO;

@Service
public class AddModeloMaterialServiceImpl implements AddModeloMaterialService {

	/** Repositories & Services **/

	@Autowired
	public ModeloMaterialDAO modeloMaterialDAO;

	@Autowired
	public TipoMaterialDAO tipoMaterialDAO;

	/** Functions **/

	@Override
	public ModeloMaterial crearModeloMaterial(AddModeloMaterialJSP addModeloMaterialJSP) {

		ModeloMaterial modeloMaterial = new ModeloMaterial();
		modeloMaterial.setSerie(addModeloMaterialJSP.getSerie());
		modeloMaterial.setSubserie(addModeloMaterialJSP.getSubserie());
		modeloMaterial.setNotas(addModeloMaterialJSP.getNotas());

		TipoMaterial tipoMaterial = tipoMaterialDAO.getOne(Integer.parseInt(addModeloMaterialJSP.getIdTipoMaterial()));

		modeloMaterial.setTipoMaterial(tipoMaterial);

		ModeloMaterial out = modeloMaterialDAO.save(modeloMaterial);

		return out;
	}

	@Override
	public void guardarModeloMaterial(AddModeloMaterialJSP addModeloMaterialJSP) {

		ModeloMaterial modeloMaterial = modeloMaterialDAO
				.getOne(Integer.parseInt(addModeloMaterialJSP.getIdModeloMaterial()));

		modeloMaterial.setSerie(addModeloMaterialJSP.getSerie());
		modeloMaterial.setSubserie(addModeloMaterialJSP.getSubserie());
		modeloMaterial.setNotas(addModeloMaterialJSP.getNotas());

		TipoMaterial tipoMaterial = tipoMaterialDAO.getOne(Integer.parseInt(addModeloMaterialJSP.getIdTipoMaterial()));

		modeloMaterial.setTipoMaterial(tipoMaterial);

		modeloMaterialDAO.save(modeloMaterial);
	}

}
