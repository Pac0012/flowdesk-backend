package com.flowdesk.repository;

import com.flowdesk.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate date);

    List<Attendance> findByDate(LocalDate date);

    // Used to compute streaks and points on the frontend without
    // adding a new stats table. Inclusive of both bounds.
    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);
}