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

import eu.eurogestion.ese.domain.Material;
import eu.eurogestion.ese.domain.MaterialComposicion;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class MaterialDAOCustomImpl implements MaterialDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<Material> findByIdComposicion(List<Integer> listIdComposicion, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Material> cr = cb.createQuery(Material.class);
		Root<Material> root = cr.from(Material.class);

		List<Predicate> predicates = new ArrayList<>();

		List<Integer> listIn = findMateridalComposicionByIdComposicion(listIdComposicion);

		if (listIn.isEmpty()) {
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}

		predicates.add(root.get("idMaterial").in(listIn));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Material> query = entityManager.createQuery(cr);
		List<Material> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Material> countRoot = countQuery.from(Material.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long countByIdComposicion(List<Integer> listIdComposicion) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Material> countRoot = countQuery.from(Material.class);

		List<Predicate> predicates = new ArrayList<>();

		List<Integer> listIn = findMateridalComposicionByIdComposicion(listIdComposicion);

		if (listIn.isEmpty()) {
			return 0L;
		}

		predicates.add(countRoot.get("idMaterial").in(listIn));

		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		return entityManager.createQuery(countQuery).getSingleResult();

	}

	@Override
	public Page<Material> findByNotIdComposicion(List<Integer> listIdComposicion, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Material> cr = cb.createQuery(Material.class);
		Root<Material> root = cr.from(Material.class);

		List<Predicate> predicates = new ArrayList<>();

		List<Integer> listIn = findMateridalComposicionByIdComposicion(listIdComposicion);

		if (listIn.isEmpty()) {
			cr.select(root).distinct(true);
		} else {
			predicates.add(cb.not(root.get("idMaterial").in(listIn)));
			cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));
		}

		TypedQuery<Material> query = entityManager.createQuery(cr);
		List<Material> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Material> countRoot = countQuery.from(Material.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long countByNotIdComposicion(List<Integer> listIdComposicion) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Material> countRoot = countQuery.from(Material.class);

		List<Predicate> predicates = new ArrayList<>();

		List<Integer> listIn = findMateridalComposicionByIdComposicion(listIdComposicion);

		if (!listIn.isEmpty()) {
			predicates.add(cb.not(countRoot.get("idMaterial").in(listIn)));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		return entityManager.createQuery(countQuery).getSingleResult();

	}

	private List<Integer> findMateridalComposicionByIdComposicion(List<Integer> listIdComposicion) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
		Root<MaterialComposicion> root = cr.from(MaterialComposicion.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(root.get("composicion").get("idComposicion").in(listIdComposicion));

		cr.select(root.get("material").get("idMaterial")).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Integer> query = entityManager.createQuery(cr);

		return query.getResultList();
	}

	@Override
	public Page<Material> findByFilters(String modeloMaterial, String nve, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Material> cr = cb.createQuery(Material.class);
		Root<Material> root = cr.from(Material.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(modeloMaterial)) {
			predicates.add(cb.equal(root.get("modeloMaterial").get("idModeloMaterial"), modeloMaterial));
		}

		if (StringUtils.isNotBlank(nve)) {
			predicates.add(cb.like(root.get("nve"), "%" + nve + "%"));
		}

		predicates.add(cb.isNull(root.get("fechaBaja")));

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Material> query = entityManager.createQuery(cr);

		List<Material> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Material> countRoot = countQuery.from(Material.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long countByFilters(String modeloMaterial, String nve) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Material> countRoot = countQuery.from(Material.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(modeloMaterial)) {
			predicates.add(cb.equal(countRoot.get("modeloMaterial").get("idModeloMaterial"), modeloMaterial));
		}

		if (StringUtils.isNotBlank(nve)) {
			predicates.add(cb.like(countRoot.get("nve"), "%" + nve + "%"));
		}

		predicates.add(cb.isNull(countRoot.get("fechaBaja")));

		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		return entityManager.createQuery(countQuery).getSingleResult();

	}
}
