package eu.eurogestion.ese.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Composicion;
import eu.eurogestion.ese.domain.MaterialComposicion;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.MaterialComposicionDAO;

@Service
public class ComposicionServiceImpl implements ComposicionService {

	/** Repositories & Services **/

	@Autowired
	public ComposicionDAO composicionDAO;

	@Autowired
	public MaterialComposicionDAO materialComposicionDAO;

	/** Functions **/

	@Override
	public void eliminarComposicion(Integer idComposicion) throws EseException {
		Composicion composicion = composicionDAO.getOne(idComposicion);
		for (MaterialComposicion materialComposicion : composicion.getListMaterialComposicion()) {
			materialComposicionDAO.delete(materialComposicion);
		}

		composicionDAO.delete(composicion);
	}
}
