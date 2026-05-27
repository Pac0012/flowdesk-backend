package com.flowdesk.dto;
import lombok.*;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TaskRequest { private String title; private String description; private String priority; private LocalDate dueDate; private Long assignedToId; }
