package com.flowdesk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private String status = "INCOMPLETE";

    @Column(nullable = false)
    @Builder.Default
    private String priority = "MEDIUM";

    private LocalDate dueDate;

    @Builder.Default
    private Integer storyPoints = 3;

    private Long sprintId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", nullable = false)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;

    @Builder.Default
    private Integer rating = 0;

    private String ratingComment;
    private LocalDateTime ratedAt;
    private LocalDateTime completedAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}