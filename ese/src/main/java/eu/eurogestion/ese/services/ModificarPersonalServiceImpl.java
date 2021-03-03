package eu.eurogestion.ese.services;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoTituloDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ModificarPersonalServiceImpl implements ModificarPersonalService {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public TituloDAO tituloDAO;

	@Autowired
	public EstadoTituloDAO estadoTituloDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void bajaLogicaPersonal(String idPersonal) throws EseException {

		Personal personal;
		try {
			personal = personalDAO.getOne(Integer.parseInt(idPersonal));
			personal.setFechaBaja(new Date());
			personalDAO.save(personal);

		} catch (RuntimeException e) {
			throw new EseException(e.getMessage());
		}
	}

	@Override
	public void caducarTitulo(DetallePersonalJSP detallePersonalJSP) throws EseException {

		Titulo titulo = tituloDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdTitulo()));
		titulo.setEstadoTitulo(estadoTituloDAO.getOne(Constantes.ESTADO_TITULO_CADUCADO));

		tituloDAO.save(titulo);
	}

	@Override
	public void anadirLicenciaConduccionPersonal(DetallePersonalJSP detallePersonalJSP) throws EseException {

		byte[] fichero;
		try {
			fichero = detallePersonalJSP.getLicenciaConduccion().getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_LICENCIA_CONDUCCION);
		String tipoFichero = detallePersonalJSP.getLicenciaConduccion().getContentType();

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), tipoFichero);

		Personal personal = personalDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdPersonal()));

		personal.setEvidencia81(evidencia);

		personalDAO.save(personal);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}
}
