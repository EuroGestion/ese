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

import eu.eurogestion.ese.domain.HistoricoMaquinista;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class HistoricoMaquinistaDAOCustomImpl implements HistoricoMaquinistaDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * Obtiene todas los HistoricoMaquinista correspondientes en base a unos
	 * filtros.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	public Page<HistoricoMaquinista> findAllReportesFinServicioByPersonal(String idPersonal, PageRequest pageRequest) {

		if (StringUtils.isBlank(idPersonal)) {
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<HistoricoMaquinista> cr = cb.createQuery(HistoricoMaquinista.class);
		Root<HistoricoMaquinista> root = cr.from(HistoricoMaquinista.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<HistoricoMaquinista> query = entityManager.createQuery(cr);
		List<HistoricoMaquinista> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<HistoricoMaquinista> countRoot = countQuery.from(HistoricoMaquinista.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	public Page<HistoricoMaquinista> findAllReportesFinServicioByFilters(String idPersonal, String numeroTren,
			String fecha, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<HistoricoMaquinista> cr = cb.createQuery(HistoricoMaquinista.class);
		Root<HistoricoMaquinista> root = cr.from(HistoricoMaquinista.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(idPersonal)) {
			predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));
		}
		if (StringUtils.isNotBlank(numeroTren)) {
			predicates.add(cb.like(cb.upper(root.get("tren").get("numero")), "%" + numeroTren.toUpperCase() + "%"));
		}

		if (StringUtils.isNotBlank(fecha)) {
			predicates.add(cb.equal(root.get("fecha"), Utiles.parseDatePantalla(fecha)));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<HistoricoMaquinista> query = entityManager.createQuery(cr);
		List<HistoricoMaquinista> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<HistoricoMaquinista> countRoot = countQuery.from(HistoricoMaquinista.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

}
