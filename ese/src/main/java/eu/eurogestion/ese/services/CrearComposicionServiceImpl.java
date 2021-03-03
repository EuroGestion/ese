package eu.eurogestion.ese.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Composicion;
import eu.eurogestion.ese.domain.Material;
import eu.eurogestion.ese.domain.MaterialComposicion;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CrearComposicionJSP;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.MaterialComposicionDAO;
import eu.eurogestion.ese.repository.MaterialDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class CrearComposicionServiceImpl implements CrearComposicionService {

	/** Repositories & Services **/

	@Autowired
	public TrenDAO trenDAO;

	@Autowired
	public ComposicionDAO composicionDAO;

	@Autowired
	public MaterialDAO materialDAO;

	@Autowired
	public MaterialComposicionDAO materialComposicionDAO;

	/** Functions **/

	@Override
	public List<Integer> generarComposiciones(CrearComposicionJSP crearComposicionJSP) throws EseException {

		List<Integer> listIdsComposicion = new ArrayList<>();

		GregorianCalendar fechaInicio = new GregorianCalendar();
		fechaInicio.setTime(Utiles.parseDatePantalla(crearComposicionJSP.getFechaInicio()));
		GregorianCalendar fechaFin = new GregorianCalendar();
		fechaFin.setTime(Utiles.parseDatePantalla(crearComposicionJSP.getFechaFin()));
		fechaFin.add(Calendar.DAY_OF_WEEK, 1);

		while (fechaFin.after(fechaInicio)) {
			if (crearComposicionJSP.getListaDias().contains(String.valueOf(fechaInicio.get(Calendar.DAY_OF_WEEK)))) {
				Composicion composicion = new Composicion();
				composicion.setTren(trenDAO.getOne(Integer.parseInt(crearComposicionJSP.getIdTren())));
				composicion.setFecha(fechaInicio.getTime());

				Composicion composicionBBDD = composicionDAO.save(composicion);
				listIdsComposicion.add(composicionBBDD.getIdComposicion());
			}
			fechaInicio.add(Calendar.DAY_OF_WEEK, 1);

		}

		return listIdsComposicion;
	}

	@Override
	public void addMaterialComposicion(List<Integer> listaIdsComposicion, Integer idMaterial) throws EseException {

		Material material = materialDAO.getOne(idMaterial);
		for (Integer id : listaIdsComposicion) {
			MaterialComposicion materialComposicion = new MaterialComposicion();
			materialComposicion.setComposicion(composicionDAO.getOne(id));
			materialComposicion.setMaterial(material);
			materialComposicionDAO.save(materialComposicion);
		}

	}

	@Override
	public void eliminarMaterialComposicion(List<Integer> listaIdsComposicion, Integer idMaterial) throws EseException {

		List<MaterialComposicion> listaMaterialComposicion = materialComposicionDAO
				.findByIdsComposicionAndIdMaterial(listaIdsComposicion, idMaterial);

		for (MaterialComposicion materialComposicion : listaMaterialComposicion) {

			materialComposicionDAO.delete(materialComposicion);

		}

	}

}
