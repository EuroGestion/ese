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

import eu.eurogestion.ese.domain.MaterialComposicion;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class MaterialComposicionDAOCustomImpl implements MaterialComposicionDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<MaterialComposicion> findByIdsComposicionAndIdMaterial(List<Integer> listIdComposicion,
			Integer idMaterial) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MaterialComposicion> cr = cb.createQuery(MaterialComposicion.class);
		Root<MaterialComposicion> root = cr.from(MaterialComposicion.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("material").get("idMaterial"), idMaterial));
		predicates.add(root.get("composicion").get("idComposicion").in(listIdComposicion));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<MaterialComposicion> query = entityManager.createQuery(cr);
		return query.getResultList();
	}

}
