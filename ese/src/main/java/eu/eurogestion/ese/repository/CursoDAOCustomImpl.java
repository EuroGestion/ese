package eu.eurogestion.ese.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
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

import eu.eurogestion.ese.domain.Curso;
import eu.eurogestion.ese.domain.CursoAlumno;
import eu.eurogestion.ese.domain.Personal;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class CursoDAOCustomImpl implements CursoDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Curso> findCursoByPersonal(String idPersonal) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Curso> cr = cb.createQuery(Curso.class);
		Root<Curso> root = cr.from(Curso.class);
		Join<Curso, CursoAlumno> join = root.join("listCursoAlumno");
		Join<CursoAlumno, Personal> joinPersonal = join.join("personal");

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(joinPersonal.get("idPersonal"), idPersonal));
		predicates.add(cb.greaterThan(root.get("fechaCaducidad"), new Date()));
		predicates.add(cb.lessThanOrEqualTo(root.get("validoDesde"), new Date()));
		predicates.add(cb.equal(join.get("estadoCursoAlumno").get("idEstadoCursoAlumno"),
				Constantes.ESTADO_CURSO_ALUMNO_SUPERADO));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Curso> query = entityManager.createQuery(cr);
		return query.getResultList();
	}

	@Override
	public Page<Curso> findCursoByFilters(String idEstado, String idCentroFormacion, String fechaInicio,
			String fechaFin, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Curso> cr = cb.createQuery(Curso.class);
		Root<Curso> root = cr.from(Curso.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(idCentroFormacion)) {
			predicates.add(cb.equal(root.get("centroFor").get("idCompania"), Integer.valueOf(idCentroFormacion)));
		}

		if (StringUtils.isNotBlank(fechaInicio)) {
			predicates.add(cb.equal(root.get("fechaInicio"), Utiles.parseDatePantalla(fechaInicio)));
		}

		if (StringUtils.isNotBlank(fechaFin)) {
			predicates.add(cb.equal(root.get("fechaFin"), Utiles.parseDatePantalla(fechaFin)));
		}

		List<Integer> listIdEstado = new ArrayList<>();
		if (StringUtils.isNotBlank(idEstado)) {
			listIdEstado.add(Integer.valueOf(idEstado));
		} else {
			listIdEstado.add(Constantes.ESTADO_CURSO_CREADO);
			listIdEstado.add(Constantes.ESTADO_CURSO_PLANIFICADO);
			listIdEstado.add(Constantes.ESTADO_CURSO_PENDIENTE_APROVACION);
		}

		predicates.add(root.get("estadoCurso").get("idEstadoCurso").in(listIdEstado));

		cr.select(root).where(predicates.toArray(new Predicate[] {}));
		Sort sort = pageRequest.getSort();
		cr.orderBy(QueryUtils.toOrders(sort, root, cb));
		TypedQuery<Curso> query = entityManager.createQuery(cr);
		List<Curso> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Curso> countRoot = countQuery.from(Curso.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Long findCursoByYear(Integer anno) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<Curso> root = cr.from(Curso.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("fechaFin")), anno));
		predicates.add(cb.equal(root.get("estadoCurso").get("idEstadoCurso"), Constantes.ESTADO_CURSO_APROBADO));

		cr.select(cb.count(root)).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> query = entityManager.createQuery(cr);
		return query.getSingleResult();
	}

}
