package eu.eurogestion.ese.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.domain.RolPermiso;
import eu.eurogestion.ese.pojo.UsuarioLoginJSP;
import eu.eurogestion.ese.pojo.UsuarioRegistradoJSP;
import eu.eurogestion.ese.repository.ImagenesDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class LoginController {

	/** Repositories & Services **/

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public ImagenesDAO imagenesDAO;

	/** Functions **/

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(final UsuarioLoginJSP usuarioLogin, Model model, HttpSession session) {
		try {
			Personal personal = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(usuarioLogin.getNombre(),
					usuarioLogin.getPassword());
			if (personal != null) {
				Map<String, Integer> mapa = new HashMap<>();
				for (RolPermiso rolPermiso : personal.getRol().getListRolPermiso()) {
					mapa.put(String.valueOf(rolPermiso.getPermiso().getOpcion().getNombre()),
							rolPermiso.getPermiso().getTipoPermiso().getIdTipoPermiso());

				}
				UsuarioRegistradoJSP usuarioRegistrado = new UsuarioRegistradoJSP();
				usuarioRegistrado.setMapaPermisos(mapa);
				usuarioRegistrado.setMapaOpciones(Utiles.obtenerMapOpciones());
				usuarioRegistrado.setNombre(personal.getNombre());
				usuarioRegistrado.setApellido(personal.getApellido1());
				usuarioRegistrado.setCargo(personal.getCargo().getNombre());
				usuarioRegistrado.setIdPersonal(personal.getIdPersonal().toString());
				usuarioRegistrado.setLogoAplicacion(Base64.encodeBase64String(Utiles
						.convertBlobTobyteArray(imagenesDAO.getOne(Constantes.IMAGENES_LOGO_APLICACION).getValor())));
				usuarioRegistrado.setLogoEmpresa(Base64.encodeBase64String(Utiles
						.convertBlobTobyteArray(imagenesDAO.getOne(Constantes.IMAGENES_LOGO_COMPANIA).getValor())));
				session.setAttribute("usuario", usuarioRegistrado);
				return "welcome";
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		model.addAttribute("usuarioLogin", usuarioLogin);
		model.addAttribute("error", "La combinación de Usuario y Contraseña no es correcta.");
		return "login";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("usuarioLogin", new UsuarioLoginJSP());
		return "login";
	}

	@RequestMapping(value = "/volverOlvidarPassword", method = RequestMethod.GET)
	public String volverOlvidarPassword(Model model) {
		model.addAttribute("info", "Se ha restablecido la contraseña, por favor mire su email.");
		model.addAttribute("usuarioLogin", new UsuarioLoginJSP());
		return "login";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model, HttpSession session) {
		session.removeAttribute("usuario");
		return "redirect:/";
	}

	@RequestMapping(value = "/cancelar", method = { RequestMethod.GET, RequestMethod.POST })
	public String cancelar(HttpSession session) {
		Object usuario = session.getAttribute("usuario");
		if (usuario != null) {
			return "welcome";
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String welcome() {

		return "welcome";
	}

	/**
	 * METODO PARA IMPRESIONES DE PRUEBA, NO USAR / ELIMINAR
	 */
//	@RequestMapping(value = "/login", method = RequestMethod.POST)
//	public String login(Model model, HttpSession session, javax.servlet.http.HttpServletResponse response)
//			throws java.text.ParseException, java.io.IOException {
//
//		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
//
//		eu.eurogestion.ese.domain.Localidad localidad = new Localidad();
//		localidad.setNombre("MADRID");
//
//		eu.eurogestion.ese.domain.Pais pais = new Pais();
//		pais.setNombre("España");
//
//		eu.eurogestion.ese.domain.Compania compania = new Compania();
//		compania.setLocalidad(localidad);
//		compania.setPais(pais);
//		compania.setNombre("TRANSFESA");
//		compania.setLicencia("5/00M/2/02/5");
//		compania.setDocumento("A-06.000.871");
//		compania.setTipoVia("c/");
//		compania.setVia("Musgo");
//		compania.setNumero("1");
//		compania.setPlanta("");
//		compania.setPuerta("");
//
//		eu.eurogestion.ese.domain.Personal personal = new Personal();
//		personal.setCompania(compania);
//		personal.setApellido1("Sánchez");
//		personal.setApellido2("Roldán");
//		personal.setNombre("Manuel");
//		personal.setDocumento("81933907N");
//		personal.setTelefono("635214578");
//		personal.setNacionalidad("Española");
//		personal.setFechaNac(formatter.parse("1980/01/01"));
//		personal.setTipoVia("C/");
//		personal.setVia("Los Fernández");
//		personal.setNumero("3");
//		personal.setPlanta("");
//		personal.setPuerta("");
//		personal.setLocalidad(localidad);
//
//		eu.eurogestion.ese.domain.RevisionPsico revisionPsico = new eu.eurogestion.ese.domain.RevisionPsico();
//		revisionPsico.setFechaRealizacion(formatter.parse("2018/01/01"));
//		revisionPsico.setFechaCaducidad(formatter.parse("2020/01/01"));
//
//		eu.eurogestion.ese.domain.Titulo titulo = new eu.eurogestion.ese.domain.Titulo();
//		titulo.setPersonal(personal);
//		titulo.setRevisionPsico(revisionPsico);
//		titulo.setInfoAdicionalCategoria("Nada relevante");
//
//		eu.eurogestion.ese.domain.Curso curso1 = new eu.eurogestion.ese.domain.Curso();
//		curso1.setValidoDesde(formatter.parse("2018/01/01"));
//		curso1.setFechaCaducidad(formatter.parse("2019/01/01"));
//		curso1.setTituloCurso("Unidades de transporte combinado + MMPP");
//
//		eu.eurogestion.ese.domain.TituloCurso tituloCurso1 = new eu.eurogestion.ese.domain.TituloCurso();
//		tituloCurso1.setIdTituloCurso(1);
//		tituloCurso1.setTitulo(titulo);
//		tituloCurso1.setCurso(curso1);
//
//		eu.eurogestion.ese.domain.Curso curso2 = new eu.eurogestion.ese.domain.Curso();
//		curso2.setValidoDesde(formatter.parse("2018/02/01"));
//		curso2.setFechaCaducidad(formatter.parse("2020/01/01"));
//		curso2.setTituloCurso("Unidades de transporte simple");
//
//		eu.eurogestion.ese.domain.TituloCurso tituloCurso2 = new eu.eurogestion.ese.domain.TituloCurso();
//		tituloCurso2.setIdTituloCurso(2);
//		tituloCurso2.setTitulo(titulo);
//		tituloCurso2.setCurso(curso2);
//
//		List<eu.eurogestion.ese.domain.TituloCurso> listTituloCursos = new ArrayList<>();
//		listTituloCursos.add(tituloCurso1);
//		listTituloCursos.add(tituloCurso2);
//
//		titulo.setListTituloCursos(listTituloCursos);
//
////		eu.eurogestion.ese.utils.UtilesPdf.generarDocumentoHabPerOpeTrenAOT(response, titulo, "Óscar Lázaro Seco",
////				"Director de Seguridad en la Circulación", Boolean.TRUE);
//		eu.eurogestion.ese.utils.UtilesPdf.generarDocumentoHabPerOpeTrenAOT(response, titulo, "Óscar Lázaro Seco",
//				"Director de Seguridad en la Circulación", Boolean.FALSE);
//
//		return "login";
//	}

}