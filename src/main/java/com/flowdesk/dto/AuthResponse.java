package com.flowdesk.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse { private String token; private String role; private String fullName; private Long userId; private String email; private String jobTitle; }
