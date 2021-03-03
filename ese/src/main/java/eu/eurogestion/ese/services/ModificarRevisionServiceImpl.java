package eu.eurogestion.ese.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.RevisionPsico;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ModificarRevisionJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoRevisionDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ModificarRevisionServiceImpl implements ModificarRevisionService {

	/** Repositories & Services **/

	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	@Autowired
	public EstadoRevisionDAO estadoRevisionDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void modificarRevision(ModificarRevisionJSP modificarRevisionJSP, HttpSession sesion) throws EseException {

		byte[] fichero;
		try {
			fichero = modificarRevisionJSP.getEvidencia().getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_RESULTADO_PRUEBA_PSICOFISICA);
		String tipoFichero = modificarRevisionJSP.getEvidencia().getContentType();
		Date fechaDocumento = Utiles.parseDatePantalla(modificarRevisionJSP.getFechaDocumento());

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				fechaDocumento, tipoFichero);

		RevisionPsico revision = revisionPsicoDAO.getOne(Integer.parseInt(modificarRevisionJSP.getIdRevision()));
		revision.setCausa(modificarRevisionJSP.getCausa());
		revision.setFechaRealizacion(Utiles.parseDatePantalla(modificarRevisionJSP.getFechaRevision()));
		revision.setEvidencia(evidencia);
		if (StringUtils.isNotBlank(modificarRevisionJSP.getObservaciones())) {
			revision.setObservaciones(modificarRevisionJSP.getObservaciones());
		}

		if (Constantes.ESTADO_REVISION_APTO.toString().equals(modificarRevisionJSP.getResolucion())) {
			revision.setValidoDesde(Utiles.parseDatePantalla(modificarRevisionJSP.getValidoDesde()));
			revision.setFechaCaducidad(Utiles.parseDatePantalla(modificarRevisionJSP.getFechaCaducidad()));
			revision.setEstadoRevision(estadoRevisionDAO.getOne(Constantes.ESTADO_REVISION_APTO));
		} else {
			revision.setEstadoRevision(estadoRevisionDAO.getOne(Constantes.ESTADO_REVISION_NO_APTO));
		}

		revisionPsicoDAO.save(revision);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}
}
