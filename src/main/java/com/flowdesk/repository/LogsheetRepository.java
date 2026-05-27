package com.flowdesk.repository;
import com.flowdesk.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LogsheetRepository extends JpaRepository<Logsheet, Long> {
    List<Logsheet> findBySubmittedByOrderBySubmittedAtDesc(User user);
    List<Logsheet> findAllByOrderBySubmittedAtDesc();
}
