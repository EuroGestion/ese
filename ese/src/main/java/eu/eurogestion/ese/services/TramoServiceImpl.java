package eu.eurogestion.ese.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Tramo;
import eu.eurogestion.ese.pojo.DetalleTramoJSP;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TramoDAO;

@Service
public class TramoServiceImpl implements TramoService {

	/** Repositories & Services **/

	@Autowired
	public TramoDAO tramoDAO;
	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	/** Functions **/

	@Override
	public void guardarTramo(DetalleTramoJSP detalleTramoJSP) {

		Tramo tramo;
		if (StringUtils.isNotBlank(detalleTramoJSP.getIdTramo())) {
			tramo = tramoDAO.getOne(Integer.parseInt(detalleTramoJSP.getIdTramo()));
		} else {
			tramo = new Tramo();
		}
		tramo.setNombre(detalleTramoJSP.getNombre());
		tramo.setPuntoOrigen(puntoInfraestructuraDAO.getOne(Integer.parseInt(detalleTramoJSP.getIdOrigen())));
		tramo.setPuntoDestino(puntoInfraestructuraDAO.getOne(Integer.parseInt(detalleTramoJSP.getIdDestino())));
		tramo.setEsEspecial(Boolean.FALSE);
		tramo = tramoDAO.save(tramo);

		detalleTramoJSP.setIdTramo(tramo.getIdTramo().toString());
	}

}
