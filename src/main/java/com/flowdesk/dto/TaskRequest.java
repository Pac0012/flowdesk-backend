package com.flowdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private Long assignedToId;
    private Integer storyPoints;
    private Long sprintId;
}