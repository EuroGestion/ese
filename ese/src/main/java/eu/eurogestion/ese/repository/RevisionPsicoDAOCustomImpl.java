package eu.eurogestion.ese.repository;

import java.util.ArrayList;
import java.util.Date;
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

import eu.eurogestion.ese.domain.RevisionPsico;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class RevisionPsicoDAOCustomImpl implements RevisionPsicoDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public RevisionPsico findLastRevisionPsico(String idPersonal) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RevisionPsico> cr = cb.createQuery(RevisionPsico.class);
		Root<RevisionPsico> root = cr.from(RevisionPsico.class);
		root.join("personal");

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));
		predicates.add(cb.greaterThan(root.get("fechaCaducidad"), new Date()));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}))
				.orderBy(cb.desc(root.get("validoDesde")));

		TypedQuery<RevisionPsico> query = entityManager.createQuery(cr);

		List<RevisionPsico> listOut = query.getResultList();
		if (listOut.isEmpty()) {
			return null;
		}

		return listOut.get(0);
	}

	@Override
	public Page<RevisionPsico> findRevisionPsicoByFilters(String idCentro, String dni, String fechaRevision,
			PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RevisionPsico> cr = cb.createQuery(RevisionPsico.class);
		Root<RevisionPsico> root = cr.from(RevisionPsico.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(idCentro)) {
			predicates.add(cb.equal(root.get("compania").get("idCompania"), Integer.valueOf(idCentro)));
		}

		if (StringUtils.isNotBlank(fechaRevision)) {
			predicates.add(cb.equal(root.get("fechaRealizacion"), Utiles.parseDatePantalla(fechaRevision)));
		}

		if (StringUtils.isNotBlank(dni)) {
			predicates.add(cb.equal(root.get("personal").get("documento"), dni));
		}

		predicates.add(root.get("estadoRevision").get("idEstadoRevision").in(Constantes.ESTADO_REVISION_PLANIFICADO));

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<RevisionPsico> query = entityManager.createQuery(cr);
		List<RevisionPsico> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<RevisionPsico> countRoot = countQuery.from(RevisionPsico.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Page<RevisionPsico> findAllByPersonal(String idPersonal, PageRequest pageRequest) {

		if (StringUtils.isBlank(idPersonal)) {
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RevisionPsico> cr = cb.createQuery(RevisionPsico.class);
		Root<RevisionPsico> root = cr.from(RevisionPsico.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));

		cr.select(root).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<RevisionPsico> query = entityManager.createQuery(cr);
		List<RevisionPsico> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<RevisionPsico> countRoot = countQuery.from(RevisionPsico.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long findRevisionPsicoByYear(Integer anno) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<RevisionPsico> root = cr.from(RevisionPsico.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("fechaRealizacion")), anno));

		cr.select(cb.count(root)).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> query = entityManager.createQuery(cr);
		return query.getSingleResult();
	}
}
