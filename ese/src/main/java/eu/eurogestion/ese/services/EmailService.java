package eu.eurogestion.ese.services;

import javax.mail.MessagingException;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface EmailService {

	void sendEmail(String email, String subject, String text);
	void sendEmailConArchivos(String email, String subject, String text, byte[] archivo, String nombreArchivo,
			String tipoArchivo) throws MessagingException;

}
