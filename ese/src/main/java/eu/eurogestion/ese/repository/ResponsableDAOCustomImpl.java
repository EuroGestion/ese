package eu.eurogestion.ese.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Responsable;
import eu.eurogestion.ese.utils.Constantes;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class ResponsableDAOCustomImpl implements ResponsableDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Responsable> findRSandRO() {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Responsable> cr = cb.createQuery(Responsable.class);
		Root<Responsable> root = cr.from(Responsable.class);
		root.join("tipoResponsable");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(root.get("tipoResponsable").get("idTipoResponsable")
				.in(Arrays.asList(Constantes.TIPO_RESPONSABLE_SEGURIDAD, Constantes.TIPO_RESPONSABLE_OPERACIONES)));
		predicates.add(root.get("fechaFin").isNull());

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Responsable> query = entityManager.createQuery(cr);

		return query.getResultList();
	}
}
