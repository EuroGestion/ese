package eu.eurogestion.ese.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Material;
import eu.eurogestion.ese.domain.ModeloMaterial;
import eu.eurogestion.ese.pojo.AddMaterialJSP;
import eu.eurogestion.ese.repository.MaterialDAO;
import eu.eurogestion.ese.repository.ModeloMaterialDAO;

@Service
public class AddMaterialServiceImpl implements AddMaterialService {

	/** Repositories & Services **/

	@Autowired
	public ModeloMaterialDAO modeloMaterialDAO;

	@Autowired
	public MaterialDAO materialDAO;

	/** Functions **/

	@Override
	public Material crearMaterial(AddMaterialJSP addMaterialJSP) {

		Material material = new Material();

		ModeloMaterial modeloMaterial = modeloMaterialDAO
				.getOne(Integer.parseInt(addMaterialJSP.getIdModeloMaterialGuardar()));

		material.setModeloMaterial(modeloMaterial);
		material.setNve(addMaterialJSP.getNve());

		Material out = materialDAO.save(material);
		return out;
	}

	@Override
	public void guardarMaterial(AddMaterialJSP addMaterialJSP) {

		Material material = materialDAO.getOne(Integer.parseInt(addMaterialJSP.getIdMaterial()));

		ModeloMaterial modeloMaterial = modeloMaterialDAO
				.getOne(Integer.parseInt(addMaterialJSP.getIdModeloMaterialGuardar()));

		material.setModeloMaterial(modeloMaterial);
		material.setNve(addMaterialJSP.getNve());

		materialDAO.save(material);
	}

}
