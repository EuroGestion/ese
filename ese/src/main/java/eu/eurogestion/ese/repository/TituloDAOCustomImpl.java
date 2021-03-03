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

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Titulo;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class TituloDAOCustomImpl implements TituloDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	public Page<Titulo> findAllByPersonal(String idPersonal, PageRequest pageRequest) {

		if (StringUtils.isBlank(idPersonal)) {
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Titulo> cr = cb.createQuery(Titulo.class);
		Root<Titulo> root = cr.from(Titulo.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Titulo> query = entityManager.createQuery(cr);
		List<Titulo> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Titulo> countRoot = countQuery.from(Titulo.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
