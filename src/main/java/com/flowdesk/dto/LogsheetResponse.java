package com.flowdesk.dto;
import lombok.*;
import java.time.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LogsheetResponse { private Long id; private LocalDate weekFrom; private LocalDate weekTo; private int hoursLogged; private String fileName; private String status; private String submittedByName; private Long submittedById; private LocalDateTime submittedAt; }
