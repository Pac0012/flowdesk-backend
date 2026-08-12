package com.flowdesk.controller;


import com.flowdesk.entity.Notice;
import com.flowdesk.repository.NoticeRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")

public class NoticeController {

    private final NoticeRepository noticeRepository;

    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // GET /api/notices
    @GetMapping
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    // POST /api/notices
    @PostMapping
    public Notice createNotice(@RequestBody Notice notice) {

        if (notice.getCreatedAt() == null) {
            notice.setCreatedAt(LocalDateTime.now());
        }

        return noticeRepository.save(notice);
    }
}

