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

import eu.eurogestion.ese.domain.Opcion;
import eu.eurogestion.ese.domain.RolPermiso;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class OpcionDAOCustomImpl implements OpcionDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	public List<Opcion> findAllByIdRol(String idRol) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Opcion> cr = cb.createQuery(Opcion.class);
		Root<Opcion> root = cr.from(Opcion.class);

		List<Predicate> predicates = new ArrayList<>();
		List<Integer> listaIdsOpcionesRol = findOpcionesByIdRol(idRol);

		if (!listaIdsOpcionesRol.isEmpty()) {
			predicates.add(cb.not(root.get("idOpcion").in(listaIdsOpcionesRol)));
		}

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Opcion> query = entityManager.createQuery(cr);
		return query.getResultList();

	}

	private List<Integer> findOpcionesByIdRol(String idRol) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
		Root<RolPermiso> root = cr.from(RolPermiso.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("rol").get("idRol"), idRol));

		cr.select(root.get("permiso").get("opcion").get("idOpcion")).distinct(true)
				.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Integer> query = entityManager.createQuery(cr);
		return query.getResultList();

	}

}
