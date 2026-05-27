package com.flowdesk.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardStats { private long totalTasks; private long completedTasks; private long pendingTasks; private long totalReports; private long totalLogsheets; private long teamMembers; }
