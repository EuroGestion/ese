package eu.eurogestion.ese.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CambiarPasswordJSP;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class CambiarPasswordServiceImpl implements CambiarPasswordService {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	/** Functions **/

	@Override
	public void confirmacionCambiarPassword(CambiarPasswordJSP cambiarPasswordJSP) throws EseException {

		Personal personal = personalDAO.getOne(Integer.parseInt(cambiarPasswordJSP.getIdPersonal()));

		personal.setClave(Utiles.cifrarPassword(cambiarPasswordJSP.getPasswordNueva()));
		personalDAO.save(personal);
	}

	@Override
	public boolean mismaPasswordPropia(CambiarPasswordJSP cambiarPasswordJSP) {
		Personal personal = personalDAO.getOne(Integer.parseInt(cambiarPasswordJSP.getIdPersonal()));

		String passwordAntigua = Utiles.cifrarPassword(cambiarPasswordJSP.getPasswordAntigua());

		if (passwordAntigua.equals(personal.getClave())) {
			return true;
		}
		return false;
	}
}
