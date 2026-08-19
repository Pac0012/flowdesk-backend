package com.flowdesk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity 
@Table(name="tasks") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Task {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable=false) 
    private String title;

    @Column(length=2000) 
    private String description;

    @Column(nullable=false) 
    @Builder.Default 
    private String status = "INCOMPLETE"; // INCOMPLETE, IN_PROGRESS, QA_TESTING, COMPLETED

    @Column(nullable=false) 
    @Builder.Default 
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH

    private Integer storyPoints;
    private Long sprintId; // null = in Product Backlog

    private LocalDate dueDate;

    @ManyToOne(fetch=FetchType.EAGER) 
    @JoinColumn(name="assigned_to_id", nullable=false) 
    private User assignedTo;

    @ManyToOne(fetch=FetchType.EAGER) 
    @JoinColumn(name="assigned_by_id", nullable=false) 
    private User assignedBy;

    @Builder.Default 
    private int rating = 0;
    private String ratingComment;

    @Column(updatable=false) 
    @Builder.Default 
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime completedAt;
    private LocalDateTime ratedAt;
}