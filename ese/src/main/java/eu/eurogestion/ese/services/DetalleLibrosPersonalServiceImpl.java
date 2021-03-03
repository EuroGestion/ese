package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.LibroPersonal;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleLibrosPersonalJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.LibroPersonalDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class DetalleLibrosPersonalServiceImpl implements DetalleLibrosPersonalService {

	/** Repositories & Services **/

	@Autowired
	public LibroPersonalDAO libroPersonalDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public TareaPendienteDAO tareaPendienteDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void firmarLibro(DetalleLibrosPersonalJSP detalleLibrosPersonalJSP) throws EseException {

		LibroPersonal libroPersonal = libroPersonalDAO
				.getOne(Integer.parseInt(detalleLibrosPersonalJSP.getIdLibroPersonal()));

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoAcuRecDocSer(libroPersonal);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_PYC_DOCUMENTACION_RECIBIDA);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), Constantes.TYPE_PDF);

		libroPersonal.setEvidencia(evidencia);

		libroPersonalDAO.save(libroPersonal);

		// Comprobar si el personal tiene alguna tarea pendiente sin completar
		Long count = libroPersonalDAO.findWithoutEvidence(detalleLibrosPersonalJSP.getIdPersonal());
		if (count == 0) {
			TareaPendiente tareaPendiente = tareaPendienteDAO.findTareaPendienteByIdDestinatarioAndTipoTarea(
					Integer.parseInt(detalleLibrosPersonalJSP.getIdPersonal()),
					Constantes.TIPO_TAREA_RECEPCION_DOCUMENTACION);

			tareaPendiente.setLeido(true);

			tareaPendienteDAO.save(tareaPendiente);
		}

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}
}
