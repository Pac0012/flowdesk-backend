package com.flowdesk.repository;
import com.flowdesk.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(User user);
    List<Task> findByAssignedToOrderByCreatedAtDesc(User user);
    List<Task> findAllByOrderByCreatedAtDesc();
    List<Task> findByStatus(String status);
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = ?1") long countByAssignedTo(User user);
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = ?1 AND t.status = 'COMPLETED'") long countCompletedByAssignedTo(User user);
}
