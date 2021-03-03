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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Pasf;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class PasfDAOCustomImpl implements PasfDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<Pasf> findPasfByFilters(String anno, String estado, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Pasf> cr = cb.createQuery(Pasf.class);
		Root<Pasf> root = cr.from(Pasf.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(anno)) {
			predicates.add(cb.equal(root.get("anno"), Integer.valueOf(anno)));
		}

		if (StringUtils.isNotBlank(estado)) {
			predicates.add(cb.equal(root.get("estadoPasf").get("idEstadoPasf"), Integer.valueOf(estado)));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Pasf> query = entityManager.createQuery(cr);
		List<Pasf> result = query.setFirstResult((int) pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize())
				.getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Pasf> countRoot = countQuery.from(Pasf.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
