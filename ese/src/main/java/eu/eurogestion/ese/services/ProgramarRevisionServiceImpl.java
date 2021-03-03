package eu.eurogestion.ese.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.domain.EstadoRevision;
import eu.eurogestion.ese.domain.RevisionPsico;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.ProgramarRevisionJSP;
import eu.eurogestion.ese.repository.CompaniaDAO;
import eu.eurogestion.ese.repository.EstadoRevisionDAO;
import eu.eurogestion.ese.repository.PersonalDAO;
import eu.eurogestion.ese.repository.RevisionPsicoDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class ProgramarRevisionServiceImpl implements ProgramarRevisionService {

	/** Repositories & Services **/

	@Autowired
	public CompaniaDAO companiaDAO;

	@Autowired
	public PersonalDAO personalDAO;

	@Autowired
	public RevisionPsicoDAO revisionPsicoDAO;

	@Autowired
	public EstadoRevisionDAO estadoRevisionDAO;

	/** Functions **/

	@Override
	public void crearRevision(ProgramarRevisionJSP programarRevision) throws EseException {

		Compania centroMedico = companiaDAO.getOne(Integer.parseInt(programarRevision.getIdCentroMedico()));
		EstadoRevision estadoRevision = estadoRevisionDAO.getOne(Constantes.ESTADO_REVISION_PLANIFICADO);

		for (String idPersonal : programarRevision.getListaPersonalTotalSelect()) {
			RevisionPsico revisionPsico = new RevisionPsico();
			revisionPsico.setCausa(programarRevision.getCausa());
			revisionPsico.setEstadoRevision(estadoRevision);
			revisionPsico.setPersonal(personalDAO.getOne(Integer.parseInt(idPersonal)));
			revisionPsico.setCompania(centroMedico);
			revisionPsico.setFechaRealizacion(Utiles.parseDatePantalla(programarRevision.getFechaRevision()));
			if (StringUtils.isNotBlank(programarRevision.getObservaciones())) {
				revisionPsico.setObservaciones(programarRevision.getObservaciones());
			}

			revisionPsicoDAO.save(revisionPsico);
		}
	}
}
