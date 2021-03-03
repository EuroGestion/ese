package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Year;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.AuditoriaPasf;
import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.domain.CursoPasf;
import eu.eurogestion.ese.domain.EstadoPasf;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.InspeccionPasf;
import eu.eurogestion.ese.domain.Pasf;
import eu.eurogestion.ese.domain.TipoCurso;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.TipoInspeccion;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.PasfJSP;
import eu.eurogestion.ese.repository.AuditoriaPasfDAO;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CursoPasfDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EstadoPasfDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.InspeccionPasfDAO;
import eu.eurogestion.ese.repository.PasfDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.TipoCursoDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.repository.TipoInspeccionDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class PasfServiceImpl implements PasfService {

	/** Repositories & Services **/

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public PasfDAO pasfDAO;

	@Autowired
	public EstadoPasfDAO estadoPasfDAO;

	@Autowired
	public CargoDAO cargoDAO;

	@Autowired
	public TipoCursoDAO tipoCursoDAO;

	@Autowired
	public TipoInspeccionDAO tipoInspeccionDAO;

	@Autowired
	public CursoPasfDAO cursoPasfDAO;

	@Autowired
	public InspeccionPasfDAO inspeccionPasfDAO;

	@Autowired
	public AuditoriaPasfDAO auditoriaPasfDAO;

	@Autowired
	public PersonalDAO personalDAO;

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
	public void guardarPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException {

		Pasf pasf = new Pasf();
		if (pasfJSP.getIdEstadoPasf() == null) {

			if (Year.now().getValue() > Integer.valueOf(pasfJSP.getAnno())
					|| Year.now().getValue() + Constantes.MAXIMA_PREVISION_PASF < Integer.valueOf(pasfJSP.getAnno())) {
				throw new EseException("ERROR: Año fuera de rango válido (Maximo 5 años a futuro).");
			}

			if (pasfDAO.findByAnno(Integer.valueOf(pasfJSP.getAnno())) != null) {
				throw new EseException("ERROR: Ya existe plan para el año escogido.");
			}

			pasf.setAnno(Integer.valueOf(pasfJSP.getAnno()));

			EstadoPasf estadoPasf = estadoPasfDAO.getOne(Constantes.ESTADO_PASF_CREADO);
			pasf.setEstadoPasf(estadoPasf);

			pasfJSP.setIdEstadoPasf(estadoPasf.getIdEstadoPasf());

			pasfJSP.setDescarrilamiento(Constantes.ZERO);
			pasfJSP.setColision(Constantes.ZERO);
			pasfJSP.setAccidentePasoNivel(Constantes.ZERO);
			pasfJSP.setIncendio(Constantes.ZERO);
			pasfJSP.setArrollamientoVia(Constantes.ZERO);
			pasfJSP.setArrollamientoInterseccion(Constantes.ZERO);
			pasfJSP.setCaidaPersonas(Constantes.ZERO);
			pasfJSP.setSuicidio(Constantes.ZERO);
			pasfJSP.setDescomposicion(Constantes.ZERO);
			pasfJSP.setDetencion(Constantes.ZERO);
			pasfJSP.setInvasionVia(Constantes.ZERO);
			pasfJSP.setIncidentesTransportesExcepcionales(Constantes.ZERO);
			pasfJSP.setRebaseSennal(Constantes.ZERO);
			pasfJSP.setConatoColision(Constantes.ZERO);
			pasfJSP.setEnganchePantografo(Constantes.ZERO);
			pasfJSP.setOtros(Constantes.ZERO);
			pasfJSP.setCursosFormativos(Constantes.ZERO);
			pasfJSP.setRevisionesAptitudPsicofisica(Constantes.ZERO);
			pasfJSP.setIso(Constantes.ZERO);
			pasfJSP.setIssc(Constantes.ZERO);
			pasfJSP.setIsmp(Constantes.ZERO);
			pasfJSP.setIseet(Constantes.ZERO);
			pasfJSP.setCad(Constantes.ZERO);
			pasfJSP.setAuditorias(Constantes.ZERO);

		} else {
			pasf = pasfDAO.getOne(pasfJSP.getIdPasf());

		}

		pasf.setDescarrilamiento(Integer.valueOf(pasfJSP.getDescarrilamiento()));
		pasf.setColision(Integer.valueOf(pasfJSP.getColision()));
		pasf.setAccidentePn(Integer.valueOf(pasfJSP.getAccidentePasoNivel()));
		pasf.setIncendio(Integer.valueOf(pasfJSP.getIncendio()));
		pasf.setArrollamientoVia(Integer.valueOf(pasfJSP.getArrollamientoVia()));
		pasf.setArrollamientoInterseccion(Integer.valueOf(pasfJSP.getArrollamientoInterseccion()));
		pasf.setCaidaPersonas(Integer.valueOf(pasfJSP.getCaidaPersonas()));
		pasf.setSuicidio(Integer.valueOf(pasfJSP.getSuicidio()));
		pasf.setDescomposiciónCarga(Integer.valueOf(pasfJSP.getDescomposicion()));
		pasf.setDetencionTren(Integer.valueOf(pasfJSP.getDetencion()));
		pasf.setInvasionVia(Integer.valueOf(pasfJSP.getInvasionVia()));
		pasf.setIncidenteTe(Integer.valueOf(pasfJSP.getIncidentesTransportesExcepcionales()));
		pasf.setRebaseSenal(Integer.valueOf(pasfJSP.getRebaseSennal()));
		pasf.setConatoColision(Integer.valueOf(pasfJSP.getConatoColision()));
		pasf.setEnganche(Integer.valueOf(pasfJSP.getEnganchePantografo()));
		pasf.setOtros(Integer.valueOf(pasfJSP.getOtros()));
		pasf.setCursos(Integer.valueOf(pasfJSP.getCursosFormativos()));
		pasf.setRevisiones(Integer.valueOf(pasfJSP.getRevisionesAptitudPsicofisica()));
		pasf.setIso(Integer.valueOf(pasfJSP.getIso()));
		pasf.setIssc(Integer.valueOf(pasfJSP.getIssc()));
		pasf.setIsmp(Integer.valueOf(pasfJSP.getIsmp()));
		pasf.setIseet(Integer.valueOf(pasfJSP.getIseet()));
		pasf.setCad(Integer.valueOf(pasfJSP.getCad()));
		pasf.setAuditorias(Integer.valueOf(pasfJSP.getAuditorias()));

		pasf = pasfDAO.save(pasf);

		pasfJSP.setIdPasf(pasf.getIdPasf());
	}

	@Override
	public void guardarCursoPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException {

		CursoPasf cursoPasf = new CursoPasf();

		Cargo cargo = cargoDAO.getOne(Integer.valueOf(pasfJSP.getCursoCargo()));
		cursoPasf.setCargo(cargo);

		TipoCurso categoria = tipoCursoDAO.getOne(Integer.valueOf(pasfJSP.getCursoCategoria()));
		cursoPasf.setCategoria(categoria);

		cursoPasf.setDescripcion(pasfJSP.getCursoDescripcion());
		cursoPasf.setNumAsistentes(Integer.valueOf(pasfJSP.getCursoAsistentes()));
		cursoPasf.setDuracion(Integer.valueOf(pasfJSP.getCursoDuracion()));

		Pasf pasf = pasfDAO.getOne(Integer.valueOf(pasfJSP.getIdPasf()));
		cursoPasf.setPasf(pasf);

		pasfJSP.setCursoCargo(null);
		pasfJSP.setCursoCategoria(null);
		pasfJSP.setCursoDescripcion(null);
		pasfJSP.setCursoAsistentes(null);
		pasfJSP.setCursoDuracion(null);

		cursoPasfDAO.save(cursoPasf);
	}

	@Override
	public void guardarInspeccionPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException {

		InspeccionPasf inspeccionPasf = new InspeccionPasf();

		TipoInspeccion tipoInspeccion = tipoInspeccionDAO.getOne(Integer.valueOf(pasfJSP.getInspeccionTipo()));
		inspeccionPasf.setTipoInspeccion(tipoInspeccion);

		inspeccionPasf.setDescripcion(pasfJSP.getInspeccionDescripcion());
		inspeccionPasf.setNumInspecciones(Integer.valueOf(pasfJSP.getInspeccionNumero()));
		inspeccionPasf.setDuracion(Integer.valueOf(pasfJSP.getInspeccionDuracion()));

		Pasf pasf = pasfDAO.getOne(Integer.valueOf(pasfJSP.getIdPasf()));
		inspeccionPasf.setPasf(pasf);

		pasfJSP.setInspeccionTipo(null);
		pasfJSP.setInspeccionDescripcion(null);
		pasfJSP.setInspeccionNumero(null);
		pasfJSP.setInspeccionDuracion(null);

		inspeccionPasfDAO.save(inspeccionPasf);
	}

	@Override
	public void guardarAuditoriaPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException {

		AuditoriaPasf auditoriaPasf = new AuditoriaPasf();

		auditoriaPasf.setDescripcion(pasfJSP.getAuditoriaDescripcion());
		auditoriaPasf.setDuracion(Integer.valueOf(pasfJSP.getAuditoriaDuracion()));

		Pasf pasf = pasfDAO.getOne(Integer.valueOf(pasfJSP.getIdPasf()));
		auditoriaPasf.setPasf(pasf);

		pasfJSP.setAuditoriaDescripcion(null);
		pasfJSP.setAuditoriaDuracion(null);

		auditoriaPasfDAO.save(auditoriaPasf);
	}

	@Override
	public void aprobarPasf(PasfJSP pasfJSP, HttpSession sesion) throws EseException {

		guardarPasf(pasfJSP, sesion);

		// TODO placeholder, pdf falso.
		byte[] fichero;
		try {
			fichero = utilesPDFService.generarDocumentoPlaAnuSegFer(pasfDAO.getOne(pasfJSP.getIdPasf()));
		} catch (IOException | SQLException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(Constantes.TIPO_EVIDENCIA_PASF);

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), Constantes.TYPE_PDF);

		Pasf pasf = pasfDAO.getOne(pasfJSP.getIdPasf());

		EstadoPasf estadoPasf = estadoPasfDAO.getOne(Constantes.ESTADO_PASF_APROBADO);

		pasf.setEstadoPasf(estadoPasf);
		pasf.setEvidencia(evidencia);

		pasfDAO.save(pasf);

		blockChainService.uploadDocumento(evidencia.getDocumento());

		pasfJSP.setIdEstadoPasf(Constantes.ESTADO_PASF_APROBADO);
	}
}
