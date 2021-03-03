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

import eu.eurogestion.ese.domain.LibroPersonal;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class LibroPersonalDAOCustomImpl implements LibroPersonalDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<LibroPersonal> findLibrosPersonalByFilters(Integer idLibro, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<LibroPersonal> cr = cb.createQuery(LibroPersonal.class);
		Root<LibroPersonal> root = cr.from(LibroPersonal.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("libro").get("idLibro"), idLibro));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<LibroPersonal> query = entityManager.createQuery(cr);
		List<LibroPersonal> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<LibroPersonal> countRoot = countQuery.from(LibroPersonal.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Page<LibroPersonal> findByFilters(String idPersonal, String titulo, String fecha, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<LibroPersonal> cr = cb.createQuery(LibroPersonal.class);
		Root<LibroPersonal> root = cr.from(LibroPersonal.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));

		if (StringUtils.isNotBlank(titulo)) {
			predicates.add(cb.like(root.get("libro").get("titulo"), "%" + titulo + "%"));
		}

		if (StringUtils.isNotBlank(fecha)) {
			predicates.add(cb.equal(root.get("libro").get("fechaSubida"),
					Utiles.convertStringToDate(fecha, Constantes.FORMATO_FECHA_PANTALLA)));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<LibroPersonal> query = entityManager.createQuery(cr);
		List<LibroPersonal> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<LibroPersonal> countRoot = countQuery.from(LibroPersonal.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long findWithoutEvidence(String idPersonal) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<LibroPersonal> root = cr.from(LibroPersonal.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));
		predicates.add(cb.isNull(root.get("evidencia")));

		cr.select(cb.count(root)).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> query = entityManager.createQuery(cr);
		return query.getSingleResult();
	}

}
