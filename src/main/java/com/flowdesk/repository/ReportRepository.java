package com.flowdesk.repository;
import com.flowdesk.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findBySubmittedByOrderBySubmittedAtDesc(User user);
    List<Report> findAllByOrderBySubmittedAtDesc();
}
