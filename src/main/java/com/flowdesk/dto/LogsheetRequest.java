package com.flowdesk.dto;
import lombok.*;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LogsheetRequest { private LocalDate weekFrom; private LocalDate weekTo; private int hoursLogged; }
