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

import eu.eurogestion.ese.domain.Proveedor;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class ProveedorDAOCustomImpl implements ProveedorDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<Proveedor> findAllProveedoresByFilters(String documento, String nombre, String estado,
			PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Proveedor> cr = cb.createQuery(Proveedor.class);
		Root<Proveedor> root = cr.from(Proveedor.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(documento)) {
			predicates.add(cb.equal(root.get("compania").get("documento"), documento));
		}

		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(root.get("compania").get("nombre"), nombre));
		}

		if (StringUtils.isNotBlank(estado)) {
			predicates.add(cb.equal(root.get("estadoProveedor").get("idEstadoProveedor"), estado));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Proveedor> query = entityManager.createQuery(cr);
		List<Proveedor> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Proveedor> countRoot = countQuery.from(Proveedor.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
