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

import eu.eurogestion.ese.domain.TomaServicio;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class TomaServicioDAOCustomImpl implements TomaServicioDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	public Page<TomaServicio> findAllByFilters(String idPersonal, String numeroTren, String idEstado,
			PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TomaServicio> cr = cb.createQuery(TomaServicio.class);
		Root<TomaServicio> root = cr.from(TomaServicio.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(idPersonal)) {
			predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));
		}
		if (StringUtils.isNotBlank(numeroTren)) {
			predicates.add(cb.like(cb.upper(root.get("tren").get("numero")), "%" + numeroTren.toUpperCase() + "%"));
		}

		if (StringUtils.isNotBlank(idEstado)) {
			predicates.add(cb.equal(root.get("estadoToma").get("idEstadoHistorico"), idEstado));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<TomaServicio> query = entityManager.createQuery(cr);
		List<TomaServicio> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<TomaServicio> countRoot = countQuery.from(TomaServicio.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
