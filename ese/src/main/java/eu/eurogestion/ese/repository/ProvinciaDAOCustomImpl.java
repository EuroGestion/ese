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

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.Provincia;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class ProvinciaDAOCustomImpl implements ProvinciaDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Provincia> findProvinciaByPais(Integer idPais) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Provincia> cr = cb.createQuery(Provincia.class);
		Root<Provincia> root = cr.from(Provincia.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("pais").get("idPais"), idPais));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Provincia> query = entityManager.createQuery(cr);
		return query.getResultList();
	}
}
