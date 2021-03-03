package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.CausaSuspension;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.Suspension;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.SuspenderTituloJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
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
public class SuspenderTituloServiceImpl implements SuspenderTituloService {

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

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void suspenderTitulo(SuspenderTituloJSP suspenderTituloJSP, HttpSession sesion) throws EseException {

		Titulo titulo = tituloDAO.getOne(Integer.parseInt(suspenderTituloJSP.getIdTitulo()));
		CausaSuspension causaSuspension = causaSuspensionDAO.getOne(Integer.parseInt(suspenderTituloJSP.getIdCausa()));

		titulo.setEstadoTitulo(estadoTituloDAO.getOne(Constantes.ESTADO_TITULO_SUSPENDIDO));

		tituloDAO.save(titulo);

		Suspension suspension = new Suspension();
		suspension.setTitulo(titulo);
		suspension.setFechaSuspension(Utiles.parseDatePantalla(suspenderTituloJSP.getFechaSuspension()));
		suspension.setCausaSuspension(causaSuspension);

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				suspenderTituloJSP.getNombreResponsableSeguridad(),
				suspenderTituloJSP.getPasswordResponsableSeguridad());

		suspension.setFirmaResponsable(responsableSeguridad);
		suspension.setFechahoraFirmaResponsable(new Date());

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) sesion.getAttribute("usuario");
		Personal personal = personalDAO.getOne(Integer.parseInt(usuarioRegistrado.getIdPersonal()));
		String nombre = personal.getNombre() + " " + personal.getApellido1();
		if (StringUtils.isNotBlank(personal.getApellido2())) {
			nombre += " " + personal.getApellido2();
		}

		titulo.setListSuspension(Arrays.asList(new Suspension[] { suspension }));
		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoSuspensionTituloHabilitantePersonalFerroviario(titulo, nombre,
					personal.getCompania().getNombre());
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_SUSPENDER_TITULO);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), Constantes.TYPE_PDF);

		suspension.setEvidencia(evidencia);
		suspensionDAO.save(suspension);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}
}
