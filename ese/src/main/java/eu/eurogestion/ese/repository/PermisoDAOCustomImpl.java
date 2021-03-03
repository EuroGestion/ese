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

import eu.eurogestion.ese.domain.Permiso;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class PermisoDAOCustomImpl implements PermisoDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Permiso findByIdOpcionAndIdTipoPermiso(String idOpcion, String idTipoPermiso) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Permiso> cr = cb.createQuery(Permiso.class);
		Root<Permiso> root = cr.from(Permiso.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("opcion").get("idOpcion"), idOpcion));

		predicates.add(cb.equal(root.get("tipoPermiso").get("idTipoPermiso"), idTipoPermiso));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Permiso> query = entityManager.createQuery(cr);
		List<Permiso> listaPermisos = query.getResultList();

		if (listaPermisos.isEmpty()) {
			return null;
		}
		return listaPermisos.get(0);
	}
}
