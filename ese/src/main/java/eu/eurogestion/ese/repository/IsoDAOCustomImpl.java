package eu.eurogestion.ese.repository;

import java.util.ArrayList;
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

import eu.eurogestion.ese.domain.Iso;
import eu.eurogestion.ese.utils.Constantes;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class IsoDAOCustomImpl implements IsoDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Long countByYear(Integer anno) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<Iso> root = cr.from(Iso.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("is").get("fechaInspeccion")), anno));
		predicates.add(cb.notEqual(root.get("is").get("estadoInspeccion").get("idEstadoInspeccion"),
				Constantes.ESTADO_INSPECCION_PLANIFICADA));

		cr.select(cb.count(root)).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> query = entityManager.createQuery(cr);
		return query.getSingleResult();
	}
}
