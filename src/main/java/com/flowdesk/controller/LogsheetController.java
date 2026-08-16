package com.flowdesk.controller;

import com.flowdesk.dto.*;
import com.flowdesk.entity.*;
import com.flowdesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logsheets")
@RequiredArgsConstructor
public class LogsheetController {

    private final LogsheetRepository logRepo;
    private final UserRepository userRepo;

    private File getStorageDir() {
        File dir = new File(System.getProperty("user.dir"), "uploads" + File.separator + "logsheets");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @GetMapping
    public ResponseEntity<List<LogsheetResponse>> getAll(Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<Logsheet> list = "ADMIN".equalsIgnoreCase(String.valueOf(u.getRole()))
                ? logRepo.findAllByOrderBySubmittedAtDesc()
                : logRepo.findBySubmittedByOrderBySubmittedAtDesc(u);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogsheetResponse> submit(
            @RequestParam String weekFrom,
            @RequestParam String weekTo,
            @RequestParam int hoursLogged,
            @RequestParam(required = false) MultipartFile file,
            Authentication auth) {

        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        String fileName = null;
        String filePathStr = null;

        if (file != null && !file.isEmpty()) {
            try {
                File dir = getStorageDir();
                fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File targetFile = new File(dir, fileName);

                file.transferTo(targetFile);
                filePathStr = targetFile.getAbsolutePath();

                System.out.println("=== LOGSHEET FILE SAVED AT: " + filePathStr + " ===");
            } catch (Exception e) {
                System.err.println("=== FAILED TO SAVE LOGSHEET FILE ===");
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        Logsheet logsheet = Logsheet.builder()
                .weekFrom(LocalDate.parse(weekFrom))
                .weekTo(LocalDate.parse(weekTo))
                .hoursLogged(hoursLogged)
                .fileName(fileName)
                .filePath(filePathStr)
                .submittedBy(u)
                .status("PENDING")
                .submittedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(toDto(logRepo.save(logsheet)));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<LogsheetResponse> approve(@PathVariable Long id) {
        return logRepo.findById(id).map(l -> {
            l.setStatus("APPROVED");
            l.setReviewedAt(LocalDateTime.now());
            return ResponseEntity.ok(toDto(logRepo.save(l)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Logsheet l = logRepo.findById(id).orElse(null);
        if (l == null) {
            System.err.println("=== LOGSHEET RECORD NOT FOUND FOR ID: " + id + " ===");
            return ResponseEntity.notFound().build();
        }

        File targetFile = null;

        // Try using absolute stored path
        if (l.getFilePath() != null && !l.getFilePath().trim().isEmpty()) {
            targetFile = new File(l.getFilePath());
        }

        // Fallback using fileName
        if ((targetFile == null || !targetFile.exists()) && l.getFileName() != null && !l.getFileName().trim().isEmpty()) {
            targetFile = new File(getStorageDir(), l.getFileName());
        }

        if (targetFile == null || !targetFile.exists()) {
            System.err.println("=== FILE DOES NOT EXIST ON DISK: " + (targetFile != null ? targetFile.getAbsolutePath() : "NULL") + " ===");
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new UrlResource(targetFile.toURI());
            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("=== RESOURCE NOT READABLE: " + targetFile.getAbsolutePath() + " ===");
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(targetFile.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String downloadName = l.getFileName() != null ? l.getFileName().replaceFirst("^\\d+_", "") : targetFile.getName();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private LogsheetResponse toDto(Logsheet l) {
        String submitterName = "";
        if (l.getSubmittedBy() != null) {
            submitterName = l.getSubmittedBy().getFullName() != null
                    ? l.getSubmittedBy().getFullName()
                    : l.getSubmittedBy().getEmail();
        }

        return LogsheetResponse.builder()
                .id(l.getId())
                .weekFrom(l.getWeekFrom())
                .weekTo(l.getWeekTo())
                .hoursLogged(l.getHoursLogged())
                .fileName(l.getFileName())
                .status(l.getStatus())
                .submittedByName(submitterName)
                .submittedById(l.getSubmittedBy() != null ? l.getSubmittedBy().getId() : null)
                .submittedAt(l.getSubmittedAt())
                .build();
    }
}