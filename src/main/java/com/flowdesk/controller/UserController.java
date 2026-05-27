package com.flowdesk.controller;
import com.flowdesk.dto.*;
import com.flowdesk.entity.*;
import com.flowdesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepo;
    private final TaskRepository taskRepo;
    @GetMapping("/me") public ResponseEntity<UserResponse> me(Authentication auth) { return ResponseEntity.ok(toDto(userRepo.findByEmail(auth.getName()).orElseThrow())); }
    @GetMapping("/team") public ResponseEntity<List<UserResponse>> team() { return ResponseEntity.ok(userRepo.findByRole("MEMBER").stream().map(this::toDto).collect(Collectors.toList())); }
    @GetMapping("/stats") public ResponseEntity<DashboardStats> stats(Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        boolean isAdmin = "ADMIN".equals(u.getRole());
        long total = isAdmin ? taskRepo.count() : taskRepo.countByAssignedTo(u);
        long done = isAdmin ? taskRepo.findByStatus("COMPLETED").size() : taskRepo.countCompletedByAssignedTo(u);
        return ResponseEntity.ok(DashboardStats.builder().totalTasks(total).completedTasks(done).pendingTasks(total-done).teamMembers(userRepo.findByRole("MEMBER").size()).build());
    }
    private UserResponse toDto(User u) {
        long total = taskRepo.countByAssignedTo(u); long done = taskRepo.countCompletedByAssignedTo(u);
        List<Task> rated = taskRepo.findByAssignedTo(u).stream().filter(t->t.getRating()>0).collect(Collectors.toList());
        double avg = rated.isEmpty()?0:rated.stream().mapToInt(Task::getRating).average().orElse(0);
        return UserResponse.builder().id(u.getId()).email(u.getEmail()).fullName(u.getFullName()).role(u.getRole()).jobTitle(u.getJobTitle()).totalTasks(total).completedTasks(done).avgRating(Math.round(avg*10.0)/10.0).build();
    }
}
