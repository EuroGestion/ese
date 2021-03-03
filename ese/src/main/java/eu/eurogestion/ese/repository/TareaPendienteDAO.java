package eu.eurogestion.ese.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.eurogestion.ese.domain.TareaPendiente;

/**
 * @author Rmerino, alvaro
 *
 */
@Repository
@Transactional
public interface TareaPendienteDAO extends JpaRepository<TareaPendiente, Integer> {
	/**
	 * Obtiene todas las tareas pendientes, no leidas, de un usuario.
	 * 
	 * @param idDestinatario Integer con el valor tarea_pendiente.id_destinatario en
	 *                       DDBB
	 * @return Lista de tareas pendientes (0-n).
	 */
	@Query("SELECT tarea FROM TareaPendiente tarea  WHERE tarea.leido = '0' and tarea.destinatario.idPersonal = ?1")
	public Page<TareaPendiente> findTareaPendienteByIdDestinatario(Integer idDestinatario, Pageable pageable);

	@Query("SELECT tarea FROM TareaPendiente tarea  WHERE tarea.leido = '0' and tarea.idTareaPte = ?1 AND tarea.tablaTareaPte = ?2")
	public TareaPendiente findTareaPendienteByReference(Integer idTareaPte, String tablaTareaPte);

	@Query("SELECT tarea FROM TareaPendiente tarea  WHERE tarea.leido = '0' and tarea.destinatario.idPersonal = ?1 AND tarea.tipoTarea.idTipoTarea = ?2")
	public TareaPendiente findTareaPendienteByIdDestinatarioAndTipoTarea(Integer idDestinatario, Integer tipoTarea);
}
