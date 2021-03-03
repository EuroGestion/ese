package eu.eurogestion.ese.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.utils.Constantes;

@Service
public class EmailServiceImpl implements EmailService {

	/** Repositories & Services **/

	@Autowired
	private JavaMailSender javaMailSender;

	/** Functions **/

	public void sendEmail(String email, String subject, String text) {

		SimpleMailMessage msg = new SimpleMailMessage();

		msg.setTo(email);
		msg.setFrom(Constantes.EMAIL_ENVIO_CORREOS);
		msg.setSubject(subject);
		msg.setText(text);

		javaMailSender.send(msg);
	}

	public void sendEmailConArchivos(String email, String subject, String text, byte[] archivo, String nombreArchivo,
			String tipoArchivo) throws MessagingException {

		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(Constantes.EMAIL_ENVIO_CORREOS);
		helper.setTo(email);
		helper.setText(text);
		helper.setSubject(subject);

		helper.addAttachment(nombreArchivo, new ByteArrayDataSource(archivo, tipoArchivo));

		javaMailSender.send(message);
	}

}
