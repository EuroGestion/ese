package eu.eurogestion.ese.services;

import java.util.Date;

import eu.eurogestion.ese.domain.EseToken;

public interface DIDEseService {

	/**
	 * Creacion del DIDEse de un usuario
	 * 
	 * @param usuarioNombreCompleto Nombre completo del usuario
	 * 
	 * @return DIDEse del usuario
	 */
	String crearDIDEse(String usuarioNombreCompleto);

	/**
	 * Creacion del EseToken de una transaccion
	 * 
	 * @param usuarioDIDEse   DIDEse del usuario firmante
	 * @param documentoNombre Id del documentoto firmado
	 * @param firmaFecha      Fecha de la firma
	 * 
	 * @return EseToken de la transaccion
	 */
	EseToken crearEseToken(String usuarioDIDEse, String documentoId, Date firmaFecha);

	/**
	 * Encriptacion del EseToken como cadena de texto
	 * 
	 * @param eseToken EseToken de una transaccion
	 * @return EseToken encriptado
	 */
	String encriptacionEseTokenATexto(EseToken eseToken);
}
