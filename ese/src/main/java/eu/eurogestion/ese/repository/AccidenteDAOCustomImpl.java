package eu.eurogestion.ese.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
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

import eu.eurogestion.ese.domain.Accidente;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class AccidenteDAOCustomImpl implements AccidenteDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<Accidente> findAccidenteByFilters(String tipo, String causa, String numIdentificacion, String fecha,
			String lugar, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Accidente> cr = cb.createQuery(Accidente.class);
		Root<Accidente> root = cr.from(Accidente.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(tipo)) {
			predicates.add(cb.equal(root.get("tipoAccidente").get("idTipoAccidente"), Integer.valueOf(tipo)));
		}

		if (StringUtils.isNotBlank(causa)) {
			predicates.add(cb.equal(root.get("causaAccidente").get("idCausaAccidente"), Integer.valueOf(causa)));
		}

		if (StringUtils.isNotBlank(numIdentificacion)) {
			predicates.add(cb.equal(root.get("numeroSuceso"), numIdentificacion));
		}

		if (StringUtils.isNotBlank(fecha)) {
			predicates.add(cb.equal(root.get("fechaAccidente"), Utiles.parseDatePantalla(fecha)));
		}

		if (StringUtils.isNotBlank(lugar)) {
			predicates.add(cb.equal(root.get("lugarAccidente"), lugar));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Accidente> query = entityManager.createQuery(cr);
		List<Accidente> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Accidente> countRoot = countQuery.from(Accidente.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long countAccidenteByDateAndCausa(Integer anno, Integer causa) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<Accidente> root = cr.from(Accidente.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("causaAccidente").get("idCausaAccidente"), causa));
		predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("fechaAccidente")), anno));

		cr.select(cb.count(root)).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> query = entityManager.createQuery(cr);
		return query.getSingleResult();
	}
}
