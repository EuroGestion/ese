package eu.eurogestion.ese.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.AnexoAnomalia;
import eu.eurogestion.ese.domain.Anomalia;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AddAnomaliaJSP;
import eu.eurogestion.ese.repository.AnexoAnomaliaDAO;
import eu.eurogestion.ese.repository.AnomaliaDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoAnomaliaDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.InformeAnomaliasDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoAnomaliaDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class AddAnomaliaServiceImpl implements AddAnomaliaService {

	/** Repositories & Services **/

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public AnomaliaDAO anomaliaDAO;

	@Autowired
	public TipoAnomaliaDAO tipoAnomaliaDAO;

	@Autowired
	public AnexoAnomaliaDAO anexoAnomaliaDAO;

	@Autowired
	public EstadoAnomaliaDAO estadoAnomaliaDAO;

	@Autowired
	public InformeAnomaliasDAO informeAnomaliasDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	public void creacionAnexo(AddAnomaliaJSP addAnomaliaJSP, HttpSession session) throws EseException {

		byte[] fichero;
		try {
			fichero = addAnomaliaJSP.getEvidencia().getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_ANEXO);
		String tipoFichero = addAnomaliaJSP.getEvidencia().getContentType();

		Evidencia evidencia = utilesPDFService.crearEvidencia(addAnomaliaJSP.getDescripcionEvidencia(), fichero, md5,
				tipoEvidencia, Utiles.sysdate(), tipoFichero);

		Anomalia anomalia = anomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnomalia()));

		AnexoAnomalia anexoAnomalia = new AnexoAnomalia();
		anexoAnomalia.setAnomalia(anomalia);
		anexoAnomalia.setDescripcion(addAnomaliaJSP.getDescripcionEvidencia());
		anexoAnomalia.setEvidencia(evidencia);

		anexoAnomalia = anexoAnomaliaDAO.save(anexoAnomalia);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	public Anomalia guardarAnomalia(AddAnomaliaJSP addAnomaliaJSP) throws EseException {

		Anomalia anomalia;
		if (StringUtils.isNotBlank(addAnomaliaJSP.getIdAnomalia())) {
			anomalia = anomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdAnomalia()));
		} else {
			anomalia = new Anomalia();
			anomalia.setFechaApertura(new Date());
		}

		anomalia.setTipoAnomalia(tipoAnomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdTipoAnomalia())));
		anomalia.setEstadoAnomalia(estadoAnomaliaDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdEstado())));
		anomalia.setDescripcionSituacion(addAnomaliaJSP.getDescripcionAnomalia());
		anomalia.setMedidasAdoptadas(addAnomaliaJSP.getMedidasAdoptadas());
		anomalia.setDatosTecnicos(addAnomaliaJSP.getDatosTecnicos());
		anomalia.setLimitacionesExplotacion(addAnomaliaJSP.getLimitaciones());
		anomalia.setControlSeguimiento(addAnomaliaJSP.getControlSeguimiento());
		anomalia.setResponsableResolucion(personalDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdResponsable())));
		anomalia.setInformeAnomalias(
				informeAnomaliasDAO.getOne(Integer.parseInt(addAnomaliaJSP.getIdInformeAnomalia())));

		Anomalia anomaliaBBDD;
		try {
			anomaliaBBDD = anomaliaDAO.save(anomalia);
		} catch (Exception e) {
			throw new EseException(e.getMessage());
		}

		return anomaliaBBDD;
	}
}
