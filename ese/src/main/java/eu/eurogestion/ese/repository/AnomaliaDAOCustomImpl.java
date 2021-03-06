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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Anomalia;
import eu.eurogestion.ese.utils.Constantes;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class AnomaliaDAOCustomImpl implements AnomaliaDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<Anomalia> findByInformeAnomalia(Integer idInformeAnomalia, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Anomalia> cr = cb.createQuery(Anomalia.class);
		Root<Anomalia> root = cr.from(Anomalia.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("informeAnomalias").get("idInformeAnomalias"), idInformeAnomalia));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Anomalia> query = entityManager.createQuery(cr);
		List<Anomalia> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Anomalia> countRoot = countQuery.from(Anomalia.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long countByInformeAnomalia(Integer idInformeAnomalia) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<Anomalia> root = cr.from(Anomalia.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("informeAnomalias").get("idInformeAnomalias"), idInformeAnomalia));
		predicates
				.add(cb.equal(root.get("estadoAnomalia").get("idEstadoAnomalia"), Constantes.ESTADO_ANOMALIA_ABIERTA));

		cr.select(cb.count(root));
		cr.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> query = entityManager.createQuery(cr);
		return query.getSingleResult();
	}
}
