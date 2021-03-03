package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Material;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AddMaterialJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface AddMaterialService {

	public Material crearMaterial(AddMaterialJSP addMaterialJSP);

	public void guardarMaterial(AddMaterialJSP addMaterialJSP);

}
