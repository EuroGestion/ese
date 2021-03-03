package eu.eurogestion.ese.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import eu.eurogestion.ese.blockchain.DocumentoAtril;
import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.domain.AnexoInformeAnomalia;
import eu.eurogestion.ese.domain.Anomalia;
import eu.eurogestion.ese.domain.AuditoriaPasf;
import eu.eurogestion.ese.domain.Cad;
import eu.eurogestion.ese.domain.Cargo;
import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.CursoPasf;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Evidencia;
import eu.eurogestion.ese.domain.HistoricoMaquinista;
import eu.eurogestion.ese.domain.IdiomaPersona;
import eu.eurogestion.ese.domain.InformeAnomalias;
import eu.eurogestion.ese.domain.InspeccionPasf;
import eu.eurogestion.ese.domain.Iscc;
import eu.eurogestion.ese.domain.Iseet;
import eu.eurogestion.ese.domain.Ismp;
import eu.eurogestion.ese.domain.Iso;
import eu.eurogestion.ese.domain.LibroPersonal;
import eu.eurogestion.ese.domain.Pasf;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.Revocacion;
import eu.eurogestion.ese.domain.Suspension;
import eu.eurogestion.ese.domain.TipoCompania;
import eu.eurogestion.ese.domain.TipoEvidencia;
import eu.eurogestion.ese.domain.Titulo;
import eu.eurogestion.ese.domain.TituloCurso;
import eu.eurogestion.ese.domain.TomaServicio;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.repository.CargoDAO;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.EvidenciaDAO;
import eu.eurogestion.ese.repository.ImagenesDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class UtilesPDFServiceImpl implements UtilesPDFService {

	/** Repositories & Services **/

	@Autowired
	public ImagenesDAO imagenesDAO;

	@Autowired
	public DocumentoDAO documentoDAO;

	@Autowired
	public EvidenciaDAO evidenciaDAO;

	@Autowired
	public CargoDAO cargoDao;

	@Autowired
	public DIDEseService didEseService;

	@Autowired
	public BlockChainService blockChainService;

	/** Functions **/

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarCertificadoComplementarioHabilitacion(eu.eurogestion.ese.domain.
	 * Titulo)
	 */
	@Override
	public byte[] generarCertificadoComplementarioHabilitacion(Titulo titulo) throws IOException, SQLException {

		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_CERT_COMP_HABI_C).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Obtencion y preparacion de los datos
		Personal personal = titulo.getPersonal();
		Compania compania = personal.getCompania();
		TipoCompania tipoCompania = compania.getTipoCompania();

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 0
		if (personal.getLicencia() != null) {
			Integer sizeLicencia = personal.getLicencia().length() <= 13 ? personal.getLicencia().length() : 13;
			for (int i = 0; i < sizeLicencia; i++) {
				fields.get("Personal_licencia_" + i).setValue(String.valueOf(personal.getLicencia().charAt(i)));
			}
		}

		if (personal.getApellido1() != null && personal.getApellido2() != null) {
			String apellidos = personal.getApellido1().concat(" ").concat(personal.getApellido2());
			fields.get("Personal_apellidos").setValue(apellidos);
		}

		if (personal.getNombre() != null) {
			fields.get("Personal_nombre").setValue(personal.getNombre());
		}

		if (personal.getDocEmpresa() != null) {
			Integer sizeDocEmpresa = personal.getDocEmpresa().length() <= 13 ? personal.getDocEmpresa().length() : 13;
			for (int i = 0; i < sizeDocEmpresa; i++) {
				fields.get("Personal_doc_empresa_" + i).setValue(String.valueOf(personal.getDocEmpresa().charAt(i)));
			}
		}

		String validoDesde = Utiles.convertDateToString(titulo.getValidoDesde(), Constantes.FORMATO_FECHA_BLOCKCHAIN);
		for (int i = 0; i < validoDesde.length(); i++) {
			fields.get("Titulo_valido_desde_" + i).setValue(String.valueOf(validoDesde.charAt(i)));
		}

		String fechaCaducidad = Utiles.convertDateToString(titulo.getFechaCaducidad(),
				Constantes.FORMATO_FECHA_BLOCKCHAIN);
		for (int i = 0; i < fechaCaducidad.length(); i++) {
			fields.get("Titulo_fecha_caducidad_" + i).setValue(String.valueOf(fechaCaducidad.charAt(i)));
		}

		fields.get("Compania_nombre").setValue(compania.getNombre());

		if (compania.getTipoVia() != null && compania.getVia() != null && compania.getNumero() != null) {
			String via = compania.getTipoVia().concat(" ")
					.concat(compania.getVia().concat(" ").concat(compania.getNumero().toString()));
			fields.get("Compania_via").setValue(via);
		}

		if (titulo.getNumReferencia() != null) {
			fields.get("Titulo_num_referencia").setValue(titulo.getNumReferencia());
		}

		// 1
		fields.get("Compania_nombre1").setValue(compania.getNombre());

		if (tipoCompania.getValor().equals(Constantes.EMPRESA_FERROVIARIA)) {
			fields.get("Tipo_compania_valor_emp").setValue("true");
		} else {
			fields.get("Tipo_compania_valor_adm").setValue("true");
		}

		if (titulo.getLugarTrabajo() != null) {
			fields.get("Titulo_lugar_trabajo").setValue(titulo.getLugarTrabajo());
		}

		if (compania.getTipoVia() != null && compania.getVia() != null && compania.getNumero() != null) {
			String via1 = compania.getTipoVia().concat(" ")
					.concat(compania.getVia().concat(" ").concat(compania.getNumero().toString()));
			fields.get("Compania_via1").setValue(via1);
		}

		if (compania.getLocalidad() != null && compania.getPais() != null) {
			String localidad = compania.getLocalidad().getNombre().concat(" ").concat(compania.getPais().getNombre());
			fields.get("Compania_localidad").setValue(localidad);
		}

		// 2
		fields.get("Personal_lugar_nacimiento").setValue(personal.getLugarNacimiento());

		if (personal.getFechaNac() != null) {
			String fechaNac = Utiles.convertDateToString(personal.getFechaNac(), Constantes.FORMATO_FECHA_PANTALLA);
			fields.get("Personal_fecha_nac").setValue(fechaNac);
		}

		fields.get("Personal_nacionalidad").setValue(personal.getNacionalidad());

		if (personal.getTipoVia() != null && personal.getVia() != null && personal.getNumero() != null) {
			String via = personal.getTipoVia().concat(" ")
					.concat(personal.getVia().concat(" ").concat(personal.getNumero().toString()));
			fields.get("Personal_via").setValue(via);
		}

		fields.get("Personal_localidad").setValue(personal.getLocalidad().getNombre());

		String provincia = personal.getLocalidad().getNombre().concat(" ").concat(personal.getPais().getNombre());
		fields.get("Personal_provincia").setValue(provincia);

		// 3
		if (titulo.getCategoria() != null) {
			fields.get("Titulo_categoria").setValue(titulo.getCategoria());
		}

		if (titulo.getNotaTipoTitulo() != null) {
			fields.get("Titulo_nota_tipo_titulo").setValue("Notas:".concat(titulo.getNotaTipoTitulo()));
		}

		// 4
		if (titulo.getInfoAdicionalCategoria() != null) {
			fields.get("Titulo_info_adicional_categoria").setValue(titulo.getInfoAdicionalCategoria());
		}

		// 5
		if (titulo.getPersonal() != null && titulo.getPersonal().getListIdiomaPersona() != null) {
			for (int i = 0; i < 4; i++) {
				IdiomaPersona idiomaPersona = titulo.getPersonal().getListIdiomaPersona().get(i);
				if (idiomaPersona.getNivelIdioma() != null
						&& idiomaPersona.getNivelIdioma().getIdNivelIdioma() != Constantes.NIVEL_IDIOMA_NATIVO
						&& idiomaPersona.getFechaBaja() == null) {
					String fecha = "";
					String lengua = "";
					String notas = "";

					fecha = Utiles.convertDateToString(idiomaPersona.getFecha(), Constantes.FORMATO_FECHA_LARGO);
					lengua = idiomaPersona.getIdioma().getNombre() + " " + idiomaPersona.getNivelIdioma().getNombre();

					if (idiomaPersona.getNotas() != null) {
						notas = idiomaPersona.getNotas();
					}

					fields.get("Titulo_idiomas_" + i).setValue(fecha + " " + lengua + " " + notas);
				}
			}
		}

		// 6
		if (titulo.getRestricciones() != null) {
			fields.get("Titulo_restricciones").setValue(titulo.getRestricciones());
		}

		// 7
		if (titulo.getListTituloCursos() != null) {
			Integer i = 0;
			for (TituloCurso tituloCurso : titulo.getListTituloCursos()) {
				if (tituloCurso.getCurso() != null && tituloCurso.getCurso().getTipoCurso() != null
						&& tituloCurso.getCurso().getTipoCurso().getValor() != null && tituloCurso.getCurso()
								.getTipoCurso().getValor().equals(Constantes.TIPO_CURSO_MATERIAL_NOMBRE)) {
					if (tituloCurso.getCurso().getModeloMaterial() != null && i < 6) {
						String fecha = "";
						String descripcion = "";
						String notas = "";

						fecha = Utiles.convertDateToString(tituloCurso.getCurso().getValidoDesde(),
								Constantes.FORMATO_FECHA_LARGO);

						descripcion = tituloCurso.getCurso().getModeloMaterial().getSerie();

						if (tituloCurso.getCurso().getModeloMaterial().getNotas() != null) {
							notas = tituloCurso.getCurso().getModeloMaterial().getNotas();
						}
						fields.get("Modelo_material_serie_" + i).setValue(fecha + " - " + descripcion + " - " + notas);
						i++;
					}
				}
			}
		}

		// 8
		if (titulo.getListTituloCursos() != null) {
			Integer i = 0;
			for (TituloCurso tituloCurso : titulo.getListTituloCursos()) {
				if (tituloCurso.getCurso() != null && tituloCurso.getCurso().getTipoCurso() != null
						&& tituloCurso.getCurso().getTipoCurso().getValor() != null && tituloCurso.getCurso()
								.getTipoCurso().getValor().equals(Constantes.TIPO_CURSO_INFRAESTRUCTURA_NOMBRE)) {
					if (tituloCurso.getCurso().getInfraestructura() != null && i < 76) {
						String fecha = "";
						String extension = "";

						fecha = Utiles.convertDateToString(tituloCurso.getCurso().getValidoDesde(),
								Constantes.FORMATO_FECHA_LARGO);

						extension = tituloCurso.getCurso().getInfraestructura();

						fields.get("Curso_infraestructura_" + i).setValue(fecha + " - " + extension);
						i++;
					}
				}
			}
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoSuspensionTituloHabilitantePersonalFerroviario(eu.eurogestion
	 * .ese.domain.Titulo, java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] generarDocumentoSuspensionTituloHabilitantePersonalFerroviario(Titulo titulo, String nombreUsuario,
			String companiaUsuario) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_MOD_DOC_SUS_TIT_HAB).getValor();
//		String template = "./templates/TemplateModDocSusTitHab.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Obtencion y preparacion de los datos
		Personal personal = titulo.getPersonal();
		Compania compania = personal.getCompania();
		Suspension suspension = null;
		for (Suspension susp : titulo.getListSuspension()) {
			if (susp.getFechaResolucion() == null) {
				suspension = susp;
			}
		}

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// datos personal
		if (personal.getNombre() != null && personal.getApellido1() != null && personal.getApellido2() != null) {
			String nombreCompleto = personal.getNombre().concat(" ").concat(personal.getApellido1()).concat(" ")
					.concat(personal.getApellido2());
			fields.get("personal_nombre").setValue(nombreCompleto);
		}

		fields.get("personal_dni").setValue(personal.getDocumento());

		if (personal.getTelefono() != null) {
			fields.get("personal_telefono").setValue(personal.getTelefono());
		}

		if (compania.getNombre() != null) {
			fields.get("compania_nombre").setValue(compania.getNombre());
		}

		if (compania.getTipoVia() != null && compania.getVia() != null && compania.getNumero() != null
				&& compania.getPlanta() != null && compania.getPuerta() != null && compania.getLocalidad() != null
				&& compania.getPais() != null) {
			String via = compania.getTipoVia().concat(" ").concat(compania.getVia()).concat(" ")
					.concat(compania.getNumero().toString()).concat(" ").concat(compania.getPlanta().toString())
					.concat(compania.getPuerta()).concat(", ")
					.concat(compania.getLocalidad().getNombre().concat(" ").concat(compania.getPais().getNombre()));
			fields.get("compania_direccion").setValue(via);
		}

		if (compania.getTelefono() != null) {
			fields.get("compania_telefono").setValue(compania.getTelefono());
		}

		fields.get("usuario").setValue(nombreUsuario.concat(","));

		fields.get("compania_seguridad").setValue(companiaUsuario.concat(","));

		fields.get("titulo").setValue(titulo.getNombre().concat(","));

		String fechaSuspension = Utiles.convertDateToString(suspension.getFechaSuspension(),
				Constantes.FORMATO_FECHA_LARGO);
		fields.get("fecha_suspension").setValue(fechaSuspension.concat("."));

		if (suspension.getCausaSuspension() != null) {
			fields.get("causa").setValue(suspension.getCausaSuspension().getValor());
		}

		if (compania.getLocalidad() != null && compania.getLocalidad().getNombre() != null) {
			fields.get("lugar_fecha").setValue(compania.getLocalidad().getNombre().concat(" a ")
					.concat(Utiles.convertDateToString(new Date(), Constantes.FORMATO_FECHA_DOCUMENTAL)));
		}

		// firma responsable
		if (suspension.getFirmaResponsable() != null && suspension.getFirmaResponsable().getNombreCompleto() != null
				&& suspension.getFechahoraFirmaResponsable() != null) {
			fields.get("firma_responsable")
					.setValue(suspension.getFirmaResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(suspension.getFechahoraFirmaResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoRevocacionTituloHabilitantePersonalFerroviario(eu.eurogestion
	 * .ese.domain.Titulo, java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] generarDocumentoRevocacionTituloHabilitantePersonalFerroviario(Titulo titulo, String nombreUsuario,
			String companiaUsuario) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_MOD_DOC_REV_TIT_HAB).getValor();
//		String template = "./templates/TemplateModDocRevTitHab.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Obtencion y preparacion de los datos
		Personal personal = titulo.getPersonal();
		Compania compania = personal.getCompania();
		Revocacion revocacion = titulo.getRevocacion();

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		if (personal.getNombre() != null && personal.getApellido1() != null && personal.getApellido2() != null) {
			String nombreCompleto = personal.getNombre().concat(" ").concat(personal.getApellido1()).concat(" ")
					.concat(personal.getApellido2());
			fields.get("personal_nombre").setValue(nombreCompleto);
		}

		fields.get("personal_dni").setValue(personal.getDocumento());

		if (personal.getTelefono() != null) {
			fields.get("personal_telefono").setValue(personal.getTelefono());
		}

		if (compania.getNombre() != null) {
			fields.get("compania_nombre").setValue(compania.getNombre());
		}

		if (compania.getTipoVia() != null && compania.getVia() != null && compania.getNumero() != null
				&& compania.getPlanta() != null && compania.getPuerta() != null && compania.getLocalidad() != null
				&& compania.getPais() != null) {
			String via = compania.getTipoVia().concat(" ").concat(compania.getVia()).concat(" ")
					.concat(compania.getNumero().toString()).concat(" ").concat(compania.getPlanta().toString())
					.concat(compania.getPuerta()).concat(", ")
					.concat(compania.getLocalidad().getNombre().concat(" ").concat(compania.getPais().getNombre()));
			fields.get("compania_direccion").setValue(via);
		}

		if (compania.getTelefono() != null) {
			fields.get("compania_telefono").setValue(compania.getTelefono());
		}

		fields.get("usuario_nombre").setValue(nombreUsuario.concat(","));

		fields.get("usuario_compania").setValue(companiaUsuario.concat(","));

		fields.get("titulo_nombre").setValue(titulo.getNombre().concat(","));

		String fechaSuspension = Utiles.convertDateToString(revocacion.getFechaRevocacion(),
				Constantes.FORMATO_FECHA_LARGO);
		fields.get("revocacion_fecha").setValue(fechaSuspension.concat("."));

		if (revocacion.getCausaRevocacion() != null) {
			fields.get("revocacion_causa").setValue(revocacion.getCausaRevocacion().getValor());
		}

		if (compania.getLocalidad() != null && compania.getLocalidad().getNombre() != null) {
			fields.get("lugar_fecha").setValue(compania.getLocalidad().getNombre().concat(" a ")
					.concat(Utiles.convertDateToString(new Date(), Constantes.FORMATO_FECHA_DOCUMENTAL)));
		}

		// firma responsable
		if (revocacion.getFirmaResponsable() != null && revocacion.getFirmaResponsable().getNombreCompleto() != null
				&& revocacion.getFechahoraFirmaResponsable() != null) {
			fields.get("firma_responsable")
					.setValue(revocacion.getFirmaResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(revocacion.getFechahoraFirmaResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoHabPerOpeTrenROC
	 * (eu.eurogestion.ese.domain.Titulo, java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] generarDocumentoHabPerOpeTrenROC(Titulo titulo, String usuarioNombre, String usuarioCargo)
			throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_MOD_DOC_HAB_PER_OPE_TREN_ROC).getValor();
//		String template = "./templates/TemplateModDocHabPerOpeTrenROCTransfesa.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Obtencion y preparacion de los datos
		Personal personal = titulo.getPersonal();
		List<Curso> listaCursos = new ArrayList<Curso>();
		for (TituloCurso tituloCurso : titulo.getListTituloCursos()) {
			listaCursos.add(tituloCurso.getCurso());
		}

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		if (personal.getApellido1() != null && personal.getApellido2() != null) {
			String apellidos = personal.getApellido1().concat(" ").concat(personal.getApellido2());
			fields.get("personal_apellidos").setValue(apellidos);
		}

		if (personal.getNombre() != null) {
			fields.get("personal_nombre").setValue(personal.getNombre());
		}

		fields.get("personal_documento").setValue(personal.getDocumento());

		if (personal.getNacionalidad() != null) {
			fields.get("personal_nacionalidad").setValue(personal.getNacionalidad());
		}

		if (personal.getFechaNac() != null) {
			String fechaNac = Utiles.convertDateToString(personal.getFechaNac(), Constantes.FORMATO_FECHA_LARGO);
			fields.get("personal_fecha_nac").setValue(fechaNac);
		}

		if (personal.getTipoVia() != null && personal.getVia() != null && personal.getNumero() != null
				&& personal.getPlanta() != null && personal.getPuerta() != null && personal.getLocalidad() != null) {
			String via = personal.getTipoVia().concat(" ").concat(personal.getVia()).concat(" ")
					.concat(personal.getNumero().toString()).concat(" ").concat(personal.getPlanta().toString())
					.concat(personal.getPuerta()).concat(", ").concat(personal.getLocalidad().getNombre());
			fields.get("personal_direccion").setValue(via);
		}

		listaCursos.sort(Comparator.comparing(Curso::getValidoDesde).reversed());
		Integer maxRows = listaCursos.size() < 6 ? listaCursos.size() : 6;
		for (int i = 0; i < maxRows; i++) {
			fields.get("curso_titulo_curso_" + i).setValue(listaCursos.get(i).getTituloCurso());
			fields.get("curso_valido_desde_" + i).setValue(
					Utiles.convertDateToString(listaCursos.get(i).getValidoDesde(), Constantes.FORMATO_FECHA_LARGO));

			fields.get("curso_fecha_caducidad_" + i).setValue(
					Utiles.convertDateToString(listaCursos.get(i).getFechaCaducidad(), Constantes.FORMATO_FECHA_LARGO));
		}

		fields.get("fecha_actual")
				.setValue(Utiles.convertDateToString(new Date(), Constantes.FORMATO_FECHA_DOCUMENTAL));

		fields.get("usuario_nombre").setValue(usuarioNombre);

		fields.get("usuario_cargo").setValue(usuarioCargo);

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoHabPerOpeTrenAOT_AOTM_OVM(eu.eurogestion.ese.domain.Titulo,
	 * java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public byte[] generarDocumentoHabPerOpeTrenAOT_AOTM_OVM(Titulo titulo, String usuarioNombre, String usuarioCargo,
			Integer tipoTitulo) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_MOD_DOC_HAB_PER_OPE_TREN_AOT_AOTM_OVM)
				.getValor();
//		String template = "./templates/TemplateModDocHabPerOpeTrenAOT_AOTM_OVMTransfesa.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Obtencion y preparacion de los datos
		Personal personal = titulo.getPersonal();
		List<Curso> listaCursos = new ArrayList<Curso>();
		for (TituloCurso tituloCurso : titulo.getListTituloCursos()) {
			listaCursos.add(tituloCurso.getCurso());
		}
		listaCursos.sort(Comparator.comparing(Curso::getValidoDesde).reversed());

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		String tituloDoc = "";
		switch (tipoTitulo) {
		case 2:
			tituloDoc = Constantes.TITULO_AOTM;
			break;
		case 4:
			tituloDoc = Constantes.TITULO_OVM;
			break;
		case 5:
			tituloDoc = Constantes.TITULO_AOT;
			break;
		}
		fields.get("documento_titulo").setValue(tituloDoc);

		if (personal.getApellido1() != null && personal.getApellido2() != null) {
			String apellidos = personal.getApellido1().concat(" ").concat(personal.getApellido2());
			fields.get("personal_apellidos").setValue(apellidos);
		}

		if (personal.getNombre() != null) {
			fields.get("personal_nombre").setValue(personal.getNombre());
		}

		fields.get("personal_documento").setValue(personal.getDocumento());

		if (personal.getNacionalidad() != null) {
			fields.get("personal_nacionalidad").setValue(personal.getNacionalidad());
		}

		if (personal.getFechaNac() != null) {
			String fechaNac = Utiles.convertDateToString(personal.getFechaNac(), Constantes.FORMATO_FECHA_LARGO);
			fields.get("personal_fecha_nac").setValue(fechaNac);
		}

		if (personal.getTipoVia() != null && personal.getVia() != null && personal.getNumero() != null
				&& personal.getPlanta() != null && personal.getPuerta() != null && personal.getLocalidad() != null) {
			String via = personal.getTipoVia().concat(" ").concat(personal.getVia()).concat(" ")
					.concat(personal.getNumero().toString()).concat(" ").concat(personal.getPlanta().toString())
					.concat(personal.getPuerta()).concat(", ").concat(personal.getLocalidad().getNombre());
			fields.get("personal_direccion").setValue(via);
		}

		Date ultimoCursoValidoDesde = listaCursos.get(0).getValidoDesde();
		String ultimaFechaValidoDesde = Utiles.convertDateToString(ultimoCursoValidoDesde,
				Constantes.FORMATO_FECHA_LARGO);
		fields.get("ultimo_curso_valido_desde").setValue(ultimaFechaValidoDesde);

		fields.get("curso_valido_desde").setValue(
				Utiles.convertDateToString(listaCursos.get(0).getValidoDesde(), Constantes.FORMATO_FECHA_LARGO));

		fields.get("curso_fecha_caducidad").setValue(
				Utiles.convertDateToString(listaCursos.get(0).getFechaCaducidad(), Constantes.FORMATO_FECHA_LARGO));

		fields.get("revision_psycho_fecha_realizacion").setValue(Utiles
				.convertDateToString(titulo.getRevisionPsico().getFechaRealizacion(), Constantes.FORMATO_FECHA_LARGO));

		fields.get("revision_psycho_valido_desde").setValue(Utiles
				.convertDateToString(titulo.getRevisionPsico().getFechaCaducidad(), Constantes.FORMATO_FECHA_LARGO));

		fields.get("fecha_actual")
				.setValue(Utiles.convertDateToString(new Date(), Constantes.FORMATO_FECHA_DOCUMENTAL));

		fields.get("usuario_nombre").setValue(usuarioNombre);

		fields.get("usuario_cargo").setValue(usuarioCargo);

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoFicInsSegOpe(eu.
	 * eurogestion.ese.domain.Iso)
	 */
	@Override
	public byte[] generarDocumentoFicInsSegOpe(Iso iso) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_INS_SEG_OPE).getValor();
//		String template = "./templates/TemplateFicInsSegOpe.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (iso.getIs().getNumReferencia() != null) {
			fields.get("num_referncia").setValue(iso.getIs().getNumReferencia());
		}
		if (iso.getIs().getFechaInspeccion() != null) {
			String fechaInspeccion = Utiles.convertDateToString(iso.getIs().getFechaInspeccion(),
					Constantes.FORMATO_FECHA_LARGO);
			fields.get("fecha_inspeccion").setValue(fechaInspeccion);
		}
		if (iso.getIs().getLugar() != null) {
			fields.get("lugar_nombre").setValue(iso.getIs().getLugar());
		}
		if (iso.getIs().getInspector() != null && iso.getIs().getInspector().getNombreCompleto() != null) {
			fields.get("inspector_nombre").setValue(iso.getIs().getInspector().getNombreCompleto());
		}

		// 2
		if (iso.getIs().getTren() != null && iso.getIs().getTren().getNumero() != null) {
			fields.get("tren_numero").setValue(iso.getIs().getTren().getNumero());
		}
		if (iso.getIs().getTren() != null && iso.getIs().getTren().getTramo() != null
				&& iso.getIs().getTren().getTramo().getPuntoOrigen() != null
				&& iso.getIs().getTren().getTramo().getPuntoOrigen().getNombre() != null) {
			fields.get("tren_punto_origen").setValue(iso.getIs().getTren().getTramo().getPuntoOrigen().getNombre());
		}
		if (iso.getIs().getTren() != null && iso.getIs().getTren().getTramo() != null
				&& iso.getIs().getTren().getTramo().getPuntoDestino() != null
				&& iso.getIs().getTren().getTramo().getPuntoDestino().getNombre() != null) {
			fields.get("tren_punto_destino").setValue(iso.getIs().getTren().getTramo().getPuntoDestino().getNombre());
		}
		if (iso.getIs() != null && iso.getIs().getTren() != null && iso.getIs().getTren().getObservaciones() != null) {
			fields.get("tren_observaciones").setValue(iso.getIs().getTren().getObservaciones());
		}
		if (iso.getNveLocomotora() != null) {
			fields.get("nveLocomotora").setValue(iso.getNveLocomotora());
		}
		if (iso.getMaquinista() != null && iso.getMaquinista().getNombreCompleto() != null) {
			fields.get("maquinista_nombre").setValue(iso.getMaquinista().getNombreCompleto());
		}
		if (iso.getHoraInicioServicio() != null) {
			String hora = Utiles.convertDateToString(iso.getHoraInicioServicio(),
					Constantes.FORMATO_FECHA_HORAS_PANTALLA);
			fields.get("horaInicioServicio").setValue(hora);
		}
		if (iso.getHoraFinServicio() != null) {
			String hora = Utiles.convertDateToString(iso.getHoraFinServicio(), Constantes.FORMATO_FECHA_HORAS_PANTALLA);
			fields.get("horaFinServicio").setValue(hora);
		}
		if (iso.getTiempoDescanso() != null) {
			fields.get("tiempoDescanso").setValue(iso.getTiempoDescanso());
		}
		if (iso.getObservacionesCirculacionFerroviaria() != null) {
			fields.get("observacionesCirculacionFerroviaria").setValue(iso.getObservacionesCirculacionFerroviaria());
		}

		// 3
		if (iso.getLibroNormasMaquinistaLNMVerificacion() != null
				&& iso.getLibroNormasMaquinistaLNMVerificacion().getValor() != null) {
			fields.get("lnm_estado_valor").setValue(iso.getLibroNormasMaquinistaLNMVerificacion().getValor());
		}
		if (iso.getLibroNormasMaquinistaLNMObservaciones() != null) {
			fields.get("lnm_observaciones").setValue(iso.getLibroNormasMaquinistaLNMObservaciones());
		}
		if (iso.getLibroItinerarioMaquinistaLIMVerificacion() != null
				&& iso.getLibroItinerarioMaquinistaLIMVerificacion().getValor() != null) {
			fields.get("lim_estado_valor").setValue(iso.getLibroItinerarioMaquinistaLIMVerificacion().getValor());
		}
		if (iso.getLibroItinerarioMaquinistaLIMObservaciones() != null) {
			fields.get("lim_observaciones").setValue(iso.getLibroItinerarioMaquinistaLIMObservaciones());
		}
		if (iso.getLlave333Verificacion() != null && iso.getLlave333Verificacion().getValor() != null) {
			fields.get("333_estado_valor").setValue(iso.getLlave333Verificacion().getValor());
		}
		if (iso.getLlave333Observaciones() != null) {
			fields.get("333_observaciones").setValue(iso.getLlave333Observaciones());
		}
		if (iso.getLlaveCuadradilloVerificacion() != null && iso.getLlaveCuadradilloVerificacion().getValor() != null) {
			fields.get("cuadradillo_estado_valor").setValue(iso.getLlaveCuadradilloVerificacion().getValor());
		}
		if (iso.getLlave333Observaciones() != null) {
			fields.get("cuadradillo_observaciones").setValue(iso.getLlave333Observaciones());
		}
		if (iso.getLicenciaOTituloConducccionVerificacion() != null
				&& iso.getLicenciaOTituloConducccionVerificacion().getValor() != null) {
			fields.get("licencia_estado_valor").setValue(iso.getLicenciaOTituloConducccionVerificacion().getValor());
		}
		if (iso.getLicenciaOTituloConducccionObservaciones() != null) {
			fields.get("licencia_observaciones").setValue(iso.getLicenciaOTituloConducccionObservaciones());
		}
		if (iso.getDocReglamentariaVerificacion() != null && iso.getDocReglamentariaVerificacion().getValor() != null) {
			fields.get("doc_regla_estado_valor").setValue(iso.getDocReglamentariaVerificacion().getValor());
		}
		if (iso.getDocReglamentariaObservaciones() != null) {
			fields.get("doc_regla_observaciones").setValue(iso.getDocReglamentariaObservaciones());
		}
		if (iso.getTelefonoMovilVerificacion() != null && iso.getTelefonoMovilVerificacion().getValor() != null) {
			fields.get("movil_estado_valor").setValue(iso.getTelefonoMovilVerificacion().getValor());
		}
		if (iso.getTelefonoMovilObservaciones() != null) {
			fields.get("movil_observaciones").setValue(iso.getTelefonoMovilObservaciones());
		}
		if (iso.getTabletaUOrdenadorPortatilVerificacion() != null
				&& iso.getTabletaUOrdenadorPortatilVerificacion().getValor() != null) {
			fields.get("tableta_estado_valor").setValue(iso.getTabletaUOrdenadorPortatilVerificacion().getValor());
		}
		if (iso.getTabletaUOrdenadorPortatilObservaciones() != null) {
			fields.get("tableta_observaciones").setValue(iso.getTabletaUOrdenadorPortatilObservaciones());
		}
		if (iso.getObservacionesDotacionPersonal() != null) {
			fields.get("observaciones_dot_personal").setValue(iso.getObservacionesDotacionPersonal());
		}

		// 4
		if (iso.getSenalesColaOChapasReflectantesVerificacion() != null
				&& iso.getSenalesColaOChapasReflectantesVerificacion().getValor() != null) {
			fields.get("senales_estado_valor").setValue(iso.getSenalesColaOChapasReflectantesVerificacion().getValor());
		}
		if (iso.getSenalesColaOChapasReflectantesNumero() != null) {
			fields.get("senales_numero").setValue(iso.getSenalesColaOChapasReflectantesNumero().toString());
		}
		if (iso.getSenalesColaOChapasReflectantesObservaciones() != null) {
			fields.get("senales_observaciones").setValue(iso.getSenalesColaOChapasReflectantesObservaciones());
		}
		if (iso.getLinternasLucesBlancoRojoVerificacion() != null
				&& iso.getLinternasLucesBlancoRojoVerificacion().getValor() != null) {
			fields.get("linternas_estado_valor").setValue(iso.getLinternasLucesBlancoRojoVerificacion().getValor());
		}
		if (iso.getLinternasLucesBlancoRojoNumero() != null) {
			fields.get("linternas_numero").setValue(iso.getLinternasLucesBlancoRojoNumero().toString());
		}
		if (iso.getLinternasLucesBlancoRojoObservaciones() != null) {
			fields.get("linternas_observaciones").setValue(iso.getLinternasLucesBlancoRojoObservaciones());
		}
		if (iso.getBanderinesRojosVerificacion() != null && iso.getBanderinesRojosVerificacion().getValor() != null) {
			fields.get("banderines_estado_valor").setValue(iso.getBanderinesRojosVerificacion().getValor());
		}
		if (iso.getBanderinesRojosNumero() != null) {
			fields.get("banderines_numero").setValue(iso.getBanderinesRojosNumero().toString());
		}
		if (iso.getBanderinesRojosObservaciones() != null) {
			fields.get("banderines_observaciones").setValue(iso.getBanderinesRojosObservaciones());
		}
		if (iso.getCalcesAntiderivaVerificacion() != null && iso.getCalcesAntiderivaVerificacion().getValor() != null) {
			fields.get("calces_estado_valor").setValue(iso.getCalcesAntiderivaVerificacion().getValor());
		}
		if (iso.getCalcesAntiderivaNumero() != null) {
			fields.get("calces_numero").setValue(iso.getCalcesAntiderivaNumero().toString());
		}
		if (iso.getCalcesAntiderivaObservaciones() != null) {
			fields.get("calces_observaciones").setValue(iso.getCalcesAntiderivaObservaciones());
		}
		if (iso.getBarrasOUtilesCortocircuitoVerificacion() != null
				&& iso.getBarrasOUtilesCortocircuitoVerificacion().getValor() != null) {
			fields.get("barras_estado_valor").setValue(iso.getBarrasOUtilesCortocircuitoVerificacion().getValor());
		}
		if (iso.getBarrasOUtilesCortocircuitoNumero() != null) {
			fields.get("barras_numero").setValue(iso.getBarrasOUtilesCortocircuitoNumero().toString());
		}
		if (iso.getBarrasOUtilesCortocircuitoObservaciones() != null) {
			fields.get("barras_observaciones").setValue(iso.getBarrasOUtilesCortocircuitoObservaciones());
		}
		if (iso.getJuegoLlavesCerraduraVehiculosVerificacion() != null
				&& iso.getJuegoLlavesCerraduraVehiculosVerificacion().getValor() != null) {
			fields.get("llaves_estado_valor").setValue(iso.getJuegoLlavesCerraduraVehiculosVerificacion().getValor());
		}
		if (iso.getJuegoLlavesCerraduraVehiculosNumero() != null) {
			fields.get("llaves_numero").setValue(iso.getJuegoLlavesCerraduraVehiculosNumero().toString());
		}
		if (iso.getJuegoLlavesCerraduraVehiculosObservaciones() != null) {
			fields.get("llaves_observacones").setValue(iso.getJuegoLlavesCerraduraVehiculosObservaciones());
		}
		if (iso.getManualConduccionMaterialTraccionVerificacion() != null
				&& iso.getManualConduccionMaterialTraccionVerificacion().getValor() != null) {
			fields.get("manual_estado_valor")
					.setValue(iso.getManualConduccionMaterialTraccionVerificacion().getValor());
		}
		if (iso.getManualConduccionMaterialTraccionNumero() != null) {
			fields.get("manual_numero").setValue(iso.getManualConduccionMaterialTraccionNumero().toString());
		}
		if (iso.getManualConduccionMaterialTraccionObservaciones() != null) {
			fields.get("manual_observaciones").setValue(iso.getManualConduccionMaterialTraccionObservaciones());
		}
		if (iso.getLibroAveriasVerificacion() != null && iso.getLibroAveriasVerificacion().getValor() != null) {
			fields.get("libro_estado_valor").setValue(iso.getLibroAveriasVerificacion().getValor());
		}
		if (iso.getLibroAveriasNumero() != null) {
			fields.get("libro_numero").setValue(iso.getLibroAveriasNumero().toString());
		}
		if (iso.getLibroAveriasObservaciones() != null) {
			fields.get("libro_observaciones").setValue(iso.getLibroAveriasObservaciones());
		}
		if (iso.getObservacionesDotacionLocomotora() != null) {
			fields.get("observaciones_dot_locomotora").setValue(iso.getObservacionesDotacionLocomotora());
		}

		// 5
		if (iso.getDestrezaConduccionVerificacion() != null
				&& iso.getDestrezaConduccionVerificacion().getValor() != null) {
			fields.get("destreza_estado_valor").setValue(iso.getDestrezaConduccionVerificacion().getValor());
		}
		if (iso.getDestrezaConduccionObservaciones() != null) {
			fields.get("destreza_observaciones").setValue(iso.getAntesServicioObservaciones());
		}
		if (iso.getCumpleDocumentacionYNormativaVerificacion() != null
				&& iso.getCumpleDocumentacionYNormativaVerificacion().getValor() != null) {
			fields.get("normativa_estado_valor")
					.setValue(iso.getCumpleDocumentacionYNormativaVerificacion().getValor());
		}
		if (iso.getCumpleDocumentacionYNormativaObservaciones() != null) {
			fields.get("normativa_observaciones").setValue(iso.getCumpleDocumentacionYNormativaObservaciones());
		}
		if (iso.getCumpleTiemposMaximosConduccionVerificacion() != null
				&& iso.getCumpleTiemposMaximosConduccionVerificacion().getValor() != null) {
			fields.get("tiempos_estado_valor").setValue(iso.getCumpleTiemposMaximosConduccionVerificacion().getValor());
		}
		if (iso.getCumpleTiemposMaximosConduccionObservaciones() != null) {
			fields.get("tiempos_observaciones").setValue(iso.getCumpleTiemposMaximosConduccionObservaciones());
		}
		if (iso.getCumpleUsoAdecuadoDispositivosVerificacion() != null
				&& iso.getCumpleUsoAdecuadoDispositivosVerificacion().getValor() != null) {
			fields.get("dispositivos_estado_valor")
					.setValue(iso.getCumpleUsoAdecuadoDispositivosVerificacion().getValor());
		}
		if (iso.getCumpleUsoAdecuadoDispositivosObservaciones() != null) {
			fields.get("dispositivos_observaciones").setValue(iso.getCumpleUsoAdecuadoDispositivosObservaciones());
		}
		if (iso.getAntesServicioVerificacion() != null && iso.getAntesServicioVerificacion().getValor() != null) {
			fields.get("antes_estado_valor").setValue(iso.getAntesServicioVerificacion().getValor());
		}
		if (iso.getAntesServicioObservaciones() != null) {
			fields.get("antes_observaciones").setValue(iso.getAntesServicioObservaciones());
		}
		if (iso.getTrasServicioVerificacion() != null && iso.getTrasServicioVerificacion().getValor() != null) {
			fields.get("tras_estado_valor").setValue(iso.getTrasServicioVerificacion().getValor());
		}
		if (iso.getTrasServicioObservaciones() != null) {
			fields.get("tras_observacione").setValue(iso.getTrasServicioObservaciones());
		}
		if (iso.getEnRelevoVerificacion() != null && iso.getEnRelevoVerificacion().getValor() != null) {
			fields.get("relevo_estado_valor").setValue(iso.getEnRelevoVerificacion().getValor());
		}
		if (iso.getEnRelevoObservaciones() != null) {
			fields.get("relevo_observaciones").setValue(iso.getEnRelevoObservaciones());
		}
		if (iso.getSuficienteYCorrectaFormacionOperacionesVerificacion() != null
				&& iso.getSuficienteYCorrectaFormacionOperacionesVerificacion().getValor() != null) {
			fields.get("operaciones_estado_valor")
					.setValue(iso.getSuficienteYCorrectaFormacionOperacionesVerificacion().getValor());
		}
		if (iso.getSuficienteYCorrectaFormacionOperacionesObservaciones() != null) {
			fields.get("operaciones").setValue(iso.getSuficienteYCorrectaFormacionOperacionesObservaciones());
		}
		if (iso.getObservacionesAcompa() != null) {
			fields.get("observaciones_acompa").setValue(iso.getObservacionesAcompa());
		}

		// 6
		if (iso.getMedidasCautelaresAdoptadas() != null) {
			fields.get("medidas_cautelares").setValue(iso.getMedidasCautelaresAdoptadas());
		}

		// 7
		if (iso.getDocumentosAnexos() != null) {
			fields.get("anexos").setValue(iso.getDocumentosAnexos());
		}

		// 8
		if (iso.getFirmaInspector() != null && iso.getFirmaInspector().getNombreCompleto() != null
				&& iso.getFechahoraFirmaInspector() != null) {
			fields.get("firma_ficha_inspector").setValue(
					iso.getFirmaInspector().getNombreCompleto().concat("\n").concat(Utiles.convertDateToString(
							iso.getFechahoraFirmaInspector(), Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}
		if (iso.getFirmaResponsable() != null && iso.getFirmaResponsable().getNombreCompleto() != null
				&& iso.getFechahoraFirmaResponsable() != null) {
			fields.get("firma_ficha_responsable")
					.setValue(iso.getFirmaResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(iso.getFechahoraFirmaResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();

	}

	// TODO probar
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoInfInsSegResAno(
	 * eu.eurogestion.ese.domain.InformeAnomalias)
	 */
	@Override
	public byte[] generarDocumentoInfInsSegResAno(InformeAnomalias informeAnomalias) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_INF_INS_SEG_RES_ANO).getValor();
//		String template = "./templates/TemplateInfInsSegResAno.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Obtencion y preparacion de los datos

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		int pageNum = 1;
		if (informeAnomalias.getIs() != null && informeAnomalias.getIs().getNumReferencia() != null) {
			fields.get("numeroReferenciaInspeccion").setValue(informeAnomalias.getIs().getNumReferencia());
		}
		if (informeAnomalias.getCodigoInforme() != null) {
			fields.get("codigoInforme").setValue(informeAnomalias.getCodigoInforme());
		}
		if (informeAnomalias.getIs() != null && informeAnomalias.getIs().getFechaInspeccion() != null) {
			fields.get("fechaInforme").setValue(Utiles.convertDateToString(
					informeAnomalias.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_LARGO));
		}
		if (informeAnomalias.getIs() != null && informeAnomalias.getIs().getLugar() != null) {
			fields.get("lugarInspeccion").setValue(informeAnomalias.getIs().getLugar());
		}
		if (informeAnomalias.getIs() != null && informeAnomalias.getIs().getInspector() != null
				&& informeAnomalias.getIs().getInspector().getNombreCompleto() != null) {
			fields.get("responsableInspeccion").setValue(informeAnomalias.getIs().getInspector().getNombreCompleto());
		}
		if (informeAnomalias.getDescripcionInspeccion() != null) {
			fields.get("descripcionInspeccion").setValue(informeAnomalias.getDescripcionInspeccion());
		}
		if (informeAnomalias.getObservacionesInspeccion() != null) {
			fields.get("observacionesInspeccion").setValue(informeAnomalias.getObservacionesInspeccion());
		}

		// 2
		if (informeAnomalias.getDescripcionMedidasCautelares() != null) {
			fields.get("descripcionMedidasCautelares").setValue(informeAnomalias.getDescripcionMedidasCautelares());
		}

		// 3
		for (Anomalia anomalia : informeAnomalias.getListAnomalia()) {
			pageNum++;
			if (informeAnomalias.getListAnomalia().indexOf(anomalia) == 0) {
				if (anomalia.getEstadoAnomalia() != null && anomalia.getEstadoAnomalia().getValor() != null) {
					fields.get("estadoAnomaliaValor").setValue(anomalia.getEstadoAnomalia().getValor());
				}
				if (anomalia.getTipoAnomalia() != null && anomalia.getTipoAnomalia().getValor() != null) {
					fields.get("tipoAnomaliaValor").setValue(anomalia.getTipoAnomalia().getValor());
				}
				if (anomalia.getDescripcionSituacion() != null) {
					fields.get("descripcionSituacion").setValue(anomalia.getDescripcionSituacion());
				}
				if (anomalia.getMedidasAdoptadas() != null) {
					fields.get("medidasAdoptadas").setValue(anomalia.getMedidasAdoptadas());
				}
				if (anomalia.getDatosTecnicos() != null) {
					fields.get("datosTecnicos").setValue(anomalia.getDatosTecnicos());
				}
				if (anomalia.getLimitacionesExplotacion() != null) {
					fields.get("limitacionesExplotacion").setValue(anomalia.getLimitacionesExplotacion());
				}
				if (anomalia.getResponsableResolucion() != null
						&& anomalia.getResponsableResolucion().getNombreCompleto() != null) {
					fields.get("responsableResolucionNombreCompleto")
							.setValue(anomalia.getResponsableResolucion().getNombreCompleto());
				}
				if (anomalia.getFechaApertura() != null) {
					String hora = Utiles.convertDateToString(anomalia.getFechaApertura(),
							Constantes.FORMATO_FECHA_LARGO);
					fields.get("fechaApertura").setValue(hora);
				}
				if (anomalia.getFechaResolucion() != null) {
					String hora = Utiles.convertDateToString(anomalia.getFechaResolucion(),
							Constantes.FORMATO_FECHA_LARGO);
					fields.get("fechaResolucion").setValue(hora);
				}
				if (anomalia.getControlSeguimiento() != null) {
					fields.get("controlSeguimiento").setValue(anomalia.getControlSeguimiento());
				}

			} else {
				Blob blobIA = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_INF_INS_SEG_RES_ANO_2).getValor();
				ByteArrayOutputStream baosIA = new ByteArrayOutputStream();
				PdfDocument pdfIA = new PdfDocument(new PdfReader(blobIA.getBinaryStream()), new PdfWriter(baosIA));
				PdfAcroForm formIA = PdfAcroForm.getAcroForm(pdfIA, true);
				Map<String, PdfFormField> fieldsIA = formIA.getFormFields();

				if (anomalia.getEstadoAnomalia() != null && anomalia.getEstadoAnomalia().getValor() != null) {
					fieldsIA.get("estadoAnomaliaValor").setValue(anomalia.getEstadoAnomalia().getValor());
				}
				if (anomalia.getTipoAnomalia() != null && anomalia.getTipoAnomalia().getValor() != null) {
					fieldsIA.get("tipoAnomaliaValor").setValue(anomalia.getTipoAnomalia().getValor());
				}
				if (anomalia.getDescripcionSituacion() != null) {
					fieldsIA.get("descripcionSituacion").setValue(anomalia.getDescripcionSituacion());
				}
				if (anomalia.getMedidasAdoptadas() != null) {
					fieldsIA.get("medidasAdoptadas").setValue(anomalia.getMedidasAdoptadas());
				}
				if (anomalia.getDatosTecnicos() != null) {
					fieldsIA.get("datosTecnicos").setValue(anomalia.getDatosTecnicos());
				}
				if (anomalia.getLimitacionesExplotacion() != null) {
					fieldsIA.get("limitacionesExplotacion").setValue(anomalia.getLimitacionesExplotacion());
				}
				if (anomalia.getResponsableResolucion() != null
						&& anomalia.getResponsableResolucion().getNombreCompleto() != null) {
					fieldsIA.get("responsableResolucionNombreCompleto")
							.setValue(anomalia.getResponsableResolucion().getNombreCompleto());
				}
				if (anomalia.getFechaApertura() != null) {
					String hora = Utiles.convertDateToString(anomalia.getFechaApertura(),
							Constantes.FORMATO_FECHA_LARGO);
					fieldsIA.get("fechaApertura").setValue(hora);
				}
				if (anomalia.getFechaResolucion() != null) {
					String hora = Utiles.convertDateToString(anomalia.getFechaResolucion(),
							Constantes.FORMATO_FECHA_LARGO);
					fieldsIA.get("fechaResolucion").setValue(hora);
				}
				if (anomalia.getControlSeguimiento() != null) {
					fieldsIA.get("controlSeguimiento").setValue(anomalia.getControlSeguimiento());
				}

				formIA.flattenFields();
				pdfIA.close();

				Blob aux;
				aux = new SerialBlob(baosIA.toByteArray());
				PdfDocument pdfIALectura = new PdfDocument(new PdfReader(aux.getBinaryStream()));
				PdfPage pageIA = pdfIALectura.getPage(1);

				pdf.addPage(pageNum, pageIA.copyTo(pdf));
				pdfIALectura.close();
			}
		}

		// 4
		pageNum++;
		if (informeAnomalias.getListAnexoInformeAnomalia() != null) {
			String desAnexos = "";
			for (AnexoInformeAnomalia anexoInformeAnomalia : informeAnomalias.getListAnexoInformeAnomalia()) {
				desAnexos = desAnexos + anexoInformeAnomalia.getDescripcion() + "\n";
			}
			fields.get("anexoAnomaliaDescripcion").setValue(desAnexos);
		}

		// 5
		if (informeAnomalias.getDatosCompplementarios() != null) {
			fields.get("datosComplementarios").setValue(informeAnomalias.getDatosCompplementarios());
		}

		// 6
		if (informeAnomalias.getFirmaInspector() != null
				&& informeAnomalias.getFirmaInspector().getNombreCompleto() != null
				&& informeAnomalias.getFechahoraFirmaInspector() != null) {
			fields.get("firma_inspector")
					.setValue(informeAnomalias.getFirmaInspector().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(informeAnomalias.getFechahoraFirmaInspector(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}
		if (informeAnomalias.getFirmaResponsable() != null
				&& informeAnomalias.getFirmaResponsable().getNombreCompleto() != null
				&& informeAnomalias.getFechahoraFirmaResponsable() != null) {
			fields.get("firma_responsable")
					.setValue(informeAnomalias.getFirmaResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(informeAnomalias.getFechahoraFirmaResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		pdf.addPage(pdf.getPage(1));

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoFicNotDelIntAccIncFer(eu.eurogestion.ese.domain.Accidente)
	 */
	@Override
	public byte[] generarDocumentoFicNotDelIntAccIncFer(Accidente accidente) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_NOT_DEL_INT_ACC_INC_FER).getValor();
//		String template = "./templates/TemplateFicNotDelIntAccIncFer.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTipoVia() != null
				&& accidente.getCompania().getVia() != null && accidente.getCompania().getNumero() != null) {
			String via = accidente.getCompania().getTipoVia().concat(" ").concat(accidente.getCompania().getVia()
					.concat(" ").concat(accidente.getCompania().getNumero().toString()));
			fields.get("compania_via").setValue(via);
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTelefono() != null) {
			fields.get("compania_telefono").setValue(accidente.getCompania().getTelefono());
		}
		if (accidente.getResponsableSeguridad() != null
				&& accidente.getResponsableSeguridad().getNombreCompleto() != null) {
			fields.get("compania_resp_seguridad").setValue(accidente.getResponsableSeguridad().getNombreCompleto());
		}
		if (accidente.getResponsableSeguridad() != null && accidente.getResponsableSeguridad().getEmail() != null) {
			fields.get("compania_email").setValue(accidente.getResponsableSeguridad().getEmail());
		}
		if (accidente.getInvestigadorCiafNombre() != null) {
			fields.get("investigador_ciaf_nombre").setValue(accidente.getInvestigadorCiafNombre());
		}
		if (accidente.getInvestigadorCiafTelefono() != null) {
			fields.get("investigador_ciaf_telefono").setValue(accidente.getInvestigadorCiafTelefono());
		}
		if (accidente.getInvestigadorCiafTelefono() != null) {
			fields.get("investigador_ciaf_email").setValue(accidente.getInvestigadorCiafEmail());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre_2").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getFechaAccidente() != null && accidente.getHoraAccidente() != null) {
			String fecha = Utiles.convertDateToString(accidente.getFechaAccidente(), Constantes.FORMATO_FECHA_LARGO);
			String hora = Utiles.convertDateToString(accidente.getHoraAccidente(),
					Constantes.FORMATO_FECHA_HORAS_PANTALLA);
			fields.get("fecha_accidente").setValue(fecha.concat(" ").concat(hora));
		}
		if (accidente.getLugarAccidente() != null) {
			fields.get("lugar_accidente").setValue(accidente.getLugarAccidente());
		}
		if (accidente.getResponsableSeguridad() != null
				&& accidente.getResponsableSeguridad().getNombreCompleto() != null) {
			fields.get("compania_resp_seguridad_2").setValue(accidente.getResponsableSeguridad().getNombreCompleto());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre_3").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getDelegado() != null && accidente.getDelegado().getNombreCompleto() != null) {
			fields.get("delegado").setValue(accidente.getDelegado().getNombreCompleto());
		}
		if (accidente.getFirmaFichaDelegacionResponsable() != null
				&& accidente.getFirmaFichaDelegacionResponsable().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaFichaDelegacionResponsable() != null) {
			fields.get("firma_ficha_delegacion_responsable")
					.setValue(accidente.getFirmaFichaDelegacionResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaFichaDelegacionResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}
		if (accidente.getFirmaFichaDelegacionDelegado() != null
				&& accidente.getFirmaFichaDelegacionDelegado().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaFichaDelegacionDelegado() != null) {
			fields.get("firma_ficha_delegacion_delegado")
					.setValue(accidente.getFirmaFichaDelegacionDelegado().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaFichaDelegacionDelegado(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoFicAccIncFer(eu.
	 * eurogestion.ese.domain.Accidente)
	 */
	@Override
	public byte[] generarDocumentoFicAccIncFer(Accidente accidente) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_ACC_INC_FER).getValor();
//		String template = "./templates/TemplateFicAccIncFer.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTipoVia() != null
				&& accidente.getCompania().getVia() != null && accidente.getCompania().getNumero() != null) {
			String via = accidente.getCompania().getTipoVia().concat(" ").concat(accidente.getCompania().getVia()
					.concat(" ").concat(accidente.getCompania().getNumero().toString()));
			fields.get("compania_via").setValue(via);
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTelefono() != null) {
			fields.get("compania_telefono").setValue(accidente.getCompania().getTelefono());
		}

		// 2
		if (accidente.getNumeroSuceso() != null) {
			fields.get("numero_suceso").setValue(accidente.getNumeroSuceso());
		}
		if (accidente.getFechaAccidente() != null && accidente.getHoraAccidente() != null) {
			String fecha = Utiles.convertDateToString(accidente.getFechaAccidente(), Constantes.FORMATO_FECHA_LARGO);
			String hora = Utiles.convertDateToString(accidente.getHoraAccidente(),
					Constantes.FORMATO_FECHA_HORAS_PANTALLA);
			fields.get("fecha_accidente").setValue(fecha.concat(" ").concat(hora));
		}
		if (accidente.getLugarAccidente() != null) {
			fields.get("lugar_accidente").setValue(accidente.getLugarAccidente());
		}
		if (accidente.getTipoAccidente() != null && accidente.getTipoAccidente().getValor() != null) {
			fields.get("tipo_accidente_valor").setValue(accidente.getTipoAccidente().getValor());
		}
		if (accidente.getDescripcion() != null) {
			fields.get("descripcion").setValue(accidente.getDescripcion());
		}
		if (accidente.getCondicionesAtmosfericas() != null) {
			fields.get("condiciones_atmosfericas").setValue(accidente.getCondicionesAtmosfericas());
		}
		if (accidente.getCirculacionesImplicadas() != null) {
			fields.get("circulaciones_implicadas").setValue(accidente.getCirculacionesImplicadas());
		}
		if (accidente.getComposicionTrenes() != null) {
			fields.get("composicion_trenes").setValue(accidente.getComposicionTrenes());
		}
		if (accidente.getHeridosTren() != null) {
			fields.get("heridos_tren").setValue(accidente.getHeridosTren().toString());
		}
		if (accidente.getFallecidosTren() != null) {
			fields.get("fallecidos_tren").setValue(accidente.getFallecidosTren().toString());
		}
		if (accidente.getHeridosAjenos() != null) {
			fields.get("heridos_ajenos").setValue(accidente.getHeridosAjenos().toString());
		}
		if (accidente.getFallecidosAjenos() != null) {
			fields.get("fallecidos_ajenos").setValue(accidente.getFallecidosAjenos().toString());
		}
		if (accidente.getFallecidosTren() != null && accidente.getFallecidosAjenos() != null) {
			Integer total = accidente.getFallecidosTren() + accidente.getFallecidosAjenos();
			fields.get("fallecidos_total").setValue(total.toString());
		}
		if (accidente.getHeridosTren() != null && accidente.getHeridosAjenos() != null) {
			Integer total = accidente.getHeridosTren() + accidente.getHeridosAjenos();
			fields.get("heridos_total").setValue(total.toString());
		}
		if (accidente.getDanosMaterialesMaterial() != null) {
			fields.get("danos_materiales_material").setValue(accidente.getDanosMaterialesMaterial());
		}
		if (accidente.getDanosMaterialesInfraestructura() != null) {
			fields.get("danos_materiales_infraestructura").setValue(accidente.getDanosMaterialesInfraestructura());
		}
		if (accidente.getPerturbacionesServicio() != null) {
			fields.get("perturbaciones_servicio").setValue(accidente.getPerturbacionesServicio());
		}
		if (accidente.getPrevisionesRestablecimiento() != null) {
			fields.get("previsiones_restablecimiento").setValue(accidente.getPrevisionesRestablecimiento());
		}
		if (accidente.getPrimerasMedidas() != null) {
			fields.get("primeras_medidas").setValue(accidente.getPrimerasMedidas());
		}
		if (accidente.getNotificacionesEfectuadas() != null) {
			fields.get("notificaciones_efectuadas").setValue(accidente.getNotificacionesEfectuadas());
		}

		// 3
		if (accidente.getObservacionesFichaAccidente() != null) {
			fields.get("observaciones_ficha_accidente").setValue(accidente.getObservacionesFichaAccidente());
		}
		if (accidente.getFirmaFichaAccidente() != null && accidente.getFirmaFichaAccidente().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaFichaAccidente() != null) {
			fields.get("firma_ficha_accidente")
					.setValue(accidente.getFirmaFichaAccidente().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaFichaAccidente(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoFicEstInvRecDatCooCasAccIncFerr(eu.eurogestion.ese.domain.
	 * Accidente)
	 */
	@Override
	public byte[] generarDocumentoFicEstInvRecDatCooCasAccIncFerr(Accidente accidente)
			throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_EST_INV_REC_DAT_COO_CAS_ACC_INC_FERR)
				.getValor();
//		String template = "./templates/TemplateFicEstInvRecDatCooCasAccIncFerr.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTipoVia() != null
				&& accidente.getCompania().getVia() != null && accidente.getCompania().getNumero() != null) {
			String via = accidente.getCompania().getTipoVia().concat(" ").concat(accidente.getCompania().getVia()
					.concat(" ").concat(accidente.getCompania().getNumero().toString()));
			fields.get("compania_via").setValue(via);
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTelefono() != null) {
			fields.get("compania_telefono").setValue(accidente.getCompania().getTelefono());
		}

		// 2
		if (accidente.getNumeroSuceso() != null) {
			fields.get("numero_suceso").setValue(accidente.getNumeroSuceso());
		}
		if (accidente.getFechaAccidente() != null && accidente.getHoraAccidente() != null) {
			String fecha = Utiles.convertDateToString(accidente.getFechaAccidente(), Constantes.FORMATO_FECHA_LARGO);
			String hora = Utiles.convertDateToString(accidente.getHoraAccidente(),
					Constantes.FORMATO_FECHA_HORAS_PANTALLA);
			fields.get("fecha_accidente").setValue(fecha.concat(" ").concat(hora));
		}
		if (accidente.getLugarAccidente() != null) {
			fields.get("lugar_accidente").setValue(accidente.getLugarAccidente());
		}
		if (accidente.getCirculacionesImplicadas() != null) {
			fields.get("circulaciones_implicadas").setValue(accidente.getCirculacionesImplicadas());
		}
		if (accidente.getComposicionTrenes() != null) {
			fields.get("composicion_trenes").setValue(accidente.getComposicionTrenes());
		}
		if (accidente.getDescripcion() != null) {
			fields.get("descripcion").setValue(accidente.getDescripcion());
		}

		// 3
		if (accidente.getResponsableSeguridad() != null && accidente.getResponsableSeguridad().getCompania() != null
				&& accidente.getResponsableSeguridad().getCompania().getNombre() != null) {
			fields.get("compania_nombre_2").setValue(accidente.getResponsableSeguridad().getCompania().getNombre());
		}
		if (accidente.getResponsableSeguridad() != null
				&& accidente.getResponsableSeguridad().getNombreCompleto() != null) {
			fields.get("compania_resp_seguridad").setValue(accidente.getResponsableSeguridad().getNombreCompleto());
		}
		if (accidente.getResponsableSeguridad() != null && accidente.getResponsableSeguridad().getTelefono() != null) {
			fields.get("compania_resp_seguridad_telefono").setValue(accidente.getResponsableSeguridad().getTelefono());
		}
		if (accidente.getResponsableSeguridad() != null && accidente.getResponsableSeguridad().getEmail() != null) {
			fields.get("compania_resp_seguridad_email").setValue(accidente.getResponsableSeguridad().getEmail());
		}

		// 4
		if (accidente.getInvestigadorCiafNombre() != null) {
			fields.get("ciaf_nombre").setValue(accidente.getInvestigadorCiafNombre());
		}
		if (accidente.getInvestigadorCiafTelefono() != null) {
			fields.get("ciaf_telefono").setValue(accidente.getInvestigadorCiafTelefono());
		}
		if (accidente.getInvestigadorCiafEmail() != null) {
			fields.get("ciaf_email").setValue(accidente.getInvestigadorCiafEmail());
		}

		// 5
		if (accidente.getObservacionesIntervieneCiaf() != null) {
			fields.get("observaciones_interviene_ciaf").setValue(accidente.getObservacionesIntervieneCiaf());
		}

		if (accidente.getFirmaFichaEstructura() != null
				&& accidente.getFirmaFichaEstructura().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaFichaEstructura() != null) {
			fields.get("firma_ficha_estructura")
					.setValue(accidente.getFirmaFichaEstructura().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaFichaEstructura(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoFicComSugInfProAccIncFerrCIAF(eu.eurogestion.ese.domain.
	 * Accidente)
	 */
	@Override
	public byte[] generarDocumentoFicComSugInfProAccIncFerrCIAF(Accidente accidente) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_COM_SUG_INF_PRO_ACC_INC_FERR_CIAF)
				.getValor();
//		String template = "./templates/TemplateFicComSugInfProAccIncFerrCIAF.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTipoVia() != null
				&& accidente.getCompania().getVia() != null && accidente.getCompania().getNumero() != null) {
			String via = accidente.getCompania().getTipoVia().concat(" ").concat(accidente.getCompania().getVia()
					.concat(" ").concat(accidente.getCompania().getNumero().toString()));
			fields.get("compania_via").setValue(via);
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTelefono() != null) {
			fields.get("compania_telefono").setValue(accidente.getCompania().getTelefono());
		}
		if (accidente.getResponsableSeguridad() != null && accidente.getResponsableSeguridad().getEmail() != null) {
			fields.get("compania_email").setValue(accidente.getResponsableSeguridad().getEmail());
		}
		if (accidente.getResponsableSeguridad() != null
				&& accidente.getResponsableSeguridad().getNombreCompleto() != null) {
			fields.get("compania_resp_seguridad").setValue(accidente.getResponsableSeguridad().getNombreCompleto());
		}

		// 2
		if (accidente.getNumeroSuceso() != null) {
			fields.get("numero_suceso").setValue(accidente.getNumeroSuceso());
		}
		if (accidente.getFechaAccidente() != null && accidente.getHoraAccidente() != null) {
			String fecha = Utiles.convertDateToString(accidente.getFechaAccidente(), Constantes.FORMATO_FECHA_LARGO);
			String hora = Utiles.convertDateToString(accidente.getHoraAccidente(),
					Constantes.FORMATO_FECHA_HORAS_PANTALLA);
			fields.get("fecha_accidente").setValue(fecha.concat(" ").concat(hora));
		}
		if (accidente.getLugarAccidente() != null) {
			fields.get("lugar_accidente").setValue(accidente.getLugarAccidente());
		}
		if (accidente.getTipoAccidente() != null && accidente.getTipoAccidente().getValor() != null) {
			fields.get("tipo_accidente").setValue(accidente.getTipoAccidente().getValor());
		}

		// 3
		if (accidente.getNumIdInfCiaf() != null) {
			fields.get("num_id_inf_ciaf").setValue(accidente.getNumIdInfCiaf());
		}
		if (accidente.getFechaInfCiaf() != null) {
			String fecha = Utiles.convertDateToString(accidente.getFechaInfCiaf(), Constantes.FORMATO_FECHA_LARGO);
			fields.get("fecha_inf_ciaf").setValue(fecha);
		}

		// 4
		if (accidente.getComentariosAlInfCiaf() != null) {
			fields.get("comentarios_al_inf_ciaf").setValue(accidente.getComentariosAlInfCiaf());
		}

		// 5
		if (accidente.getObservacionesAlInfCiaf() != null) {
			fields.get("observaciones_al_inf_ciaf").setValue(accidente.getObservacionesAlInfCiaf());
		}
		if (accidente.getFirmaFichaComentariosCiaf() != null
				&& accidente.getFirmaFichaComentariosCiaf().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaFichaComentariosCiaf() != null) {
			fields.get("firma_ficha_comentarios_ciaf")
					.setValue(accidente.getFirmaFichaComentariosCiaf().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaFichaComentariosCiaf(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoFicMedAdop(eu.
	 * eurogestion.ese.domain.Accidente)
	 */
	@Override
	public byte[] generarDocumentoFicMedAdop(Accidente accidente) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_MED_ADOP).getValor();
//		String template = "./templates/TemplateFicMedAdop.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTipoVia() != null
				&& accidente.getCompania().getVia() != null && accidente.getCompania().getNumero() != null) {
			String via = accidente.getCompania().getTipoVia().concat(" ").concat(accidente.getCompania().getVia()
					.concat(" ").concat(accidente.getCompania().getNumero().toString()));
			fields.get("compania_via").setValue(via);
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTelefono() != null) {
			fields.get("compania_telefono").setValue(accidente.getCompania().getTelefono());
		}
		if (accidente.getResponsableSeguridad() != null && accidente.getResponsableSeguridad().getEmail() != null) {
			fields.get("compania_email").setValue(accidente.getResponsableSeguridad().getEmail());
		}
		if (accidente.getResponsableSeguridad() != null
				&& accidente.getResponsableSeguridad().getNombreCompleto() != null) {
			fields.get("compania_resp_seguridad").setValue(accidente.getResponsableSeguridad().getNombreCompleto());
		}

		// 2
		if (accidente.getFichaMedidasAno() != null) {
			fields.get("ficha_medidas_ano").setValue(accidente.getFichaMedidasAno());
		}
		if (accidente.getFichaMedidasRefInforme() != null) {
			fields.get("ficha_medidas_ref_informe").setValue(accidente.getFichaMedidasRefInforme());
		}

		// 3
		if (accidente.getFichaMedidasAdoptadas() != null) {
			fields.get("ficha_medidas_adoptadas").setValue(accidente.getFichaMedidasAdoptadas());
		}

		// 4
		if (accidente.getFichaMedidasProyectadas() != null) {
			fields.get("ficha_medidas_proyectadas").setValue(accidente.getFichaMedidasProyectadas());
		}

		// 5
		if (accidente.getFichaMedidasObservaciones() != null) {
			fields.get("ficha_medidas_observaciones").setValue(accidente.getFichaMedidasObservaciones());
		}
		if (accidente.getFirmaFichaMedidas() != null && accidente.getFirmaFichaMedidas().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaFichaMedidas() != null) {
			fields.get("firma_ficha_medidas")
					.setValue(accidente.getFirmaFichaMedidas().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaFichaMedidas(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoInfInvAccIncFerr
	 * (eu.eurogestion.ese.domain.Accidente)
	 */
	@Override
	public byte[] generarDocumentoInfInvAccIncFerr(Accidente accidente) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_INF_INV_ACC_INC_FERR).getValor();
//		String template = "./templates/TemplateInfInvAccIncFerr.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (accidente.getNumeroReferenciaFinal() != null) {
			fields.get("numero_referencia_final").setValue(accidente.getNumeroReferenciaFinal());
		}

		if (accidente.getFechaInformeFinal() != null) {
			fields.get("fecha_informe_final").setValue(
					Utiles.convertDateToString(accidente.getFechaInformeFinal(), Constantes.FORMATO_FECHA_CORTO));
		}

		// 2
		if (accidente.getCompania() != null && accidente.getCompania().getNombre() != null) {
			fields.get("compania_nombre").setValue(accidente.getCompania().getNombre());
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTipoVia() != null
				&& accidente.getCompania().getVia() != null && accidente.getCompania().getNumero() != null) {
			String via = accidente.getCompania().getTipoVia().concat(" ").concat(accidente.getCompania().getVia()
					.concat(" ").concat(accidente.getCompania().getNumero().toString()));
			fields.get("compania_via").setValue(via);
		}
		if (accidente.getCompania() != null && accidente.getCompania().getTelefono() != null) {
			fields.get("compania_telefono").setValue(accidente.getCompania().getTelefono());
		}
		if (accidente.getResponsableSeguridad() != null
				&& accidente.getResponsableSeguridad().getNombreCompleto() != null) {
			fields.get("compania_resp_seguridad").setValue(accidente.getResponsableSeguridad().getNombreCompleto());
		}
		if (accidente.getResponsableSeguridad() != null && accidente.getResponsableSeguridad().getEmail() != null) {
			fields.get("compania_email").setValue(accidente.getResponsableSeguridad().getEmail());
		}

		// 3
		if (accidente.getNumeroSuceso() != null) {
			fields.get("numero_suceso").setValue(accidente.getNumeroSuceso());
		}
		if (accidente.getFechaAccidente() != null) {
			fields.get("fecha_accidente").setValue(
					Utiles.convertDateToString(accidente.getFechaAccidente(), Constantes.FORMATO_FECHA_CORTO));
		}
		if (accidente.getLugarAccidente() != null) {
			fields.get("lugar_accidente").setValue(accidente.getLugarAccidente());
		}

		// 4
		if (accidente.getHechosSuceso() != null) {
			fields.get("hechos_suceso").setValue(accidente.getHechosSuceso());
		}
		if (accidente.getHechosCircunstancias() != null) {
			fields.get("hechos_circunstancias").setValue(accidente.getHechosCircunstancias());
		}
		if (accidente.getHechosDanos() != null) {
			fields.get("hechos_danos").setValue(accidente.getHechosDanos());
		}
		if (accidente.getHechosCircunstanciasExternas() != null) {
			fields.get("hechos_circunstancias_externas").setValue(accidente.getHechosCircunstanciasExternas());
		}

		// 5
		if (accidente.getResumenDeclaraciones() != null) {
			fields.get("resumen_declaraciones").setValue(accidente.getResumenDeclaraciones());
		}
		if (accidente.getResumenSgs() != null) {
			fields.get("resumen_sgs").setValue(accidente.getResumenSgs());
		}
		if (accidente.getResumenNormativa() != null) {
			fields.get("resumen_normativa").setValue(accidente.getResumenNormativa());
		}
		if (accidente.getResumenFuncionamiento() != null) {
			fields.get("resumen_funcionamiento").setValue(accidente.getResumenFuncionamiento());
		}
		if (accidente.getResumenDocumentacion() != null) {
			fields.get("resumen_documentacion").setValue(accidente.getResumenDocumentacion());
		}
		if (accidente.getResumenInterfaz() != null) {
			fields.get("resumen_interfaz").setValue(accidente.getResumenInterfaz());
		}
		if (accidente.getResumenOtros() != null) {
			fields.get("resumen_otros").setValue(accidente.getResumenOtros());
		}

		// 6
		if (accidente.getAnalisisDescripcion() != null) {
			fields.get("analisis_descripcion").setValue(accidente.getAnalisisDescripcion());
		}
		if (accidente.getAnalisisDeliberacion() != null) {
			fields.get("analisis_deliberacion").setValue(accidente.getAnalisisDeliberacion());
		}
		if (accidente.getAnalisisConclusiones() != null) {
			fields.get("analisis_conclusiones").setValue(accidente.getAnalisisConclusiones());
		}
		if (accidente.getAnalisisObservaciones() != null) {
			fields.get("analisis_observaciones").setValue(accidente.getAnalisisObservaciones());
		}

		// 7
		if (accidente.getMedidasAdoptadas() != null) {
			fields.get("medidas_adoptadas").setValue(accidente.getMedidasAdoptadas());
		}

		// 8
		if (accidente.getRecomendaciones() != null) {
			fields.get("recomendaciones").setValue(accidente.getRecomendaciones());
		}

		// 9
		if (accidente.getDatosComplementarios() != null) {
			fields.get("datos_complementarios").setValue(accidente.getDatosComplementarios());
		}
		if (accidente.getFirmaInformeFinalResponsable() != null
				&& accidente.getFirmaInformeFinalResponsable().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaInformeFinalResponsable() != null) {
			fields.get("firma_informe_final_responsable")
					.setValue(accidente.getFirmaInformeFinalResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaInformeFinalResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}
		if (accidente.getFirmaInformeFinalDelegado() != null
				&& accidente.getFirmaInformeFinalDelegado().getNombreCompleto() != null
				&& accidente.getFechahoraFirmaInformeFinalDelegado() != null) {
			fields.get("firma_informe_final_delegado")
					.setValue(accidente.getFirmaInformeFinalDelegado().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(accidente.getFechahoraFirmaInformeFinalDelegado(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoAutDetCAD(eu.
	 * eurogestion.ese.domain.Cad)
	 */
	@Override
	public byte[] generarDocumentoAutDetCAD(Cad cad) throws IOException, SQLException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_AUT_DET_CAD).getValor();
//		String template = "./templates/TemplateAutDetCAD.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (cad.getPersonalControlado() != null && cad.getPersonalControlado().getNombreCompleto() != null) {
			fields.get("personal_controlado_nombre").setValue(cad.getPersonalControlado().getNombreCompleto());
		}
		if (cad.getConsentimiento() != null) {
			fields.get("personal_consentimiento").setValue(cad.getConsentimiento() ? "SI" : "NO");
		}
		if (cad.getMedicamentos() != null) {
			fields.get("declaracion").setValue(cad.getMedicamentos());
		}
		if (cad.getPersonalControlado() != null && cad.getPersonalControlado().getNombreCompleto() != null
				&& cad.getFechahoraFirmaPersonal() != null) {
			fields.get("firma").setValue(cad.getPersonalControlado().getNombreCompleto().concat("/n").concat(Utiles
					.convertDateToString(cad.getFechahoraFirmaPersonal(), Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}
		if (cad.getObservacionesAutorizacion() != null) {
			fields.get("observaciones_autorizacion").setValue(cad.getObservacionesAutorizacion());
		}
		if (cad.getIs().getCreador() != null && cad.getIs().getCreador().getCompania() != null
				&& cad.getIs().getCreador().getCompania().getNombre() != null) {
			fields.get("empresa_nombre").setValue(cad.getIs().getCreador().getCompania().getNombre());
		}
		if (cad.getIs().getCreador() != null && cad.getIs().getCreador().getCompania() != null
				&& cad.getIs().getCreador().getCompania().getTipoVia() != null
				&& cad.getIs().getCreador().getCompania().getVia() != null
				&& cad.getIs().getCreador().getCompania().getNumero() != null
				&& cad.getIs().getCreador().getCompania().getPlanta() != null
				&& cad.getIs().getCreador().getCompania().getPuerta() != null
				&& cad.getIs().getCreador().getCompania().getLocalidad() != null
				&& cad.getIs().getCreador().getCompania().getPais() != null) {
			String via = cad.getIs().getCreador().getCompania().getTipoVia().concat(" ")
					.concat(cad.getIs().getCreador().getCompania().getVia()).concat(" ")
					.concat(cad.getIs().getCreador().getCompania().getNumero().toString()).concat(" ")
					.concat(cad.getIs().getCreador().getCompania().getPlanta().toString())
					.concat(cad.getIs().getCreador().getCompania().getPuerta()).concat(", ")
					.concat(cad.getIs().getCreador().getCompania().getLocalidad().getNombre().concat(" ")
							.concat(cad.getIs().getCreador().getCompania().getPais().getNombre()));
			fields.get("empresa_direccion").setValue(via);
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoFichContDetCAD(
	 * eu.eurogestion.ese.domain.Cad)
	 */
	@Override
	public byte[] generarDocumentoFichContDetCAD(Cad cad) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FICH_CONT_DET_CAD).getValor();
//		String template = "./templates/TemplateFichContDetCAD.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (cad.getIs().getNumReferencia() != null) {
			fields.get("num_referencia").setValue(cad.getIs().getNumReferencia());
		}
		if (cad.getIs().getFechaInspeccion() != null) {
			String fecha = Utiles.convertDateToString(cad.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_LARGO);
			fields.get("fecha_inspeccion").setValue(fecha);
		}
		if (cad.getTipoCad() != null && cad.getTipoCad().getValor() != null) {
			fields.get("tipo_cad").setValue(cad.getTipoCad().getValor());
		}
		if (cad.getIs().getLugar() != null) {
			fields.get("lugar").setValue(cad.getIs().getLugar());
		}
		if (cad.getIs().getInspector() != null && cad.getIs().getInspector().getNombreCompleto() != null) {
			fields.get("inspector_nombre").setValue(cad.getIs().getInspector().getNombreCompleto());
		}
		if (cad.getIs().getInspector() != null && cad.getIs().getInspector().getDocumento() != null) {
			fields.get("inspector_doc").setValue(cad.getIs().getInspector().getDocumento());
		}

		// 2
		if (cad.getPersonalControlado() != null && cad.getPersonalControlado().getNombreCompleto() != null) {
			fields.get("personal_controlado_nombre").setValue(cad.getPersonalControlado().getNombreCompleto());
		}
		if (cad.getPersonalControlado() != null && cad.getPersonalControlado().getCargo() != null
				&& cad.getPersonalControlado().getCargo().getNombre() != null) {
			fields.get("personal_controlado_cargo").setValue(cad.getPersonalControlado().getCargo().getNombre());
		}
		if (cad.getPersonalControlado() != null && cad.getPersonalControlado().getDocumento() != null) {
			fields.get("personal_controlado_doc").setValue(cad.getPersonalControlado().getDocumento());
		}

		// 3
		if (cad.getDelegado1() != null && cad.getDelegado1().getNombreCompleto() != null) {
			fields.get("personal_delegado1_nombre").setValue(cad.getDelegado1().getNombreCompleto());
		}
		if (cad.getDelegado1() != null && cad.getDelegado1().getCargo() != null
				&& cad.getDelegado1().getCargo().getNombre() != null) {
			fields.get("personal_delegado1_cargo").setValue(cad.getDelegado1().getCargo().getNombre());
		}
		if (cad.getDelegado1() != null && cad.getDelegado1().getDocumento() != null) {
			fields.get("personal_delegado1_doc").setValue(cad.getDelegado1().getDocumento());
		}
		if (cad.getDelegado1() != null && cad.getDelegado1().getCompania() != null
				&& cad.getDelegado1().getCompania().getNombre() != null) {
			fields.get("personal_delegado1_compania_nombre").setValue(cad.getDelegado1().getCompania().getNombre());
		}
		if (cad.getDelegado1() != null && cad.getDelegado1().getLocalidad() != null
				&& cad.getDelegado1().getLocalidad().getNombre() != null) {
			fields.get("personal_delegado1_residencia").setValue(cad.getDelegado1().getLocalidad().getNombre());
		}
		if (cad.getDelegado2() != null && cad.getDelegado2().getNombreCompleto() != null) {
			fields.get("personal_delegado2_nombre").setValue(cad.getDelegado2().getNombreCompleto());
		}
		if (cad.getDelegado2() != null && cad.getDelegado2().getCargo() != null
				&& cad.getDelegado2().getCargo().getNombre() != null) {
			fields.get("personal_delegado2_cargo").setValue(cad.getDelegado2().getCargo().getNombre());
		}
		if (cad.getDelegado2() != null && cad.getDelegado2().getDocumento() != null) {
			fields.get("personal_delegado2_doc").setValue(cad.getDelegado2().getDocumento());
		}
		if (cad.getDelegado2() != null && cad.getDelegado2().getCompania() != null
				&& cad.getDelegado2().getCompania().getNombre() != null) {
			fields.get("personal_delegado2_compania_nombre").setValue(cad.getDelegado2().getCompania().getNombre());
		}
		if (cad.getDelegado2() != null && cad.getDelegado2().getLocalidad() != null
				&& cad.getDelegado2().getLocalidad().getNombre() != null) {
			fields.get("personal_delegado2_residencia").setValue(cad.getDelegado2().getLocalidad().getNombre());
		}

		// 4
		if (cad.getCentroMedico() != null && cad.getCentroMedico().getNombre() != null) {
			fields.get("centro_medico_nombre").setValue(cad.getCentroMedico().getNombre());
		}

		// 5
		if (cad.getObservaciones() != null) {
			fields.get("observaciones").setValue(cad.getObservaciones());
		}

		// 6
		if (cad.getEtilometroPr2Resultado() != null && cad.getEtilometroPr2Resultado().getValor() != null) {
			fields.get("etilometro_resultado_valor").setValue(cad.getEtilometroPr2Resultado().getValor());
		} else if (cad.getEtilometroPr1Resultado() != null && cad.getEtilometroPr1Resultado().getValor() != null) {
			fields.get("etilometro_resultado_valor").setValue(cad.getEtilometroPr1Resultado().getValor());
		}
		if (cad.getEtilometroPr2Valor() != null) {
			fields.get("etilometro_valor").setValue(cad.getEtilometroPr2Valor().toPlainString());
		} else if (cad.getEtilometroPr1Valor() != null) {
			fields.get("etilometro_valor").setValue(cad.getEtilometroPr1Valor().toPlainString());
		}
		if (cad.getSangreNumeroMuestra() != null) {
			fields.get("sangre_numero_muestra").setValue(cad.getSangreNumeroMuestra());
		}
		if (cad.getOrinaNumeroMuestra() != null) {
			fields.get("orina_numero_muestra").setValue(cad.getOrinaNumeroMuestra());
		}
		if (cad.getObservacionesMuestras() != null) {
			fields.get("observaciones_muestras").setValue(cad.getObservacionesMuestras());
		}

		// 7
		if (cad.getLugarIdentificacion() != null && cad.getFechahoraIdentificaciones() != null) {
			String lugar = cad.getLugarIdentificacion();
			String fecha = Utiles.convertDateToString(cad.getFechahoraIdentificaciones(),
					Constantes.FORMATO_FECHA_LARGO);
			fields.get("is_fecha_inspeccion").setValue(" " + lugar + " a " + fecha);
		}
		if (cad.getNombrePersonalMedico() != null && cad.getDocumentoPersonalMedico() != null) {
			fields.get("firma_medico")
					.setValue(cad.getNombrePersonalMedico().concat("\n").concat(cad.getDocumentoPersonalMedico()));
		}
		if (cad.getNombreDelegadoSeguridad() != null && cad.getDocumentoDelegadoSeguridad() != null) {
			fields.get("firma_seguridad").setValue(
					cad.getNombreDelegadoSeguridad().concat("\n").concat(cad.getDocumentoDelegadoSeguridad()));
		}
		if (cad.getNombreDelegadoADIF() != null && cad.getDocumentoDelegadoADIF() != null) {
			fields.get("firma_adif")
					.setValue(cad.getNombreDelegadoADIF().concat("\n").concat(cad.getDocumentoDelegadoADIF()));
		}
		if (cad.getNombreTecnicoCIAF() != null && cad.getDocumentoTecnicoCIAF() != null) {
			fields.get("firma_ciaf")
					.setValue(cad.getNombreTecnicoCIAF().concat("\n").concat(cad.getDocumentoTecnicoCIAF()));
		}

		// 8
		if (cad.getSangreResultado() != null && cad.getSangreResultado().getValor() != null) {
			fields.get("sangre_resultado_valor").setValue(cad.getSangreResultado().getValor());
		}
		if (cad.getSangreAnalisis() != null) {
			fields.get("sangre_valor").setValue(cad.getSangreAnalisis().toPlainString());
		}
		if (cad.getOrinaResultado() != null && cad.getOrinaResultado().getValor() != null) {
			fields.get("orina_resultado_valor").setValue(cad.getOrinaResultado().getValor());
		}
		if (cad.getObservacionesLaboratorio() != null) {
			fields.get("observaciones_laboratorio").setValue(cad.getObservacionesLaboratorio());
		}

		// 9
		if (cad.getMedidasCautelares() != null) {
			fields.get("medidas_cautelares").setValue(cad.getMedidasCautelares());
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoRepFinSer(eu.
	 * eurogestion.ese.domain.HistoricoMaquinista)
	 */
	@Override
	public byte[] generarDocumentoRepFinSer(HistoricoMaquinista historicoMaquinista) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_REP_FIN_SER).getValor();
//		String template = "./templates/TemplateRepFinSerTransfesa.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();
		// 1
		fields.get("nve").setValue(historicoMaquinista.getNve());
		fields.get("fecha")
				.setValue(Utiles.convertDateToString(historicoMaquinista.getFecha(), Constantes.FORMATO_FECHA_LARGO));
		fields.get("tren_numero").setValue(historicoMaquinista.getTren().getNumero());
		if (historicoMaquinista.getPractico() != null) {
			if (historicoMaquinista.getPractico()) {
				fields.get("practico_true").setValue("true");
			} else {
				fields.get("practico_false").setValue("true");
			}
		}
		fields.get("personal_nombre").setValue(historicoMaquinista.getPersonal().getNombreCompleto());
		String trayecto = historicoMaquinista.getPtoOrigen().getNombre().concat(" - ")
				.concat(historicoMaquinista.getPtoFin().getNombre());
		fields.get("trayecto").setValue(trayecto);
		if (historicoMaquinista.getPredecesor() != null) {
			fields.get("predecesor_nombre").setValue(historicoMaquinista.getPredecesor().getNombreCompleto());
		}
		if (historicoMaquinista.getSucesor() != null) {
			fields.get("sucesor_nombre").setValue(historicoMaquinista.getSucesor().getNombreCompleto());
		}
		// 2
		fields.get("hora_toma").setValue(
				Utiles.convertDateToString(historicoMaquinista.getHoraToma(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		fields.get("hora_salida").setValue(Utiles.convertDateToString(historicoMaquinista.getHoraSalida(),
				Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		if (historicoMaquinista.getHoraIniRefrigerio() != null) {
			fields.get("hora_ini_refrigerio").setValue(Utiles.convertDateToString(
					historicoMaquinista.getHoraIniRefrigerio(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		if (historicoMaquinista.getEstacionRefrigerio() != null) {
			fields.get("estacion_refrigerio").setValue(historicoMaquinista.getEstacionRefrigerio());
		}
		if (historicoMaquinista.getHoraFinRefrigerio() != null) {
			fields.get("hora_fin_refrigerio").setValue(Utiles.convertDateToString(
					historicoMaquinista.getHoraFinRefrigerio(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		if (historicoMaquinista.getHoraLlegada() != null) {
			fields.get("hora_llegada").setValue(Utiles.convertDateToString(historicoMaquinista.getHoraLlegada(),
					Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		fields.get("hora_deje").setValue(
				Utiles.convertDateToString(historicoMaquinista.getHoraDeje(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		if (historicoMaquinista.getInicioViajeAntes() != null) {
			fields.get("inicio_viaje_antes").setValue(Utiles.convertDateToString(
					historicoMaquinista.getInicioViajeAntes(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		if (historicoMaquinista.getFinViajeAntes() != null) {
			fields.get("fin_viaje_antes").setValue(Utiles.convertDateToString(historicoMaquinista.getFinViajeAntes(),
					Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		if (historicoMaquinista.getInicioViajeDespues() != null) {
			fields.get("inicio_viaje_despues").setValue(Utiles.convertDateToString(
					historicoMaquinista.getInicioViajeDespues(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		if (historicoMaquinista.getFinViajeDespues() != null) {
			fields.get("fin_viaje_despues").setValue(Utiles.convertDateToString(
					historicoMaquinista.getFinViajeDespues(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		// 3
		if (historicoMaquinista.getInicioEm2000() != null) {
			fields.get("inicio_em2000").setValue(historicoMaquinista.getInicioEm2000());
		}
		if (historicoMaquinista.getFinEm2000() != null) {
			fields.get("fin_em2000").setValue(historicoMaquinista.getFinEm2000());
		}
		if (historicoMaquinista.getInicioTeloc() != null) {
			fields.get("inicio_teloc").setValue(historicoMaquinista.getInicioTeloc());
		}
		if (historicoMaquinista.getFinTeloc() != null) {
			fields.get("fin_teloc").setValue(historicoMaquinista.getFinTeloc());
		}
		if (historicoMaquinista.getKwHFin() != null) {
			fields.get("kw_h_fin").setValue(historicoMaquinista.getKwHFin());
		}
		if (historicoMaquinista.getNivelCombustibleInicio() != null) {
			fields.get("nivel_combustible_inicio").setValue(historicoMaquinista.getNivelCombustibleInicio());
		}
		if (historicoMaquinista.getNivelCombustibleFin() != null) {
			fields.get("nivel_combustible_fin").setValue(historicoMaquinista.getNivelCombustibleFin());
		}
		if (historicoMaquinista.getEstacionamientoLoco() != null) {
			fields.get("estacionamiento_loco").setValue(historicoMaquinista.getEstacionamientoLoco());
		}
		if (historicoMaquinista.getSenalesColaReflectante() != null) {
			fields.get("senales_cola_reflectante").setValue(historicoMaquinista.getSenalesColaReflectante());
		}
		if (historicoMaquinista.getSenalesColaLuminosas() != null) {
			fields.get("senales_cola_luminosas").setValue(historicoMaquinista.getSenalesColaLuminosas());
		}
		if (historicoMaquinista.getLinternas() != null) {
			fields.get("linternas").setValue(historicoMaquinista.getLinternas());
		}
		if (historicoMaquinista.getBanderiniesRojos() != null) {
			fields.get("banderinies_rojos").setValue(historicoMaquinista.getBanderiniesRojos());
		}
		if (historicoMaquinista.getCalcesAntideriva() != null) {
			fields.get("calces_antideriva").setValue(historicoMaquinista.getCalcesAntideriva());
		}
		if (historicoMaquinista.getBarrasCortocircuito() != null) {
			fields.get("barras_cortocircuito").setValue(historicoMaquinista.getBarrasCortocircuito());
		}
		if (historicoMaquinista.getLlaveTrinquete() != null) {
			fields.get("llave_trinquete").setValue(historicoMaquinista.getLlaveTrinquete());
		}
		if (historicoMaquinista.getOtros() != null) {
			fields.get("otros").setValue(historicoMaquinista.getOtros());
		}
		if (historicoMaquinista.getEstacionRepostaje() != null) {
			fields.get("estacion_repostaje").setValue(historicoMaquinista.getEstacionRepostaje());
		}
		if (historicoMaquinista.getLitrosRepostados() != null) {
			fields.get("litros_repostados").setValue(historicoMaquinista.getLitrosRepostados());
		}
		// 4
		if (historicoMaquinista.getAveriasLocomotora() != null) {
			fields.get("averias_locomotora").setValue(historicoMaquinista.getAveriasLocomotora());
		}
		// 5
		if (historicoMaquinista.getAveriasRemolcado() != null) {
			fields.get("averias_remolcado").setValue(historicoMaquinista.getAveriasRemolcado());
		}
		// 6
		if (historicoMaquinista.getOtraInformacion() != null) {
			fields.get("otra_informacion").setValue(historicoMaquinista.getOtraInformacion());
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoTomSer(eu.
	 * eurogestion.ese.domain.TomaServicio)
	 */
	@Override
	public byte[] generarDocumentoTomSer(TomaServicio tomaServicio) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_TOM_SER).getValor();
//		String template = "./templates/TemplateTomSer.pdf";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		fields.get("tren_numero").setValue(tomaServicio.getTren().getNumero());
		fields.get("nve").setValue(tomaServicio.getNve());
		if (tomaServicio.getFecha() != null) {
			fields.get("fecha")
					.setValue(Utiles.convertDateToString(tomaServicio.getFecha(), Constantes.FORMATO_FECHA_LARGO));
		}
		fields.get("personal_nombre").setValue(tomaServicio.getPersonal().getNombreCompleto());
		fields.get("lugar_inspeccion").setValue(tomaServicio.getLugarInspeccion());
		if (tomaServicio.getHora() != null) {
			fields.get("hora").setValue(
					Utiles.convertDateToString(tomaServicio.getHora(), Constantes.FORMATO_FECHA_HORAS_PANTALLA));
		}
		if (tomaServicio.getMotivo() != null) {
			fields.get("motivo").setValue(tomaServicio.getMotivo());
		}
		// 3
		if (tomaServicio.getRealizarAccion()) {
			fields.get("realizar_accion_SI").setValue("SI");
		} else {
			fields.get("realizar_accion_NO").setValue("SI");
		}
		if (tomaServicio.getDocumentacionReglamentaria()) {
			fields.get("documentacion_reglamentaria_SI").setValue("SI");
		} else {
			fields.get("documentacion_reglamentaria_NO").setValue("SI");
		}
		if (tomaServicio.getLibroTelefonemas()) {
			fields.get("libro_telefonemas_SI").setValue("SI");
		} else {
			fields.get("libro_telefonemas_NO").setValue("SI");
		}
		if (tomaServicio.getLibroAverias()) {
			fields.get("libro_averias_SI").setValue("SI");
		} else {
			fields.get("libro_averias_NO").setValue("SI");
		}
		if (tomaServicio.getDotacionUtilesServicio()) {
			fields.get("dotacion_utiles-servicio_SI").setValue("SI");
		} else {
			fields.get("dotacion_utiles-servicio_NO").setValue("SI");
		}
		if (tomaServicio.getSenalizacionCabezaCola()) {
			fields.get("senalizacion_cabeza_cola_SI").setValue("SI");
		} else {
			fields.get("senalizacion_cabeza_cola_NO").setValue("SI");
		}
		if (tomaServicio.getVisibilidadAdecuada()) {
			fields.get("visibilidad_adecuada_SI").setValue("SI");
		} else {
			fields.get("visibilidad_adecuada_NO").setValue("SI");
		}
		if (tomaServicio.getAnomaliasRodajeCaja()) {
			fields.get("anomalias_rodaje_caja_SI").setValue("SI");
		} else {
			fields.get("anomalias_rodaje_caja_NO").setValue("SI");
		}
		if (tomaServicio.getAnomaliasSuspension()) {
			fields.get("anomalias_suspension_SI").setValue("SI");
		} else {
			fields.get("anomalias_suspension_NO").setValue("SI");
		}
		if (tomaServicio.getAnomaliasChoqueTraccion()) {
			fields.get("anomalias_choque_traccion_SI").setValue("SI");
		} else {
			fields.get("anomalias_choque_traccion_NO").setValue("SI");
		}
		if (tomaServicio.getEstadoPrecintos()) {
			fields.get("estado_precintos_SI").setValue("SI");
		} else {
			fields.get("estado_precintos_NO").setValue("SI");
		}
		if (tomaServicio.getPosicionPalancaCambiador()) {
			fields.get("posicion_palanca_cambiador_SI").setValue("SI");
		} else {
			fields.get("posicion_palanca_cambiador_NO").setValue("SI");
		}
		if (tomaServicio.getFrenosEstacionamiento()) {
			fields.get("frenos_estacionamiento_SI").setValue("SI");
		} else {
			fields.get("frenos_estacionamiento_NO").setValue("SI");
		}
		if (tomaServicio.getConfiguracionFrenado()) {
			fields.get("configuracion_frenado_SI").setValue("SI");
		} else {
			fields.get("configuracion_frenado_NO").setValue("SI");
		}
		if (tomaServicio.getDispositivoVigilanciaHm()) {
			fields.get("dispositivo_vigilancia_hm_SI").setValue("SI");
		} else {
			fields.get("dispositivo_vigilancia_hm_NO").setValue("SI");
		}
		if (tomaServicio.getValvulaEmergenciaSeta()) {
			fields.get("valvula_emergencia_seta_SI").setValue("SI");
		} else {
			fields.get("valvula_emergencia_seta_NO").setValue("SI");
		}
		if (tomaServicio.getPruebasFreno()) {
			fields.get("pruebas_freno_SI").setValue("SI");
		} else {
			fields.get("pruebas_freno_NO").setValue("SI");
		}
		if (tomaServicio.getPruebaInversionMarcha()) {
			fields.get("prueba_inversion_marcha_SI").setValue("SI");
		} else {
			fields.get("prueba_inversion_marcha_NO").setValue("SI");
		}
		if (tomaServicio.getAsfaCorrecto()) {
			fields.get("asfa_correcto_SI").setValue("SI");
		} else {
			fields.get("asfa_correcto_NO").setValue("SI");
		}
		if (tomaServicio.getEquipoRadiotelefonia()) {
			fields.get("equipo_radiotelefonia_SI").setValue("SI");
		} else {
			fields.get("equipo_radiotelefonia_NO").setValue("SI");
		}
		if (tomaServicio.getInspeccionVisual()) {
			fields.get("inspeccion_visual_SI").setValue("SI");
		} else {
			fields.get("inspeccion_visual_NO").setValue("SI");
		}
		if (tomaServicio.getDatosDocumentoTren()) {
			fields.get("datos_documento_tren_SI").setValue("SI");
		} else {
			fields.get("datos_documento_tren_NO").setValue("SI");
		}
		if (tomaServicio.getLibroTelefonemasRelevo()) {
			fields.get("libro_telefonemas_relevo_SI").setValue("SI");
		} else {
			fields.get("libro_telefonemas_relevo_NO").setValue("SI");
		}
		if (tomaServicio.getNoExistenNotificaciones()) {
			fields.get("no_existen_notificaciones_SI").setValue("SI");
		} else {
			fields.get("no_existen_notificaciones_NO").setValue("SI");
		}
		// 3
		if (tomaServicio.getObservaciones() != null) {
			fields.get("observaciones").setValue(tomaServicio.getObservaciones());
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoFicInsSegEquEleSegMatRodTra(eu.eurogestion.ese.domain.Isset)
	 */
	@Override
	public byte[] generarDocumentoFicInsSegEquEleSegMatRodTra(Iseet iseet) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_INS_SEG_EQU_ELE_SEG_MAT_ROD_TRA).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (iseet.getIs().getNumReferencia() != null) {
			fields.get("num_referencia").setValue(iseet.getIs().getNumReferencia());
		}
		if (iseet.getIs().getFechaInspeccion() != null) {
			fields.get("fecha_inspeccion").setValue(
					Utiles.convertDateToString(iseet.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_LARGO));
		}
		if (iseet.getIs().getLugar() != null) {
			fields.get("lugar").setValue(iseet.getIs().getLugar());
		}
		if (iseet.getIs().getInspector() != null && iseet.getIs().getInspector().getNombreCompleto() != null) {
			fields.get("inspector_nombre").setValue(iseet.getIs().getInspector().getNombreCompleto());
		}
		// 2
		if (iseet.getIs().getTren() != null && iseet.getIs().getTren().getNumero() != null) {
			fields.get("tren_numero").setValue(iseet.getIs().getTren().getNumero());
		}
		if (iseet.getIs().getTren() != null && iseet.getIs().getTren().getTramo() != null
				&& iseet.getIs().getTren().getTramo().getPuntoOrigen() != null
				&& iseet.getIs().getTren().getTramo().getPuntoOrigen().getNombre() != null) {
			fields.get("tren_tramo_punto_origen_nombre")
					.setValue(iseet.getIs().getTren().getTramo().getPuntoOrigen().getNombre());
		}
		if (iseet.getIs().getTren() != null && iseet.getIs().getTren().getTramo() != null
				&& iseet.getIs().getTren().getTramo().getPuntoDestino() != null
				&& iseet.getIs().getTren().getTramo().getPuntoDestino().getNombre() != null) {
			fields.get("tren_tramo_punto_destino_nombre")
					.setValue(iseet.getIs().getTren().getTramo().getPuntoDestino().getNombre());
		}
		if (iseet.getNve() != null) {
			fields.get("nve").setValue(iseet.getNve());
		}
		if (iseet.getMaquinista() != null && iseet.getMaquinista().getNombreCompleto() != null) {
			fields.get("maquinista_nombre").setValue(iseet.getMaquinista().getNombreCompleto());
		}
		if (iseet.getObservacionesCirculacion() != null) {
			fields.get("observaciones_circulacion").setValue(iseet.getObservacionesCirculacion());
		}
		// 3
		if (iseet.getCaja1() != null && iseet.getCaja1().getValor() != null) {
			fields.get("caja_1").setValue(iseet.getCaja1().getValor());
		}
		if (iseet.getCaja2() != null && iseet.getCaja2().getValor() != null) {
			fields.get("caja_2").setValue(iseet.getCaja2().getValor());
		}
		if (iseet.getCaja3() != null && iseet.getCaja3().getValor() != null) {
			fields.get("caja_3").setValue(iseet.getCaja3().getValor());
		}
		if (iseet.getCaja4() != null && iseet.getCaja4().getValor() != null) {
			fields.get("caja_4").setValue(iseet.getCaja4().getValor());
		}
		if (iseet.getCaja5() != null && iseet.getCaja5().getValor() != null) {
			fields.get("caja_5").setValue(iseet.getCaja5().getValor());
		}
		if (iseet.getCaja6() != null && iseet.getCaja6().getValor() != null) {
			fields.get("caja_6").setValue(iseet.getCaja6().getValor());
		}
		if (iseet.getCaja7() != null && iseet.getCaja7().getValor() != null) {
			fields.get("caja_7").setValue(iseet.getCaja7().getValor());
		}
		if (iseet.getCaja7() != null && iseet.getCaja7().getValor() != null) {
			fields.get("caja_7").setValue(iseet.getCaja7().getValor());
		}
		if (iseet.getElementosChoque8() != null && iseet.getElementosChoque8().getValor() != null) {
			fields.get("elementos_choque_8").setValue(iseet.getElementosChoque8().getValor());
		}
		if (iseet.getElementosChoque9() != null && iseet.getElementosChoque9().getValor() != null) {
			fields.get("elementos_choque_9").setValue(iseet.getElementosChoque9().getValor());
		}
		if (iseet.getElementosChoque10() != null && iseet.getElementosChoque10().getValor() != null) {
			fields.get("elementos_choque_10").setValue(iseet.getElementosChoque10().getValor());
		}
		if (iseet.getElementosChoque11() != null && iseet.getElementosChoque11().getValor() != null) {
			fields.get("elementos_choque_11").setValue(iseet.getElementosChoque11().getValor());
		}
		if (iseet.getElementosChoque12() != null && iseet.getElementosChoque12().getValor() != null) {
			fields.get("elementos_choque_12").setValue(iseet.getElementosChoque12().getValor());
		}
		if (iseet.getElementosChoque13() != null && iseet.getElementosChoque13().getValor() != null) {
			fields.get("elementos_choque_13").setValue(iseet.getElementosChoque13().getValor());
		}
		if (iseet.getElementosChoque14() != null && iseet.getElementosChoque14().getValor() != null) {
			fields.get("elementos_choque_14").setValue(iseet.getElementosChoque14().getValor());
		}
		if (iseet.getElementosChoque15() != null && iseet.getElementosChoque15().getValor() != null) {
			fields.get("elementos_choque_15").setValue(iseet.getElementosChoque15().getValor());
		}
		if (iseet.getElementosChoque16() != null && iseet.getElementosChoque16().getValor() != null) {
			fields.get("elementos_choque_16").setValue(iseet.getElementosChoque16().getValor());
		}
		if (iseet.getElementosChoque17() != null && iseet.getElementosChoque17().getValor() != null) {
			fields.get("elementos_choque_17").setValue(iseet.getElementosChoque17().getValor());
		}
		if (iseet.getElementosChoque18() != null && iseet.getElementosChoque18().getValor() != null) {
			fields.get("elementos_choque_18").setValue(iseet.getElementosChoque18().getValor());
		}
		if (iseet.getElementosChoque19() != null && iseet.getElementosChoque19().getValor() != null) {
			fields.get("elementos_choque_19").setValue(iseet.getElementosChoque19().getValor());
		}
		if (iseet.getElementosChoque20() != null && iseet.getElementosChoque20().getValor() != null) {
			fields.get("elementos_choque_20").setValue(iseet.getElementosChoque20().getValor());
		}
		if (iseet.getElementosChoque21() != null && iseet.getElementosChoque21().getValor() != null) {
			fields.get("elementos_choque_21").setValue(iseet.getElementosChoque21().getValor());
		}
		if (iseet.getElementosChoque22() != null && iseet.getElementosChoque22().getValor() != null) {
			fields.get("elementos_choque_22").setValue(iseet.getElementosChoque22().getValor());
		}
		if (iseet.getElementosChoque23() != null && iseet.getElementosChoque23().getValor() != null) {
			fields.get("elementos_choque_23").setValue(iseet.getElementosChoque23().getValor());
		}
		if (iseet.getElementosChoque24() != null && iseet.getElementosChoque24().getValor() != null) {
			fields.get("elementos_choque_24").setValue(iseet.getElementosChoque24().getValor());
		}
		if (iseet.getObservacionesElementosChoque() != null) {
			fields.get("observaciones_elementos_choque").setValue(iseet.getObservacionesElementosChoque());
		}
		if (iseet.getBogies25() != null && iseet.getBogies25().getValor() != null) {
			fields.get("bogies_25").setValue(iseet.getBogies25().getValor());
		}
		if (iseet.getBogies26() != null && iseet.getBogies26().getValor() != null) {
			fields.get("bogies_26").setValue(iseet.getBogies26().getValor());
		}
		if (iseet.getBogies27() != null && iseet.getBogies27().getValor() != null) {
			fields.get("bogies_27").setValue(iseet.getBogies27().getValor());
		}
		if (iseet.getBogies28() != null && iseet.getBogies28().getValor() != null) {
			fields.get("bogies_28").setValue(iseet.getBogies28().getValor());
		}
		if (iseet.getObservacionesBogies() != null) {
			fields.get("observaciones_bogies").setValue(iseet.getObservacionesBogies());
		}
		if (iseet.getSuspension29() != null && iseet.getSuspension29().getValor() != null) {
			fields.get("suspencion_29").setValue(iseet.getSuspension29().getValor());
		}
		if (iseet.getSuspension30() != null && iseet.getSuspension30().getValor() != null) {
			fields.get("suspencion_30").setValue(iseet.getSuspension30().getValor());
		}
		if (iseet.getSuspension31() != null && iseet.getSuspension31().getValor() != null) {
			fields.get("suspencion_31").setValue(iseet.getSuspension31().getValor());
		}
		if (iseet.getSuspension32() != null && iseet.getSuspension32().getValor() != null) {
			fields.get("suspencion_32").setValue(iseet.getSuspension32().getValor());
		}
		if (iseet.getSuspension33() != null && iseet.getSuspension33().getValor() != null) {
			fields.get("suspencion_33").setValue(iseet.getSuspension33().getValor());
		}
		if (iseet.getObservacionesSuspension() != null) {
			fields.get("observaciones_suspension").setValue(iseet.getObservacionesSuspension());
		}
		if (iseet.getRodaje34() != null && iseet.getRodaje34().getValor() != null) {
			fields.get("rodaje_34").setValue(iseet.getRodaje34().getValor());
		}
		if (iseet.getRodaje35() != null && iseet.getRodaje35().getValor() != null) {
			fields.get("rodaje_35").setValue(iseet.getRodaje35().getValor());
		}
		if (iseet.getRodaje36() != null && iseet.getRodaje36().getValor() != null) {
			fields.get("rodaje_36").setValue(iseet.getRodaje36().getValor());
		}
		if (iseet.getRodaje37() != null && iseet.getRodaje37().getValor() != null) {
			fields.get("rodaje_37").setValue(iseet.getRodaje37().getValor());
		}
		if (iseet.getRodaje38() != null && iseet.getRodaje38().getValor() != null) {
			fields.get("rodaje_38").setValue(iseet.getRodaje38().getValor());
		}
		if (iseet.getRodaje39() != null && iseet.getRodaje39().getValor() != null) {
			fields.get("rodaje_39").setValue(iseet.getRodaje39().getValor());
		}
		if (iseet.getRodaje40() != null && iseet.getRodaje40().getValor() != null) {
			fields.get("rodaje_40").setValue(iseet.getRodaje40().getValor());
		}
		if (iseet.getRodaje41() != null && iseet.getRodaje41().getValor() != null) {
			fields.get("rodaje_41").setValue(iseet.getRodaje41().getValor());
		}
		if (iseet.getRodaje42() != null && iseet.getRodaje42().getValor() != null) {
			fields.get("rodaje_42").setValue(iseet.getRodaje42().getValor());
		}
		if (iseet.getRodaje43() != null && iseet.getRodaje43().getValor() != null) {
			fields.get("rodaje_43").setValue(iseet.getRodaje43().getValor());
		}
		if (iseet.getRodaje44() != null && iseet.getRodaje44().getValor() != null) {
			fields.get("rodaje_44").setValue(iseet.getRodaje44().getValor());
		}
		if (iseet.getRodaje45() != null && iseet.getRodaje45().getValor() != null) {
			fields.get("rodaje_45").setValue(iseet.getRodaje45().getValor());
		}
		if (iseet.getRodaje46() != null && iseet.getRodaje46().getValor() != null) {
			fields.get("rodaje_46").setValue(iseet.getRodaje46().getValor());
		}
		if (iseet.getRodaje47() != null && iseet.getRodaje47().getValor() != null) {
			fields.get("rodaje_47").setValue(iseet.getRodaje47().getValor());
		}
		if (iseet.getRodaje48() != null && iseet.getRodaje48().getValor() != null) {
			fields.get("rodaje_48").setValue(iseet.getRodaje48().getValor());
		}
		if (iseet.getRodaje49() != null && iseet.getRodaje49().getValor() != null) {
			fields.get("rodaje_49").setValue(iseet.getRodaje49().getValor());
		}
		if (iseet.getRodaje50() != null && iseet.getRodaje50().getValor() != null) {
			fields.get("rodaje_50").setValue(iseet.getRodaje50().getValor());
		}
		if (iseet.getRodaje51() != null && iseet.getRodaje51().getValor() != null) {
			fields.get("rodaje_51").setValue(iseet.getRodaje51().getValor());
		}
		if (iseet.getObservacionesRodaje() != null) {
			fields.get("observaciones_rodaje").setValue(iseet.getObservacionesRodaje());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno52() != null
				&& iseet.getIseet2().getFreno52().getValor() != null) {
			fields.get("freno_52").setValue(iseet.getIseet2().getFreno52().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno53() != null
				&& iseet.getIseet2().getFreno53().getValor() != null) {
			fields.get("freno_53").setValue(iseet.getIseet2().getFreno53().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno54() != null
				&& iseet.getIseet2().getFreno54().getValor() != null) {
			fields.get("freno_54").setValue(iseet.getIseet2().getFreno54().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno55() != null
				&& iseet.getIseet2().getFreno55().getValor() != null) {
			fields.get("freno_55").setValue(iseet.getIseet2().getFreno55().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno56() != null
				&& iseet.getIseet2().getFreno56().getValor() != null) {
			fields.get("freno_56").setValue(iseet.getIseet2().getFreno56().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno57() != null
				&& iseet.getIseet2().getFreno57().getValor() != null) {
			fields.get("freno_57").setValue(iseet.getIseet2().getFreno57().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno58() != null
				&& iseet.getIseet2().getFreno58().getValor() != null) {
			fields.get("freno_58").setValue(iseet.getIseet2().getFreno58().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno59() != null
				&& iseet.getIseet2().getFreno59().getValor() != null) {
			fields.get("freno_59").setValue(iseet.getIseet2().getFreno59().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno60() != null
				&& iseet.getIseet2().getFreno60().getValor() != null) {
			fields.get("freno_60").setValue(iseet.getIseet2().getFreno60().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno61() != null
				&& iseet.getIseet2().getFreno61().getValor() != null) {
			fields.get("freno_61").setValue(iseet.getIseet2().getFreno61().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno62() != null
				&& iseet.getIseet2().getFreno62().getValor() != null) {
			fields.get("freno_62").setValue(iseet.getIseet2().getFreno62().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno63() != null
				&& iseet.getIseet2().getFreno63().getValor() != null) {
			fields.get("freno_63").setValue(iseet.getIseet2().getFreno63().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno64() != null
				&& iseet.getIseet2().getFreno64().getValor() != null) {
			fields.get("freno_64").setValue(iseet.getIseet2().getFreno64().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno65() != null
				&& iseet.getIseet2().getFreno65().getValor() != null) {
			fields.get("freno_65").setValue(iseet.getIseet2().getFreno65().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno66() != null
				&& iseet.getIseet2().getFreno66().getValor() != null) {
			fields.get("freno_66").setValue(iseet.getIseet2().getFreno66().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno67() != null
				&& iseet.getIseet2().getFreno67().getValor() != null) {
			fields.get("freno_67").setValue(iseet.getIseet2().getFreno67().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno68() != null
				&& iseet.getIseet2().getFreno68().getValor() != null) {
			fields.get("freno_68").setValue(iseet.getIseet2().getFreno68().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno69() != null
				&& iseet.getIseet2().getFreno69().getValor() != null) {
			fields.get("freno_69").setValue(iseet.getIseet2().getFreno69().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno70() != null
				&& iseet.getIseet2().getFreno70().getValor() != null) {
			fields.get("freno_70").setValue(iseet.getIseet2().getFreno70().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno71() != null
				&& iseet.getIseet2().getFreno71().getValor() != null) {
			fields.get("freno_71").setValue(iseet.getIseet2().getFreno71().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno72() != null
				&& iseet.getIseet2().getFreno72().getValor() != null) {
			fields.get("freno_72").setValue(iseet.getIseet2().getFreno72().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno73() != null
				&& iseet.getIseet2().getFreno73().getValor() != null) {
			fields.get("freno_73").setValue(iseet.getIseet2().getFreno73().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno74() != null
				&& iseet.getIseet2().getFreno74().getValor() != null) {
			fields.get("freno_74").setValue(iseet.getIseet2().getFreno74().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno75() != null
				&& iseet.getIseet2().getFreno75().getValor() != null) {
			fields.get("freno_75").setValue(iseet.getIseet2().getFreno75().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno76() != null
				&& iseet.getIseet2().getFreno76().getValor() != null) {
			fields.get("freno_76").setValue(iseet.getIseet2().getFreno76().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno77() != null
				&& iseet.getIseet2().getFreno77().getValor() != null) {
			fields.get("freno_77").setValue(iseet.getIseet2().getFreno77().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno78() != null
				&& iseet.getIseet2().getFreno78().getValor() != null) {
			fields.get("freno_78").setValue(iseet.getIseet2().getFreno78().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno79() != null
				&& iseet.getIseet2().getFreno79().getValor() != null) {
			fields.get("freno_79").setValue(iseet.getIseet2().getFreno79().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno80() != null
				&& iseet.getIseet2().getFreno80().getValor() != null) {
			fields.get("freno_80").setValue(iseet.getIseet2().getFreno80().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno81() != null
				&& iseet.getIseet2().getFreno81().getValor() != null) {
			fields.get("freno_81").setValue(iseet.getIseet2().getFreno81().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno82() != null
				&& iseet.getIseet2().getFreno82().getValor() != null) {
			fields.get("freno_82").setValue(iseet.getIseet2().getFreno82().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno83() != null
				&& iseet.getIseet2().getFreno83().getValor() != null) {
			fields.get("freno_83").setValue(iseet.getIseet2().getFreno83().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno84() != null
				&& iseet.getIseet2().getFreno84().getValor() != null) {
			fields.get("freno_84").setValue(iseet.getIseet2().getFreno84().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno85() != null
				&& iseet.getIseet2().getFreno85().getValor() != null) {
			fields.get("freno_85").setValue(iseet.getIseet2().getFreno85().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno86() != null
				&& iseet.getIseet2().getFreno86().getValor() != null) {
			fields.get("freno_86").setValue(iseet.getIseet2().getFreno86().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno87() != null
				&& iseet.getIseet2().getFreno87().getValor() != null) {
			fields.get("freno_87").setValue(iseet.getIseet2().getFreno87().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno88() != null
				&& iseet.getIseet2().getFreno88().getValor() != null) {
			fields.get("freno_88").setValue(iseet.getIseet2().getFreno88().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno89() != null
				&& iseet.getIseet2().getFreno89().getValor() != null) {
			fields.get("freno_89").setValue(iseet.getIseet2().getFreno89().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno90() != null
				&& iseet.getIseet2().getFreno90().getValor() != null) {
			fields.get("freno_90").setValue(iseet.getIseet2().getFreno90().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno91() != null
				&& iseet.getIseet2().getFreno91().getValor() != null) {
			fields.get("freno_91").setValue(iseet.getIseet2().getFreno91().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getFreno92() != null
				&& iseet.getIseet2().getFreno92().getValor() != null) {
			fields.get("freno_92").setValue(iseet.getIseet2().getFreno92().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getObservacionesFreno() != null) {
			fields.get("observaciones_freno").setValue(iseet.getIseet2().getObservacionesFreno());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia93() != null
				&& iseet.getIseet2().getVigilancia93().getValor() != null) {
			fields.get("vigilancia_93").setValue(iseet.getIseet2().getVigilancia93().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia94() != null
				&& iseet.getIseet2().getVigilancia94().getValor() != null) {
			fields.get("vigilancia_94").setValue(iseet.getIseet2().getVigilancia94().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia95() != null
				&& iseet.getIseet2().getVigilancia95().getValor() != null) {
			fields.get("vigilancia_95").setValue(iseet.getIseet2().getVigilancia95().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia96() != null
				&& iseet.getIseet2().getVigilancia96().getValor() != null) {
			fields.get("vigilancia_96").setValue(iseet.getIseet2().getVigilancia96().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia97() != null
				&& iseet.getIseet2().getVigilancia97().getValor() != null) {
			fields.get("vigilancia_97").setValue(iseet.getIseet2().getVigilancia97().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia98() != null
				&& iseet.getIseet2().getVigilancia98().getValor() != null) {
			fields.get("vigilancia_98").setValue(iseet.getIseet2().getVigilancia98().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia99() != null
				&& iseet.getIseet2().getVigilancia99().getValor() != null) {
			fields.get("vigilancia_99").setValue(iseet.getIseet2().getVigilancia99().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia100() != null
				&& iseet.getIseet2().getVigilancia100().getValor() != null) {
			fields.get("vigilancia_100").setValue(iseet.getIseet2().getVigilancia100().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia101() != null
				&& iseet.getIseet2().getVigilancia101().getValor() != null) {
			fields.get("vigilancia_101").setValue(iseet.getIseet2().getVigilancia101().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia102() != null
				&& iseet.getIseet2().getVigilancia102().getValor() != null) {
			fields.get("vigilancia_102").setValue(iseet.getIseet2().getVigilancia102().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getVigilancia103() != null
				&& iseet.getIseet2().getVigilancia103().getValor() != null) {
			fields.get("vigilancia_103").setValue(iseet.getIseet2().getVigilancia103().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getObservacionesVigilancia() != null) {
			fields.get("observaciones_vigilancia").setValue(iseet.getIseet2().getObservacionesVigilancia());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getAlumbrado104() != null
				&& iseet.getIseet2().getAlumbrado104().getValor() != null) {
			fields.get("alumbrado_104").setValue(iseet.getIseet2().getAlumbrado104().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getAlumbrado105() != null
				&& iseet.getIseet2().getAlumbrado105().getValor() != null) {
			fields.get("alumbrado_105").setValue(iseet.getIseet2().getAlumbrado105().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getAlumbrado106() != null
				&& iseet.getIseet2().getAlumbrado106().getValor() != null) {
			fields.get("alumbrado_106").setValue(iseet.getIseet2().getAlumbrado106().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getAlumbrado107() != null
				&& iseet.getIseet2().getAlumbrado107().getValor() != null) {
			fields.get("alumbrado_107").setValue(iseet.getIseet2().getAlumbrado107().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getAlumbrado108() != null
				&& iseet.getIseet2().getAlumbrado108().getValor() != null) {
			fields.get("alumbrado_108").setValue(iseet.getIseet2().getAlumbrado108().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getAlumbrado109() != null
				&& iseet.getIseet2().getAlumbrado109().getValor() != null) {
			fields.get("alumbrado_109").setValue(iseet.getIseet2().getAlumbrado109().getValor());
		}
		if (iseet.getIseet2() != null && iseet.getIseet2().getObservacionesAlumbrado() != null) {
			fields.get("observaciones_alumbrado").setValue(iseet.getIseet2().getObservacionesAlumbrado());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getPantografo110() != null
				&& iseet.getIseet3().getPantografo110().getValor() != null) {
			fields.get("pantografo_110").setValue(iseet.getIseet3().getPantografo110().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getPantografo111() != null
				&& iseet.getIseet3().getPantografo111().getValor() != null) {
			fields.get("pantografo_111").setValue(iseet.getIseet3().getPantografo111().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getPantografo112() != null
				&& iseet.getIseet3().getPantografo112().getValor() != null) {
			fields.get("pantografo_112").setValue(iseet.getIseet3().getPantografo112().getValor());
		}

		if (iseet.getIseet3() != null && iseet.getIseet3().getPantografo113() != null
				&& iseet.getIseet3().getPantografo113().getValor() != null) {
			fields.get("pantografo_113").setValue(iseet.getIseet3().getPantografo113().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getObservacionesPantografo() != null) {
			fields.get("observaciones_pantografo").setValue(iseet.getIseet3().getObservacionesPantografo());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento114() != null
				&& iseet.getIseet3().getCompartimento114().getValor() != null) {
			fields.get("compartimento_114").setValue(iseet.getIseet3().getCompartimento114().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento115() != null
				&& iseet.getIseet3().getCompartimento115().getValor() != null) {
			fields.get("compartimento_115").setValue(iseet.getIseet3().getCompartimento115().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento116() != null
				&& iseet.getIseet3().getCompartimento116().getValor() != null) {
			fields.get("compartimento_116").setValue(iseet.getIseet3().getCompartimento116().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento117() != null
				&& iseet.getIseet3().getCompartimento117().getValor() != null) {
			fields.get("compartimento_117").setValue(iseet.getIseet3().getCompartimento117().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento118() != null
				&& iseet.getIseet3().getCompartimento118().getValor() != null) {
			fields.get("compartimento_118").setValue(iseet.getIseet3().getCompartimento118().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento119() != null
				&& iseet.getIseet3().getCompartimento119().getValor() != null) {
			fields.get("compartimento_119").setValue(iseet.getIseet3().getCompartimento119().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento120() != null
				&& iseet.getIseet3().getCompartimento120().getValor() != null) {
			fields.get("compartimento_120").setValue(iseet.getIseet3().getCompartimento120().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getCompartimento121() != null
				&& iseet.getIseet3().getCompartimento121().getValor() != null) {
			fields.get("compartimento_121").setValue(iseet.getIseet3().getCompartimento121().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getObservacionesCompartimento() != null) {
			fields.get("observaciones_compartimento").setValue(iseet.getIseet3().getObservacionesCompartimento());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getFuncionalidad122() != null
				&& iseet.getIseet3().getFuncionalidad122().getValor() != null) {
			fields.get("funcionalidad_122").setValue(iseet.getIseet3().getFuncionalidad122().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getFuncionalidad123() != null
				&& iseet.getIseet3().getFuncionalidad123().getValor() != null) {
			fields.get("funcionalidad_123").setValue(iseet.getIseet3().getFuncionalidad123().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getFuncionalidad124() != null
				&& iseet.getIseet3().getFuncionalidad124().getValor() != null) {
			fields.get("funcionalidad_124").setValue(iseet.getIseet3().getFuncionalidad124().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getFuncionalidad125() != null
				&& iseet.getIseet3().getFuncionalidad125().getValor() != null) {
			fields.get("funcionalidad_125").setValue(iseet.getIseet3().getFuncionalidad125().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getFuncionalidad126() != null
				&& iseet.getIseet3().getFuncionalidad126().getValor() != null) {
			fields.get("funcionalidad_126").setValue(iseet.getIseet3().getFuncionalidad126().getValor());
		}
		if (iseet.getIseet3() != null && iseet.getIseet3().getObservacionesFuncionalidad() != null) {
			fields.get("observaciones_funcionalidad").setValue(iseet.getIseet3().getObservacionesFuncionalidad());
		}
		// 4
		if (iseet.getIseet3() != null && iseet.getIseet3().getMedidasCautelares() != null) {
			fields.get("medidas_cautelares").setValue(iseet.getIseet3().getMedidasCautelares());
		}
		// 5
		if (iseet.getIseet3() != null && iseet.getIseet3().getDocumentosAnexos() != null) {
			fields.get("documentos_anexos").setValue(iseet.getIseet3().getDocumentosAnexos());
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoAcuRecDocSer(eu.eurogestion.ese.domain.LibroPersonal)
	 */
	@Override
	public byte[] generarDocumentoAcuRecDocSer(LibroPersonal libroPersonal) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_ACU_REC_DOC_SER).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (libroPersonal.getLibro() != null && libroPersonal.getLibro().getTitulo() != null) {
			fields.get("libro_titulo").setValue(libroPersonal.getLibro().getTitulo());
		}
		if (libroPersonal.getPersonal() != null && libroPersonal.getPersonal().getNombreCompleto() != null) {
			fields.get("personal_nombre").setValue(libroPersonal.getPersonal().getNombreCompleto());
		}
		if (libroPersonal.getPersonal() != null && libroPersonal.getPersonal().getDocumento() != null) {
			fields.get("personal_documento").setValue(libroPersonal.getPersonal().getDocumento());
		}
//		if (libroPersonal.getPersonal() != null && libroPersonal.getPersonal() != null) {
//			fields.get("personal_firma").setValue(libroPersonal.getPersonal());
//		}
		if (libroPersonal != null) {
			fields.get("fecha")
					.setValue(Utiles.convertDateToString(new Date(), Constantes.FORMATO_FECHA_COMPLETA_FIRMA));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	// TODO probar
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.eurogestion.ese.services.UtilesPDFService#generarDocumentoFicISMPMerPel(
	 * eu. eurogestion.ese.domain.Ismp)
	 */
	@Override
	public byte[] generarDocumentoFicISMPMerPel(Ismp ismp) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_ISMP_MER_PEL).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfReader reader = new PdfReader(template.getBinaryStream());
		PdfWriter writer = new PdfWriter(baos);
		PdfDocument pdf = new PdfDocument(reader, writer);
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (ismp.getIs() != null && ismp.getIs().getNumReferencia() != null) {
			fields.get("Is_num_referencia").setValue(ismp.getIs().getNumReferencia());
		}
		if (ismp.getIs() != null && ismp.getIs().getFechaInspeccion() != null) {
			fields.get("Is_fecha_inspeccion").setValue(
					Utiles.convertDateToString(ismp.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_LARGO));
		}
		if (ismp.getIs() != null && ismp.getIs().getLugar() != null) {
			fields.get("Is_lugar").setValue(ismp.getIs().getLugar());
		}
		if (ismp.getIs() != null && ismp.getIs().getInspector() != null
				&& ismp.getIs().getInspector().getNombreCompleto() != null) {
			fields.get("Is_inspector_nombre").setValue(ismp.getIs().getInspector().getNombreCompleto());
		}
		// 2
		if (ismp.getIs() != null && ismp.getIs().getTren() != null && ismp.getIs().getTren().getNumero() != null) {
			fields.get("Is_tren_numero").setValue(ismp.getIs().getTren().getNumero());
		}
		if (ismp.getIs() != null && ismp.getIs().getTren() != null && ismp.getIs().getTren().getTramo() != null
				&& ismp.getIs().getTren().getTramo().getPuntoOrigen() != null
				&& ismp.getIs().getTren().getTramo().getPuntoOrigen().getNombreLargo() != null) {
			fields.get("Is_tren_tramo_punto_origen")
					.setValue(ismp.getIs().getTren().getTramo().getPuntoOrigen().getNombreLargo());
		}
		if (ismp.getIs() != null && ismp.getIs().getTren() != null && ismp.getIs().getTren().getTramo() != null
				&& ismp.getIs().getTren().getTramo().getPuntoDestino() != null
				&& ismp.getIs().getTren().getTramo().getPuntoDestino().getNombreLargo() != null) {
			fields.get("Is_tren_tramo_punto_destino")
					.setValue(ismp.getIs().getTren().getTramo().getPuntoDestino().getNombreLargo());
		}
		if (ismp.getTiposMercancias() != null) {
			fields.get("tipos_de_mercancias").setValue(ismp.getTiposMercancias());
		}
		if (ismp.getNumeroVagones() != null) {
			fields.get("numero_vagones").setValue(ismp.getNumeroVagones());
		}
		// 3
		if (ismp.getEtiquetasVagonesBuenEstado() != null) {
			fields.get("etiquetas_vagones_buen_estado").setValue(ismp.getEtiquetasVagonesBuenEstado().toString());
		}
		if (ismp.getEtiquetasVagonesMal() != null) {
			fields.get("etiquetas_vagones_mal").setValue(ismp.getEtiquetasVagonesMal().toString());
		}
		if (ismp.getEtiquetasVagonesIncorrecto() != null) {
			fields.get("etiquetas_vagones_incorrecto").setValue(ismp.getEtiquetasVagonesIncorrecto().toString());
		}
		if (ismp.getEtiquetasVagonesInspeccionados() != null) {
			fields.get("etiquetas_vagones_inspeccionados")
					.setValue(ismp.getEtiquetasVagonesInspeccionados().toString());
		}
		if (ismp.getFaltaTotalEtiquetas() != null) {
			fields.get("falta_total_etiquetas").setValue(ismp.getFaltaTotalEtiquetas().toString());
		}
		if (ismp.getFaltaAlgunaEtiqueta() != null) {
			fields.get("falta_alguna_etiqueta").setValue(ismp.getFaltaAlgunaEtiqueta().toString());
		}
		if (ismp.getEtiquetaInadecuada() != null) {
			fields.get("etiqueta_inadecuada").setValue(ismp.getEtiquetaInadecuada().toString());
		}
		if (ismp.getTamanoInadecuadaPanel() != null) {
			fields.get("tamano_inadecuado").setValue(ismp.getTamanoInadecuadaPanel().toString());
		}
		if (ismp.getOtras() != null) {
			fields.get("otras").setValue(ismp.getOtras().toString());
		}
		if (ismp.getEtiquetasObservaciones() != null) {
			fields.get("etiquetas_observaciones").setValue(ismp.getEtiquetasObservaciones());
		}
		if (ismp.getPanelesVagonesBuenEstado() != null) {
			fields.get("paneles_vagones_buen_estado").setValue(ismp.getPanelesVagonesBuenEstado().toString());
		}
		if (ismp.getPanelesVagonesMalEstado() != null) {
			fields.get("paneles_vagones_mal_estado").setValue(ismp.getPanelesVagonesMalEstado().toString());
		}
		if (ismp.getPanelesVagonesIncorrecto() != null) {
			fields.get("paneles_vagones_incorrecto").setValue(ismp.getPanelesVagonesIncorrecto().toString());
		}
		if (ismp.getPanelesVagonesInspeccionados() != null) {
			fields.get("paneles_vagones_inspeccionados").setValue(ismp.getPanelesVagonesInspeccionados().toString());
		}
		if (ismp.getNumeroOnuIncorrecto() != null) {
			fields.get("numero_onu_incorrecto").setValue(ismp.getNumeroOnuIncorrecto().toString());
		}
		if (ismp.getFaltaPanelLateral() != null) {
			fields.get("falta_panel_lateral").setValue(ismp.getFaltaPanelLateral().toString());
		}
		if (ismp.getFaltaPanelDosLaterales() != null) {
			fields.get("falta_panel_dos_laterales").setValue(ismp.getFaltaPanelDosLaterales().toString());
		}
		if (ismp.getTamanoInadecuadaPanel() != null) {
			fields.get("tamano_inadecuada_panel").setValue(ismp.getTamanoInadecuadaPanel().toString());
		}
		if (ismp.getPanelesOtras() != null) {
			fields.get("paneles_otras").setValue(ismp.getPanelesOtras().toString());
		}
		if (ismp.getPanelesObservaciones() != null) {
			fields.get("paneles_observaciones").setValue(ismp.getPanelesObservaciones());
		}
		// 4
		if (ismp.getCartaVacioCorrecta() != null) {
			fields.get("carta_vacio_correcta").setValue(ismp.getCartaVacioCorrecta().toString());
		}
		if (ismp.getCartaVacioIncorrecta() != null) {
			fields.get("carta_vacio_incorrecta").setValue(ismp.getCartaVacioIncorrecta().toString());
		}
		if (ismp.getCartaVacioCorrecta() != null || ismp.getCartaVacioIncorrecta() != null) {
			Integer correcta = ismp.getCartaVacioCorrecta() != null ? ismp.getCartaVacioCorrecta() : 0;
			Integer incorrecta = ismp.getCartaVacioIncorrecta() != null ? ismp.getCartaVacioIncorrecta() : 0;
			fields.get("carta_vacio_total").setValue(String.valueOf(correcta + incorrecta));
		}
		if (ismp.getCartaCargadoCorrecta() != null) {
			fields.get("carta_cargado_correcta").setValue(ismp.getCartaCargadoCorrecta().toString());
		}
		if (ismp.getCartaCargadoIncorrecta() != null) {
			fields.get("carta_cargado_incorrecta").setValue(ismp.getCartaCargadoIncorrecta().toString());
		}
		if (ismp.getCartaCargadoCorrecta() != null || ismp.getCartaCargadoIncorrecta() != null) {
			Integer correcta = ismp.getCartaCargadoCorrecta() != null ? ismp.getCartaCargadoCorrecta() : 0;
			Integer incorrecta = ismp.getCartaCargadoIncorrecta() != null ? ismp.getCartaCargadoIncorrecta() : 0;
			fields.get("carta_cargado_total").setValue(String.valueOf(correcta + incorrecta));
		}
		if (ismp.getDocumentosInspeccionadosCorrecta() != null) {
			fields.get("documentos_inspeccionados_correcta")
					.setValue(ismp.getDocumentosInspeccionadosCorrecta().toString());
		}
		if (ismp.getDocumentosInspeccionadosIncorrecta() != null) {
			fields.get("documentos_inspeccionados_incorrecta")
					.setValue(ismp.getDocumentosInspeccionadosIncorrecta().toString());
		}
		if (ismp.getDocumentosInspeccionadosCorrecta() != null
				|| ismp.getDocumentosInspeccionadosIncorrecta() != null) {
			Integer correcta = ismp.getDocumentosInspeccionadosCorrecta() != null
					? ismp.getDocumentosInspeccionadosCorrecta()
					: 0;
			Integer incorrecta = ismp.getDocumentosInspeccionadosIncorrecta() != null
					? ismp.getDocumentosInspeccionadosIncorrecta()
					: 0;
			fields.get("documentos_inspeccionados_total").setValue(String.valueOf(correcta + incorrecta));
		}
		if (ismp.getCartaOrdenInadecuado() != null) {
			fields.get("carta_orden_inadecuado").setValue(ismp.getCartaOrdenInadecuado().toString());
		}
		if (ismp.getCartaExcesoLexico() != null) {
			fields.get("carta_exceso_lexico").setValue(ismp.getCartaExcesoLexico().toString());
		}
		if (ismp.getCartaDenominacionIncorrecta() != null) {
			fields.get("carta_denominacion_incorrecta").setValue(ismp.getCartaDenominacionIncorrecta().toString());
		}
		if (ismp.getCartaNumeroIncorrecto() != null) {
			fields.get("carta_numero_incorrecto").setValue(ismp.getCartaNumeroIncorrecto().toString());
		}
		if (ismp.getCartaFaltaFrase() != null) {
			fields.get("carta_falta_frase").setValue(ismp.getCartaFaltaFrase().toString());
		}
		if (ismp.getCartaFaltaDatos() != null) {
			fields.get("carta_falta_datos").setValue(ismp.getCartaFaltaDatos().toString());
		}
		if (ismp.getCartaFaltaMencion() != null) {
			fields.get("carta_falta_mencion").setValue(ismp.getCartaFaltaMencion().toString());
		}
		if (ismp.getCartaOtras() != null) {
			fields.get("carta_otras").setValue(ismp.getCartaOtras().toString());
		}
		if (ismp.getRecepcionBoi() != null) {
			fields.get("recepcion_boi").setValue(ismp.getRecepcionBoi() ? "SI" : "NO");
		}
		if (ismp.getRecepcionBoiObservaciones() != null) {
			fields.get("recepcion_boi_observaciones").setValue(ismp.getRecepcionBoiObservaciones());
		}
		if (ismp.getRecepcionFicha() != null) {
			fields.get("recepcion_ficha").setValue(ismp.getRecepcionFicha() ? "SI" : "NO");
		}
		if (ismp.getRecepcionFichaObservaciones() != null) {
			fields.get("recepcion_ficha_observaciones").setValue(ismp.getRecepcionFichaObservaciones());
		}
		if (ismp.getCartaObservaciones() != null) {
			fields.get("carta_observaciones").setValue(ismp.getCartaObservaciones());
		}
		// 5
		if (ismp.getReconocimientoMaterialTerminal() != null) {
			fields.get("reconocimiento_material_terminal")
					.setValue(ismp.getReconocimientoMaterialTerminal() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoMaterialTerminalObservaciones() != null) {
			fields.get("reconocimiento_material_terminal_observaciones")
					.setValue(ismp.getReconocimientoMaterialTerminalObservaciones());
		}
		if (ismp.getReconocimientoMaterialArchiva() != null) {
			fields.get("reconocimiento_material_archiva")
					.setValue(ismp.getReconocimientoMaterialArchiva() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoMaterialArchivaObservaciones() != null) {
			fields.get("reconocimiento_material_archiva_observaciones")
					.setValue(ismp.getReconocimientoMaterialArchivaObservaciones());
		}
		if (ismp.getReconocimientoMaterialOrigen() != null) {
			fields.get("reconocimiento_material_origen").setValue(ismp.getReconocimientoMaterialOrigen() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoMaterialOrigenObservaciones() != null) {
			fields.get("reconocimiento_material_origen_observaciones")
					.setValue(ismp.getReconocimientoMaterialOrigenObservaciones());
		}
		if (ismp.getReconocimientoPosteriorTerminal() != null) {
			fields.get("reconocimiento_posterior_terminal")
					.setValue(ismp.getReconocimientoPosteriorTerminal() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoPosteriorTerminalObservaciones() != null) {
			fields.get("reconocimiento_posterior_terminal_observaciones")
					.setValue(ismp.getReconocimientoPosteriorTerminalObservaciones());
		}
		if (ismp.getReconocimientoPosteriorModelo() != null) {
			fields.get("reconocimiento_posterior_modelo")
					.setValue(ismp.getReconocimientoPosteriorModelo() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoPosteriorModeloObservaciones() != null) {
			fields.get("reconocimiento_posterior_modelo_observaciones")
					.setValue(ismp.getReconocimientoPosteriorModeloObservaciones());
		}
		if (ismp.getReconocimientoVisualDeposito() != null) {
			fields.get("reconocimiento_visual_deposito").setValue(ismp.getReconocimientoVisualDeposito() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoVisualDepositoObservaciones() != null) {
			fields.get("reconocimiento_visual_deposito_observaciones")
					.setValue(ismp.getReconocimientoVisualDepositoObservaciones());
		}
		if (ismp.getReconocimientoVisualDefectos() != null) {
			fields.get("reconocimiento_visual_defectos").setValue(ismp.getReconocimientoVisualDefectos() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoVisualDefectosObservaciones() != null) {
			fields.get("reconocimiento_visual_defectos_observaciones")
					.setValue(ismp.getReconocimientoVisualDefectosObservaciones());
		}
		if (ismp.getReconocimientoVisualCapacidad() != null) {
			fields.get("reconocimiento_visual_capacidad")
					.setValue(ismp.getReconocimientoVisualCapacidad() ? "SI" : "NO");
		}
		if (ismp.getReconocimientoVisualCapacidadObservaciones() != null) {
			fields.get("reconocimiento_visual_capacidad_observaciones")
					.setValue(ismp.getReconocimientoVisualCapacidadObservaciones());
		}
		if (ismp.getReconocimientoObservaciones() != null) {
			fields.get("reconocimiento_observaciones").setValue(ismp.getReconocimientoObservaciones());
		}
		// 6
		if (ismp.getPrescripcionSeNotifica() != null) {
			fields.get("prescripcion_se_notifica").setValue(ismp.getPrescripcionSeNotifica() ? "SI" : "NO");
		}
		if (ismp.getPrescripcionSeNotificaObservaciones() != null) {
			fields.get("prescripcion_se_notifica_observaciones")
					.setValue(ismp.getPrescripcionSeNotificaObservaciones());
		}
		if (ismp.getPrescripcionCorresponde() != null) {
			fields.get("prescripcion_corresponde").setValue(ismp.getPrescripcionCorresponde() ? "SI" : "NO");
		}
		if (ismp.getPrescripcionCorrespondeObservaciones() != null) {
			fields.get("prescripcion_corresponde_observaciones")
					.setValue(ismp.getPrescripcionCorrespondeObservaciones());
		}
		// 7
		if (ismp.getMedidasCautelares() != null) {
			fields.get("medidas_cautelares").setValue(ismp.getMedidasCautelares());
		}
		// 8
		if (ismp.getListaDocumentos() != null) {
			fields.get("lista_documentos").setValue(ismp.getListaDocumentos());
		}
		// 9
		if (ismp.getFirmaInspector() != null && ismp.getFirmaInspector().getNombreCompleto() != null
				&& ismp.getFechahoraFirmaInspector() != null) {
			fields.get("firma_inspector").setValue(
					ismp.getFirmaInspector().getNombreCompleto().concat("\n").concat(Utiles.convertDateToString(
							ismp.getFechahoraFirmaInspector(), Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}
		if (ismp.getFirmaResponsable() != null && ismp.getFirmaResponsable().getNombreCompleto() != null
				&& ismp.getFechahoraFirmaResponsable() != null) {
			fields.get("firma_responsable")
					.setValue(ismp.getFirmaResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(ismp.getFechahoraFirmaResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/**
	 * @param documentoAtril
	 * @param response
	 * @throws IOException
	 */
	private void descargaArchivo(DocumentoAtril documentoAtril, HttpServletResponse response) throws IOException {
		// setting some response headers
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		// setting the content type
		response.setContentType(documentoAtril.getType());

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		output.write(documentoAtril.getFile());

		// the contentlength
		response.setContentLength(output.size());
		// write ByteArrayOutputStream to the ServletOutputStream
		OutputStream out = response.getOutputStream();
		output.writeTo(out);
		out.flush();
		out.close();
	}

	/**
	 * @param response
	 * @throws IOException
	 */
	private void errorPantalla(HttpServletResponse response) throws IOException {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfWriter(output));
		Document document = new Document(pdf);
		document.add(new Paragraph("¡DATOS ALTERADOS!").setFontSize(20).setTextAlignment(TextAlignment.CENTER));
		document.add(new Paragraph("El archivo original ha sido modificado.").setFontSize(15)
				.setTextAlignment(TextAlignment.CENTER));
		document.close();

		// setting some response headers
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		// setting the content type
		response.setContentType("application/pdf");

		// the contentlength
		response.setContentLength(output.size());
		// write ByteArrayOutputStream to the ServletOutputStream
		OutputStream out = response.getOutputStream();
		output.writeTo(out);
		out.flush();
		out.close();
	}

	private DocumentoAtril descargaPDF(Documento fichero) throws IOException {

		DocumentoAtril documentoAtril = new DocumentoAtril();
		documentoAtril.setFile(Utiles.convertBlobTobyteArray(fichero.getFichero()));
		documentoAtril.setType(fichero.getTipoFichero());

		return documentoAtril;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#crearPDF(java.lang.String)
	 */
	public byte[] crearPDF(String texto) throws IOException {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfWriter(output));
		Document document = new Document(pdf);
		document.add(new Paragraph(texto).setFontSize(15).setTextAlignment(TextAlignment.CENTER));
		document.close();
		pdf.close();

		return output.toByteArray();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoFicInsSegEstCarComFer(eu. eurogestion.ese.domain.Iscc)
	 */
	@Override
	public byte[] generarDocumentoFicInsSegEstCarComFer(Iscc iscc) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_FIC_INS_SEG_EST_CAR_COM_FER).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

		// Mapeo de los datos en el formulario
		Map<String, PdfFormField> fields = form.getFormFields();

		// 1
		if (iscc.getIs() != null && iscc.getIs().getNumReferencia() != null) {
			fields.get("is_num_referencia").setValue(iscc.getIs().getNumReferencia());
		}
		if (iscc.getIs() != null && iscc.getIs().getFechaInspeccion() != null) {
			fields.get("is_fecha_inspeccion").setValue(
					Utiles.convertDateToString(iscc.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_LARGO));
		}
		if (iscc.getIs() != null && iscc.getIs().getLugar() != null) {
			fields.get("is_lugar").setValue(iscc.getIs().getLugar());
		}
		if (iscc.getIs() != null && iscc.getIs().getInspector() != null
				&& iscc.getIs().getInspector().getNombreCompleto() != null) {
			fields.get("is_inspector_nombre").setValue(iscc.getIs().getInspector().getNombreCompleto());
		}

		// 2
		if (iscc.getIs() != null && iscc.getIs().getTren() != null && iscc.getIs().getTren().getNumero() != null) {
			fields.get("is_tren_numero").setValue(iscc.getIs().getTren().getNumero());
		}
		if (iscc.getIs() != null && iscc.getIs().getTren() != null && iscc.getIs().getTren().getTramo() != null
				&& iscc.getIs().getTren().getTramo().getPuntoOrigen() != null
				&& iscc.getIs().getTren().getTramo().getPuntoOrigen().getNombre() != null) {
			fields.get("is_tren_origen").setValue(iscc.getIs().getTren().getTramo().getPuntoOrigen().getNombre());
		}
		if (iscc.getIs() != null && iscc.getIs().getTren() != null && iscc.getIs().getTren().getTramo() != null
				&& iscc.getIs().getTren().getTramo().getPuntoDestino() != null
				&& iscc.getIs().getTren().getTramo().getPuntoDestino().getNombre() != null) {
			fields.get("is_tren_destino").setValue(iscc.getIs().getTren().getTramo().getPuntoDestino().getNombre());
		}
		if (iscc.getTiposMercancias() != null) {
			fields.get("tipos_de_mercancias").setValue(iscc.getTiposMercancias());
		}
		if (iscc.getNumeroVagones() != null) {
			fields.get("numero_vagones").setValue(iscc.getNumeroVagones());
		}
		if (iscc.getTraficoVagonesInspeccionado() != null) {
			fields.get("trafico_vagones_inspeccionado").setValue(iscc.getTraficoVagonesInspeccionado());
		}
		if (iscc.getTraficoObservaciones() != null) {
			fields.get("trafico_observaciones").setValue(iscc.getTraficoObservaciones());
		}

		// 3
		if (iscc.getCargaTransporteRealizaObservaciones() != null) {
			fields.get("carga_transporte_se_realiza_observaciones")
					.setValue(iscc.getCargaTransporteRealizaObservaciones());
		}
		if (iscc.getCargaCohesionObservaciones() != null) {
			fields.get("carga_la_cohesion_observaciones").setValue(iscc.getCargaCohesionObservaciones());
		}
		if (iscc.getCargaMercanciasObservaciones() != null) {
			fields.get("carga_las_mercancias_observaciones").setValue(iscc.getCargaMercanciasObservaciones());
		}
		if (iscc.getCargaDistribucion() != null) {
			fields.get("carga_la_distribucion").setValue(iscc.getCargaDistribucion().toString());
		}
		if (iscc.getCargaDistribucionObservaciones() != null) {
			fields.get("carga_la_distribucion_observaciones").setValue(iscc.getCargaDistribucionObservaciones());
		}
		if (iscc.getCargaCargasConcentradas() != null) {
			fields.get("carga_cargas_concentradas").setValue(iscc.getCargaCargasConcentradas().toString());
		}
		if (iscc.getCargaCargasConcentradasObservaciones() != null) {
			fields.get("carga_cargas_concentradas_observaciones")
					.setValue(iscc.getCargaCargasConcentradasObservaciones());
		}
		if (iscc.getCargaDistancia() != null) {
			fields.get("carga_la_distancia").setValue(iscc.getCargaDistancia().toString());
		}
		if (iscc.getCargaDistanciaObservaciones() != null) {
			fields.get("carga_la_distancia_observaciones").setValue(iscc.getCargaDistanciaObservaciones());
		}
		if (iscc.getCargaAltura() != null) {
			fields.get("carga_la_altura").setValue(iscc.getCargaAltura().toString());
		}
		if (iscc.getCargaAlturaObservaciones() != null) {
			fields.get("carga_la_altura_observaciones").setValue(iscc.getCargaAlturaObservaciones());
		}
		if (iscc.getCargaRebasa() != null) {
			fields.get("carga_se_rebasa").setValue(iscc.getCargaRebasa().toString());
		}
		if (iscc.getCargaRebasaObservaciones() != null) {
			fields.get("carga_se_rebasa_observaciones").setValue(iscc.getCargaRebasaObservaciones());
		}
		if (iscc.getCargaCargamento() != null) {
			fields.get("carga_el_cargamento").setValue(iscc.getCargaCargamento().toString());
		}
		if (iscc.getCargaCargamentoObservaciones() != null) {
			fields.get("carga_el_cargamento_observaciones").setValue(iscc.getCargaCargamentoObservaciones());
		}
		if (iscc.getCargaObservaciones() != null) {
			fields.get("carga_observaciones").setValue(iscc.getCargaObservaciones());
		}

		// 4
		if (iscc.getVagonesConservan() != null) {
			fields.get("vagones_se_conservan").setValue(iscc.getVagonesConservan().toString());
		}
		if (iscc.getVagonesConservanObservaciones() != null) {
			fields.get("vagones_se_conservan_observaciones").setValue(iscc.getVagonesConservanObservaciones());
		}
		if (iscc.getVagonesCerradosTechos() != null) {
			fields.get("vagones_cerrados_techos").setValue(iscc.getVagonesCerradosTechos().toString());
		}
		if (iscc.getVagonesCerradosTechosObservaciones() != null) {
			fields.get("vagones_cerrados_techos_observaciones").setValue(iscc.getVagonesCerradosTechosObservaciones());
		}
		if (iscc.getVagonesCerradasPuertas() != null) {
			fields.get("vagones_cerradas_puertas").setValue(iscc.getVagonesCerradasPuertas().toString());
		}
		if (iscc.getVagonesCerradasPuertasObservaciones() != null) {
			fields.get("vagones_cerradas_puertas_observaciones")
					.setValue(iscc.getVagonesCerradasPuertasObservaciones());
		}
		if (iscc.getVagonesCerradasTapas() != null) {
			fields.get("vagones_cerradas_tapas").setValue(iscc.getVagonesCerradasTapas().toString());
		}
		if (iscc.getVagonesCerradasTapasObservaciones() != null) {
			fields.get("vagones_cerradas_tapas_observaciones").setValue(iscc.getVagonesCerradasTapasObservaciones());
		}
		if (iscc.getVagonesCerradasCompuertas() != null) {
			fields.get("vagones_cerradas_compuertas").setValue(iscc.getVagonesCerradasCompuertas().toString());
		}
		if (iscc.getVagonesCerradasCompuertasObservaciones() != null) {
			fields.get("vagones_cerradas_compuertas_observaciones")
					.setValue(iscc.getVagonesCerradasCompuertasObservaciones());
		}
		if (iscc.getVagonesCerradasValvulas() != null) {
			fields.get("vagones_cerradas_valvulas").setValue(iscc.getVagonesCerradasValvulas().toString());
		}
		if (iscc.getVagonesCerradasValvulasObservaciones() != null) {
			fields.get("vagones_cerradas_valvulas_observaciones")
					.setValue(iscc.getVagonesCerradasValvulasObservaciones());
		}
		if (iscc.getVagonesBordesLevantados() != null) {
			fields.get("vagones_bordes_levantados").setValue(iscc.getVagonesBordesLevantados().toString());
		}
		if (iscc.getVagonesBordesLevantadosObservaciones() != null) {
			fields.get("vagones_bordes_levantados_observaciones")
					.setValue(iscc.getVagonesBordesLevantadosObservaciones());
		}
		if (iscc.getVagonesBordesBajados() != null) {
			fields.get("vagones_bordes_bajados").setValue(iscc.getVagonesBordesBajados().toString());
		}
		if (iscc.getVagonesBordesBajadosObservaciones() != null) {
			fields.get("vagones_bordes_bajados_observaciones").setValue(iscc.getVagonesBordesBajadosObservaciones());
		}
		if (iscc.getVagonesBordesBajadosInmovil() != null) {
			fields.get("vagones_bordes_bajados_inmovil").setValue(iscc.getVagonesBordesBajadosInmovil().toString());
		}
		if (iscc.getVagonesBordesBajadosInmovilObservaciones() != null) {
			fields.get("vagones_bordes_bajados_inmovil_observaciones")
					.setValue(iscc.getVagonesBordesBajadosInmovilObservaciones());
		}
		if (iscc.getVagonesVisiblesInscripciones() != null) {
			fields.get("vagones_visibles_inscripciones").setValue(iscc.getVagonesVisiblesInscripciones().toString());
		}
		if (iscc.getVagonesVisiblesInscripcionesObservaciones() != null) {
			fields.get("vagones_visibles_inscripciones_observaciones")
					.setValue(iscc.getVagonesVisiblesInscripcionesObservaciones());
		}
		if (iscc.getVagonesTelerosAmovibles() != null) {
			fields.get("vagones_teleros_amovibles").setValue(iscc.getVagonesTelerosAmovibles().toString());
		}
		if (iscc.getVagonesTelerosAmoviblesObservaciones() != null) {
			fields.get("vagones_teleros_amovibles_observaciones")
					.setValue(iscc.getVagonesTelerosAmoviblesObservaciones());
		}
		if (iscc.getVagonesTelerosPivotantes() != null) {
			fields.get("vagones_teleros_pivotantes").setValue(iscc.getVagonesTelerosPivotantes().toString());
		}
		if (iscc.getVagonesTelerosPivotantesObservaciones() != null) {
			fields.get("vagones_teleros_pivotantes_observaciones")
					.setValue(iscc.getVagonesTelerosPivotantesObservaciones());
		}
		if (iscc.getVagonesTelerosPivotantesHorizontal() != null) {
			fields.get("vagones_teleros_pivotantes_horizontal")
					.setValue(iscc.getVagonesTelerosPivotantesHorizontal().toString());
		}
		if (iscc.getVagonesTelerosPivotantesHorizontalObservaciones() != null) {
			fields.get("vagones_teleros_pivotantes_horizontal_observaciones")
					.setValue(iscc.getVagonesTelerosPivotantesHorizontalObservaciones());
		}
		if (iscc.getVagonesCadenasTeleros() != null) {
			fields.get("vagones_cadenas_teleros").setValue(iscc.getVagonesCadenasTeleros().toString());
		}
		if (iscc.getVagonesCadenasTelerosObservaciones() != null) {
			fields.get("vagones_cadenas_teleros_observaciones").setValue(iscc.getVagonesCadenasTelerosObservaciones());
		}
		if (iscc.getVagonesDispositivosTension() != null) {
			fields.get("vagones_dispositivos_tension").setValue(iscc.getVagonesDispositivosTension().toString());
		}
		if (iscc.getVagonesDispositivosTensionObservaciones() != null) {
			fields.get("vagones_dispositivos_tension_observaciones")
					.setValue(iscc.getVagonesDispositivosTensionObservaciones());
		}
		if (iscc.getVagonesPiso() != null) {
			fields.get("vagones_piso").setValue(iscc.getVagonesPiso().toString());
		}
		if (iscc.getVagonesPisoObservaciones() != null) {
			fields.get("vagones_piso_observaciones").setValue(iscc.getVagonesPisoObservaciones());
		}
		if (iscc.getVagonesMercancias() != null) {
			fields.get("vagones_mercancias").setValue(iscc.getVagonesMercancias().toString());
		}
		if (iscc.getVagonesMercanciasObservaciones() != null) {
			fields.get("vagones_mercancias_observaciones").setValue(iscc.getVagonesMercanciasObservaciones());
		}
		if (iscc.getVagonesParedes() != null) {
			fields.get("vagones_paredes").setValue(iscc.getVagonesParedes().toString());
		}
		if (iscc.getVagonesParedesObservaciones() != null) {
			fields.get("vagones_paredes_observaciones").setValue(iscc.getVagonesParedesObservaciones());
		}
		if (iscc.getVagonesPuertas() != null) {
			fields.get("vagones_puertas").setValue(iscc.getVagonesPuertas().toString());
		}
		if (iscc.getVagonesPuertasObservaciones() != null) {
			fields.get("vagones_puertas_observaciones").setValue(iscc.getVagonesPuertasObservaciones());
		}
		if (iscc.getVagonesAnillas() != null) {
			fields.get("vagones_anillas").setValue(iscc.getVagonesAnillas().toString());
		}
		if (iscc.getVagonesAnillasObservaciones() != null) {
			fields.get("vagones_anillas_observaciones").setValue(iscc.getVagonesAnillasObservaciones());
		}
		if (iscc.getVagonesEquipamientos() != null) {
			fields.get("vagones_equipamientos").setValue(iscc.getVagonesEquipamientos().toString());
		}
		if (iscc.getVagonesEquipamientosObservaciones() != null) {
			fields.get("vagones_equipamientos_observaciones").setValue(iscc.getVagonesEquipamientosObservaciones());
		}
		if (iscc.getVagonesObservaciones() != null) {
			fields.get("vagones_observaciones").setValue(iscc.getVagonesObservaciones());
		}

		// 5
		if (iscc.getAcondicionamientoReparto() != null) {
			fields.get("acondicionamiento_reparto").setValue(iscc.getAcondicionamientoReparto().toString());
		}
		if (iscc.getAcondicionamientoRepartoObservaciones() != null) {
			fields.get("acondicionamiento_reparto_observaciones")
					.setValue(iscc.getAcondicionamientoRepartoObservaciones());
		}
		if (iscc.getAcondicionamientoMercancias() != null) {
			fields.get("acondicionamiento_mercancias").setValue(iscc.getAcondicionamientoMercancias().toString());
		}
		if (iscc.getAcondicionamientoMercanciasObservaciones() != null) {
			fields.get("acondicionamiento_mercancias_observaciones")
					.setValue(iscc.getAcondicionamientoMercanciasObservaciones());
		}
		if (iscc.getAcondicionamientoPesadas() != null) {
			fields.get("acondicionamiento_pesadas").setValue(iscc.getAcondicionamientoPesadas().toString());
		}
		if (iscc.getAcondicionamientoPesadasObservaciones() != null) {
			fields.get("acondicionamiento_pesadas_observaciones")
					.setValue(iscc.getAcondicionamientoPesadasObservaciones());
		}
		if (iscc.getAcondicionamientoSusceptibles() != null) {
			fields.get("acondicionamiento_susceptibles").setValue(iscc.getAcondicionamientoSusceptibles().toString());
		}
		if (iscc.getAcondicionamientoSusceptiblesObservaciones() != null) {
			fields.get("acondicionamiento_susceptibles_observaciones")
					.setValue(iscc.getAcondicionamientoSusceptiblesObservaciones());
		}
		if (iscc.getAcondicionamientoSolidamenteSujeta() != null) {
			fields.get("acondicionamientoSolidamenteSujeta")
					.setValue(iscc.getAcondicionamientoSolidamenteSujeta().toString());
		}
		if (iscc.getAcondicionamientoSolidamenteSujetaObservaciones() != null) {
			fields.get("acondicionamiento_solidamente_sujeta_observaciones")
					.setValue(iscc.getAcondicionamientoSolidamenteSujetaObservaciones());
		}
		if (iscc.getAcondicionamientoLonaProxima() != null) {
			fields.get("acondicionamiento_lona_proxima").setValue(iscc.getAcondicionamientoLonaProxima().toString());
		}
		if (iscc.getAcondicionamientoLonaProximaObservaciones() != null) {
			fields.get("acondicionamiento_lona_proxima_observaciones")
					.setValue(iscc.getAcondicionamientoLonaProximaObservaciones());
		}
		if (iscc.getAcondicionamientoLonaSeparacion() != null) {
			fields.get("acondicionamiento_lona_separacion")
					.setValue(iscc.getAcondicionamientoLonaSeparacion().toString());
		}
		if (iscc.getAcondicionamientoLonaSeparacionObservaciones() != null) {
			fields.get("acondicionamiento_lona_separacion_observaciones")
					.setValue(iscc.getAcondicionamientoLonaSeparacionObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarParedes() != null) {
			fields.get("acondicionamiento_verificar_paredes")
					.setValue(iscc.getAcondicionamientoVerificarParedes().toString());
		}
		if (iscc.getAcondicionamientoVerificarParedesObservaciones() != null) {
			fields.get("acondicionamiento_verificar_paredes_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarParedesObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarCalces() != null) {
			fields.get("acondicionamiento_verificar_calces")
					.setValue(iscc.getAcondicionamientoVerificarCalces().toString());
		}
		if (iscc.getAcondicionamientoVerificarCalcesObservaciones() != null) {
			fields.get("acondicionamiento_verificar_calces_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarCalcesObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarAmarre() != null) {
			fields.get("acondicionamiento_verificar_amarre")
					.setValue(iscc.getAcondicionamientoVerificarAmarre().toString());
		}
		if (iscc.getAcondicionamientoVerificarAmarreObservaciones() != null) {
			fields.get("acondicionamiento_verificar_amarre_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarAmarreObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarDosAmarras() != null) {
			fields.get("acondicionamiento_verificar_dos_amarras")
					.setValue(iscc.getAcondicionamientoVerificarDosAmarras().toString());
		}
		if (iscc.getAcondicionamientoVerificarDosAmarrasObservaciones() != null) {
			fields.get("acondicionamiento_verificar_dos_ammarras_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarDosAmarrasObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarCalces7() != null) {
			fields.get("acondicionamiento_verificar_calces_7")
					.setValue(iscc.getAcondicionamientoVerificarCalces7().toString());
		}
		if (iscc.getAcondicionamientoVerificarCalces7Observaciones() != null) {
			fields.get("acondicionamiento_verificar_calces_7_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarCalces7Observaciones());
		}
		if (iscc.getAcondicionamientoVerificarBastidores() != null) {
			fields.get("acondicionamiento_verificar_bastidores")
					.setValue(iscc.getAcondicionamientoVerificarBastidores().toString());
		}
		if (iscc.getAcondicionamientoVerificarBastidoresObservaciones() != null) {
			fields.get("acondicionamiento_verificar_bastidores_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarBastidoresObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarCunas() != null) {
			fields.get("acondicionamiento_verificar_cunas")
					.setValue(iscc.getAcondicionamientoVerificarCunas().toString());
		}
		if (iscc.getAcondicionamientoVerificarCunasObservaciones() != null) {
			fields.get("acondicionamiento_verificar_cunas_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarCunasObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarTransversal() != null) {
			fields.get("acondicionamiento_verificar_transversal")
					.setValue(iscc.getAcondicionamientoVerificarTransversal().toString());
		}
		if (iscc.getAcondicionamientoVerificarTransversalObservaciones() != null) {
			fields.get("acondicionamiento_verificar_transversal_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarTransversalObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarLongitudinal() != null) {
			fields.get("acondicionamiento_verificar_longitudinal")
					.setValue(iscc.getAcondicionamientoVerificarLongitudinal().toString());
		}
		if (iscc.getAcondicionamientoVerificarLongitudinalObservaciones() != null) {
			fields.get("acondicionamiento_verificar_longitudinal_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarLongitudinalObservaciones());
		}
		if (iscc.getAcondicionamientoVerificarBobinasMenos10() != null) {
			fields.get("acondicionamiento_verificar_bobinas_menos_10")
					.setValue(iscc.getAcondicionamientoVerificarBobinasMenos10().toString());
		}
		if (iscc.getAcondicionamientoVerificarBobinasMenos10Observaciones() != null) {
			fields.get("acondicionamiento_verificar_bobinas_menos_10_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarBobinasMenos10Observaciones());
		}
		if (iscc.getAcondicionamientoVerificarBobinasMas10() != null) {
			fields.get("acondicionamiento_verificar_bobinas_mas_10")
					.setValue(iscc.getAcondicionamientoVerificarBobinasMas10().toString());
		}
		if (iscc.getAcondicionamientoVerificarBobinasMas10Observaciones() != null) {
			fields.get("acondicionamiento_verificar_bobinas_mas_10_observaciones")
					.setValue(iscc.getAcondicionamientoVerificarBobinasMas10Observaciones());
		}
		if (iscc.getAcondicionamientoBascularAgrupamiento() != null) {
			fields.get("acondicionamiento_bascular_agrupamiento")
					.setValue(iscc.getAcondicionamientoBascularAgrupamiento().toString());
		}
		if (iscc.getAcondicionamientoBascularAgrupamientoObservaciones() != null) {
			fields.get("acondicionamiento_bascular_agrupamiento_observaciones")
					.setValue(iscc.getAcondicionamientoBascularAgrupamientoObservaciones());
		}
		if (iscc.getAcondicionamientoBascularPuntales() != null) {
			fields.get("acondicionamiento_bascular_puntales")
					.setValue(iscc.getAcondicionamientoBascularPuntales().toString());
		}
		if (iscc.getAcondicionamientoBascularPuntalesObservaciones() != null) {
			fields.get("acondicionamiento_bascular_puntales_observaciones")
					.setValue(iscc.getAcondicionamientoBascularPuntalesObservaciones());
		}
		if (iscc.getAcondicionamientoBascularCaballetes() != null) {
			fields.get("acondicionamiento_bascular_caballetes")
					.setValue(iscc.getAcondicionamientoBascularCaballetes().toString());
		}
		if (iscc.getAcondicionamientoBascularCaballetesObservaciones() != null) {
			fields.get("acondicionamiento_bascular_caballetes_observaciones")
					.setValue(iscc.getAcondicionamientoBascularCaballetesObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaRepartidas() != null) {
			fields.get("acondicionamiento_apilada_repartidas")
					.setValue(iscc.getAcondicionamientoApiladaRepartidas().toString());
		}
		if (iscc.getAcondicionamientoApiladaRepartidasObservaciones() != null) {
			fields.get("acondicionamiento_apilada_repartidas_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaRepartidasObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaAnchura() != null) {
			fields.get("acondicionamiento_apilada_anchura")
					.setValue(iscc.getAcondicionamientoApiladaAnchura().toString());
		}
		if (iscc.getAcondicionamientoApiladaAnchuraObservaciones() != null) {
			fields.get("acondicionamiento_apilada_anchura_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaAnchuraObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaCapas() != null) {
			fields.get("acondicionamiento_apilada_capas").setValue(iscc.getAcondicionamientoApiladaCapas().toString());
		}
		if (iscc.getAcondicionamientoApiladaCapasObservaciones() != null) {
			fields.get("acondicionamiento_apilada_capas_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaCapasObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaRozamiento() != null) {
			fields.get("acondicionamiento_apilada_rozamiento")
					.setValue(iscc.getAcondicionamientoApiladaRozamiento().toString());
		}
		if (iscc.getAcondicionamientoApiladaRozamientoObservaciones() != null) {
			fields.get("acondicionamiento_apilada_rozamiento_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaRozamientoObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaBastidores() != null) {
			fields.get("acondicionamiento_apilada_bastidores")
					.setValue(iscc.getAcondicionamientoApiladaBastidores().toString());
		}
		if (iscc.getAcondicionamientoApiladaBastidoresObservaciones() != null) {
			fields.get("acondicionamiento_apilada_bastidores_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaBastidoresObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaUnidades() != null) {
			fields.get("acondicionamiento_apilada_unidades")
					.setValue(iscc.getAcondicionamientoApiladaUnidades().toString());
		}
		if (iscc.getAcondicionamientoApiladaUnidadesObservaciones() != null) {
			fields.get("acondicionamiento_apilada_unidades_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaUnidadesObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaCintas() != null) {
			fields.get("acondicionamiento_apilada_cintas")
					.setValue(iscc.getAcondicionamientoApiladaCintas().toString());
		}
		if (iscc.getAcondicionamientoApiladaCintasObservaciones() != null) {
			fields.get("acondicionamiento_apilada_cintas_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaCintasObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaParteInferior() != null) {
			fields.get("acondicionamiento_apilada_parte_inferior")
					.setValue(iscc.getAcondicionamientoApiladaParteInferior().toString());
		}
		if (iscc.getAcondicionamientoApiladaParteInferiorObservaciones() != null) {
			fields.get("acondicionamiento_apilada_parte_inferior_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaParteInferiorObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaIntercalares() != null) {
			fields.get("acondicionamiento_apilada_intercalares")
					.setValue(iscc.getAcondicionamientoApiladaIntercalares().toString());
		}
		if (iscc.getAcondicionamientoApiladaIntercalaresObservaciones() != null) {
			fields.get("acondicionamiento_apilada_intercalares_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaIntercalaresObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaImbricadas() != null) {
			fields.get("acondicionamiento_apilada_imbricadas")
					.setValue(iscc.getAcondicionamientoApiladaImbricadas().toString());
		}
		if (iscc.getAcondicionamientoApiladaImbricadasObservaciones() != null) {
			fields.get("acondicionamiento_apilada_imbricadas_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaImbricadasObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaOscilar() != null) {
			fields.get("acondicionamiento_apilada_oscilar")
					.setValue(iscc.getAcondicionamientoApiladaOscilar().toString());
		}
		if (iscc.getAcondicionamientoApiladaOscilarObservaciones() != null) {
			fields.get("acondicionamiento_apilada_oscilar_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaOscilarObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaTubos() != null) {
			fields.get("acondicionamiento_apilada_tubos").setValue(iscc.getAcondicionamientoApiladaTubos().toString());
		}
		if (iscc.getAcondicionamientoApiladaTubosObservaciones() != null) {
			fields.get("acondicionamiento_apilada_tubos_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaTubosObservaciones());
		}
		if (iscc.getAcondicionamientoApiladaCapasCalces() != null) {
			fields.get("acondicionamiento_apilada_capas_calces")
					.setValue(iscc.getAcondicionamientoApiladaCapasCalces().toString());
		}
		if (iscc.getAcondicionamientoApiladaCapasCalcesObservaciones() != null) {
			fields.get("acondicionamiento_apilada_capas_calces_observaciones")
					.setValue(iscc.getAcondicionamientoApiladaCapasCalcesObservaciones());
		}
		if (iscc.getAcondicionamientoRigidas() != null) {
			fields.get("acondicionamiento_rigidas").setValue(iscc.getAcondicionamientoRigidas().toString());
		}
		if (iscc.getAcondicionamientoRigidasObservaciones() != null) {
			fields.get("acondicionamiento_rigidas_observaciones")
					.setValue(iscc.getAcondicionamientoRigidasObservaciones());
		}
		if (iscc.getAcondicionamientoFlexibles() != null) {
			fields.get("acondicionamiento_flexibles").setValue(iscc.getAcondicionamientoFlexibles().toString());
		}
		if (iscc.getAcondicionamientoFlexiblesObservaciones() != null) {
			fields.get("acondicionamiento_flexibles_observaciones")
					.setValue(iscc.getAcondicionamientoFlexiblesObservaciones());
		}
		if (iscc.getAcondicionamientoObservaciones() != null) {
			fields.get("acondicionamiento_observaciones").setValue(iscc.getAcondicionamientoObservaciones());
		}

		// 6
		if (iscc.getMediosCalcesRetencion() != null) {
			fields.get("medios_calces_retencion").setValue(iscc.getMediosCalcesRetencion().toString());
		}
		if (iscc.getMediosCalcesRetencionObservaciones() != null) {
			fields.get("medios_calces_retencion_observaciones").setValue(iscc.getMediosCalcesRetencionObservaciones());
		}
		if (iscc.getMediosCalcesRodadura() != null) {
			fields.get("medios_calces_rodadura").setValue(iscc.getMediosCalcesRodadura().toString());
		}
		if (iscc.getMediosCalcesRodaduraObservaciones() != null) {
			fields.get("medios_calces_rodadura_observaciones").setValue(iscc.getMediosCalcesRodaduraObservaciones());
		}
		if (iscc.getMediosIntercalares() != null) {
			fields.get("medios_intercalares").setValue(iscc.getMediosIntercalares().toString());
		}
		if (iscc.getMediosIntercalaresObservaciones() != null) {
			fields.get("medios_intercalares_observaciones").setValue(iscc.getMediosIntercalaresObservaciones());
		}
		if (iscc.getMediosAmarres() != null) {
			fields.get("medios_amarres").setValue(iscc.getMediosAmarres().toString());
		}
		if (iscc.getMediosAmarresObservaciones() != null) {
			fields.get("medios_amarres_observaciones").setValue(iscc.getMediosAmarresObservaciones());
		}
		if (iscc.getMediosObservaciones() != null) {
			fields.get("medios_observaciones").setValue(iscc.getMediosObservaciones());
		}

		// 7
		if (iscc.getMedidasCautelares() != null) {
			fields.get("medidas_cautelares").setValue(iscc.getMedidasCautelares());
		}

		// 8
		if (iscc.getListaAnexos() != null) {
			fields.get("lista_anexos").setValue(iscc.getListaAnexos());
		}

		// 9
		if (iscc.getFirmaInspector() != null && iscc.getFirmaInspector().getNombreCompleto() != null
				&& iscc.getFechahoraFirmaInspector() != null) {
			fields.get("firma_inspector").setValue(
					iscc.getFirmaInspector().getNombreCompleto().concat("\n").concat(Utiles.convertDateToString(
							iscc.getFechahoraFirmaInspector(), Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}
		if (iscc.getFirmaResponsable() != null && iscc.getFirmaResponsable().getNombreCompleto() != null
				&& iscc.getFechahoraFirmaResponsable() != null) {
			fields.get("firma_responsable")
					.setValue(iscc.getFirmaResponsable().getNombreCompleto().concat("\n")
							.concat(Utiles.convertDateToString(iscc.getFechahoraFirmaResponsable(),
									Constantes.FORMATO_FECHA_COMPLETA_FIRMA)));
		}

		form.flattenFields();
		pdf.close();

		return baos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.eurogestion.ese.services.UtilesPDFService#
	 * generarDocumentoPlaAnuSegFer(eu.eurogestion.ese.domain.Pasf)
	 */
	@Override
	public byte[] generarDocumentoPlaAnuSegFer(Pasf pasf) throws IOException, SQLException {
		Blob template = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_PLA_ANU_SEG_FER).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdf = new PdfDocument(new PdfReader(template.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
		Map<String, PdfFormField> fields = form.getFormFields();
		int pageNum = 1;

		// 0
		if (pasf.getAnno() != null) {
			fields.get("anno").setValue(pasf.getAnno().toString());
			form.partialFormFlattening("anno");
		}

		// 1
		if (pasf.getDescarrilamiento() != null) {
			fields.get("descarrilamiento").setValue(pasf.getDescarrilamiento().toString());
			form.partialFormFlattening("descarrilamiento");
		}
		if (pasf.getColision() != null) {
			fields.get("colision").setValue(pasf.getColision().toString());
			form.partialFormFlattening("colision");
		}
		if (pasf.getAccidentePn() != null) {
			fields.get("accidente_pn").setValue(pasf.getAccidentePn().toString());
			form.partialFormFlattening("accidente_pn");
		}
		if (pasf.getIncendio() != null) {
			fields.get("incendio").setValue(pasf.getIncendio().toString());
			form.partialFormFlattening("incendio");
		}
		if (pasf.getArrollamientoVia() != null) {
			fields.get("arrollamiento_via").setValue(pasf.getArrollamientoVia().toString());
			form.partialFormFlattening("arrollamiento_via");
		}
		if (pasf.getArrollamientoInterseccion() != null) {
			fields.get("arrollamiento_interseccion").setValue(pasf.getArrollamientoInterseccion().toString());
			form.partialFormFlattening("arrollamiento_interseccion");
		}
		if (pasf.getCaidaPersonas() != null) {
			fields.get("caida_personas").setValue(pasf.getCaidaPersonas().toString());
			form.partialFormFlattening("caida_personas");
		}
		if (pasf.getSuicidio() != null) {
			fields.get("suicidio").setValue(pasf.getSuicidio().toString());
			form.partialFormFlattening("suicidio");
		}
		if (pasf.getDescomposiciónCarga() != null) {
			fields.get("descomposicion_carga").setValue(pasf.getDescomposiciónCarga().toString());
			form.partialFormFlattening("descomposicion_carga");
		}
		if (pasf.getDetencionTren() != null) {
			fields.get("detencion_tren").setValue(pasf.getDetencionTren().toString());
			form.partialFormFlattening("detencion_tren");
		}
		if (pasf.getInvasionVia() != null) {
			fields.get("invasion_via").setValue(pasf.getInvasionVia().toString());
			form.partialFormFlattening("invasion_via");
		}
		if (pasf.getIncidenteTe() != null) {
			fields.get("incidente_te").setValue(pasf.getIncidenteTe().toString());
			form.partialFormFlattening("incidente_te");
		}
		if (pasf.getRebaseSenal() != null) {
			fields.get("rebase_senal").setValue(pasf.getRebaseSenal().toString());
			form.partialFormFlattening("rebase_senal");
		}
		if (pasf.getConatoColision() != null) {
			fields.get("conato_colision").setValue(pasf.getConatoColision().toString());
			form.partialFormFlattening("conato_colision");
		}
		if (pasf.getEnganche() != null) {
			fields.get("enganche").setValue(pasf.getEnganche().toString());
			form.partialFormFlattening("enganche");
		}
		if (pasf.getOtros() != null) {
			fields.get("otros").setValue(pasf.getOtros().toString());
			form.partialFormFlattening("otros");
		}

		// 2
		if (pasf.getIso() != null) {
			fields.get("iso").setValue(pasf.getIso().toString());
			form.partialFormFlattening("iso");
		}
		if (pasf.getIssc() != null) {
			fields.get("issc").setValue(pasf.getIssc().toString());
			form.partialFormFlattening("issc");
		}
		if (pasf.getIsmp() != null) {
			fields.get("ismp").setValue(pasf.getIsmp().toString());
			form.partialFormFlattening("ismp");
		}
		if (pasf.getIseet() != null) {
			fields.get("iseet").setValue(pasf.getIseet().toString());
			form.partialFormFlattening("iseet");
		}
		if (pasf.getCad() != null) {
			fields.get("cad").setValue(pasf.getCad().toString());
			form.partialFormFlattening("cad");
		}

		// 3
		if (pasf.getCursos() != null) {
			fields.get("cursos").setValue(pasf.getCursos().toString());
			form.partialFormFlattening("cursos");
		}
		if (pasf.getRevisiones() != null) {
			fields.get("revisiones").setValue(pasf.getRevisiones().toString());
			form.partialFormFlattening("revisiones");
		}

		// 4
		if (pasf.getAuditorias() != null) {
			fields.get("auditorias").setValue(pasf.getAuditorias().toString());
			form.partialFormFlattening("auditorias");
		}

		// 5
		pageNum++;
		for (int filaAS = 0; filaAS < 30; filaAS++) {
			form.partialFormFlattening("as_tipo_" + filaAS);
			form.partialFormFlattening("as_desc_" + filaAS);
			form.partialFormFlattening("as_nins_" + filaAS);
			form.partialFormFlattening("as_dura_" + filaAS);
		}
		int filaAS = 0;
		int finPrimeralistaInspeccion = pasf.getListInspeccionPasf().size() > 30 ? 30
				: pasf.getListInspeccionPasf().size();
		for (InspeccionPasf inspeccionPasf : pasf.getListInspeccionPasf().subList(0, finPrimeralistaInspeccion)) {

			if (inspeccionPasf.getTipoInspeccion() != null && inspeccionPasf.getTipoInspeccion().getCodigo() != null) {
				fields.get("as_tipo_" + filaAS).setValue(inspeccionPasf.getTipoInspeccion().getCodigo());
			}
			if (inspeccionPasf.getDescripcion() != null) {
				fields.get("as_desc_" + filaAS).setValue(inspeccionPasf.getDescripcion());
			}
			if (inspeccionPasf.getNumInspecciones() != null) {
				fields.get("as_nins_" + filaAS).setValue(inspeccionPasf.getNumInspecciones().toString());
			}
			if (inspeccionPasf.getDuracion() != null) {
				fields.get("as_dura_" + filaAS).setValue(inspeccionPasf.getDuracion().toString());
			}
			filaAS++;
		}
		form.flattenFields();
		while (filaAS < pasf.getListInspeccionPasf().size()) {
			pageNum++;

			int ultimo = filaAS + 30;
			if (ultimo > pasf.getListInspeccionPasf().size()) {
				ultimo = pasf.getListInspeccionPasf().size();
			}

			rellenarPDFInspeccion(pdf, pageNum, pasf.getListInspeccionPasf().subList(filaAS, ultimo));
			filaAS = filaAS + 30;
		}

		// 6
		pageNum++;
		for (int filaIS = 0; filaIS < 30; filaIS++) {
			form.partialFormFlattening("is_desc_" + filaIS);
			form.partialFormFlattening("is_dura_" + filaIS);
		}
		int filaIS = 0;
		int finPrimeralistaAuditoria = pasf.getListAuditoriaPasf().size() > 30 ? 30
				: pasf.getListAuditoriaPasf().size();
		for (AuditoriaPasf auditoriaPasf : pasf.getListAuditoriaPasf().subList(0, finPrimeralistaAuditoria)) {

			if (auditoriaPasf.getDescripcion() != null) {
				fields.get("is_desc_" + filaIS).setValue(auditoriaPasf.getDescripcion());
			}
			if (auditoriaPasf.getDuracion() != null) {
				fields.get("is_dura_" + filaIS).setValue(auditoriaPasf.getDuracion().toString());
			}
			filaIS++;
		}
		form.flattenFields();

		while (filaIS < pasf.getListAuditoriaPasf().size()) {
			pageNum++;

			int ultimo = filaIS + 30;
			if (ultimo > pasf.getListAuditoriaPasf().size()) {
				ultimo = pasf.getListAuditoriaPasf().size();
			}

			rellenarPDFAuditoria(pdf, pageNum, pasf.getListAuditoriaPasf().subList(filaIS, ultimo));
			filaIS = filaIS + 30;
		}

		// 7
		pageNum++;
		for (int filaAF = 0; filaAF < 30; filaAF++) {
			form.partialFormFlattening("af_cat_" + filaAF);
			form.partialFormFlattening("af_cargo_" + filaAF);
			form.partialFormFlattening("af_desc_" + filaAF);
			form.partialFormFlattening("af_asis_" + filaAF);
			form.partialFormFlattening("af_dura_" + filaAF);
		}
		int filaAF = 0;

		int finPrimeralistaCurso = pasf.getListCursoPasf().size() > 30 ? 30 : pasf.getListCursoPasf().size();
		for (CursoPasf cursoPasf : pasf.getListCursoPasf().subList(0, finPrimeralistaCurso)) {

			if (cursoPasf.getCategoria() != null && cursoPasf.getCategoria().getValor() != null) {
				fields.get("af_cat_" + filaAF).setValue(cursoPasf.getCategoria().getValor());
			}
			if (cursoPasf.getCargo() != null && cursoPasf.getCargo().getAcronimo() != null) {
				fields.get("af_cargo_" + filaAF).setValue(cursoPasf.getCargo().getAcronimo());
			}
			if (cursoPasf.getDescripcion() != null) {
				fields.get("af_desc_" + filaAF).setValue(cursoPasf.getDescripcion());
			}
			if (cursoPasf.getDescripcion() != null) {
				fields.get("af_asis_" + filaAF).setValue(cursoPasf.getNumAsistentes().toString());
			}
			if (cursoPasf.getDuracion() != null) {
				fields.get("af_dura_" + filaAF).setValue(cursoPasf.getDuracion().toString());
			}
			filaAF++;
		}

		form.flattenFields();

		while (filaAF < pasf.getListCursoPasf().size()) {
			pageNum++;

			int ultimo = filaAF + 30;
			if (ultimo > pasf.getListCursoPasf().size()) {
				ultimo = pasf.getListCursoPasf().size();
			}

			rellenarPDFCurso(pdf, pageNum, pasf.getListCursoPasf().subList(filaAF, ultimo));
			filaAF = filaAF + 30;
		}

		// acros
		pageNum++;
		for (int filaAcro = 0; filaAcro < 57; filaAcro++) {
			form.partialFormFlattening("acro_" + filaAcro);
			form.partialFormFlattening("acro_desc_" + filaAcro);
		}
		int filaAcro = 0;

		List<Cargo> listaCargo = cargoDao.findAll();
		int finPrimeralistaAcro = listaCargo.size() > 57 ? 57 : listaCargo.size();
		for (Cargo cargo : listaCargo.subList(0, finPrimeralistaAcro)) {

			if (cargo.getAcronimo() != null) {
				fields.get("acro_" + filaAcro).setValue(cargo.getAcronimo());
			}
			if (cargo.getNombre() != null) {
				fields.get("acro_desc_" + filaAcro).setValue(cargo.getNombre());
			}
			filaAcro++;
		}

		form.flattenFields();

		while (filaAcro < listaCargo.size()) {
			pageNum++;

			int ultimo = filaAcro + 57;
			if (ultimo > listaCargo.size()) {
				ultimo = listaCargo.size();
			}

			rellenarPDFAcro(pdf, pageNum, listaCargo.subList(filaAcro, ultimo));
			filaAcro = filaAcro + 57;
		}

		form.flattenFields();
		pdf.close();
		return baos.toByteArray();
	}

	private void rellenarPDFAcro(PdfDocument pdf, int pageNum, List<Cargo> lista) throws IOException, SQLException {

		Blob blobAF = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_ACRO).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdfAF = new PdfDocument(new PdfReader(blobAF.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfAF, true);
		Map<String, PdfFormField> fields = form.getFormFields();
		for (int filaAcro = 0; filaAcro < 57; filaAcro++) {
			form.partialFormFlattening("acro_" + filaAcro);
			form.partialFormFlattening("acro_desc_" + filaAcro);

		}
		int filaAcro = 0;
		for (Cargo cargo : lista) {
			if (cargo.getAcronimo() != null) {
				fields.get("af_cat_" + filaAcro).setValue(cargo.getAcronimo());
			}
			if (cargo.getNombre() != null) {
				fields.get("af_cargo_" + filaAcro).setValue(cargo.getNombre());
			}
			filaAcro++;
		}
		form.flattenFields();
		pdfAF.close();
		Blob aux;

		aux = new SerialBlob(baos.toByteArray());

		PdfDocument pdfAFLectura = new PdfDocument(new PdfReader(aux.getBinaryStream()));
		PdfPage pageAcro = pdfAFLectura.getPage(1);

		pdf.addPage(pageNum, pageAcro.copyTo(pdf));
		pdfAFLectura.close();

	}

	private void rellenarPDFCurso(PdfDocument pdf, int pageNum, List<CursoPasf> lista)
			throws IOException, SQLException {

		Blob blobAF = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_AF).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdfAF = new PdfDocument(new PdfReader(blobAF.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfAF, true);
		Map<String, PdfFormField> fields = form.getFormFields();
		for (int filaAF = 0; filaAF < 30; filaAF++) {
			form.partialFormFlattening("af_cat_" + filaAF);
			form.partialFormFlattening("af_cargo_" + filaAF);
			form.partialFormFlattening("af_desc_" + filaAF);
			form.partialFormFlattening("af_asis_" + filaAF);
			form.partialFormFlattening("af_dura_" + filaAF);
		}
		int filaAF = 0;
		for (CursoPasf cursoPasf : lista) {
			if (cursoPasf.getCategoria() != null && cursoPasf.getCategoria().getValor() != null) {
				fields.get("af_cat_" + filaAF).setValue(cursoPasf.getCategoria().getValor());
			}
			if (cursoPasf.getCargo() != null && cursoPasf.getCargo().getAcronimo() != null) {
				fields.get("af_cargo_" + filaAF).setValue(cursoPasf.getCargo().getAcronimo());
			}
			if (cursoPasf.getDescripcion() != null) {
				fields.get("af_desc_" + filaAF).setValue(cursoPasf.getDescripcion());
			}
			if (cursoPasf.getDescripcion() != null) {
				fields.get("af_asis_" + filaAF).setValue(cursoPasf.getNumAsistentes().toString());
			}
			if (cursoPasf.getDuracion() != null) {
				fields.get("af_dura_" + filaAF).setValue(cursoPasf.getDuracion().toString());
			}
			filaAF++;
		}
		form.flattenFields();
		pdfAF.close();
		Blob aux;

		aux = new SerialBlob(baos.toByteArray());

		PdfDocument pdfAFLectura = new PdfDocument(new PdfReader(aux.getBinaryStream()));
		PdfPage pageAF = pdfAFLectura.getPage(1);

		pdf.addPage(pageNum, pageAF.copyTo(pdf));
		pdfAFLectura.close();

	}

	private void rellenarPDFAuditoria(PdfDocument pdf, int pageNum, List<AuditoriaPasf> lista)
			throws IOException, SQLException {

		Blob blobIS = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_IS).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdfIS = new PdfDocument(new PdfReader(blobIS.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfIS, true);
		Map<String, PdfFormField> fields = form.getFormFields();
		for (int filaIS = 0; filaIS < 30; filaIS++) {
			form.partialFormFlattening("is_desc_" + filaIS);
			form.partialFormFlattening("is_dura_" + filaIS);
		}
		int filaIS = 0;
		for (AuditoriaPasf auditoriaPasf : lista) {
			if (auditoriaPasf.getDescripcion() != null) {
				fields.get("is_desc_" + filaIS).setValue(auditoriaPasf.getDescripcion());
			}
			if (auditoriaPasf.getDuracion() != null) {
				fields.get("is_dura_" + filaIS).setValue(auditoriaPasf.getDuracion().toString());
			}
			filaIS++;
		}
		form.flattenFields();
		pdfIS.close();
		Blob aux;

		aux = new SerialBlob(baos.toByteArray());

		PdfDocument pdfISLectura = new PdfDocument(new PdfReader(aux.getBinaryStream()));
		PdfPage pageIS = pdfISLectura.getPage(1);

		pdf.addPage(pageNum, pageIS.copyTo(pdf));
		pdfISLectura.close();

	}

	private void rellenarPDFInspeccion(PdfDocument pdf, int pageNum, List<InspeccionPasf> lista)
			throws IOException, SQLException {

		Blob blobAS = imagenesDAO.getOne(Constantes.IMAGENES_TEMPLATE_PLA_ANU_SEG_FER_AS).getValor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfDocument pdfAS = new PdfDocument(new PdfReader(blobAS.getBinaryStream()), new PdfWriter(baos));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfAS, true);
		Map<String, PdfFormField> fields = form.getFormFields();
		for (int filaAS = 0; filaAS < 30; filaAS++) {
			form.partialFormFlattening("as_tipo_" + filaAS);
			form.partialFormFlattening("as_desc_" + filaAS);
			form.partialFormFlattening("as_nins_" + filaAS);
			form.partialFormFlattening("as_dura_" + filaAS);
		}
		int filaAS = 0;
		for (InspeccionPasf inspeccionPasf : lista) {
			if (inspeccionPasf.getTipoInspeccion() != null && inspeccionPasf.getTipoInspeccion().getCodigo() != null) {
				fields.get("as_tipo_" + filaAS).setValue(inspeccionPasf.getTipoInspeccion().getCodigo());
			}
			if (inspeccionPasf.getDescripcion() != null) {
				fields.get("as_desc_" + filaAS).setValue(inspeccionPasf.getDescripcion());
			}
			if (inspeccionPasf.getNumInspecciones() != null) {
				fields.get("as_nins_" + filaAS).setValue(inspeccionPasf.getNumInspecciones().toString());
			}
			if (inspeccionPasf.getDuracion() != null) {
				fields.get("as_dura_" + filaAS).setValue(inspeccionPasf.getDuracion().toString());
			}
			filaAS++;
		}
		form.flattenFields();
		pdfAS.close();
		Blob aux;

		aux = new SerialBlob(baos.toByteArray());

		PdfDocument pdfASLectura = new PdfDocument(new PdfReader(aux.getBinaryStream()));
		PdfPage pageAS = pdfASLectura.getPage(1);

		pdf.addPage(pageNum, pageAS.copyTo(pdf));
		pdfASLectura.close();

	}

	public Evidencia crearEvidencia(String titulo, byte[] fichero, String md5, TipoEvidencia tipoEvidencia,
			Date fechaDocumento, String tipoFichero) throws EseException {

		Evidencia evidencia = new Evidencia();
		evidencia.setDocumento(crearDocumento(titulo, fichero, md5, fechaDocumento, tipoFichero, null));
		evidencia.setTipoEvidencia(tipoEvidencia);
		evidenciaDAO.save(evidencia);

		return evidencia;
	}

	public Documento crearDocumento(String titulo, byte[] fichero, String md5, Date fechaDocumento, String tipoFichero,
			String observaciones) throws EseException {
		Documento documento = new Documento();
		documento.setFechaDocumento(fechaDocumento);
		documento.setFechaSubida(Utiles.sysdate());
		documento.setHashDocumento(md5);
		if (StringUtils.isNotBlank(observaciones)) {
			documento.setObservaciones(observaciones);
		}

		Blob aux;
		try {
			aux = new SerialBlob(fichero);
		} catch (SQLException e) {
			throw new EseException(e.getMessage());
		}

		documento.setFichero(aux);
		documento.setTipoFichero(tipoFichero);
		documento.setTitulo(titulo);
		documentoDAO.save(documento);
		return documento;
	}

	public void descargarEvidencia(Documento fichero, HttpServletResponse response) throws IOException {
		DocumentoAtril documentoAtril = descargaPDF(fichero);

		String md5Blockchain;
		try {
			md5Blockchain = blockChainService.downloadDocumento(fichero.getIdDocumento().toString());
		} catch (EseException e) {
			throw new IOException(e.getMessage());
		}

		String md5PDF = Utiles.calculateHashMD5(documentoAtril.getFile());
		if (!md5PDF.equals(md5Blockchain)) {
			errorPantalla(response);
			return;
		}
		descargaArchivo(documentoAtril, response);
	}

	public void descargarDocumento(Documento fichero, HttpServletResponse response) throws IOException {
		DocumentoAtril documentoAtril = descargaPDF(fichero);

		String md5PDF = Utiles.calculateHashMD5(documentoAtril.getFile());
		if (!md5PDF.equals(fichero.getHashDocumento())) {
			errorPantalla(response);
			return;
		}
		descargaArchivo(documentoAtril, response);
	}
}
