package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.ModeloMaterial;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AddModeloMaterialJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface AddModeloMaterialService {

	public ModeloMaterial crearModeloMaterial(AddModeloMaterialJSP addModeloMaterialJSP);

	public void guardarModeloMaterial(AddModeloMaterialJSP addModeloMaterialJSP);

}
