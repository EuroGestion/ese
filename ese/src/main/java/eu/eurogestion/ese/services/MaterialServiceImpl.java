package eu.eurogestion.ese.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Material;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.repository.MaterialDAO;

@Service
public class MaterialServiceImpl implements MaterialService {

	/** Repositories & Services **/

	@Autowired
	public MaterialDAO materialDAO;

	/** Functions **/

	@Override
	public void bajaMaterial(Integer idMaterial) throws EseException {
		Material material = materialDAO.getOne(idMaterial);
		material.setFechaBaja(new Date());
		materialDAO.save(material);
	}

}
