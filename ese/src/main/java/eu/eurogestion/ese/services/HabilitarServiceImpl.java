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

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.domain.TituloCurso;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.HabilitacionJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.CursoDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoTituloDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TipoTituloDAO;
import eu.eurogestion.ese.repository.TituloCursoDAO;
import eu.eurogestion.ese.repository.TituloDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class HabilitarServiceImpl implements HabilitarService {

	/** Repositories & Services **/

	@Autowired
	public CursoDAO cursoDAO;

	@Autowired
	public TituloCursoDAO tituloCursoDAO;

	@Autowired
	public TipoTituloDAO tipoTituloDAO;

	@Autowired
	public TituloDAO tituloDAO;

	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EstadoTituloDAO estadoTituloDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public Titulo altaTitulo(HabilitacionJSP habilitacion, HttpSession sesion) throws EseException {
		Personal personal = personalDAO.getOne(Integer.parseInt(habilitacion.getIdPersonal()));
		Compania compania = companiaDAO.getOne(personal.getCompania().getIdCompania());

		Titulo tituloDB = crearTituloDB(habilitacion, personal, compania);
		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) sesion.getAttribute("usuario");
		String usuarioCargo = usuarioRegistrado.getCargo();
		Personal personalSesion = personalDAO.getOne(Integer.parseInt(usuarioRegistrado.getIdPersonal()));
		String usuarioNombre = personalSesion.getNombre() + " " + personalSesion.getApellido1();
		if (StringUtils.isNotBlank(personalSesion.getApellido2())) {
			usuarioNombre += " " + personalSesion.getApellido2();
		}

		byte[] fichero;
		try {
			switch (tituloDB.getTipoTitulo().getIdTipoTitulo()) {
			case Constantes.TIPO_TITULO_CERTIFICADO_COMPLEMENTARIO:
				try {
					fichero = utilesPDFService.generarCertificadoComplementarioHabilitacion(tituloDB);
				} catch (SQLException e) {
					throw new EseException(e.getMessage());
				}

				break;
			case Constantes.TIPO_TITULO_AUXILIAR_OPERACIONES_TREN_CON_MANIOBRAS:
				try {
					fichero = utilesPDFService.generarDocumentoHabPerOpeTrenAOT_AOTM_OVM(tituloDB, usuarioNombre,
							usuarioCargo, tituloDB.getTipoTitulo().getIdTipoTitulo());
				} catch (SQLException e) {
					throw new EseException(e.getMessage());
				}

				break;
			case Constantes.TIPO_TITULO_RESPONSABLE_OPERACIONES_CARGA:
				try {
					fichero = utilesPDFService.generarDocumentoHabPerOpeTrenROC(tituloDB, usuarioNombre, usuarioCargo);
				} catch (SQLException e) {
					throw new EseException(e.getMessage());
				}

				break;
			case Constantes.TIPO_TITULO_OPERADOR_VEHICULO_MANIOBRAS:
				try {
					fichero = utilesPDFService.generarDocumentoHabPerOpeTrenAOT_AOTM_OVM(tituloDB, usuarioNombre,
							usuarioCargo, tituloDB.getTipoTitulo().getIdTipoTitulo());
				} catch (SQLException e) {
					throw new EseException(e.getMessage());
				}

				break;
			case Constantes.TIPO_TITULO_AUXILIAR_OPERACIONES_TREN:
				try {
					fichero = utilesPDFService.generarDocumentoHabPerOpeTrenAOT_AOTM_OVM(tituloDB, usuarioNombre,
							usuarioCargo, tituloDB.getTipoTitulo().getIdTipoTitulo());
				} catch (SQLException e) {
					throw new EseException(e.getMessage());
				}
				break;

			default:
				throw new EseException("el tipo del titulo es incorrecto");
			}
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		return creacionEvidenciaTitulo(compania, tituloDB, fichero);
	}

	private Titulo creacionEvidenciaTitulo(Compania compania, Titulo tituloDB, byte[] fichero) throws EseException {

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_GENERAR_TITULO_HABILITANTE);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tituloDB.getTipoTitulo().getModeloDocumento(), fichero,
				md5, tipoEvidencia, Utiles.sysdate(), Constantes.TYPE_PDF);

		tituloDB.setEvidencia(evidencia);
		tituloDAO.save(tituloDB);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		return tituloDB;
	}

	private Titulo crearTituloDB(HabilitacionJSP habilitacion, Personal personal, Compania compania) {

		Titulo titulo = new Titulo();
		titulo.setTipoTitulo(tipoTituloDAO.getOne(Integer.parseInt(habilitacion.getIdTipoTitulo())));
		titulo.setRevisionPsico(revisionPsicoDAO.getOne(Integer.parseInt(habilitacion.getIdRevPsicofisica())));
		if (StringUtils.isNotBlank(habilitacion.getFechaExpedicion())) {
			titulo.setValidoDesde(Utiles.parseDatePantalla(habilitacion.getFechaExpedicion()));
		} else {
			titulo.setValidoDesde(Utiles.sysdate());
		}

		if (StringUtils.isNotBlank(habilitacion.getNumeroReferencia())) {
			titulo.setNumReferencia(habilitacion.getNumeroReferencia());
		}
		if (StringUtils.isNotBlank(habilitacion.getCategoriasConduccion())) {
			titulo.setCategoria(habilitacion.getCategoriasConduccion());
		}
		if (StringUtils.isNotBlank(habilitacion.getNotas())) {
			titulo.setNotaTipoTitulo(habilitacion.getNotas());
		}
		if (StringUtils.isNotBlank(habilitacion.getInfoAdicional())) {
			titulo.setInfoAdicionalCategoria(habilitacion.getInfoAdicional());
		}

		titulo.setLocalidad(personal.getLocalidad());
		titulo.setProvincia(personal.getProvincia());
		titulo.setPais(personal.getPais());
		titulo.setNombre(compania.getNombre());
		titulo.setPersonal(personal);
		Curso curso = cursoDAO.findFirstByIdCursoInOrderByFechaCaducidadAsc(
				convertListStringToListInteger(habilitacion.getListaCursos()));
		Date fechaCaducidad = titulo.getRevisionPsico().getFechaCaducidad();
		if (curso.getFechaCaducidad().before(titulo.getRevisionPsico().getFechaCaducidad())) {
			fechaCaducidad = curso.getFechaCaducidad();
		}
		titulo.setFechaCaducidad(fechaCaducidad);

		titulo.setEstadoTitulo(estadoTituloDAO.getOne(Constantes.ESTADO_TITULO_CONCEDIDO));

		Titulo tituloDB = tituloDAO.save(titulo);

		List<TituloCurso> listaTituloCurso = new ArrayList<>();
		for (String idCurso : habilitacion.getListaCursos()) {
			TituloCurso tituloCurso = new TituloCurso();
			tituloCurso.setCurso(cursoDAO.getOne(Integer.parseInt(idCurso)));
			tituloCurso.setTitulo(tituloDB);
			listaTituloCurso.add(tituloCursoDAO.save(tituloCurso));

		}

		tituloDB.setListTituloCursos(listaTituloCurso);
		return tituloDB;
	}

	private List<Integer> convertListStringToListInteger(List<String> listaString) {

		List<Integer> listaInteger = new ArrayList<>();

		for (String string : listaString) {
			listaInteger.add(Integer.parseInt(string));
		}

		return listaInteger;
	}
}
