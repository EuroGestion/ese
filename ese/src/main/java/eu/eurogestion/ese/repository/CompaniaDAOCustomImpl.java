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

import eu.eurogestion.ese.domain.Compania;
import eu.eurogestion.ese.utils.Constantes;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class CompaniaDAOCustomImpl implements CompaniaDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * Obtiene todas los Companias en activo correspondientes a centros de
	 * formacion.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	public List<Compania> findAllCompaniaFormacion() {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Compania> cr = cb.createQuery(Compania.class);
		Root<Compania> root = cr.from(Compania.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));
		predicates.add(
				cb.equal(root.get("tipoCompania").get("idTipoCompania"), Constantes.TIPO_COMPANIA_CENTRO_FORMACION));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Compania> query = entityManager.createQuery(cr);
		return query.getResultList();
	}

	/**
	 * Obtiene todas los Companias en activo correspondientes a centros medicos.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	public List<Compania> findAllCompaniaCentroMedico() {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Compania> cr = cb.createQuery(Compania.class);
		Root<Compania> root = cr.from(Compania.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));
		predicates
				.add(cb.equal(root.get("tipoCompania").get("idTipoCompania"), Constantes.TIPO_COMPANIA_CENTRO_MEDICO));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Compania> query = entityManager.createQuery(cr);
		return query.getResultList();
	}

	/**
	 * Obtiene todas los Companias en activo correspondientes en base a unos
	 * filtros.
	 * 
	 * @return Lista de Companias (0-n).
	 */
	public Page<Compania> findAllCompaniaAltaByFilters(String documento, String nombre, String idTipoCompania,
			PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Compania> cr = cb.createQuery(Compania.class);
		Root<Compania> root = cr.from(Compania.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));

		if (StringUtils.isNotBlank(documento)) {
			predicates.add(cb.equal(root.get("documento"), documento));
		}
		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(root.get("nombre"), "%" + nombre + "%"));
		}

		if (StringUtils.isNotBlank(idTipoCompania)) {
			predicates.add(cb.equal(root.get("tipoCompania").get("idTipoCompania"), idTipoCompania));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Compania> query = entityManager.createQuery(cr);

		List<Compania> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Compania> countRoot = countQuery.from(Compania.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

}
