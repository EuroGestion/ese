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

import eu.eurogestion.ese.domain.RolPermiso;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class RolPermisoDAOCustomImpl implements RolPermisoDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	public Page<RolPermiso> findAllByIdRol(String idRol, PageRequest pageRequest) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RolPermiso> cr = cb.createQuery(RolPermiso.class);
		Root<RolPermiso> root = cr.from(RolPermiso.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("rol").get("idRol"), idRol));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<RolPermiso> query = entityManager.createQuery(cr);
		List<RolPermiso> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<RolPermiso> countRoot = countQuery.from(RolPermiso.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);

	}

	public Long countAllByIdRol(String idRol) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<RolPermiso> countRoot = countQuery.from(RolPermiso.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(countRoot.get("rol").get("idRol"), idRol));

		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		return entityManager.createQuery(countQuery).getSingleResult();

	}

}
