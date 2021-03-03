package eu.eurogestion.ese.services;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.domain.AnexoAccidente;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.MedidaAccidente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AnexosInvestigacionJSP;
import eu.eurogestion.ese.repository.AccidenteDAO;
import eu.eurogestion.ese.repository.AnexoAccidenteDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.MedidaAccidenteDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class AnexosInvestigacionServiceImpl implements AnexosInvestigacionService {

	/** Repositories & Services **/

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public AccidenteDAO accidenteDAO;

	@Autowired
	public MedidaAccidenteDAO medidaAccidenteDAO;

	@Autowired
	public AnexoAccidenteDAO anexoAccidenteDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void guardarEvidenciaMedidasAdoptadas(AnexosInvestigacionJSP anexosInvestigacion, HttpSession sesion)
			throws EseException {

		Accidente accidente = accidenteDAO.getOne(anexosInvestigacion.getIdInvestigacion());

		Evidencia evidencia = crearEvidencia(anexosInvestigacion.getEvidenciaMedidasAdoptadas(),
				anexosInvestigacion.getDescripcionEvidenciaMedidasAdoptadas(),
				Constantes.TIPO_EVIDENCIA_SF_REGISTRO_MEDIDAS_ADOPTADAS, accidente.getIdAccidente().toString());

		MedidaAccidente medidaAccidente = new MedidaAccidente();
		medidaAccidente.setAccidente(accidente);
		medidaAccidente.setDescripcion(anexosInvestigacion.getDescripcionEvidenciaMedidasAdoptadas());
		medidaAccidente.setEvidencia(evidencia);

		medidaAccidenteDAO.save(medidaAccidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	@Override
	public void guardarEvidenciaDocumentosAnexos(AnexosInvestigacionJSP anexosInvestigacion, HttpSession sesion)
			throws EseException {

		Accidente accidente = accidenteDAO.getOne(anexosInvestigacion.getIdInvestigacion());

		Evidencia evidencia = crearEvidencia(anexosInvestigacion.getEvidenciaDocAnexo(),
				anexosInvestigacion.getDescripcionEvidenciaDocAnexo(),
				Constantes.TIPO_EVIDENCIA_SF_REGISTRO_DOCUMENTOS_ANEXOS, accidente.getIdAccidente().toString());

		AnexoAccidente anexoAccidente = new AnexoAccidente();
		anexoAccidente.setAccidente(accidente);
		anexoAccidente.setDescripcion(anexosInvestigacion.getDescripcionEvidenciaDocAnexo());
		anexoAccidente.setEvidencia(evidencia);

		anexoAccidenteDAO.save(anexoAccidente);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private Evidencia crearEvidencia(MultipartFile ficheroPantalla, String descripcionFichero, Integer tipoEvidenciaId,
			String idAccidente) throws EseException {

		byte[] fichero;
		try {
			fichero = ficheroPantalla.getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(tipoEvidenciaId);
		String tipoFichero = ficheroPantalla.getContentType();

		Evidencia evidencia = utilesPDFService.crearEvidencia(descripcionFichero, fichero, md5, tipoEvidencia,
				Utiles.sysdate(), tipoFichero);

		return evidencia;
	}
}
