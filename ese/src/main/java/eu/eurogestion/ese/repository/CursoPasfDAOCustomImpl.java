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

import eu.eurogestion.ese.domain.CursoPasf;

/**
 * @author Rmerino, alvaro
 *
 */

@Repository
@Transactional
public class CursoPasfDAOCustomImpl implements CursoPasfDAOCustom {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Page<CursoPasf> findCursoPasfByPasf(String idPasf, PageRequest pageRequest) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CursoPasf> cr = cb.createQuery(CursoPasf.class);
		Root<CursoPasf> root = cr.from(CursoPasf.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("pasf").get("idPasf"), Integer.valueOf(idPasf)));

		cr.select(root).distinct(true).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<CursoPasf> query = entityManager.createQuery(cr);
		List<CursoPasf> result = query.setFirstResult((int) pageRequest.getOffset())
				.setMaxResults(pageRequest.getPageSize()).getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<CursoPasf> countRoot = countQuery.from(CursoPasf.class);
		countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(result, pageRequest, count);
	}

	@Override
	public Integer sumByPasfAndCategory(Integer idPasf, Integer category) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
		Root<CursoPasf> root = cr.from(CursoPasf.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("pasf").get("idPasf"), idPasf));
		predicates.add(cb.equal(root.get("categoria").get("idTipoCurso"), category));

		cr.select(cb.sum(root.get("numAsistentes"))).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Integer> query = entityManager.createQuery(cr);
		Integer resultado = query.getSingleResult();
		if (resultado == null) {
			resultado = 0;
		}
		return resultado;
	}

}
