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

import eu.eurogestion.ese.domain.AnexoInformeAnomalia;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class AnexoInformeAnomaliaDAOCustomImpl implements AnexoInformeAnomaliaDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<AnexoInformeAnomalia> findByInformeAnomalia(Integer idInformeAnomalia, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AnexoInformeAnomalia> cr = cb.createQuery(AnexoInformeAnomalia.class);
		Root<AnexoInformeAnomalia> root = cr.from(AnexoInformeAnomalia.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("informeAnomalias").get("idInformeAnomalias"), idInformeAnomalia));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<AnexoInformeAnomalia> query = entityManager.createQuery(cr);
		List<AnexoInformeAnomalia> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AnexoInformeAnomalia> countRoot = countQuery.from(AnexoInformeAnomalia.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
