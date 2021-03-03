package eu.eurogestion.ese.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CrearComposicionJSP;

@Transactional(rollbackFor = { EseException.class, RuntimeException.class })
public interface CrearComposicionService {

	List<Integer> generarComposiciones(CrearComposicionJSP crearComposicionJSP) throws EseException;

	void addMaterialComposicion(List<Integer> listaIdsComposicion, Integer idMaterial) throws EseException;

	void eliminarMaterialComposicion(List<Integer> listaIdsComposicion, Integer idMaterial) throws EseException;

}
