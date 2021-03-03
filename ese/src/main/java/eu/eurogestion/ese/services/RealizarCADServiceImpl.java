package eu.eurogestion.ese.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Cad;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Responsable;
import eu.eurogestion.ese.domain.ResultadoCad;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.RealizarCADJSP;
import eu.eurogestion.ese.repository.CadDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoInspeccionDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.IsDAO;
import eu.eurogestion.ese.repository.LimiteAlcoholDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ResponsableDAO;
import eu.eurogestion.ese.repository.ResultadoCadDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoCadDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class RealizarCADServiceImpl implements RealizarCADService {

	/** Repositories & Services **/

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public CadDAO cadDAO;

	@Autowired
	public IsDAO isDAO;

	@Autowired
	public LimiteAlcoholDAO limiteAlcoholDAO;

	@Autowired
	public EstadoInspeccionDAO estadoInspeccionDAO;

	@Autowired
	public TipoCadDAO tipoCadDAO;

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public ResultadoCadDAO resultadoCadDAO;

	@Autowired
	public ResponsableDAO responsableDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public EmailService emailService;

	@Autowired
	public TareaPendienteDAO tareaPendienteDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	public void autorizarCAD(RealizarCADJSP realizarCADJSP, HttpSession session) throws EseException {

		Cad cad = cadDAO.getOne(realizarCADJSP.getIdInformeCAD());
		cad.setPersonalControlado(personalDAO.getOne(Integer.parseInt(realizarCADJSP.getIdPersonalSometidoControl())));
		cad.setFechahoraFirmaPersonal(new Date());
		cad.setConsentimiento(Boolean.TRUE);
		if (StringUtils.isNotBlank(realizarCADJSP.getMedicamentos())) {
			cad.setMedicamentos(realizarCADJSP.getMedicamentos());

		}
		if (StringUtils.isNotBlank(realizarCADJSP.getObservacionesConsentimiento())) {
			cad.setObservacionesAutorizacion(realizarCADJSP.getObservacionesConsentimiento());

		}
		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoAutDetCAD(cad);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		Evidencia evidencia = crearEvidencia(fichero, Constantes.TIPO_EVIDENCIA_FICHA_CONSENTIMIENTO_CAD);

		cad.setEvidenciaTipo37(evidencia);
		cad.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_EN_CURSO));
		cadDAO.save(cad);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	public void noAutorizarCAD(RealizarCADJSP realizarCADJSP, HttpSession session) throws EseException {

		Cad cad = cadDAO.getOne(realizarCADJSP.getIdInformeCAD());
		cad.setPersonalControlado(personalDAO.getOne(Integer.parseInt(realizarCADJSP.getIdPersonalSometidoControl())));
		cad.setFechahoraFirmaPersonal(new Date());
		cad.setConsentimiento(Boolean.FALSE);
		if (StringUtils.isNotBlank(realizarCADJSP.getMedicamentos())) {
			cad.setMedicamentos(realizarCADJSP.getMedicamentos());
		}

		if (StringUtils.isNotBlank(realizarCADJSP.getObservacionesConsentimiento())) {
			cad.setObservacionesAutorizacion(realizarCADJSP.getObservacionesConsentimiento());
		}

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoAutDetCAD(cad);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		Evidencia evidencia = crearEvidencia(fichero, Constantes.TIPO_EVIDENCIA_FICHA_CONSENTIMIENTO_CAD);

		cad.setEvidenciaTipo37(evidencia);
		cad.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_FINALIZADA));
		cadDAO.save(cad);
		cerrarTareaPendiente(cad);

		List<Responsable> listaResponsable = responsableDAO.findRSandRO();

		for (Responsable responsable : listaResponsable) {
			try {
				emailService.sendEmailConArchivos(responsable.getPersonal().getEmail(),
						"Autorización de detección de CAD", "Autorización de detección de CAD", fichero,
						"Autorización de detección de CAD.pdf", Constantes.TYPE_PDF);
			} catch (MessagingException e) {
				throw new EseException(e.getMessage());
			}
		}

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private void cerrarTareaPendiente(Cad cad) {

		TareaPendiente tareaPendiente = tareaPendienteDAO.findTareaPendienteByReference(cad.getIdCad(),
				Constantes.TAREAPTE_REALIZAR_CAD);

		if (tareaPendiente != null) {
			tareaPendiente.setLeido(Boolean.TRUE);
			tareaPendienteDAO.save(tareaPendiente);
		}
	}

	public RealizarCADJSP convertIdCADToRealizarCADJSP(Integer idCAD) {

		RealizarCADJSP realizarCADJSP = new RealizarCADJSP();
		Cad cad = cadDAO.getOne(idCAD);
		realizarCADJSP.setIdInformeCAD(idCAD);
		if (cad.getPersonalControlado() != null) {
			realizarCADJSP.setIdPersonalSometidoControl(cad.getPersonalControlado().getIdPersonal().toString());
		}
		if (cad.getConsentimiento() != null) {
			realizarCADJSP.setConsentimiento(cad.getConsentimiento());
		} else {
			realizarCADJSP.setConsentimiento(Boolean.FALSE);
		}
		if (cad.getIs().getEstadoInspeccion() != null) {

			realizarCADJSP.setIdEstadoInspeccion(cad.getIs().getEstadoInspeccion().getIdEstadoInspeccion());
		}
		realizarCADJSP.setMedicamentos(cad.getMedicamentos());
		realizarCADJSP.setObservacionesConsentimiento(cad.getObservacionesAutorizacion());
		realizarCADJSP.setLugar(cad.getIs().getLugar());
		if (cad.getTipoCad() != null) {
			realizarCADJSP.setIdTipoControl(cad.getTipoCad().getIdTipoCad().toString());
		}
		if (cad.getCentroMedico() != null) {
			realizarCADJSP.setIdCentroMedico(cad.getCentroMedico().getIdCompania().toString());
		}
		if (cad.getDelegado1() != null) {
			realizarCADJSP.setIdDelegadoSeguridad1(cad.getDelegado1().getIdPersonal().toString());
		}
		if (cad.getDelegado2() != null) {
			realizarCADJSP.setIdDelegadoSeguridad2(cad.getDelegado2().getIdPersonal().toString());
		}
		realizarCADJSP.setObservaciones(cad.getObservaciones());
		if (cad.getEtilometroPr1Valor() != null) {
			realizarCADJSP.setPrimeraPrueba(cad.getEtilometroPr1Valor().toPlainString());
		}
		if (cad.getEtilometroPr1Hora() != null) {
			realizarCADJSP.setHoraPrimeraPrueba(Utiles.formatStringHorasPantalla(cad.getEtilometroPr1Hora()));
		}
		if (cad.getEtilometroPr1Resultado() != null) {
			realizarCADJSP.setResultadoPrimeraPrueba(cad.getEtilometroPr1Resultado().getValor());
		}
		if (cad.getEtilometroPr2Valor() != null) {
			realizarCADJSP.setSegundaPrueba(cad.getEtilometroPr2Valor().toPlainString());
		}
		if (cad.getEtilometroPr2Hora() != null) {
			realizarCADJSP.setHoraSegundaPrueba(Utiles.formatStringHorasPantalla(cad.getEtilometroPr2Hora()));
		}
		if (cad.getEtilometroPr2Resultado() != null) {
			realizarCADJSP.setResultadoSegundaPrueba(cad.getEtilometroPr2Resultado().getValor());
		}

		realizarCADJSP.setNumeroMuestraSangre(cad.getSangreNumeroMuestra());
		if (cad.getSangreAnalisis() != null) {
			realizarCADJSP.setAnalisisSangre(cad.getSangreAnalisis().toPlainString());
		}
		if (cad.getSangreResultado() != null) {
			realizarCADJSP.setIdResultadoSangre(cad.getSangreResultado().getIdResultadoCad().toString());
		}
		realizarCADJSP.setNumeroMuestraOrina(cad.getOrinaNumeroMuestra());
		if (cad.getOrinaAnalisis() != null) {
			realizarCADJSP.setAnalisisOrina(cad.getOrinaAnalisis().toPlainString());
		}
		if (cad.getOrinaResultado() != null) {
			realizarCADJSP.setIdResultadoOrina(cad.getOrinaResultado().getIdResultadoCad().toString());
		}
		realizarCADJSP.setObservacionesRecogidaPrueba(cad.getObservacionesMuestras());
		realizarCADJSP.setObservacionesLaboratorioPrueba(cad.getObservacionesLaboratorio());

		realizarCADJSP.setLugarIdentificacion(cad.getLugarIdentificacion());
		if (cad.getFechahoraIdentificaciones() != null) {
			realizarCADJSP.setDiaHoraIdentificacion(Utiles.convertDateToString(cad.getFechahoraIdentificaciones(),
					Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		}
		realizarCADJSP.setNombrePersonalMedico(cad.getNombrePersonalMedico());
		realizarCADJSP.setDniPersonalMedico(cad.getDocumentoPersonalMedico());
		realizarCADJSP.setNombreDelegadoSeguridad(cad.getNombreDelegadoSeguridad());
		realizarCADJSP.setDniDelegadoSeguridad(cad.getDocumentoDelegadoSeguridad());
		realizarCADJSP.setNombreDelegadoADIF(cad.getNombreDelegadoADIF());
		realizarCADJSP.setDniDelegadoADIF(cad.getDocumentoDelegadoADIF());
		realizarCADJSP.setNombreTecnicoCIAF(cad.getNombreTecnicoCIAF());
		realizarCADJSP.setDniTecnicoCIAF(cad.getDocumentoTecnicoCIAF());

		realizarCADJSP.setMedidasCautelaresTomadas(cad.getMedidasCautelares());
		realizarCADJSP.setLimiteAlcohol(limiteAlcoholDAO.findByFechaFinIsNull().getValor().floatValue());

		return realizarCADJSP;
	}

	public Cad guardarCAD(RealizarCADJSP realizarCAD) {

		Map<Integer, ResultadoCad> mapaResultados = new HashMap<Integer, ResultadoCad>();
		mapaResultados.put(Constantes.RESULTADO_CAD_POSITIVO,
				resultadoCadDAO.getOne(Constantes.RESULTADO_CAD_POSITIVO));
		mapaResultados.put(Constantes.RESULTADO_CAD_NEGATIVO,
				resultadoCadDAO.getOne(Constantes.RESULTADO_CAD_NEGATIVO));
		Cad cad = cadDAO.getOne(realizarCAD.getIdInformeCAD());

		cad.getIs().setLugar(realizarCAD.getLugar());
		cad.setTipoCad(tipoCadDAO.getOne(Integer.parseInt(realizarCAD.getIdTipoControl())));
		cad.setCentroMedico(companiaDAO.getOne(Integer.parseInt(realizarCAD.getIdCentroMedico())));

		if (StringUtils.isNotBlank(realizarCAD.getIdDelegadoSeguridad1())) {
			cad.setDelegado1(personalDAO.getOne(Integer.parseInt(realizarCAD.getIdDelegadoSeguridad1())));
		} else {
			cad.setDelegado1(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getIdDelegadoSeguridad2())) {
			cad.setDelegado2(personalDAO.getOne(Integer.parseInt(realizarCAD.getIdDelegadoSeguridad2())));
		} else {
			cad.setDelegado2(null);
		}
		if (StringUtils.isNotBlank(realizarCAD.getObservaciones())) {
			cad.setObservaciones(realizarCAD.getObservaciones());
		} else {
			cad.setObservaciones(null);
		}
		cad.setEtilometroPr1Valor(new BigDecimal(realizarCAD.getPrimeraPrueba()));
		cad.setEtilometroPr1Hora(Utiles.parseTimeHorasPantalla(realizarCAD.getHoraPrimeraPrueba()));
		cad.setEtilometroPr1Resultado(mapaResultados.get(
				comprobarEtilometro(new BigDecimal(realizarCAD.getPrimeraPrueba()), realizarCAD.getLimiteAlcohol())));

		if (StringUtils.isNotBlank(realizarCAD.getSegundaPrueba())) {
			cad.setEtilometroPr2Valor(new BigDecimal(realizarCAD.getSegundaPrueba()));
			cad.setEtilometroPr2Resultado(mapaResultados.get(comprobarEtilometro(
					new BigDecimal(realizarCAD.getSegundaPrueba()), realizarCAD.getLimiteAlcohol())));
		} else {
			cad.setEtilometroPr2Valor(null);
			cad.setEtilometroPr2Resultado(null);
		}
		if (StringUtils.isNotBlank(realizarCAD.getHoraSegundaPrueba())) {
			cad.setEtilometroPr2Hora(Utiles.parseTimeHorasPantalla(realizarCAD.getHoraSegundaPrueba()));
		} else {
			cad.setEtilometroPr2Hora(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getNumeroMuestraSangre())) {
			cad.setSangreNumeroMuestra(realizarCAD.getNumeroMuestraSangre());
		} else {
			cad.setSangreNumeroMuestra(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getAnalisisSangre())) {
			cad.setSangreAnalisis(new BigDecimal(realizarCAD.getAnalisisSangre()));
		} else {
			cad.setSangreAnalisis(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getIdResultadoSangre())) {
			cad.setSangreResultado(resultadoCadDAO.getOne(Integer.parseInt(realizarCAD.getIdResultadoSangre())));
		} else {
			cad.setSangreResultado(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getNumeroMuestraOrina())) {
			cad.setOrinaNumeroMuestra(realizarCAD.getNumeroMuestraOrina());
		} else {
			cad.setOrinaNumeroMuestra(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getAnalisisOrina())) {
			cad.setOrinaAnalisis(new BigDecimal(realizarCAD.getAnalisisOrina()));
		} else {
			cad.setOrinaAnalisis(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getIdResultadoOrina())) {
			cad.setOrinaResultado(resultadoCadDAO.getOne(Integer.parseInt(realizarCAD.getIdResultadoOrina())));
		} else {
			cad.setOrinaResultado(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getObservacionesRecogidaPrueba())) {
			cad.setObservacionesMuestras(realizarCAD.getObservacionesRecogidaPrueba());
		} else {
			cad.setObservacionesMuestras(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getObservacionesRecogidaPrueba())) {
			cad.setObservacionesLaboratorio(realizarCAD.getObservacionesLaboratorioPrueba());
		} else {
			cad.setObservacionesLaboratorio(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getLugarIdentificacion())) {
			cad.setLugarIdentificacion(realizarCAD.getLugarIdentificacion());
		} else {
			cad.setLugarIdentificacion(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getDiaHoraIdentificacion())) {
			cad.setFechahoraIdentificaciones(Utiles.convertStringToDate(realizarCAD.getDiaHoraIdentificacion(),
					Constantes.FORMATO_FECHA_COMPLETA_PANTALLA));
		} else {
			cad.setFechahoraIdentificaciones(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getNombrePersonalMedico())) {
			cad.setNombrePersonalMedico(realizarCAD.getNombrePersonalMedico());
		} else {
			cad.setNombrePersonalMedico(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getDniPersonalMedico())) {
			cad.setDocumentoPersonalMedico(realizarCAD.getDniPersonalMedico());
		} else {
			cad.setDocumentoPersonalMedico(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getNombreDelegadoSeguridad())) {
			cad.setNombreDelegadoSeguridad(realizarCAD.getNombreDelegadoSeguridad());
		} else {
			cad.setNombreDelegadoSeguridad(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getDniDelegadoSeguridad())) {
			cad.setDocumentoDelegadoSeguridad(realizarCAD.getDniDelegadoSeguridad());
		} else {
			cad.setDocumentoDelegadoSeguridad(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getNombreDelegadoADIF())) {
			cad.setNombreDelegadoADIF(realizarCAD.getNombreDelegadoADIF());
		} else {
			cad.setNombreDelegadoADIF(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getDniDelegadoADIF())) {
			cad.setDocumentoDelegadoADIF(realizarCAD.getDniDelegadoADIF());
		} else {
			cad.setDocumentoDelegadoADIF(null);
		}
		if (StringUtils.isNotBlank(realizarCAD.getNombreTecnicoCIAF())) {
			cad.setNombreTecnicoCIAF(realizarCAD.getNombreTecnicoCIAF());
		} else {
			cad.setNombreTecnicoCIAF(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getDniTecnicoCIAF())) {
			cad.setDocumentoTecnicoCIAF(realizarCAD.getDniTecnicoCIAF());
		} else {
			cad.setDocumentoTecnicoCIAF(null);
		}

		if (StringUtils.isNotBlank(realizarCAD.getMedidasCautelaresTomadas())) {
			cad.setMedidasCautelares(realizarCAD.getMedidasCautelaresTomadas());
		} else {
			cad.setMedidasCautelares(null);
		}
		isDAO.save(cad.getIs());
		return cadDAO.save(cad);
	}

	private Integer comprobarEtilometro(BigDecimal valor, Float limite) {

		BigDecimal limiteNumero = new BigDecimal(limite);
		if (valor.compareTo(limiteNumero) == 1) {
			return Constantes.RESULTADO_CAD_POSITIVO;
		} else {
			return Constantes.RESULTADO_CAD_NEGATIVO;
		}
	}

	@Override
	public void crearFichaCAD(RealizarCADJSP realizarCADJSP, HttpSession session) throws EseException {

		Cad cad = guardarCAD(realizarCADJSP);

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoFichContDetCAD(cad);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		Evidencia evidencia = crearEvidencia(fichero, Constantes.TIPO_EVIDENCIA_RESULTADO_PRUEBA_CAD);

		cad.setEvidenciaTipo38(evidencia);
		cad.getIs().setEstadoInspeccion(estadoInspeccionDAO.getOne(Constantes.ESTADO_INSPECCION_FINALIZADA));
		if (StringUtils.isNotBlank(realizarCADJSP.getObservacionesConsentimiento())) {
			cad.setObservacionesAutorizacion(realizarCADJSP.getObservacionesConsentimiento());
		}

		cadDAO.save(cad);

		cerrarTareaPendiente(cad);

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private Evidencia crearEvidencia(byte[] fichero, Integer tipoEvidenciaId) throws EseException {

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(tipoEvidenciaId);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), Constantes.TYPE_PDF);

		return evidencia;
	}
}
