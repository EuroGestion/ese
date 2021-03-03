package eu.eurogestion.ese.services;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface ModificarPersonalService {

	/**
	 * Metodo que hace un delete logico de personal modificando la fecha baja de un
	 * personal
	 * 
	 * @param idPersonal el id del personal que se va a dar de baja
	 * 
	 * @throws EseException
	 */
	void bajaLogicaPersonal(String idPersonal) throws EseException;

	void caducarTitulo(DetallePersonalJSP detallePersonalJSP) throws EseException;

	void anadirLicenciaConduccionPersonal(DetallePersonalJSP detallePersonalJSP) throws EseException;

}
