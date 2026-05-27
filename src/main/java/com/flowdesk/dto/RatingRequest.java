package com.flowdesk.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RatingRequest { private int rating; private String comment; }
