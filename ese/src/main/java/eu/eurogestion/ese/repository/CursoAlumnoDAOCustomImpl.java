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

import eu.eurogestion.ese.domain.CursoAlumno;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class CursoAlumnoDAOCustomImpl implements CursoAlumnoDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Long countByYearAndType(Integer anno, Integer tipo) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<CursoAlumno> root = cr.from(CursoAlumno.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("curso").get("fechaFin")), anno));
		predicates.add(cb.equal(root.get("curso").get("tipoCurso").get("idTipoCurso"), tipo));

		cr.select(cb.count(root)).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> query = entityManager.createQuery(cr);
		return query.getSingleResult();
	}

	@Override
	public Page<CursoAlumno> findAllByPersonal(String idPersonal, PageRequest pageRequest) {

		if (StringUtils.isBlank(idPersonal)) {
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CursoAlumno> cr = cb.createQuery(CursoAlumno.class);
		Root<CursoAlumno> root = cr.from(CursoAlumno.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));

		cr.select(root).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<CursoAlumno> query = entityManager.createQuery(cr);
		List<CursoAlumno> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<CursoAlumno> countRoot = countQuery.from(CursoAlumno.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}
}
