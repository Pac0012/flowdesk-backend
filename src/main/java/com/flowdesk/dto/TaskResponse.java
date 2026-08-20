package com.flowdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private Integer storyPoints;
    private Long sprintId;
    private String assignedToName;
    private Long assignedToId;
    private String assignedByName;
    private Integer rating;
    private String ratingComment;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}