package eu.eurogestion.ese.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import eu.eurogestion.ese.domain.AnexoProveedor;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Proveedor;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.HomologacionJSP;
import eu.eurogestion.ese.repository.AnexoProveedorDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoProveedorDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ProveedorDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class HomologacionServiceImpl implements HomologacionService {

	/** Repositories & Services **/

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public ProveedorDAO proveedorDAO;

	@Autowired
	public EstadoProveedorDAO estadoProveedorDAO;

	@Autowired
	public AnexoProveedorDAO anexoProveedorDAO;

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void guardarEvidenciaDocumentosAnexos(HomologacionJSP homologacionJSP, HttpSession sesion)
			throws EseException {

		Proveedor proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));

		Evidencia evidencia = crearEvidencia(homologacionJSP.getEvidencia(), homologacionJSP.getDescripcionEvidencia(),
				Constantes.TIPO_EVIDENCIA_ANEXO);

		AnexoProveedor anexoProveedor = new AnexoProveedor();
		anexoProveedor.setProveedor(proveedor);
		anexoProveedor.setDescripcion(homologacionJSP.getDescripcionEvidencia());
		anexoProveedor.setEvidencia(evidencia);

		anexoProveedorDAO.save(anexoProveedor);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		homologacionJSP.setDescripcionEvidencia("");
	}

	private Evidencia crearEvidencia(MultipartFile ficheroPantalla, String descripcionFichero, Integer tipoEvidenciaId)
			throws EseException {

		byte[] fichero;
		try {
			fichero = ficheroPantalla.getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(tipoEvidenciaId);

		Evidencia evidencia = utilesPDFService.crearEvidencia(descripcionFichero, fichero, md5, tipoEvidencia,
				Utiles.sysdate(), ficheroPantalla.getContentType());

		return evidencia;
	}

	@Override
	public void guardarProveedor(HomologacionJSP homologacionJSP, HttpSession sesion, Model model) throws EseException {

		int idEstado = homologacionJSP.getIdEstadoProveedor();

		Proveedor proveedor;

		if (StringUtils.isBlank(homologacionJSP.getIdProveedor())) {
			proveedor = new Proveedor();
		} else {
			proveedor = proveedorDAO.getOne(Integer.parseInt(homologacionJSP.getIdProveedor()));
		}

		switch (idEstado) {
		case Constantes.ESTADO_PROVEEDOR_NO_EXISTE:
			proveedor.setCompania(companiaDAO.getOne(Integer.parseInt(homologacionJSP.getIdCompaniaHomologacion())));
			proveedor.setEstadoProveedor(estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_CREADO));
			proveedor = proveedorDAO.save(proveedor);

			homologacionJSP.setIdProveedor(proveedor.getIdProveedor().toString());
			homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_CREADO);
			model.addAttribute("info", " Se ha creado correctamente");
			break;
		case Constantes.ESTADO_PROVEEDOR_CREADO:
			Evidencia evidencia67 = crearEvidencia(homologacionJSP.getEvidencia(),
					homologacionJSP.getDescripcionEvidencia(), Constantes.TIPO_EVIDENCIA_PYC_DOCUMENTACION_ENVIADA);

			proveedor.setEvidencia67(evidencia67);
			proveedor.setEstadoProveedor(estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_ENVIADO_INFORMACION));
			proveedor = proveedorDAO.save(proveedor);

			blockChainService.uploadDocumento(evidencia67.getDocumento());

			homologacionJSP.setDescripcionEvidenciaEnvioInformacion(homologacionJSP.getDescripcionEvidencia());
			homologacionJSP.setDescripcionEvidencia("");
			homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_ENVIADO_INFORMACION);
			model.addAttribute("info", " Se ha añadido correctamente");
			break;
		case Constantes.ESTADO_PROVEEDOR_ENVIADO_INFORMACION:
			Evidencia evidencia68 = crearEvidencia(homologacionJSP.getEvidencia(),
					homologacionJSP.getDescripcionEvidencia(), Constantes.TIPO_EVIDENCIA_PYC_ACUSE_DE_RECIBO);

			proveedor.setEvidencia68(evidencia68);
			proveedor.setEstadoProveedor(estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_RECIBIDO_INFORMACION));
			proveedor = proveedorDAO.save(proveedor);

			blockChainService.uploadDocumento(evidencia68.getDocumento());

			homologacionJSP.setDescripcionEvidenciaInformacionRecibida(homologacionJSP.getDescripcionEvidencia());
			homologacionJSP.setDescripcionEvidencia("");
			homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_RECIBIDO_INFORMACION);
			model.addAttribute("info", " Se ha añadido correctamente");
			break;
		case Constantes.ESTADO_PROVEEDOR_RECIBIDO_INFORMACION:
			Evidencia evidencia69 = crearEvidencia(homologacionJSP.getEvidencia(),
					homologacionJSP.getDescripcionEvidencia(), Constantes.TIPO_EVIDENCIA_PYC_SOLICITUD_DOCUMENTACION);

			proveedor.setEvidencia69(evidencia69);
			proveedor
					.setEstadoProveedor(estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_SOLICITADA));
			proveedor = proveedorDAO.save(proveedor);

			blockChainService.uploadDocumento(evidencia69.getDocumento());

			homologacionJSP.setDescripcionEvidenciaSolicitudDocumentacion(homologacionJSP.getDescripcionEvidencia());
			homologacionJSP.setDescripcionEvidencia("");
			homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_SOLICITADA);
			model.addAttribute("info", " Se ha añadido correctamente");
			break;
		case Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_SOLICITADA:
			Evidencia evidencia70 = crearEvidencia(homologacionJSP.getEvidencia(),
					homologacionJSP.getDescripcionEvidencia(), Constantes.TIPO_EVIDENCIA_PYC_DOCUMENTACION_RECIBIDA);

			proveedor.setEvidencia70(evidencia70);
			proveedor.setEstadoProveedor(estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_ANALIZANDO));
			proveedor = proveedorDAO.save(proveedor);

			blockChainService.uploadDocumento(evidencia70.getDocumento());

			homologacionJSP.setDescripcionEvidenciaDocumentacionRecibida(homologacionJSP.getDescripcionEvidencia());
			homologacionJSP.setDescripcionEvidencia("");
			homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_ANALIZANDO);
			model.addAttribute("info", " Se ha añadido correctamente");
			break;
		case Constantes.ESTADO_PROVEEDOR_ANALIZANDO:
			proveedor.setEstadoProveedor(estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_FIN_ANALISIS));

			homologacionJSP.setDescripcionEvidencia("");
			homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_FIN_ANALISIS);
			break;
		case Constantes.ESTADO_PROVEEDOR_FIN_ANALISIS:
			proveedor.setFechaResolucion(new Date());
			Evidencia evidencia = null;
			if (homologacionJSP.getResultadoHomologacion()) {
				evidencia = crearEvidencia(homologacionJSP.getEvidencia(), homologacionJSP.getDescripcionEvidencia(),
						Constantes.TIPO_EVIDENCIA_PYC_FICHA_EVALUACION);

				proveedor.setEvidencia72(evidencia);
				proveedor.setEstadoProveedor(
						estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_ACEPTADA));

				homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_ACEPTADA);
			} else {
				evidencia = crearEvidencia(homologacionJSP.getEvidencia(), homologacionJSP.getDescripcionEvidencia(),
						Constantes.TIPO_EVIDENCIA_PYC_INFORME_NO_CONFORMIDAD);

				proveedor.setEvidencia74(evidencia);
				proveedor.setEstadoProveedor(
						estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_RECHAZADA));

				homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_RECHAZADA);
			}

			proveedor = proveedorDAO.save(proveedor);

			blockChainService.uploadDocumento(evidencia.getDocumento());

			homologacionJSP.setDescripcionEvidenciaResultadoHomologacion(homologacionJSP.getDescripcionEvidencia());
			homologacionJSP.setDescripcionEvidencia("");
			homologacionJSP.setFechaHomologacion(
					Utiles.convertDateToString(proveedor.getFechaResolucion(), Constantes.FORMATO_FECHA_PANTALLA));
			model.addAttribute("info", " Se ha añadido correctamente");
			break;
		case Constantes.ESTADO_PROVEEDOR_HOMOLOGACION_ACEPTADA:
			Evidencia evidencia73 = crearEvidencia(homologacionJSP.getEvidencia(),
					homologacionJSP.getDescripcionEvidencia(), Constantes.TIPO_EVIDENCIA_PYC_COMUNICACION_RS);

			proveedor.setEvidencia73(evidencia73);
			proveedor.setEstadoProveedor(estadoProveedorDAO.getOne(Constantes.ESTADO_PROVEEDOR_FINALIZADO));
			proveedor = proveedorDAO.save(proveedor);

			blockChainService.uploadDocumento(evidencia73.getDocumento());

			homologacionJSP.setDescripcionEvidenciaComunicacion(homologacionJSP.getDescripcionEvidencia());
			homologacionJSP.setDescripcionEvidencia("");
			homologacionJSP.setIdEstadoProveedor(Constantes.ESTADO_PROVEEDOR_FINALIZADO);
			model.addAttribute("info", " Se ha añadido correctamente");
			break;
		default:
			break;
		}
	}
}
