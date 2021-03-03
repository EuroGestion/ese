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
import org.springframework.util.CollectionUtils;

import eu.eurogestion.ese.domain.Composicion;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class ComposicionDAOCustomImpl implements ComposicionDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Composicion findComposicionByFilters(String idTren, String fecha) throws EseException {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Composicion> cr = cb.createQuery(Composicion.class);
		Root<Composicion> root = cr.from(Composicion.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("fecha"), Utiles.parseDatePantalla(fecha)));
		predicates.add(cb.equal(root.get("tren").get("idTren"), idTren));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Composicion> query = entityManager.createQuery(cr);
		List<Composicion> lista = query.getResultList();
		if (CollectionUtils.isEmpty(lista)) {
			throw new EseException("No existe la composicion para ese tren en esa fecha");
		}
		return lista.get(0);
	}

	@Override
	public Page<Composicion> findListComposicionesByFilters(String idTren, String fecha, PageRequest pageRequest) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Composicion> cr = cb.createQuery(Composicion.class);
		Root<Composicion> root = cr.from(Composicion.class);

		List<Predicate> predicates = new ArrayList<>();
		if (StringUtils.isNotBlank(fecha)) {
			predicates.add(cb.equal(root.get("fecha"), Utiles.parseDatePantalla(fecha)));
		}
		if (StringUtils.isNotBlank(idTren)) {

			predicates.add(cb.equal(root.get("tren").get("idTren"), idTren));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Composicion> query = entityManager.createQuery(cr);
		List<Composicion> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Composicion> countRoot = countQuery.from(Composicion.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long countListComposicionesByFilters(String idTren, String fecha) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<Composicion> root = cr.from(Composicion.class);

		List<Predicate> predicates = new ArrayList<>();
		if (StringUtils.isNotBlank(fecha)) {
			predicates.add(cb.equal(root.get("fecha"), Utiles.parseDatePantalla(fecha)));
		}
		if (StringUtils.isNotBlank(idTren)) {

			predicates.add(cb.equal(root.get("tren").get("idTren"), idTren));
		}

		cr.select(cb.count(root)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		return entityManager.createQuery(cr).getSingleResult();

	}

	@Override
	public boolean existeComposicionesByFilters(String idTren, String fechaInicio, String fechaFin) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Composicion> cr = cb.createQuery(Composicion.class);
		Root<Composicion> root = cr.from(Composicion.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.between(root.get("fecha"), Utiles.parseDatePantalla(fechaInicio),
				Utiles.parseDatePantalla(fechaFin)));

		predicates.add(cb.equal(root.get("tren").get("idTren"), idTren));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Composicion> query = entityManager.createQuery(cr);
		List<Composicion> lista = query.getResultList();
		return !lista.isEmpty();
	}

}
