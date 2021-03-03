package eu.eurogestion.ese.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eurogestion.ese.domain.AnexoInformeAnomalia;
import eu.eurogestion.ese.domain.Anomalia;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.InformeAnomalias;
import eu.eurogestion.ese.domain.Iscc;
import eu.eurogestion.ese.domain.Iseet;
import eu.eurogestion.ese.domain.Ismp;
import eu.eurogestion.ese.domain.Iso;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.AddAnomaliaJSP;
import eu.eurogestion.ese.pojo.BuscadorInspeccionJSP;
import eu.eurogestion.ese.pojo.CrearInformeAnomaliasJSP;
import eu.eurogestion.ese.pojo.DetallePersonalJSP;
import eu.eurogestion.ese.repository.AnexoInformeAnomaliaDAO;
import eu.eurogestion.ese.repository.AnomaliaDAO;
import eu.eurogestion.ese.repository.InformeAnomaliasDAO;
import eu.eurogestion.ese.repository.IsDAO;
import eu.eurogestion.ese.repository.IsccDAO;
import eu.eurogestion.ese.repository.IseetDAO;
import eu.eurogestion.ese.repository.IsmpDAO;
import eu.eurogestion.ese.repository.IsoDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.services.CrearInformeAnomaliasService;
import eu.eurogestion.ese.services.UtilesPDFService;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alvaro
 *
 */
@Slf4j
@Controller
public class CrearInformeAnomaliasController {

	/**
	 * Repositorio de la clase de dominio Iso
	 */
	@Autowired
	public IsoDAO isoDAO;

	@Autowired
	public IseetDAO iseetDAO;

	@Autowired
	public IsmpDAO ismpDAO;

	@Autowired
	public IsccDAO isccDAO;

	@Autowired
	public IsDAO isDAO;

	/**
	 * Repositorio de la clase de dominio InformeAnomalias
	 */
	@Autowired
	public InformeAnomaliasDAO informeAnomaliasDAO;
	/**
	 * Repositorio de la clase de dominio AnexoInformeAnomalia
	 */
	@Autowired
	public AnexoInformeAnomaliaDAO anexoInformeAnomaliaDAO;

	/**
	 * Repositorio de la clase de dominio Anomalia
	 */
	@Autowired
	public AnomaliaDAO anomaliaDAO;

	@Autowired
	public PersonalDAO personalDAO;

	/**
	 * Servicio de la clase de CrearInformeAnomalias
	 */
	@Autowired
	public CrearInformeAnomaliasService crearInformeAnomaliasService;

	@Autowired
	public UtilesPDFService utilesPDFService;

