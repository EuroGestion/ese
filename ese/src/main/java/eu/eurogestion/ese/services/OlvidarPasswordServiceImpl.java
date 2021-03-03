package eu.eurogestion.ese.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.OlvidarPasswordJSP;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class OlvidarPasswordServiceImpl implements OlvidarPasswordService {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public EmailService emailService;

	/** Functions **/

	@Override
	public void confirmacionOlvidarPassword(OlvidarPasswordJSP olvidarPasswordJSP) throws EseException {

		String password = Utiles.crearPassword();
		Personal personal = personalDAO.findByEmailAndFechaBajaIsNull(olvidarPasswordJSP.getEmail());
		personal.setClave(Utiles.cifrarPassword(password));
		personalDAO.save(personal);

		String email = personal.getEmail();
		String titulo = "Contraseña";
		String mensaje = "Se ha restablecido la contraseña en  su sistema SaRa.\nLa nueva contraseña es: " + password
				+ "\nPara su seguridad cambie la contraseña cuando acceda a la aplicación.";

		emailService.sendEmail(email, titulo, mensaje);

	}

	@Override
	public boolean emailValido(OlvidarPasswordJSP olvidarPasswordJSP) {

		Personal personal = personalDAO.findByEmailAndFechaBajaIsNull(olvidarPasswordJSP.getEmail());

		if (personal != null) {
			return true;
		}
		return false;
	}

}
