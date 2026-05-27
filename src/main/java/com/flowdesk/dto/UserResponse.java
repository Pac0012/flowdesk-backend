package com.flowdesk.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse { private Long id; private String email; private String fullName; private String role; private String jobTitle; private long totalTasks; private long completedTasks; private double avgRating; }
