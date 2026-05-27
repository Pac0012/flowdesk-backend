package com.flowdesk.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReportResponse { private Long id; private String title; private String reportMonth; private String fileName; private String status; private String submittedByName; private Long submittedById; private LocalDateTime submittedAt; private String reviewComment; }
