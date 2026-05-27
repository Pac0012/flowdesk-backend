package com.flowdesk.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity @Table(name="users") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String email;
    @Column(nullable=false) private String password;
    @Column(nullable=false) private String fullName;
    @Column(nullable=false) private String role;
    private String jobTitle;
    @Column(nullable=false) @Builder.Default private boolean active=true;
    @Column(updatable=false) @Builder.Default private LocalDateTime createdAt=LocalDateTime.now();
    @OneToMany(mappedBy="assignedTo",fetch=FetchType.LAZY) @ToString.Exclude @EqualsAndHashCode.Exclude private List<Task> tasks;
}
