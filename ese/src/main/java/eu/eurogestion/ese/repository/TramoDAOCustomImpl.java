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

import eu.eurogestion.ese.domain.Tramo;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class TramoDAOCustomImpl implements TramoDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<Tramo> findAllByFilters(String nombre, String idPuntoOrigen, String idPuntoDestino,
			PageRequest pageRequest) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tramo> cr = cb.createQuery(Tramo.class);
		Root<Tramo> root = cr.from(Tramo.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(root.get("nombre"), "%" + nombre + "%"));
		}
		if (StringUtils.isNotBlank(idPuntoOrigen)) {
			predicates.add(
					cb.equal(root.get("puntoOrigen").get("idPuntoInfraestructura"), Integer.parseInt(idPuntoOrigen)));
		}

		if (StringUtils.isNotBlank(idPuntoDestino)) {
			predicates.add(
					cb.equal(root.get("puntoDestino").get("idPuntoInfraestructura"), Integer.parseInt(idPuntoDestino)));
		}

		predicates.add(cb.equal(root.get("esEspecial"), Boolean.FALSE));
		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Tramo> query = entityManager.createQuery(cr);
		List<Tramo> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Tramo> countRoot = countQuery.from(Tramo.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
