package com.flowdesk.config;

import com.flowdesk.entity.Task;
import com.flowdesk.entity.User;
import com.flowdesk.repository.TaskRepository;
import com.flowdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final TaskRepository taskRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        String defaultPass = encoder.encode("Password123!");

        // Administrator / Team Lead
        User admin = userRepo.save(User.builder()
                .email("owethu@flowdesk.io")
                .password(defaultPass)
                .fullName("Owethu Mabeloane")
                .role("ADMIN")
                .jobTitle("Administrator & Team Lead")
                .build());

        // VPS Team Members
        List<User> members = List.of(
            User.builder().email("modipadi@flowdesk.io").password(defaultPass).fullName("Modipadi").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("mhlongo@flowdesk.io").password(defaultPass).fullName("Mhlongo").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("boitumelo@flowdesk.io").password(defaultPass).fullName("Boitumelo").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("kgothatso@flowdesk.io").password(defaultPass).fullName("Kgothatso").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("thato@flowdesk.io").password(defaultPass).fullName("Thato").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("lihle@flowdesk.io").password(defaultPass).fullName("Lihle").role("MEMBER").jobTitle("UI/UX Designer").build(),
            User.builder().email("ntokozo@flowdesk.io").password(defaultPass).fullName("Ntokozo").role("MEMBER").jobTitle("Backend Developer").build(),
            User.builder().email("light@flowdesk.io").password(defaultPass).fullName("Light Mnisi").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("percy@flowdesk.io").password(defaultPass).fullName("Percy Thotse").role("MEMBER").jobTitle("DevOps & Database").build(),
            User.builder().email("arnold@flowdesk.io").password(defaultPass).fullName("Arnold").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("klass@flowdesk.io").password(defaultPass).fullName("Klass Modikwa").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("amanda@flowdesk.io").password(defaultPass).fullName("Amanda").role("MEMBER").jobTitle("Business Analyst").build(),
            User.builder().email("theo@flowdesk.io").password(defaultPass).fullName("Theo").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("lebohang@flowdesk.io").password(defaultPass).fullName("Lebohang").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("zithelo@flowdesk.io").password(defaultPass).fullName("Zithelo Timba").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("nkululeko@flowdesk.io").password(defaultPass).fullName("Nkululeko Hlabisa").role("MEMBER").jobTitle("QA Engineer").build(),
            User.builder().email("valentino@flowdesk.io").password(defaultPass).fullName("Valentino").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("simiso@flowdesk.io").password(defaultPass).fullName("Simiso").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("moseamo@flowdesk.io").password(defaultPass).fullName("Selaelo Moseamo").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("gently@flowdesk.io").password(defaultPass).fullName("Gently Siboza").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("samson@flowdesk.io").password(defaultPass).fullName("Samson Gumede").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("sanele@flowdesk.io").password(defaultPass).fullName("Sanele Robin").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("timba@flowdesk.io").password(defaultPass).fullName("Timba").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("thamaga@flowdesk.io").password(defaultPass).fullName("Thamaga").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("freedom@flowdesk.io").password(defaultPass).fullName("Freedom").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("penuel@flowdesk.io").password(defaultPass).fullName("Penuel").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("sabelo@flowdesk.io").password(defaultPass).fullName("Sabelo").role("MEMBER").jobTitle("Developer").build(),
            User.builder().email("kwanda@flowdesk.io").password(defaultPass).fullName("Kwanda").role("MEMBER").jobTitle("Developer").build()
        );

        userRepo.saveAll(members);

        // Initial Seed Task
        taskRepo.save(Task.builder()
                .title("SCRUM-220: Change footer.php to comic UI/UX")
                .description("Align footer style with comic theme specifications")
                .priority("HIGH")
                .status("IN_PROGRESS")
                .storyPoints(5)
                .dueDate(LocalDate.now().plusDays(3))
                .assignedTo(admin)
                .assignedBy(admin)
                .build());

        log.info("Successfully seeded {} team members with default password 'Password123!'", members.size() + 1);
    }
}