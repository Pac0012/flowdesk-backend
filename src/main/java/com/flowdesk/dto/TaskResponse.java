package com.flowdesk.dto;
import lombok.*;
import java.time.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TaskResponse { private Long id; private String title; private String description; private String status; private String priority; private LocalDate dueDate; private String assignedToName; private Long assignedToId; private String assignedByName; private int rating; private String ratingComment; private LocalDateTime createdAt; private LocalDateTime completedAt; }
