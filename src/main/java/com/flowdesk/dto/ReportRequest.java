package com.flowdesk.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReportRequest { private String title; private String reportMonth; }