	@ModelAttribute("personal")
	public List<Personal> listPersonalAll() {
		List<Personal> lista = new ArrayList<>();
		Personal estadoVerificacionInforme = new Personal();
		estadoVerificacionInforme.setNombre("Seleccione uno:");
		lista.add(estadoVerificacionInforme);
		lista.addAll(personalDAO.findByFechaBajaIsNull());
		return lista;
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/crearInformeAnomaliasISO", method = RequestMethod.POST)
	public String crearInformeAnomaliasISO(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iso iso = isoDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		if (iso.getIs().getInformeAnomalias() == null) {
			InformeAnomalias informeAnomalias = new InformeAnomalias();
			informeAnomaliasDAO.save(informeAnomalias);
			iso.getIs().setInformeAnomalias(informeAnomalias);
			isDAO.save(iso.getIs());
			isoDAO.save(iso);
		}
		crearInformeAnomaliasJSP.setCodigoInforme(iso.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iso.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iso.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iso.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iso.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iso.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iso.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iso.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.FALSE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISO);

		if (iso.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iso.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iso.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iso.getIs().getInformeAnomalias().getFirmaInspector() != null);

		crearInformeAnomaliasJSP.setTieneEvidencia(iso.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/verInformeAnomaliasISO", method = RequestMethod.POST)
	public String verInformeAnomaliasISO(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iso iso = isoDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));

		crearInformeAnomaliasJSP.setCodigoInforme(iso.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iso.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iso.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iso.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iso.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iso.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iso.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iso.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.TRUE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISO);
		if (iso.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iso.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iso.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iso.getIs().getInformeAnomalias().getFirmaInspector() != null);

		crearInformeAnomaliasJSP.setTieneEvidencia(iso.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/crearInformeAnomaliasISEET", method = RequestMethod.POST)
	public String crearInformeAnomaliasISEET(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iseet iseet = iseetDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		if (iseet.getIs().getInformeAnomalias() == null) {
			InformeAnomalias informeAnomalias = new InformeAnomalias();
			informeAnomaliasDAO.save(informeAnomalias);
			iseet.getIs().setInformeAnomalias(informeAnomalias);
			isDAO.save(iseet.getIs());
			iseetDAO.save(iseet);
		}
		crearInformeAnomaliasJSP.setCodigoInforme(iseet.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iseet.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iseet.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iseet.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iseet.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iseet.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iseet.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iseet.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.FALSE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISEET);
		if (iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iseet.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iseet.getIs().getInformeAnomalias().getFirmaInspector() != null);

		crearInformeAnomaliasJSP.setTieneEvidencia(iseet.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/verInformeAnomaliasISEET", method = RequestMethod.POST)
	public String verInformeAnomaliasISEET(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iseet iseet = iseetDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));

		crearInformeAnomaliasJSP.setCodigoInforme(iseet.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iseet.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iseet.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iseet.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iseet.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iseet.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iseet.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iseet.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.TRUE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISEET);
		if (iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iseet.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iseet.getIs().getInformeAnomalias().getFirmaInspector() != null);

		crearInformeAnomaliasJSP.setTieneEvidencia(iseet.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/crearInformeAnomaliasISMP", method = RequestMethod.POST)
	public String crearInformeAnomaliasISMP(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Ismp ismp = ismpDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		if (ismp.getIs().getInformeAnomalias() == null) {
			InformeAnomalias informeAnomalias = new InformeAnomalias();
			informeAnomaliasDAO.save(informeAnomalias);
			ismp.getIs().setInformeAnomalias(informeAnomalias);
			isDAO.save(ismp.getIs());
			ismpDAO.save(ismp);
		}
		crearInformeAnomaliasJSP.setCodigoInforme(ismp.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(ismp.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(ismp.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(ismp.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(ismp.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(ismp.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(ismp.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(ismp.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.FALSE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISMP);
		if (ismp.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					ismp.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (ismp.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					ismp.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(ismp.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| ismp.getIs().getInformeAnomalias().getFirmaInspector() != null);

		crearInformeAnomaliasJSP.setTieneEvidencia(ismp.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/verInformeAnomaliasISMP", method = RequestMethod.POST)
	public String verInformeAnomaliasISMP(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Ismp ismp = ismpDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));

		crearInformeAnomaliasJSP.setCodigoInforme(ismp.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(ismp.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(ismp.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(ismp.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(ismp.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(ismp.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(ismp.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(ismp.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.TRUE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISMP);
		if (ismp.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					ismp.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (ismp.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					ismp.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(ismp.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| ismp.getIs().getInformeAnomalias().getFirmaInspector() != null);
		crearInformeAnomaliasJSP.setTieneEvidencia(ismp.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/crearInformeAnomaliasISCC", method = RequestMethod.POST)
	public String crearInformeAnomaliasISCC(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iscc iscc = isccDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));
		if (iscc.getIs().getInformeAnomalias() == null) {
			InformeAnomalias informeAnomalias = new InformeAnomalias();
			informeAnomaliasDAO.save(informeAnomalias);
			iscc.getIs().setInformeAnomalias(informeAnomalias);
			isDAO.save(iscc.getIs());
			isccDAO.save(iscc);
		}
		crearInformeAnomaliasJSP.setCodigoInforme(iscc.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iscc.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iscc.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iscc.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iscc.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iscc.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iscc.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iscc.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.FALSE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISMP);

		if (iscc.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iscc.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iscc.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iscc.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iscc.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iscc.getIs().getInformeAnomalias().getFirmaInspector() != null);
		crearInformeAnomaliasJSP.setTieneEvidencia(iscc.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/verInformeAnomaliasISCC", method = RequestMethod.POST)
	public String verInformeAnomaliasISCC(BuscadorInspeccionJSP buscadorInspeccionJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iscc iscc = isccDAO.getOne(Integer.parseInt(buscadorInspeccionJSP.getIdInspeccion()));

		crearInformeAnomaliasJSP.setCodigoInforme(iscc.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iscc.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iscc.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iscc.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iscc.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iscc.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iscc.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iscc.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.TRUE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISMP);
		if (iscc.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iscc.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iscc.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iscc.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iscc.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iscc.getIs().getInformeAnomalias().getFirmaInspector() != null);
		crearInformeAnomaliasJSP.setTieneEvidencia(iscc.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/crearInformeAnomaliasPersonalISO", method = RequestMethod.POST)
	public String crearInformeAnomaliasPersonalISO(DetallePersonalJSP detallePersonalJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iso iso = isoDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));
		if (iso.getIs().getInformeAnomalias() == null) {
			InformeAnomalias informeAnomalias = new InformeAnomalias();
			informeAnomaliasDAO.save(informeAnomalias);
			iso.getIs().setInformeAnomalias(informeAnomalias);
			isDAO.save(iso.getIs());
			isoDAO.save(iso);
		}
		crearInformeAnomaliasJSP.setCodigoInforme(iso.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iso.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iso.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iso.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iso.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iso.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iso.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iso.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.FALSE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISO);

		crearInformeAnomaliasJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());
		crearInformeAnomaliasJSP.setPaginaVuelta("detallePersonal");

		if (iso.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iso.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iso.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iso.getIs().getInformeAnomalias().getFirmaInspector() != null);
		crearInformeAnomaliasJSP.setTieneEvidencia(iso.getIs().getInformeAnomalias().getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/verInformeAnomaliasPersonalISO", method = RequestMethod.POST)
	public String verInformeAnomaliasPersonalISO(DetallePersonalJSP detallePersonalJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iso iso = isoDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));

		crearInformeAnomaliasJSP.setCodigoInforme(iso.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iso.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iso.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iso.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iso.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iso.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iso.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iso.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.TRUE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISO);

		if (iso.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iso.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iso.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iso.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iso.getIs().getInformeAnomalias().getFirmaInspector() != null);
		crearInformeAnomaliasJSP.setTieneEvidencia(iso.getIs().getInformeAnomalias().getEvidencia20() != null);

		crearInformeAnomaliasJSP.setPaginaVuelta("detallePersonal");
		crearInformeAnomaliasJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/crearInformeAnomaliasPersonalISEET", method = RequestMethod.POST)
	public String crearInformeAnomaliasPersonalISEET(DetallePersonalJSP detallePersonalJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iseet iseet = iseetDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));
		if (iseet.getIs().getInformeAnomalias() == null) {
			InformeAnomalias informeAnomalias = new InformeAnomalias();
			informeAnomaliasDAO.save(informeAnomalias);
			iseet.getIs().setInformeAnomalias(informeAnomalias);
			isDAO.save(iseet.getIs());
			iseetDAO.save(iseet);
		}
		crearInformeAnomaliasJSP.setCodigoInforme(iseet.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iseet.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iseet.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iseet.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iseet.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iseet.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iseet.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iseet.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.FALSE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISEET);

		if (iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iseet.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iseet.getIs().getInformeAnomalias().getFirmaInspector() != null);
		crearInformeAnomaliasJSP.setTieneEvidencia(iseet.getIs().getInformeAnomalias().getEvidencia20() != null);

		crearInformeAnomaliasJSP.setPaginaVuelta("detallePersonal");
		crearInformeAnomaliasJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/verInformeAnomaliasPersonalISEET", method = RequestMethod.POST)
	public String verInformeAnomaliasPersonalISEET(DetallePersonalJSP detallePersonalJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();

		Iseet iseet = iseetDAO.getOne(Integer.parseInt(detallePersonalJSP.getIdInspeccion()));

		crearInformeAnomaliasJSP.setCodigoInforme(iseet.getIs().getInformeAnomalias().getCodigoInforme());
		crearInformeAnomaliasJSP.setNumeroInspeccion(iseet.getIs().getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(
				Utiles.convertDateToString(iseet.getIs().getFechaInspeccion(), Constantes.FORMATO_FECHA_PANTALLA));
		crearInformeAnomaliasJSP.setNombreInspector(iseet.getIs().getInspector().getNombreCompleto());
		crearInformeAnomaliasJSP
				.setDescripcionDatosInspeccion(iseet.getIs().getInformeAnomalias().getDescripcionInspeccion());
		crearInformeAnomaliasJSP
				.setObservacionesDatosInspeccion(iseet.getIs().getInformeAnomalias().getObservacionesInspeccion());
		crearInformeAnomaliasJSP
				.setMedidasCautelares(iseet.getIs().getInformeAnomalias().getDescripcionMedidasCautelares());
		crearInformeAnomaliasJSP
				.setIdInformeAnomalias(iseet.getIs().getInformeAnomalias().getIdInformeAnomalias().toString());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(Boolean.TRUE);
		crearInformeAnomaliasJSP.setIdTipoInspeccion(Constantes.TIPO_INSPECCION_ISEET);

		if (iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP.setIdFirmaResponsableSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaResponsable().getIdPersonal());
		}

		if (iseet.getIs().getInformeAnomalias().getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(
					iseet.getIs().getInformeAnomalias().getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(iseet.getIs().getInformeAnomalias().getFirmaResponsable() != null
				|| iseet.getIs().getInformeAnomalias().getFirmaInspector() != null);

		crearInformeAnomaliasJSP.setTieneEvidencia(iseet.getIs().getInformeAnomalias().getEvidencia20() != null);

		crearInformeAnomaliasJSP.setPaginaVuelta("detallePersonal");
		crearInformeAnomaliasJSP.setIdPersonal(detallePersonalJSP.getIdPersonal());

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	private void cargarListasInformeAnomalias(Model model, String idInformeAnomalia, int pageAnexos, int pageAnomalias,
			int size) {
		model.addAttribute("listAnomaliasInformesAnomalias", anomaliaDAO
				.findByInformeAnomalia(Integer.parseInt(idInformeAnomalia), PageRequest.of(pageAnomalias, size)));
		model.addAttribute("listAnexosInformesAnomalias", anexoInformeAnomaliaDAO
				.findByInformeAnomalia(Integer.parseInt(idInformeAnomalia), PageRequest.of(pageAnexos, size)));
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/anadirEvidenciaInformeAnomalia", method = RequestMethod.POST)
	public String anadirEvidenciaInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model,
			HttpSession sesion) {

		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageAnomalias = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnomalias())) {
			pageAnomalias = Integer.parseInt(crearInformeAnomalias.getPageAnomalias()) - 1;
		}

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnexos())) {
			pageAnexos = Integer.parseInt(crearInformeAnomalias.getPageAnexos()) - 1;
		}

		List<String> listTextoErrorEvidencia = new ArrayList<>();
		boolean errorEvidencia = false;
		if (StringUtils.isBlank(crearInformeAnomalias.getEvidencia().getOriginalFilename())) {
			listTextoErrorEvidencia.add("El documento de la evidencia es obligatorio");
			errorEvidencia = true;
		}

		if (StringUtils.isBlank(crearInformeAnomalias.getDescripcionEvidencia())) {

			listTextoErrorEvidencia.add("La descripción de la evidencia es obligatorio");
			errorEvidencia = true;
		}

		if (errorEvidencia) {
			model.addAttribute("erroresEvidencia", listTextoErrorEvidencia);
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}
		try {
			crearInformeAnomaliasService.anadirEvidenciaInformeAnomalia(crearInformeAnomalias, sesion);
		} catch (EseException e) {
			log.error(e.getMessage());
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";

		}
		crearInformeAnomalias.setDescripcionEvidencia(null);
		model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
		cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos, pageAnomalias,
				size);
		model.addAttribute("info", " Se ha añadido correctamente");
		return "crearInformeAnomalias";

	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/guardarAnomaliaInformeAnomalia", method = RequestMethod.POST)
	public String guardarAnomaliaInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model) {

		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageAnomalias = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnomalias())) {
			pageAnomalias = Integer.parseInt(crearInformeAnomalias.getPageAnomalias()) - 1;
		}

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnexos())) {
			pageAnexos = Integer.parseInt(crearInformeAnomalias.getPageAnexos()) - 1;
		}

		if (StringUtils.isBlank(crearInformeAnomalias.getCodigoInforme())) {
			model.addAttribute("error", "El codigo del informe no puede ser nulo");
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}

		crearInformeAnomaliasService.guardarInformeAnomalias(crearInformeAnomalias);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
		cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos, pageAnomalias,
				size);
		model.addAttribute("info", "Se ha guardado correctamente");
		return "crearInformeAnomalias";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/firmaCrearInformeAnomaliasInspectorSeguridad", method = RequestMethod.POST)
	public String firmaCrearInformeAnomaliasInspectorSeguridad(CrearInformeAnomaliasJSP crearInformeAnomalias,
			Model model) {
		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageAnomalias = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnomalias())) {
			pageAnomalias = Integer.parseInt(crearInformeAnomalias.getPageAnomalias()) - 1;
		}

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnexos())) {
			pageAnexos = Integer.parseInt(crearInformeAnomalias.getPageAnexos()) - 1;
		}
		if (tieneAnomaliasAbiertas(crearInformeAnomalias)) {
			model.addAttribute("error",
					"No se puede firmar el informe de las anomalías porque tiene alguna anomalía abierta");
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}

		if (StringUtils.isBlank(crearInformeAnomalias.getNombreInspectorSeguridad())
				|| StringUtils.isBlank(crearInformeAnomalias.getPasswordInspectorSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				crearInformeAnomalias.getNombreInspectorSeguridad(),
				crearInformeAnomalias.getPasswordInspectorSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del inspector de seguridad no son correctos");
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		} else {
			crearInformeAnomalias.setIdFirmaInspectorSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			crearInformeAnomaliasService.firmaCrearInformeAnomaliasInspectorSeguridad(crearInformeAnomalias, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			crearInformeAnomalias.setIdFirmaInspectorSeguridad(null);
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}

		crearInformeAnomalias.setFirmado(Boolean.TRUE);
		model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
		cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos, pageAnomalias,
				size);
		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/firmaCrearInformeAnomaliasResponsableSeguridad", method = RequestMethod.POST)
	public String firmaCrearInformeAnomaliasResponsableSeguridad(CrearInformeAnomaliasJSP crearInformeAnomalias,
			Model model) {

		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageAnomalias = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnomalias())) {
			pageAnomalias = Integer.parseInt(crearInformeAnomalias.getPageAnomalias()) - 1;
		}

		if (StringUtils.isNotBlank(crearInformeAnomalias.getPageAnexos())) {
			pageAnexos = Integer.parseInt(crearInformeAnomalias.getPageAnexos()) - 1;
		}

		if (tieneAnomaliasAbiertas(crearInformeAnomalias)) {
			model.addAttribute("error",
					"No se puede firmar el informe de las anomalías porque tiene alguna anomalía abierta");
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}

		if (StringUtils.isBlank(crearInformeAnomalias.getNombreResponsableSeguridad())
				|| StringUtils.isBlank(crearInformeAnomalias.getPasswordResponsableSeguridad())) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}
		Personal inspectorSeguridad = personalDAO.findByNombreUsuarioAndClaveAndFechaBajaIsNull(
				crearInformeAnomalias.getNombreResponsableSeguridad(),
				crearInformeAnomalias.getPasswordResponsableSeguridad());
		if (inspectorSeguridad == null) {
			model.addAttribute("error", "El usuario o la contraseña del responsable de seguridad no son correctos");
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		} else {
			crearInformeAnomalias.setIdFirmaResponsableSeguridad(inspectorSeguridad.getIdPersonal());
		}

		try {
			crearInformeAnomaliasService.firmaCrearInformeAnomaliasResponsableSeguridad(crearInformeAnomalias, model);
		} catch (EseException e) {
			log.error(e.getMessage());
			crearInformeAnomalias.setIdFirmaResponsableSeguridad(null);
			model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
			cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos,
					pageAnomalias, size);
			return "crearInformeAnomalias";
		}

		crearInformeAnomalias.setFirmado(Boolean.TRUE);
		model.addAttribute("crearInformeAnomalias", crearInformeAnomalias);
		cargarListasInformeAnomalias(model, crearInformeAnomalias.getIdInformeAnomalias(), pageAnexos, pageAnomalias,
				size);
		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/paginacionTablaCrearInformeAnomaliaAnexos", method = RequestMethod.POST)
	public String paginacionTablaCrearInformeAnomaliaAnexos(CrearInformeAnomaliasJSP crearInformeAnomaliasJSP,
			Model model, HttpSession session) {
		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageAnomalias = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearInformeAnomaliasJSP.getPageAnexos())) {
			crearInformeAnomaliasJSP.setPageAnexos(crearInformeAnomaliasJSP.getPageAnexos().substring(
					crearInformeAnomaliasJSP.getPageAnexos().indexOf(",") + 1,
					crearInformeAnomaliasJSP.getPageAnexos().length()));
			pageAnexos = Integer.parseInt(crearInformeAnomaliasJSP.getPageAnexos()) - 1;
		}

		if (StringUtils.isNotBlank(crearInformeAnomaliasJSP.getPageAnomalias())) {
			pageAnomalias = Integer.parseInt(crearInformeAnomaliasJSP.getPageAnomalias()) - 1;
		}

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), pageAnexos, pageAnomalias,
				size);
		return "crearInformeAnomalias";
	}

	@RequestMapping(value = "/paginacionTablaCrearInformeAnomaliaAnomalias", method = RequestMethod.POST)
	public String paginacionTablaCrearInformeAnomaliaAnomalias(CrearInformeAnomaliasJSP crearInformeAnomaliasJSP,
			Model model, HttpSession session) {
		int pageAnexos = 0; // default page number is 0 (yes it is weird)
		int pageAnomalias = 0;
		int size = 5; // default page size is 10

		if (StringUtils.isNotBlank(crearInformeAnomaliasJSP.getPageAnomalias())) {
			crearInformeAnomaliasJSP.setPageAnomalias(crearInformeAnomaliasJSP.getPageAnomalias().substring(
					crearInformeAnomaliasJSP.getPageAnomalias().indexOf(",") + 1,
					crearInformeAnomaliasJSP.getPageAnomalias().length()));
			pageAnomalias = Integer.parseInt(crearInformeAnomaliasJSP.getPageAnomalias()) - 1;
		}

		if (StringUtils.isNotBlank(crearInformeAnomaliasJSP.getPageAnexos())) {
			pageAnexos = Integer.parseInt(crearInformeAnomaliasJSP.getPageAnexos()) - 1;
		}

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), pageAnexos, pageAnomalias,
				size);
		return "crearInformeAnomalias";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 */
	@RequestMapping(value = "/volverInformeAnomalia", method = RequestMethod.POST)
	public String volverInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model) {

		if (crearInformeAnomalias.getPaginaVuelta().equalsIgnoreCase("detallePersonal")) {
			return "forward:/volverInformeAnomaliaPersonal";
		}

		return "redirect:/buscadorInspeccion";
	}

	@RequestMapping(value = "/volverAnomaliaInformeAnomalia", method = RequestMethod.POST)
	public String volverAnomaliaInformeAnomalia(AddAnomaliaJSP addAnomaliaJSP, Model model) {

		CrearInformeAnomaliasJSP crearInformeAnomaliasJSP = new CrearInformeAnomaliasJSP();
		crearInformeAnomaliasJSP.setDescripcionDatosInspeccion(addAnomaliaJSP.getDescripcion());
		crearInformeAnomaliasJSP.setObservacionesDatosInspeccion(addAnomaliaJSP.getObservaciones());
		crearInformeAnomaliasJSP.setMedidasCautelares(addAnomaliaJSP.getMedidasCautelares());
		crearInformeAnomaliasJSP.setCodigoInforme(addAnomaliaJSP.getCodigoInforme());
		crearInformeAnomaliasJSP.setIdInformeAnomalias(addAnomaliaJSP.getIdInformeAnomalia());
		crearInformeAnomaliasJSP.setNumeroInspeccion(addAnomaliaJSP.getNumReferencia());
		crearInformeAnomaliasJSP.setFechaInspeccion(addAnomaliaJSP.getFechaInspeccion());
		crearInformeAnomaliasJSP.setNombreInspector(addAnomaliaJSP.getNombreInspector());
		crearInformeAnomaliasJSP.setAnomaliasAbiertas(anomaliaDAO
				.countByInformeAnomalia(Integer.parseInt(crearInformeAnomaliasJSP.getIdInformeAnomalias())).toString());
		crearInformeAnomaliasJSP.setLectura(addAnomaliaJSP.isLecturaInformeAnomalia());
		crearInformeAnomaliasJSP.setIdTipoInspeccion(addAnomaliaJSP.getIdTipoInspeccion());

		crearInformeAnomaliasJSP.setIdPersonal(addAnomaliaJSP.getIdPersonal());
		crearInformeAnomaliasJSP.setPaginaVuelta(addAnomaliaJSP.getPaginaVuelta());
		InformeAnomalias informeAnomalia = informeAnomaliasDAO
				.getOne(Integer.parseInt(addAnomaliaJSP.getIdInformeAnomalia()));
		if (informeAnomalia.getFirmaResponsable() != null) {
			crearInformeAnomaliasJSP
					.setIdFirmaResponsableSeguridad(informeAnomalia.getFirmaResponsable().getIdPersonal());
		}

		if (informeAnomalia.getFirmaInspector() != null) {
			crearInformeAnomaliasJSP.setIdFirmaInspectorSeguridad(informeAnomalia.getFirmaInspector().getIdPersonal());
		}

		crearInformeAnomaliasJSP.setFirmado(
				informeAnomalia.getFirmaResponsable() != null || informeAnomalia.getFirmaInspector() != null);

		crearInformeAnomaliasJSP.setTieneEvidencia(informeAnomalia.getEvidencia20() != null);

		model.addAttribute("crearInformeAnomalias", crearInformeAnomaliasJSP);
		cargarListasInformeAnomalias(model, crearInformeAnomaliasJSP.getIdInformeAnomalias(), 0, 0, 5);

		return "crearInformeAnomalias";
	}

	/**
	 * Metodo que inicializa el formulario de personal
	 * 
	 * @param model objeto model de la pantalla
	 * @return string del nombre de la pantalla
	 * @throws EseException
	 */
	@RequestMapping(value = "/verEvidenciaInformeAnomalia", method = RequestMethod.POST)
	public void verEvidenciaInformeAnomalia(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model,
			HttpServletResponse response) throws EseException {
		try {
			switch (crearInformeAnomalias.getIdTipoInspeccion()) {
			case Constantes.TIPO_INSPECCION_ISO:
				verEvidenciaInformeAnomaliaISO(crearInformeAnomalias, model, response);
				break;

			case Constantes.TIPO_INSPECCION_ISEET:
				verEvidenciaInformeAnomaliaISEET(crearInformeAnomalias, model, response);
				break;
			case Constantes.TIPO_INSPECCION_ISMP:
				verEvidenciaInformeAnomaliaISMP(crearInformeAnomalias, model, response);
				break;

			default:
				throw new EseException(
						"Error al ver la evidencia del informe anomalias: El tipo inspeccion es incorrecto");
			}

		} catch (NumberFormatException | IOException e) {
			log.error(e.getMessage());
		}

	}

	@RequestMapping(value = "/verEvidenciaCrearInformeAnomalias", method = RequestMethod.POST)
	public void verEvidenciaCrearInformeAnomalias(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model,
			HttpServletResponse response) throws IOException {

		InformeAnomalias anexoInformeAnomalias = informeAnomaliasDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdInformeAnomalias()));
		Documento documento = anexoInformeAnomalias.getEvidencia20().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);
	}

	private void verEvidenciaInformeAnomaliaISO(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model,
			HttpServletResponse response) throws IOException {

		AnexoInformeAnomalia anexoInformeAnomalias = anexoInformeAnomaliaDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdAnexo()));
		Documento documento = anexoInformeAnomalias.getEvidencia().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);
	}

	private void verEvidenciaInformeAnomaliaISEET(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model,
			HttpServletResponse response) throws IOException {

		AnexoInformeAnomalia anexoInformeAnomalias = anexoInformeAnomaliaDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdAnexo()));
		Documento documento = anexoInformeAnomalias.getEvidencia().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);
	}

	private void verEvidenciaInformeAnomaliaISMP(CrearInformeAnomaliasJSP crearInformeAnomalias, Model model,
			HttpServletResponse response) throws IOException {

		AnexoInformeAnomalia anexoInformeAnomalias = anexoInformeAnomaliaDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdAnexo()));
		Documento documento = anexoInformeAnomalias.getEvidencia().getDocumento();

		utilesPDFService.descargarEvidencia(documento, response);
	}

	private boolean tieneAnomaliasAbiertas(CrearInformeAnomaliasJSP crearInformeAnomalias) {

		InformeAnomalias informeAnomalias = informeAnomaliasDAO
				.getOne(Integer.parseInt(crearInformeAnomalias.getIdInformeAnomalias()));

		if (informeAnomalias != null && !CollectionUtils.isEmpty(informeAnomalias.getListAnomalia())) {
			for (Anomalia anomalia : informeAnomalias.getListAnomalia()) {
				if (Constantes.ESTADO_ANOMALIA_ABIERTA == anomalia.getEstadoAnomalia().getIdEstadoAnomalia()
						.intValue()) {
					return true;
				}
			}
		}
		return false;
	}

}