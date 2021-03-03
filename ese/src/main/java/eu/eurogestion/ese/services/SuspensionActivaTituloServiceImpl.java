package eu.eurogestion.ese.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Suspension;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.SuspensionActivaTituloJSP;
import eu.eurogestion.ese.repository.CausaSuspensionDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoTituloDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.SuspensionDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class SuspensionActivaTituloServiceImpl implements SuspensionActivaTituloService {

	/** Repositories & Services **/

	@Autowired
	public SuspensionDAO suspensionDAO;

	@Autowired
	public CausaSuspensionDAO causaSuspensionDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EstadoTituloDAO estadoTituloDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public TituloDAO tituloDAO;

	/** Functions **/

	@Override
	public void recuperarTitulo(SuspensionActivaTituloJSP suspensionActivaTituloJSP) throws EseException {

		Suspension suspension = suspensionDAO.getOne(Integer.parseInt(suspensionActivaTituloJSP.getIdSuspension()));
		Titulo titulo = tituloDAO.getOne(Integer.parseInt(suspensionActivaTituloJSP.getIdTitulo()));
		titulo.setEstadoTitulo(estadoTituloDAO.getOne(Constantes.ESTADO_TITULO_CONCEDIDO));
		suspension.setCausaResolucion(suspensionActivaTituloJSP.getCausaResolucion());
		suspension.setFechaResolucion(Utiles.parseDatePantalla(suspensionActivaTituloJSP.getFechaResolucion()));
		if (StringUtils.isNotBlank(suspensionActivaTituloJSP.getObservaciones())) {
			suspension.setObservaciones(suspensionActivaTituloJSP.getObservaciones());
		}
		tituloDAO.save(titulo);
		suspensionDAO.save(suspension);
	}

	@Override
	public void caducarTitulo(SuspensionActivaTituloJSP suspensionActivaTituloJSP) throws EseException {

		Suspension suspension = suspensionDAO.getOne(Integer.parseInt(suspensionActivaTituloJSP.getIdSuspension()));
		Titulo titulo = tituloDAO.getOne(Integer.parseInt(suspensionActivaTituloJSP.getIdTitulo()));
		titulo.setEstadoTitulo(estadoTituloDAO.getOne(Constantes.ESTADO_TITULO_CADUCADO));
		suspension.setCausaResolucion(suspensionActivaTituloJSP.getCausaResolucion());
		suspension.setFechaResolucion(Utiles.parseDatePantalla(suspensionActivaTituloJSP.getFechaResolucion()));
		if (StringUtils.isNotBlank(suspensionActivaTituloJSP.getObservaciones())) {
			suspension.setObservaciones(suspensionActivaTituloJSP.getObservaciones());
		}
		tituloDAO.save(titulo);
		suspensionDAO.save(suspension);
	}
}
