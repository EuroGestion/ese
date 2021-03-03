package eu.eurogestion.ese.services;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.EseToken;

@Service
public class DIDEseServiceImpl implements DIDEseService {

	/** Repositories & Services **/

	/** Functions **/

	@Override
	public String crearDIDEse(String usuarioNombreCompleto) {
		String usuarioNombreCompletoSinEspacios = usuarioNombreCompleto.trim();
		String clave;
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA", "SUN");
			keyPairGenerator.initialize(8, secureRandom);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			clave = keyPair.getPublic().toString();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			clave = e.getMessage().trim();
		}

		return "did:ese" + ":" + usuarioNombreCompletoSinEspacios + ":" + clave;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.DIDEseService#
	 *      crearEseToken(java.lang.String, java.lang.String)
	 */
	@Override
	public EseToken crearEseToken(String usuarioDIDEse, String documentoId, Date firmaFecha) {

		EseToken eseToken = new EseToken();
		eseToken.setUsuarioDID(usuarioDIDEse);
		eseToken.setDocumentoId(documentoId);
		eseToken.setFirmaFecha(firmaFecha);

		return eseToken;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.DIDEseService#
	 *      encriptacionEseTokenATexto(eu.eurogestion.ese.domain.EseToken)
	 */
	@Override
	public String encriptacionEseTokenATexto(EseToken eseToken) {
		try {
			String usuarioDID = eseToken.getUsuarioDID();
			String clave = usuarioDID.substring(usuarioDID.lastIndexOf(":") + 1);
			byte[] claveEncriptacionBytes = clave.getBytes();

			SecretKey secretKey = new SecretKeySpec(claveEncriptacionBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] eseTokenEncriptadoBytes = cipher.doFinal(eseToken.toString().getBytes());

			return new String(eseTokenEncriptadoBytes);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			return e.getMessage().trim();
		}
	}
}
