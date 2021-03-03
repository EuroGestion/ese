package eu.eurogestion.ese.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class PersonalDAOCustomImpl implements PersonalDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	public List<Personal> findAllMaquinistas() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));
		predicates.add(cb.equal(root.get("cargo").get("idCargo"), Constantes.CARGO_PERSONAL_MAQUINISTA));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));
		cr.orderBy(cb.asc(root.get("apellido1")), cb.asc(root.get("apellido2")), cb.asc(root.get("nombre")));
		TypedQuery<Personal> query = entityManager.createQuery(cr);
		return query.getResultList();

	}

	@Override
	public List<Personal> findPersonalByFilters(String dni, String idCargo, String nombre, String apellido) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);
		root.fetch("cargo");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));

		if (StringUtils.isNotBlank(dni)) {
			predicates.add(cb.equal(root.get("documento"), dni));
		}

		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(root.get("nombre"), "%" + nombre + "%"));
		}

		if (StringUtils.isNotBlank(apellido)) {
			predicates.add(cb.like(root.get("apellido1"), "%" + apellido + "%"));
		}

		if (StringUtils.isNotBlank(idCargo)) {
			predicates.add(cb.equal(root.get("cargo").get("idCargo"), idCargo));
		}

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));
		cr.orderBy(cb.asc(root.get("apellido1")), cb.asc(root.get("apellido2")), cb.asc(root.get("nombre")));
		TypedQuery<Personal> query = entityManager.createQuery(cr);
		return query.getResultList();
	}

	@Override
	public Page<Personal> findBuscadorPersonalByFilters(String dni, String idCargo, String nombre, String apellido,
			PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);
		root.fetch("cargo");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));

		if (StringUtils.isNotBlank(dni)) {
			predicates.add(cb.equal(root.get("documento"), dni));
		}

		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(root.get("nombre"), "%" + nombre + "%"));
		}

		if (StringUtils.isNotBlank(apellido)) {
			predicates.add(cb.like(root.get("apellido1"), "%" + apellido + "%"));
		}

		if (StringUtils.isNotBlank(idCargo)) {
			predicates.add(cb.equal(root.get("cargo").get("idCargo"), idCargo));
		}

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Personal> query = entityManager.createQuery(cr);
		List<Personal> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Personal> countRoot = countQuery.from(Personal.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	public Page<Personal> findPlanificarFormRevByFiltersTotales(String dni, String idCargo, String nombre,
			String apellido, Set<String> listIdPersonal, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);
		root.fetch("cargo");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));

		if (StringUtils.isNotBlank(dni)) {
			predicates.add(cb.equal(root.get("documento"), dni));
		}

		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(root.get("nombre"), "%" + nombre + "%"));
		}

		if (StringUtils.isNotBlank(apellido)) {
			predicates.add(cb.like(root.get("apellido1"), "%" + apellido + "%"));
		}

		if (StringUtils.isNotBlank(idCargo)) {
			predicates.add(cb.equal(root.get("cargo").get("idCargo"), idCargo));
		}

		if (!CollectionUtils.isEmpty(listIdPersonal)) {
			predicates.add(cb.not(root.get("idPersonal").in(listIdPersonal)));
		}

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Personal> query = entityManager.createQuery(cr);
		List<Personal> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Personal> countRoot = countQuery.from(Personal.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	public Long countPlanificarFormRevByFiltersTotales(String dni, String idCargo, String nombre, String apellido,
			Set<String> listIdPersonal) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Personal> countRoot = countQuery.from(Personal.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(countRoot.get("fechaBaja")));

		if (StringUtils.isNotBlank(dni)) {
			predicates.add(cb.equal(countRoot.get("documento"), dni));
		}

		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(countRoot.get("nombre"), "%" + nombre + "%"));
		}

		if (StringUtils.isNotBlank(apellido)) {
			predicates.add(cb.like(countRoot.get("apellido1"), "%" + apellido + "%"));
		}

		if (StringUtils.isNotBlank(idCargo)) {
			predicates.add(cb.equal(countRoot.get("cargo").get("idCargo"), idCargo));
		}

		if (!CollectionUtils.isEmpty(listIdPersonal)) {
			predicates.add(cb.not(countRoot.get("idPersonal").in(listIdPersonal)));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		return entityManager.createQuery(countQuery).getSingleResult();

	}

	public Page<Personal> findPlanificarFormRevByFiltersSeleccionados(Set<String> listIdPersonal,
			PageRequest pageRequest) {

		if (CollectionUtils.isEmpty(listIdPersonal)) {
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);
		root.fetch("cargo");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(root.get("idPersonal").in(listIdPersonal));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Personal> query = entityManager.createQuery(cr);
		List<Personal> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Personal> countRoot = countQuery.from(Personal.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	public Long countPlanificarFormRevByFiltersSeleccionados(Set<String> listIdPersonal) {

		if (CollectionUtils.isEmpty(listIdPersonal)) {
			return 0L;
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Personal> countRoot = countQuery.from(Personal.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(countRoot.get("idPersonal").in(listIdPersonal));

		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		return entityManager.createQuery(countQuery).getSingleResult();

	}

	@Override
	public List<Personal> findPersonalByFilters(String dni, String idCargo, String nombre, String apellido,
			List<Integer> listIdPersonal) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);
		root.fetch("cargo");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));

		if (StringUtils.isNotBlank(dni)) {
			predicates.add(cb.equal(root.get("documento"), dni));
		}

		if (StringUtils.isNotBlank(nombre)) {
			predicates.add(cb.like(root.get("nombre"), "%" + nombre + "%"));
		}

		if (StringUtils.isNotBlank(apellido)) {
			predicates.add(cb.like(root.get("apellido1"), "%" + apellido + "%"));
		}

		if (StringUtils.isNotBlank(idCargo)) {
			predicates.add(cb.equal(root.get("cargo").get("idCargo"), idCargo));
		}

		if (!CollectionUtils.isEmpty(listIdPersonal)) {
			predicates.add(cb.not(root.get("idPersonal").in(listIdPersonal)));
		}

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Personal> query = entityManager.createQuery(cr);
		return query.getResultList();
	}

	@Override
	public List<Personal> findByList(List<Integer> listIdPersonal) {

		if (CollectionUtils.isEmpty(listIdPersonal)) {
			return new ArrayList<>();
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);
		root.fetch("cargo");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isNull(root.get("fechaBaja")));

		predicates.add(root.get("idPersonal").in(listIdPersonal));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Personal> query = entityManager.createQuery(cr);
		return query.getResultList();
	}

	@Override
	public Personal findByNombreUsuarioAndClaveAndFechaBajaIsNull(String nombreUsuario, String clave) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Personal> cr = cb.createQuery(Personal.class);
		Root<Personal> root = cr.from(Personal.class);
		root.fetch("cargo");

		List<Predicate> predicates = new ArrayList<>();
		if (StringUtils.isBlank(nombreUsuario) || StringUtils.isBlank(clave)) {
			return null;
		}
		String passwordCifrada = Utiles.cifrarPassword(clave);
		predicates.add(cb.isNull(root.get("fechaBaja")));

		predicates.add(cb.equal(root.get("nombreUsuario"), nombreUsuario));

		predicates.add(cb.equal(root.get("clave"), passwordCifrada));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Personal> query = entityManager.createQuery(cr);
		List<Personal> listaPersonal = query.getResultList();

		if (listaPersonal.isEmpty()) {
			return null;
		}
		return listaPersonal.get(0);
	}

}
