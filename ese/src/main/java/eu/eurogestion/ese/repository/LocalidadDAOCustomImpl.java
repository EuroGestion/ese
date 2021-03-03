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

import eu.eurogestion.ese.domain.Localidad;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class LocalidadDAOCustomImpl implements LocalidadDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Localidad> findLocalidadByPaisAndProvincia(Integer idPais, Integer idProvincia) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Localidad> cr = cb.createQuery(Localidad.class);
		Root<Localidad> root = cr.from(Localidad.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("pais").get("idPais"), idPais));
		predicates.add(cb.equal(root.get("provincia").get("idProvincia"), idProvincia));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Localidad> query = entityManager.createQuery(cr);
		return query.getResultList();
	}
}
