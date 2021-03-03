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

import eu.eurogestion.ese.domain.ModeloMaterial;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class ModeloMaterialDAOCustomImpl implements ModeloMaterialDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<ModeloMaterial> findByFilters(String idTipoMaterial, String serie, String subserie,
			PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ModeloMaterial> cr = cb.createQuery(ModeloMaterial.class);
		Root<ModeloMaterial> root = cr.from(ModeloMaterial.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(idTipoMaterial)) {
			predicates.add(cb.equal(root.get("tipoMaterial").get("idTipoMaterial"), idTipoMaterial));
		}

		if (StringUtils.isNotBlank(serie)) {
			predicates.add(cb.like(root.get("serie"), "%" + serie + "%"));
		}

		if (StringUtils.isNotBlank(subserie)) {
			predicates.add(cb.like(root.get("subserie"), "%" + subserie + "%"));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<ModeloMaterial> query = entityManager.createQuery(cr);
		List<ModeloMaterial> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ModeloMaterial> countRoot = countQuery.from(ModeloMaterial.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
