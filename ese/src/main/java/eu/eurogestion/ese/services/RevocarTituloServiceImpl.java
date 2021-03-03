package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.Revocacion;
import eu.eurogestion.ese.domain.Suspension;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.RevocarTituloJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.CausaRevocacionDAO;
import eu.eurogestion.ese.repository.CausaSuspensionDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoTituloDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevocacionDAO;
import eu.eurogestion.ese.repository.SuspensionDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class RevocarTituloServiceImpl implements RevocarTituloService {

	/** Repositories & Services **/

	@Autowired
	public RevocacionDAO revocacionDAO;

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
	public CausaRevocacionDAO causaRevocacionDAO;

	@Autowired
	public SuspensionDAO suspensionDAO;

	@Autowired
	public CausaSuspensionDAO causaSuspensionDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void revocarTitulo(RevocarTituloJSP revocarTituloJSP, HttpSession sesion) throws EseException {

		if (StringUtils.isNotBlank(revocarTituloJSP.getIdSuspension())) {
			Suspension suspension = suspensionDAO.getOne(Integer.parseInt(revocarTituloJSP.getIdSuspension()));
			suspension.setFechaResolucion(Utiles.parseDatePantalla(revocarTituloJSP.getFechaResolucionSuspension()));
			suspension.setCausaResolucion(revocarTituloJSP.getCausaResolucionSuspension());
			if (StringUtils.isNotBlank(revocarTituloJSP.getObservacionesSuspension())) {
				suspension.setObservaciones(revocarTituloJSP.getObservacionesSuspension());
			}

			suspensionDAO.save(suspension);
		}

		Revocacion revocacion = new Revocacion();
		Titulo titulo = tituloDAO.getOne(Integer.parseInt(revocarTituloJSP.getIdTitulo()));
		titulo.setEstadoTitulo(estadoTituloDAO.getOne(Constantes.ESTADO_TITULO_REVOCADO));
		tituloDAO.save(titulo);
		revocacion.setTitulo(titulo);
		revocacion.setCausaRevocacion(
				causaRevocacionDAO.getOne(Integer.parseInt(revocarTituloJSP.getIdCausaRevocacion())));
		revocacion.setFechaRevocacion(Utiles.parseDatePantalla(revocarTituloJSP.getFechaRevocacion()));
		revocacion.setSancionesEmpresa(revocarTituloJSP.getSancionesEmpresariales());
		revocacion.setObservaciones(revocarTituloJSP.getObservaciones());

		Personal responsableSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				revocarTituloJSP.getNombreResponsableSeguridad(), revocarTituloJSP.getPasswordResponsableSeguridad());

		revocacion.setFirmaResponsable(responsableSeguridad);
		revocacion.setFechahoraFirmaResponsable(new Date());

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) sesion.getAttribute("usuario");
		Personal personal = personalDAO.getOne(Integer.parseInt(usuarioRegistrado.getIdPersonal()));
		String nombre = personal.getNombre() + " " + personal.getApellido1();
		if (StringUtils.isNotBlank(personal.getApellido2())) {
			nombre += " " + personal.getApellido2();
		}
		titulo.setRevocacion(revocacion);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoRevocacionTituloHabilitantePersonalFerroviario(titulo, nombre,
					personal.getCompania().getNombre());
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_REVOCAR_TITULO);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), Constantes.TYPE_PDF);

		revocacion.setEvidencia(evidencia);
		revocacionDAO.save(revocacion);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}
}
