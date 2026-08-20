package com.flowdesk.controller;

import com.flowdesk.dto.*;
import com.flowdesk.entity.*;
import com.flowdesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<Task> tasks = "ADMIN".equals(user.getRole())
                ? taskRepo.findAllByOrderByCreatedAtDesc()
                : taskRepo.findByAssignedToOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(tasks.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assign(@RequestBody TaskRequest req, Authentication auth) {
        User admin = userRepo.findByEmail(auth.getName()).orElseThrow();
        Optional<User> assignee = userRepo.findById(req.getAssignedToId());
        if (assignee.isEmpty()) return ResponseEntity.badRequest().body("Assignee not found");

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority() != null ? req.getPriority().toUpperCase() : "MEDIUM")
                .status("INCOMPLETE")
                .dueDate(req.getDueDate())
                .storyPoints(req.getStoryPoints() != null ? req.getStoryPoints() : 3)
                .sprintId(req.getSprintId())
                .assignedTo(assignee.get())
                .assignedBy(admin)
                .build();

        return ResponseEntity.ok(toDto(taskRepo.save(task)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskRequest req) {
        return taskRepo.findById(id).map(t -> {
            if (req.getTitle() != null) t.setTitle(req.getTitle());
            if (req.getDescription() != null) t.setDescription(req.getDescription());
            if (req.getPriority() != null) t.setPriority(req.getPriority().toUpperCase());
            if (req.getDueDate() != null) t.setDueDate(req.getDueDate());
            if (req.getStoryPoints() != null) t.setStoryPoints(req.getStoryPoints());
            if (req.getSprintId() != null) {
                t.setSprintId(req.getSprintId() == -1 ? null : req.getSprintId());
            }
            if (req.getAssignedToId() != null) {
                userRepo.findById(req.getAssignedToId()).ifPresent(t::setAssignedTo);
            }
            return ResponseEntity.ok(toDto(taskRepo.save(t)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/sprint")
    public ResponseEntity<?> updateSprint(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return taskRepo.findById(id).map(t -> {
            Object sprintVal = body.get("sprintId");
            Long sprintId = sprintVal != null ? Long.valueOf(sprintVal.toString()) : null;
            t.setSprintId(sprintId);
            return ResponseEntity.ok(toDto(taskRepo.save(t)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return taskRepo.findById(id).map(t -> {
            t.setStatus(body.get("status").toUpperCase());
            if ("COMPLETED".equals(t.getStatus())) t.setCompletedAt(LocalDateTime.now());
            return ResponseEntity.ok(toDto(taskRepo.save(t)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/rate")
    public ResponseEntity<TaskResponse> rate(@PathVariable Long id, @RequestBody RatingRequest req) {
        return taskRepo.findById(id).map(t -> {
            t.setRating(req.getRating());
            t.setRatingComment(req.getComment());
            t.setRatedAt(LocalDateTime.now());
            return ResponseEntity.ok(toDto(taskRepo.save(t)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private TaskResponse toDto(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
                .dueDate(t.getDueDate())
                .storyPoints(t.getStoryPoints() != null ? t.getStoryPoints() : 3)
                .sprintId(t.getSprintId())
                .assignedToName(t.getAssignedTo() != null ? t.getAssignedTo().getFullName() : "Unassigned")
                .assignedToId(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .assignedByName(t.getAssignedBy() != null ? t.getAssignedBy().getFullName() : null)
                .rating(t.getRating())
                .ratingComment(t.getRatingComment())
                .createdAt(t.getCreatedAt())
                .completedAt(t.getCompletedAt())
                .build();
    }
}