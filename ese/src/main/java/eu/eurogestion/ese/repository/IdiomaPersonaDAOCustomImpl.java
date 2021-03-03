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
import org.springframework.util.CollectionUtils;

import eu.eurogestion.ese.domain.IdiomaPersona;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class IdiomaPersonaDAOCustomImpl implements IdiomaPersonaDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<IdiomaPersona> findIdiomaPersonaByPersonal(String idPersonal, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<IdiomaPersona> cr = cb.createQuery(IdiomaPersona.class);
		Root<IdiomaPersona> root = cr.from(IdiomaPersona.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("personal").get("idPersonal"), Integer.valueOf(idPersonal)));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<IdiomaPersona> query = entityManager.createQuery(cr);
		List<IdiomaPersona> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<IdiomaPersona> countRoot = countQuery.from(IdiomaPersona.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	public boolean existIdiomaPersonaByPersonalAndIdiomaAndFechaBajaNull(String idPersonal, String idIdioma) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<IdiomaPersona> cr = cb.createQuery(IdiomaPersona.class);
		Root<IdiomaPersona> root = cr.from(IdiomaPersona.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("personal").get("idPersonal"), Integer.valueOf(idPersonal)));
		predicates.add(cb.equal(root.get("idioma").get("idIdioma"), Integer.valueOf(idIdioma)));
		predicates.add(cb.isNull(root.get("fechaBaja")));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<IdiomaPersona> query = entityManager.createQuery(cr);
		List<IdiomaPersona> result = query.getResultList();

		return !CollectionUtils.isEmpty(result);

	}

}
