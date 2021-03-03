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

import eu.eurogestion.ese.domain.Libro;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class LibroDAOCustomImpl implements LibroDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<Libro> findLibrosByFilters(String titulo, String fecha, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Libro> cr = cb.createQuery(Libro.class);
		Root<Libro> root = cr.from(Libro.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(titulo)) {
			predicates.add(cb.like(root.get("titulo"), "%" + titulo + "%"));
		}

		if (StringUtils.isNotBlank(fecha)) {
			predicates.add(cb.equal(root.get("fechaSubida"),
					Utiles.convertStringToDate(fecha, Constantes.FORMATO_FECHA_PANTALLA)));
		}

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Libro> query = entityManager.createQuery(cr);
		List<Libro> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Libro> countRoot = countQuery.from(Libro.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

}
