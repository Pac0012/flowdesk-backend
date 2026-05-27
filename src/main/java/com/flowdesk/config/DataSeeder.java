package com.flowdesk.config;
import com.flowdesk.entity.*;
import com.flowdesk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
@Component @RequiredArgsConstructor @Slf4j
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepo;
    private final TaskRepository taskRepo;
    private final PasswordEncoder encoder;
    @Override public void run(String... args) {
        if (userRepo.count() > 0) return;
        User admin = userRepo.save(User.builder().email("admin@flowdesk.io").password(encoder.encode("admin123")).fullName("System Admin").role("ADMIN").jobTitle("Administrator").build());
        User sipho = userRepo.save(User.builder().email("sipho@flowdesk.io").password(encoder.encode("member123")).fullName("Sipho Dlamini").role("MEMBER").jobTitle("Developer").build());
        User thandi = userRepo.save(User.builder().email("thandi@flowdesk.io").password(encoder.encode("member123")).fullName("Thandi Mokoena").role("MEMBER").jobTitle("Analyst").build());
        User lebo = userRepo.save(User.builder().email("lebo@flowdesk.io").password(encoder.encode("member123")).fullName("Lebo Khumalo").role("MEMBER").jobTitle("DevOps").build());
        User zanele = userRepo.save(User.builder().email("zanele@flowdesk.io").password(encoder.encode("member123")).fullName("Zanele Nkosi").role("MEMBER").jobTitle("Designer").build());
        taskRepo.save(Task.builder().title("Q2 Financial Report").description("Analyse Q2 financials.").priority("HIGH").status("IN_PROGRESS").dueDate(LocalDate.now().plusDays(5)).assignedTo(thandi).assignedBy(admin).build());
        taskRepo.save(Task.builder().title("Client Presentation Deck").description("Create slides for Acme Corp.").priority("MEDIUM").status("COMPLETED").rating(4).dueDate(LocalDate.now().minusDays(3)).assignedTo(sipho).assignedBy(admin).build());
        taskRepo.save(Task.builder().title("Database Migration Plan").description("Plan legacy DB migration.").priority("HIGH").status("COMPLETED").rating(5).dueDate(LocalDate.now().minusDays(5)).assignedTo(lebo).assignedBy(admin).build());
        taskRepo.save(Task.builder().title("Social Media Calendar").description("Plan June content.").priority("LOW").status("NOT_STARTED").dueDate(LocalDate.now().plusDays(10)).assignedTo(zanele).assignedBy(admin).build());
        taskRepo.save(Task.builder().title("Security Audit Review").description("Review security findings.").priority("HIGH").status("COMPLETED").rating(3).dueDate(LocalDate.now().minusDays(1)).assignedTo(sipho).assignedBy(admin).build());
        log.info("Seeded. Login: admin@flowdesk.io/admin123 | sipho@flowdesk.io/member123");
    }
}
