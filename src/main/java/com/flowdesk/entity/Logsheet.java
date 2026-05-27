package com.flowdesk.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="logsheets") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Logsheet {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private LocalDate weekFrom;
    @Column(nullable=false) private LocalDate weekTo;
    @Column(nullable=false) private int hoursLogged;
    private String fileName; private String filePath;
    @Builder.Default private String status="PENDING";
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="submitted_by_id",nullable=false) private User submittedBy;
    @Column(updatable=false) @Builder.Default private LocalDateTime submittedAt=LocalDateTime.now();
    private LocalDateTime reviewedAt; private String reviewComment;
}
