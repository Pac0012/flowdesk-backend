package com.flowdesk.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="reports") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String title;
    @Column(nullable=false) private String reportMonth;
    private String fileName; private String filePath;
    @Builder.Default private String status="PENDING";
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="submitted_by_id",nullable=false) private User submittedBy;
    private String reviewComment;
    @Column(updatable=false) @Builder.Default private LocalDateTime submittedAt=LocalDateTime.now();
    private LocalDateTime reviewedAt;
}
