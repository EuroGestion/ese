package eu.eurogestion.ese.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Libro;
import eu.eurogestion.ese.domain.LibroPersonal;
import eu.eurogestion.ese.domain.TareaPendiente;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.CrearLibroJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.LibroDAO;
import eu.eurogestion.ese.repository.LibroPersonalDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TareaPendienteDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TipoTareaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class CrearLibroServiceImpl implements CrearLibroService {

	/** Repositories & Services **/

	@Autowired
	public LibroDAO libroDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public TipoEvidenciaDAO tipoEvidenciaDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public LibroPersonalDAO libroPersonalDAO;

	@Autowired
	public TipoTareaDAO tipoTareaDAO;

	@Autowired
	public TareaPendienteDAO tareaPendienteDAO;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	@Override
	public void generarLibro(CrearLibroJSP crearLibroJSP, HttpSession session) throws EseException {

		Libro libro = new Libro();
		libro.setFechaSubida(new Date());
		libro.setTitulo(crearLibroJSP.getTitulo());

		byte[] fichero;
		try {
			fichero = crearLibroJSP.getDocumento().getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_PYC_SOLICITUD_DOCUMENTACION);
		String tipoFichero = crearLibroJSP.getDocumento().getContentType();

		Evidencia evidencia = utilesPDFService.crearEvidencia(crearLibroJSP.getTitulo(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), tipoFichero);

		libro.setEvidencia(evidencia);

		Libro libroBBD = libroDAO.save(libro);

		UsuarioRegistradoJSP usuarioRegistrado = (UsuarioRegistradoJSP) session.getAttribute("usuario");

		for (Integer idPersonal : crearLibroJSP.getListPersonalesLibro()) {
			LibroPersonal libroPersonal = new LibroPersonal();
			libroPersonal.setLibro(libroBBD);
			libroPersonal.setPersonal(personalDAO.getOne(idPersonal));
			LibroPersonal libroPersonalDDBB = libroPersonalDAO.save(libroPersonal);
			crearTareaPendiente(idPersonal, Integer.parseInt(usuarioRegistrado.getIdPersonal()),
					libroPersonalDDBB.getIdLibroPersonal());
		}

		blockChainService.uploadDocumento(evidencia.getDocumento());
	}

	private void crearTareaPendiente(Integer idPersonal, Integer idUsuarioRegistrado, Integer idLibroPersonal) {

		TareaPendiente tareaPendienteBBDD = tareaPendienteDAO.findTareaPendienteByIdDestinatarioAndTipoTarea(idPersonal,
				Constantes.TIPO_TAREA_RECEPCION_DOCUMENTACION);

		if (tareaPendienteBBDD == null) {
			TareaPendiente tareaPendiente = new TareaPendiente();
			tareaPendiente.setDestinatario(personalDAO.getOne(idPersonal));
			tareaPendiente.setFecha(new Date());
			tareaPendiente.setIdTareaPte(idLibroPersonal);
			tareaPendiente.setLeido(Boolean.FALSE);
			tareaPendiente.setOrigen(personalDAO.getOne(idUsuarioRegistrado));
			tareaPendiente.setTablaTareaPte(Constantes.TAREAPTE_FIRMA_DOCUMENTACION);
			tareaPendiente.setTipoTarea(tipoTareaDAO.getOne(Constantes.TIPO_TAREA_RECEPCION_DOCUMENTACION));
			tareaPendienteDAO.save(tareaPendiente);
		}
	}
}
