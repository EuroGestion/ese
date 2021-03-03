package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.DocumentoPersonal;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.Idioma;
import eu.eurogestion.ese.domain.IdiomaPersona;
import eu.eurogestion.ese.domain.NivelIdioma;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.Rol;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.UsuarioJSP;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.DocumentoPersonalDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.IdiomaDAO;
import eu.eurogestion.ese.repository.IdiomaPersonaDAO;
import eu.eurogestion.ese.repository.ImagenesDAO;
import eu.eurogestion.ese.repository.LocalidadDAO;
import eu.eurogestion.ese.repository.NivelIdiomaDAO;
import eu.eurogestion.ese.repository.PaisDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.ProvinciaDAO;
import eu.eurogestion.ese.repository.RolDAO;
import eu.eurogestion.ese.repository.TipoEvidenciaDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class RegistroServiceImpl implements RegistroService {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public CargoDAO cargoDAO;

	@Autowired
	public RolDAO rolDAO;

	@Autowired
	public PaisDAO paisDAO;

	@Autowired
	public ProvinciaDAO provinciaDAO;

	@Autowired
	public LocalidadDAO localidadDAO;

	@Autowired
	public IdiomaDAO idiomaDAO;

	@Autowired
	public NivelIdiomaDAO nivelIdiomaDAO;

	@Autowired
	public IdiomaPersonaDAO idiomaPersonaDAO;
	@Autowired
	public DocumentoPersonalDAO documentoPersonalDAO;

	@Autowired
	public ImagenesDAO imagenesDAO;

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
	public void registroUsuario(UsuarioJSP formularioUser) throws EseException {

		Personal personal = crearPersonalByForm(formularioUser);

		personalDAO.save(personal);

	}

	@Override
	public void modificarRegistroUsuario(UsuarioJSP formularioUser) throws EseException {

		Personal personal = crearPersonalByFormModificar(formularioUser, formularioUser.getIdPersonal());

		personalDAO.save(personal);

	}

	@Override
	public void anadirEvidenciaIdiomaPersona(UsuarioJSP formularioUser, HttpSession sesion) throws EseException {

		Personal personal = personalDAO.getOne(Integer.parseInt(formularioUser.getIdPersonal()));
		Idioma idioma = idiomaDAO.getOne(Integer.parseInt(formularioUser.getIdIdioma()));
		NivelIdioma nivelIdioma = nivelIdiomaDAO.getOne(Integer.parseInt(formularioUser.getIdNivelIdioma()));

		IdiomaPersona idiomaPersona = new IdiomaPersona();
		idiomaPersona.setIdioma(idioma);
		idiomaPersona.setNivelIdioma(nivelIdioma);
		idiomaPersona.setPersonal(personal);
		idiomaPersona.setNotas(formularioUser.getNotasEvidencia());

		if (Constantes.NIVEL_IDIOMA_NATIVO != nivelIdioma.getIdNivelIdioma()) {
			Evidencia evidencia = crearEvidencia(formularioUser.getEvidencia(), Constantes.TIPO_EVIDENCIA_NIVEL_IDIOMA);
			idiomaPersona.setEvidencia82(evidencia);
			idiomaPersona.setFecha(
					Utiles.convertStringToDate(formularioUser.getFechaIdioma(), Constantes.FORMATO_FECHA_PANTALLA));
		}

		idiomaPersonaDAO.save(idiomaPersona);

		if (idiomaPersona.getEvidencia82() != null) {
			blockChainService.uploadDocumento(idiomaPersona.getEvidencia82().getDocumento());
		}

	}

	@Override
	public void anadirDocumentoPersonal(UsuarioJSP formularioUser, HttpSession sesion) throws EseException {

		Personal personal = personalDAO.getOne(Integer.parseInt(formularioUser.getIdPersonal()));

		DocumentoPersonal documentoPersonal = new DocumentoPersonal();
		documentoPersonal.setPersonal(personal);

		Documento documento = crearDocumento(formularioUser.getEvidenciaDocumento(),
				formularioUser.getTituloDocumento(), Utiles.parseDatePantalla(formularioUser.getFechaDocumento()),
				formularioUser.getObservacionesDocumento());
		documentoPersonal.setDocumento(documento);

		documentoPersonalDAO.save(documentoPersonal);

	}

	@Override
	public void activarIdiomaPersona(UsuarioJSP formularioUser) throws EseException {

		IdiomaPersona idiomaPersona = idiomaPersonaDAO.getOne(Integer.parseInt(formularioUser.getIdIdiomaPersona()));
		idiomaPersona.setFechaBaja(null);
		idiomaPersonaDAO.save(idiomaPersona);

	}

	@Override
	public void desactivarIdiomaPersona(UsuarioJSP formularioUser) throws EseException {

		IdiomaPersona idiomaPersona = idiomaPersonaDAO.getOne(Integer.parseInt(formularioUser.getIdIdiomaPersona()));
		idiomaPersona.setFechaBaja(new Date());
		idiomaPersonaDAO.save(idiomaPersona);

	}

	private Evidencia crearEvidencia(MultipartFile ficheroPantalla, Integer idTipoEvidencia) throws EseException {

		byte[] fichero;
		try {
			fichero = ficheroPantalla.getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		TipoEvidencia tipoEvidencia = tipoEvidenciaDAO.getOne(idTipoEvidencia);
		String tipoFichero = ficheroPantalla.getContentType();

		Evidencia evidencia = utilesPDFService.crearEvidencia(tipoEvidencia.getValor(), fichero, md5, tipoEvidencia,
				Utiles.sysdate(), tipoFichero);

		return evidencia;
	}

	private Documento crearDocumento(MultipartFile ficheroPantalla, String titulo, Date fechaDocumento,
			String observaciones) throws EseException {

		byte[] fichero;
		try {
			fichero = ficheroPantalla.getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		String tipoFichero = ficheroPantalla.getContentType();

		return utilesPDFService.crearDocumento(titulo, fichero, md5, fechaDocumento, tipoFichero, observaciones);
	}

	private Personal crearPersonalByForm(UsuarioJSP newUser) throws EseException {

		Personal personal = new Personal();

		if (StringUtils.isNotBlank(newUser.getUsuario())) {

			personal.setNombreUsuario(newUser.getUsuario());
		}
		if (StringUtils.isNotBlank(newUser.getPassword())) {
			personal.setClave(Utiles.cifrarPassword(newUser.getPassword()));
		}
		personal.setFechaAlta(new Date());
		personal.setNombre(newUser.getNombre());
		personal.setApellido1(newUser.getApellido());
		personal.setApellido2(newUser.getApellido2());
		personal.setDocumento(newUser.getDocumento());
		personal.setFechaNac(Utiles.parseDatePantalla(newUser.getFechaNacimiento()));
		personal.setNacionalidad(newUser.getNacionalidad());
		personal.setLugarNacimiento(newUser.getLugarNacimiento());

		if (StringUtils.isNotBlank(newUser.getTelefono())) {
			personal.setTelefono(newUser.getTelefono());
		}
		if (StringUtils.isNotBlank(newUser.getTipoVia())) {
			personal.setTipoVia(newUser.getTipoVia());
		}
		if (StringUtils.isNotBlank(newUser.getVia())) {
			personal.setVia(newUser.getVia());
		}
		if (StringUtils.isNotBlank(newUser.getNumero())) {
			personal.setNumero(newUser.getNumero());
		}
		if (StringUtils.isNotBlank(newUser.getPlanta())) {
			personal.setPlanta(newUser.getPlanta());
		}
		if (StringUtils.isNotBlank(newUser.getPuerta())) {
			personal.setPuerta(newUser.getPuerta());
		}
		if (StringUtils.isNotBlank(newUser.getCp())) {
			personal.setCp(newUser.getCp());
		}
		if (StringUtils.isNotBlank(newUser.getIdCompania())) {
			Compania compania = companiaDAO.getOne(Integer.parseInt(newUser.getIdCompania()));
			personal.setCompania(compania);
		}
		if (StringUtils.isNotBlank(newUser.getIdCargo())) {
			Cargo cargo = cargoDAO.getOne(Integer.parseInt(newUser.getIdCargo()));
			personal.setCargo(cargo);
		}

		if (StringUtils.isNotBlank(newUser.getIdRol())) {
			Rol rol = rolDAO.getOne(Integer.parseInt(newUser.getIdRol()));
			personal.setRol(rol);
		}
		if (StringUtils.isNotBlank(newUser.getLicencia())) {
			personal.setLicencia(newUser.getLicencia());
		}
		if (StringUtils.isNotBlank(newUser.getDocEmpresa())) {
			personal.setDocEmpresa(newUser.getDocEmpresa());
		}
		if (StringUtils.isNotBlank(newUser.getEmail())) {
			personal.setEmail(newUser.getEmail());
		}
		if (StringUtils.isNotBlank(newUser.getIdLocalidad())) {
			personal.setLocalidad(localidadDAO.getOne(Integer.parseInt(newUser.getIdLocalidad())));
		}

		if (StringUtils.isNotBlank(newUser.getIdProvincia())) {
			personal.setProvincia(provinciaDAO.getOne(Integer.parseInt(newUser.getIdProvincia())));
		}
		if (StringUtils.isNotBlank(newUser.getIdPais())) {
			personal.setPais(paisDAO.getOne(Integer.parseInt(newUser.getIdPais())));
		}

		Blob aux;
		if (StringUtils.isNotBlank(newUser.getFoto().getOriginalFilename())) {
			try {
				aux = new SerialBlob(newUser.getFoto().getBytes());
			} catch (SQLException | IOException e) {
				throw new EseException(e.getMessage());
			}
		} else {
			aux = imagenesDAO.getOne(Constantes.IMAGENES_FOTO_PERSONAL_DEFAULT).getValor();
		}

		personal.setFoto(aux);

		return personal;
	}

	private Personal crearPersonalByFormModificar(UsuarioJSP newUser, String idPersonal) throws EseException {

		Personal personal = personalDAO.getOne(Integer.parseInt(idPersonal));

		personal.setNombre(newUser.getNombre());
		personal.setApellido1(newUser.getApellido());
		personal.setApellido2(newUser.getApellido2());
		personal.setDocumento(newUser.getDocumento());
		personal.setFechaNac(Utiles.parseDatePantalla(newUser.getFechaNacimiento()));
		personal.setNacionalidad(newUser.getNacionalidad());
		personal.setLugarNacimiento(newUser.getLugarNacimiento());

		if (StringUtils.isNotBlank(newUser.getTelefono())) {
			personal.setTelefono(newUser.getTelefono());
		}
		if (StringUtils.isNotBlank(newUser.getTipoVia())) {
			personal.setTipoVia(newUser.getTipoVia());
		}
		if (StringUtils.isNotBlank(newUser.getVia())) {
			personal.setVia(newUser.getVia());
		}
		if (StringUtils.isNotBlank(newUser.getNumero())) {
			personal.setNumero(newUser.getNumero());
		}
		if (StringUtils.isNotBlank(newUser.getPlanta())) {
			personal.setPlanta(newUser.getPlanta());
		}
		if (StringUtils.isNotBlank(newUser.getPuerta())) {
			personal.setPuerta(newUser.getPuerta());
		}
		if (StringUtils.isNotBlank(newUser.getCp())) {
			personal.setCp(newUser.getCp());
		}
		if (StringUtils.isNotBlank(newUser.getIdCompania())) {
			Compania compania = companiaDAO.getOne(Integer.parseInt(newUser.getIdCompania()));
			personal.setCompania(compania);
		}
		if (StringUtils.isNotBlank(newUser.getIdCargo())) {
			Cargo cargo = cargoDAO.getOne(Integer.parseInt(newUser.getIdCargo()));
			personal.setCargo(cargo);
		}

		if (StringUtils.isNotBlank(newUser.getIdRol())) {
			Rol rol = rolDAO.getOne(Integer.parseInt(newUser.getIdRol()));
			personal.setRol(rol);
		}
		if (StringUtils.isNotBlank(newUser.getLicencia())) {
			personal.setLicencia(newUser.getLicencia());
		}
		if (StringUtils.isNotBlank(newUser.getDocEmpresa())) {
			personal.setDocEmpresa(newUser.getDocEmpresa());
		}
		if (StringUtils.isNotBlank(newUser.getEmail())) {
			personal.setEmail(newUser.getEmail());
		}
		if (StringUtils.isNotBlank(newUser.getIdLocalidad())) {
			personal.setLocalidad(localidadDAO.getOne(Integer.parseInt(newUser.getIdLocalidad())));
		}

		if (StringUtils.isNotBlank(newUser.getIdProvincia())) {
			personal.setProvincia(provinciaDAO.getOne(Integer.parseInt(newUser.getIdProvincia())));
		}
		if (StringUtils.isNotBlank(newUser.getIdPais())) {
			personal.setPais(paisDAO.getOne(Integer.parseInt(newUser.getIdPais())));
		}
		if (StringUtils.isNotBlank(newUser.getFoto().getOriginalFilename())) {

			Blob aux;
			try {
				aux = new SerialBlob(newUser.getFoto().getBytes());
			} catch (SQLException | IOException e) {
				throw new EseException(e.getMessage());
			}

			personal.setFoto(aux);
		}

		return personal;
	}
}
