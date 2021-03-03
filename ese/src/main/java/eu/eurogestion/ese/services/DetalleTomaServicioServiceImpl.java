package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.TomaServicio;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.DetalleTomaServicioJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.ComposicionDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoHistoricoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TomaServicioDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class DetalleTomaServicioServiceImpl implements DetalleTomaServicioService {

	/** Repositories & Services **/

	@Autowired
	public TomaServicioDAO tomaServicioDAO;

	@Autowired
	public EstadoHistoricoDAO estadoHistoricoDAO;

	@Autowired
	public ComposicionDAO composicionDAO;

	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	@Autowired
	public TrenDAO trenDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void guardarDetalleTomaServicio(DetalleTomaServicioJSP detalleTomaServicioJSP, HttpSession sesion)
			throws EseException {

		TomaServicio tomaServicio = convertDetalleTomaServicioJSPToTomaServicio(detalleTomaServicioJSP, sesion);
		tomaServicioDAO.save(tomaServicio);

		detalleTomaServicioJSP.setIdTomaServicio(tomaServicio.getIdTomaServicio().toString());
		detalleTomaServicioJSP.setIdEstadoTomaServicio(tomaServicio.getEstadoToma().getIdEstadoHistorico());

	}

	@Override
	public void generarFichaDetalleTomaServicio(DetalleTomaServicioJSP detalleTomaServicioJSP, HttpSession sesion)
			throws EseException {

		TomaServicio tomaServicio = convertDetalleTomaServicioJSPToTomaServicio(detalleTomaServicioJSP, sesion);
		tomaServicio.setEstadoToma(estadoHistoricoDAO.getOne(Constantes.ESTADO_HISTORICO_CERRADO));

		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoTomSer(tomaServicio);
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_REPORTE_FIN_SERVICIO);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), Constantes.TYPE_PDF);

		tomaServicio.setEvidencia77(evidencia);

		tomaServicioDAO.save(tomaServicio);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		detalleTomaServicioJSP.setIdEstadoTomaServicio(tomaServicio.getEstadoToma().getIdEstadoHistorico());

	}

	private TomaServicio convertDetalleTomaServicioJSPToTomaServicio(DetalleTomaServicioJSP destalleTomaServicioJSP,
			HttpSession sesion) throws EseException {

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) sesion.getAttribute("usuario");
		Personal personal = personalDAO.getOne(Integer.parseInt(usuarioRegistrado.getIdPersonal()));
		TomaServicio tomaServicio;
		if (StringUtils.isBlank(destalleTomaServicioJSP.getIdTomaServicio())) {
			tomaServicio = new TomaServicio();
			tomaServicio.setEstadoToma(estadoHistoricoDAO.getOne(Constantes.ESTADO_HISTORICO_CREADO));
		} else {
			tomaServicio = tomaServicioDAO.getOne(Integer.parseInt(destalleTomaServicioJSP.getIdTomaServicio()));
		}

		tomaServicio.setPersonal(personal);

		tomaServicio.setFecha(
				Utiles.convertStringToDate(destalleTomaServicioJSP.getFecha(), Constantes.FORMATO_FECHA_PANTALLA));
		tomaServicio.setHora(
				Utiles.convertStringToDate(destalleTomaServicioJSP.getHora(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		tomaServicio.setNve(destalleTomaServicioJSP.getNveLocomotora());
		tomaServicio.setTren(trenDAO.getOne(Integer.parseInt(destalleTomaServicioJSP.getIdTren())));
		tomaServicio.setLugarInspeccion(destalleTomaServicioJSP.getLugar());

		if (StringUtils.isNotBlank(destalleTomaServicioJSP.getMotivo())) {
			tomaServicio.setMotivo(destalleTomaServicioJSP.getMotivo());
		}
		tomaServicio.setRealizarAccion(destalleTomaServicioJSP.getRealizarAccion());
		tomaServicio.setDocumentacionReglamentaria(destalleTomaServicioJSP.getDocumentacionReglamentaria());
		tomaServicio.setLibroTelefonemas(destalleTomaServicioJSP.getLibroTelefonemas());
		tomaServicio.setLibroAverias(destalleTomaServicioJSP.getLibroAverias());
		tomaServicio.setDotacionUtilesServicio(destalleTomaServicioJSP.getDotacionUtilesServicio());
		tomaServicio.setSenalizacionCabezaCola(destalleTomaServicioJSP.getSenalizacionCabezaCola());
		tomaServicio.setVisibilidadAdecuada(destalleTomaServicioJSP.getVisibilidadAdecuada());
		tomaServicio.setAnomaliasRodajeCaja(destalleTomaServicioJSP.getAnomaliasRodajeCaja());
		tomaServicio.setAnomaliasSuspension(destalleTomaServicioJSP.getAnomaliasSuspension());
		tomaServicio.setAnomaliasChoqueTraccion(destalleTomaServicioJSP.getAnomaliasChoqueTraccion());
		tomaServicio.setEstadoPrecintos(destalleTomaServicioJSP.getEstadoPrecintos());
		tomaServicio.setPosicionPalancaCambiador(destalleTomaServicioJSP.getPosicionPalancaCambiador());
		tomaServicio.setFrenosEstacionamiento(destalleTomaServicioJSP.getFrenosEstacionamiento());
		tomaServicio.setConfiguracionFrenado(destalleTomaServicioJSP.getConfiguracionFrenado());
		tomaServicio.setDispositivoVigilanciaHm(destalleTomaServicioJSP.getDispositivoVigilanciaHM());
		tomaServicio.setValvulaEmergenciaSeta(destalleTomaServicioJSP.getValvulaEmergenciaSeta());
		tomaServicio.setPruebasFreno(destalleTomaServicioJSP.getPruebasFreno());
		tomaServicio.setPruebaInversionMarcha(destalleTomaServicioJSP.getPruebaInversionMarcha());
		tomaServicio.setAsfaCorrecto(destalleTomaServicioJSP.getAsfaCorrecto());
		tomaServicio.setEquipoRadiotelefonia(destalleTomaServicioJSP.getEquipoRadioTelefonia());
		tomaServicio.setInspeccionVisual(destalleTomaServicioJSP.getInspeccionVisual());
		tomaServicio.setDatosDocumentoTren(destalleTomaServicioJSP.getDatosDocumentoTren());
		tomaServicio.setLibroTelefonemasRelevo(destalleTomaServicioJSP.getLibroTelefonemasRelevo());
		tomaServicio.setNoExistenNotificaciones(destalleTomaServicioJSP.getNoExistenNotificaciones());

		if (StringUtils.isNotBlank(destalleTomaServicioJSP.getObservaciones())) {
			tomaServicio.setObservaciones(destalleTomaServicioJSP.getObservaciones());
		}
		return tomaServicio;
	}
}
