package com.flowdesk.controller;
import com.flowdesk.dto.*;
import com.flowdesk.entity.*;
import com.flowdesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@RestController @RequestMapping("/api/reports") @RequiredArgsConstructor
public class ReportController {
    private final ReportRepository reportRepo;
    private final UserRepository userRepo;
    @Value("${app.upload.dir}") private String uploadDir;
    @GetMapping public ResponseEntity<List<ReportResponse>> getAll(Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<Report> list = "ADMIN".equals(u.getRole()) ? reportRepo.findAllByOrderBySubmittedAtDesc() : reportRepo.findBySubmittedByOrderBySubmittedAtDesc(u);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }
    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportResponse> submit(@RequestParam String title, @RequestParam String reportMonth, @RequestParam(required=false) MultipartFile file, Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        String fileName=null;
        if(file!=null&&!file.isEmpty()){try{Path dir=Paths.get(uploadDir,"reports");Files.createDirectories(dir);fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();Files.copy(file.getInputStream(),dir.resolve(fileName),StandardCopyOption.REPLACE_EXISTING);}catch(Exception e){fileName=file.getOriginalFilename();}}
        return ResponseEntity.ok(toDto(reportRepo.save(Report.builder().title(title).reportMonth(reportMonth).fileName(fileName).submittedBy(u).build())));
    }
    @PatchMapping("/{id}/review") public ResponseEntity<ReportResponse> review(@PathVariable Long id, @RequestBody Map<String,String> body) {
        return reportRepo.findById(id).map(r->{r.setStatus("REVIEWED");r.setReviewComment(body.get("comment"));r.setReviewedAt(LocalDateTime.now());return ResponseEntity.ok(toDto(reportRepo.save(r)));}).orElse(ResponseEntity.notFound().build());
    }
    private ReportResponse toDto(Report r) { return ReportResponse.builder().id(r.getId()).title(r.getTitle()).reportMonth(r.getReportMonth()).fileName(r.getFileName()).status(r.getStatus()).submittedByName(r.getSubmittedBy().getFullName()).submittedById(r.getSubmittedBy().getId()).submittedAt(r.getSubmittedAt()).reviewComment(r.getReviewComment()).build(); }
}
