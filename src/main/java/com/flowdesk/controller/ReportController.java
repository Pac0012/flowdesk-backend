package com.flowdesk.controller;

import com.flowdesk.dto.ReportResponse;
import com.flowdesk.entity.Report;
import com.flowdesk.entity.User;
import com.flowdesk.repository.ReportRepository;
import com.flowdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportRepository reportRepo;
    private final UserRepository userRepo;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public ReportController(ReportRepository reportRepo, UserRepository userRepo) {
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAll(Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<Report> reports;

        if ("ADMIN".equalsIgnoreCase(String.valueOf(u.getRole()))) {
            reports = reportRepo.findAllByOrderBySubmittedAtDesc();
        } else {
            reports = reportRepo.findAll().stream()
                    .filter(r -> r.getSubmittedBy() != null && u.getId().equals(r.getSubmittedBy().getId()))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(reports.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportResponse> submit(
            @RequestParam("title") String title,
            @RequestParam("reportMonth") String reportMonth,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication auth) {

        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        String fileName = null;
        String filePathStr = null;

        if (file != null && !file.isEmpty()) {
            try {
                // Ensure target uploads/reports directory exists in project root
                Path dir = Paths.get(System.getProperty("user.dir"), "uploads", "reports").toAbsolutePath().normalize();
                Files.createDirectories(dir);

                fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path targetPath = dir.resolve(fileName);

                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                filePathStr = targetPath.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        Report report = Report.builder()
                .title(title)
                .reportMonth(reportMonth)
                .fileName(fileName)
                .filePath(filePathStr)
                .submittedBy(u)
                .status("PENDING")
                .submittedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(toDto(reportRepo.save(report)));
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<ReportResponse> review(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Report r = reportRepo.findById(id).orElseThrow();
        r.setStatus("REVIEWED");
        return ResponseEntity.ok(toDto(reportRepo.save(r)));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Report r = reportRepo.findById(id).orElseThrow();
        if (r.getFilePath() == null && r.getFileName() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path file = null;

            // 1. Try saved full path
            if (r.getFilePath() != null) {
                file = Paths.get(r.getFilePath()).toAbsolutePath().normalize();
            }

            // 2. Fallback to uploads/reports folder in project root if path doesn't exist
            if (file == null || !Files.exists(file)) {
                file = Paths.get(System.getProperty("user.dir"), "uploads", "reports", r.getFileName()).toAbsolutePath().normalize();
            }

            if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String downloadName = r.getFileName() != null ? r.getFileName() : file.getFileName().toString();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private ReportResponse toDto(Report r) {
        String submitterName = "";
        if (r.getSubmittedBy() != null) {
            submitterName = r.getSubmittedBy().getEmail();
        }

        return ReportResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .reportMonth(r.getReportMonth())
                .fileName(r.getFileName())
                .status(r.getStatus() != null ? String.valueOf(r.getStatus()) : null)
                .submittedByName(submitterName)
                .submittedAt(r.getSubmittedAt())
                .build();
    }
}