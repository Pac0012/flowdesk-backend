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
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
@RestController @RequestMapping("/api/logsheets") @RequiredArgsConstructor
public class LogsheetController {
    private final LogsheetRepository logRepo;
    private final UserRepository userRepo;
    @Value("${app.upload.dir}") private String uploadDir;
    @GetMapping public ResponseEntity<List<LogsheetResponse>> getAll(Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        List<Logsheet> list = "ADMIN".equals(u.getRole()) ? logRepo.findAllByOrderBySubmittedAtDesc() : logRepo.findBySubmittedByOrderBySubmittedAtDesc(u);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }
    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogsheetResponse> submit(@RequestParam String weekFrom, @RequestParam String weekTo, @RequestParam int hoursLogged, @RequestParam(required=false) MultipartFile file, Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        String fileName=null;
        if(file!=null&&!file.isEmpty()){try{Path dir=Paths.get(uploadDir,"logsheets");Files.createDirectories(dir);fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();Files.copy(file.getInputStream(),dir.resolve(fileName),StandardCopyOption.REPLACE_EXISTING);}catch(Exception e){fileName=file.getOriginalFilename();}}
        return ResponseEntity.ok(toDto(logRepo.save(Logsheet.builder().weekFrom(LocalDate.parse(weekFrom)).weekTo(LocalDate.parse(weekTo)).hoursLogged(hoursLogged).fileName(fileName).submittedBy(u).build())));
    }
    @PatchMapping("/{id}/approve") public ResponseEntity<LogsheetResponse> approve(@PathVariable Long id) {
        return logRepo.findById(id).map(l->{l.setStatus("APPROVED");l.setReviewedAt(LocalDateTime.now());return ResponseEntity.ok(toDto(logRepo.save(l)));}).orElse(ResponseEntity.notFound().build());
    }
    private LogsheetResponse toDto(Logsheet l) { return LogsheetResponse.builder().id(l.getId()).weekFrom(l.getWeekFrom()).weekTo(l.getWeekTo()).hoursLogged(l.getHoursLogged()).fileName(l.getFileName()).status(l.getStatus()).submittedByName(l.getSubmittedBy().getFullName()).submittedById(l.getSubmittedBy().getId()).submittedAt(l.getSubmittedAt()).build(); }
}
