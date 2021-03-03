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

import eu.eurogestion.ese.domain.ViewInspeccion;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class ViewInspeccionDAOCustomImpl implements ViewInspeccionDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<ViewInspeccion> findInspeccionByFilters(String idTipo, String idInspector, String idEstado,
			String codigo, String fecha, String idTren, String origen, String nve, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ViewInspeccion> cr = cb.createQuery(ViewInspeccion.class);
		Root<ViewInspeccion> root = cr.from(ViewInspeccion.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(idTipo)) {
			predicates.add(cb.equal(root.get("viewInspeccionPK").get("idTipoInspeccion"), Integer.valueOf(idTipo)));
		}

		if (StringUtils.isNotBlank(idInspector)) {
			predicates.add(cb.equal(root.get("idInspector"), Integer.valueOf(idInspector)));
		}

		if (StringUtils.isNotBlank(idEstado)) {
			predicates.add(cb.equal(root.get("idEstadoInspeccion"), Integer.valueOf(idEstado)));
		}

		if (StringUtils.isNotBlank(idTren)) {
			predicates.add(cb.equal(root.get("idTren"), Integer.valueOf(idTren)));
		}

		if (StringUtils.isNotBlank(origen)) {
			predicates.add(cb.like(root.get("origen"), "%" + origen + "%"));
		}

		if (StringUtils.isNotBlank(codigo)) {
			predicates.add(cb.equal(root.get("codigo"), codigo));
		}

		if (StringUtils.isNotBlank(nve)) {
			predicates.add(cb.equal(root.get("nve"), nve));
		}

		if (StringUtils.isNotBlank(fecha)) {
			predicates.add(cb.equal(root.get("fecha"), Utiles.parseDatePantalla(fecha)));
		}

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<ViewInspeccion> query = entityManager.createQuery(cr);
		List<ViewInspeccion> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ViewInspeccion> countRoot = countQuery.from(ViewInspeccion.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Page<ViewInspeccion> findByPersonal(String personal, PageRequest pageRequest) {

		if (StringUtils.isBlank(personal)) {
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ViewInspeccion> cr = cb.createQuery(ViewInspeccion.class);
		Root<ViewInspeccion> root = cr.from(ViewInspeccion.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("idPersonal"), Integer.valueOf(personal)));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<ViewInspeccion> query = entityManager.createQuery(cr);
		List<ViewInspeccion> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ViewInspeccion> countRoot = countQuery.from(ViewInspeccion.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
