package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eu.eurogestion.ese.domain.AnexoInformeAnomalia;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.InformeAnomalias;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CrearInformeAnomaliasJSP;
import eu.eurogestion.ese.repository.AnexoInformeAnomaliaDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.InformeAnomaliasDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class CrearInformeAnomaliasServiceImpl implements CrearInformeAnomaliasService {

	/** Repositories & Services **/

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public InformeAnomaliasDAO informeAnomaliasDAO;

	@Autowired
	public AnexoInformeAnomaliaDAO anexoInformeAnomaliaDAO;

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void guardarInformeAnomalias(CrearInformeAnomaliasJSP crearInformeAnomalias) {

		InformeAnomalias informeAnomalias = informeAnomaliasDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdInformeAnomalias()));

		informeAnomalias.setCodigoInforme(crearInformeAnomalias.getCodigoInforme());
		if (StringUtils.isNotBlank(crearInformeAnomalias.getDescripcionDatosInspeccion())) {
			informeAnomalias.setDescripcionInspeccion(crearInformeAnomalias.getDescripcionDatosInspeccion());
		}
		if (StringUtils.isNotBlank(crearInformeAnomalias.getObservacionesDatosInspeccion())) {
			informeAnomalias.setObservacionesInspeccion(crearInformeAnomalias.getObservacionesDatosInspeccion());
		}
		if (StringUtils.isNotBlank(crearInformeAnomalias.getMedidasCautelares())) {
			informeAnomalias.setDescripcionMedidasCautelares(crearInformeAnomalias.getMedidasCautelares());
		}

		informeAnomaliasDAO.save(informeAnomalias);
	}

	public void firmaCrearInformeAnomaliasInspectorSeguridad(CrearInformeAnomaliasJSP crearInformeAnomalias,
			Model model) throws EseException {

		InformeAnomalias informeAnomalias = informeAnomaliasDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdInformeAnomalias()));
		informeAnomalias.setFirmaInspector(personalDAO.getOne(crearInformeAnomalias.getIdFirmaInspectorSeguridad()));
		informeAnomalias.setFechahoraFirmaInspector(new Date());
		informeAnomaliasDAO.save(informeAnomalias);

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (informeAnomalias.getFirmaResponsable() != null) {
			finalizarInformeAnomalias(informeAnomalias, model, mensajeInfo);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	public void firmaCrearInformeAnomaliasResponsableSeguridad(CrearInformeAnomaliasJSP crearInformeAnomalias,
			Model model) throws EseException {

		InformeAnomalias informeAnomalias = informeAnomaliasDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdInformeAnomalias()));
		informeAnomalias
				.setFirmaResponsable(personalDAO.getOne(crearInformeAnomalias.getIdFirmaResponsableSeguridad()));
		informeAnomalias.setFechahoraFirmaResponsable(new Date());
		informeAnomaliasDAO.save(informeAnomalias);

		List<String> mensajeInfo = new ArrayList<>();
		mensajeInfo.add("Se ha firmado correctamente");

		if (informeAnomalias.getFirmaInspector() != null) {
			finalizarInformeAnomalias(informeAnomalias, model, mensajeInfo);
			crearInformeAnomalias.setTieneEvidencia(Boolean.TRUE);
		} else {
			model.addAttribute("info", mensajeInfo);
		}
	}

	private void finalizarInformeAnomalias(InformeAnomalias informeAnomalias, Model model, List<String> mensajeInfo)
			throws EseException {

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoInfInsSegResAno(informeAnomalias);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_CIERRE_MEDIDAS_RA);

		Evidencia evidencia = utilesPDFService.crearEvidencia(informeAnomalias.getIs().getNumReferencia(), fichero, md5,
				tipoEvidencia, Utiles.sysdate(), Constantes.TYPE_PDF);

		informeAnomalias.setEvidencia20(evidencia);
		informeAnomaliasDAO.save(informeAnomalias);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		mensajeInfo.add("Se ha generado la evidencia correctamente");
		model.addAttribute("info", mensajeInfo);
	}

	@Override
	public void anadirEvidenciaInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomalias, HttpSession sesion)
			throws EseException {

		InformeAnomalias informeAnomalias = informeAnomaliasDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdInformeAnomalias()));

		byte[] fichero;
		try {
			fichero = crearInformeAnomalias.getEvidencia().getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_ANEXO);

		Evidencia evidencia = utilesPDFService.crearEvidencia(informeAnomalias.getIs().getNumReferencia(), fichero, md5,
				tipoEvidencia, Utiles.sysdate(), crearInformeAnomalias.getEvidencia().getContentType());

		AnexoInformeAnomalia anexoInformeAnomalia = new AnexoInformeAnomalia();
		anexoInformeAnomalia.setDescripcion(crearInformeAnomalias.getDescripcionEvidencia());
		anexoInformeAnomalia.setEvidencia(evidencia);
		anexoInformeAnomalia.setInformeAnomalias(informeAnomalias);
		anexoInformeAnomaliaDAO.save(anexoInformeAnomalia);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}
}
