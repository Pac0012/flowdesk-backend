package com.flowdesk.controller;

import com.flowdesk.entity.Attendance;
import  com.flowdesk.repository.UserRepository;
import com.flowdesk.entity.User;
import com.flowdesk.repository.AttendanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceController(
            AttendanceRepository attendanceRepository,
            UserRepository userRepository) {

        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    // POST /api/attendance/mark
    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Attendance attendance) {

        LocalDate today = LocalDate.now();

        // Make sure attendance is only recorded for today
        attendance.setDate(today);

        // Check if the user has already marked attendance today
        if (attendanceRepository.findByUserIdAndDate(
                attendance.getUserId(), today).isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Attendance has already been marked for today.");
        }

        // Set the timestamp automatically
        attendance.setTimestamp(LocalDateTime.now());

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
    }

    // GET /api/attendance/today
    @GetMapping("/today")
    public List<Map<String, Object>> getTodayAttendance() {

        LocalDate today = LocalDate.now();

        List<Attendance> records = attendanceRepository.findByDate(today);
        List<User> users = userRepository.findAll();

        List<Map<String, Object>> response = new ArrayList<>();

        for (User user : users) {

            Attendance attendance = records.stream()
                    .filter(record -> record.getUserId().equals(user.getId()))
                    .findFirst()
                    .orElse(null);

            Map<String, Object> data = new HashMap<>();

            data.put("id", attendance != null ? attendance.getId() : -user.getId());
            data.put("userId", user.getId());
            data.put("date", today);
            data.put("status", attendance != null
                    ? attendance.getStatus()
                    : Attendance.Status.ABSENT);
            data.put("timestamp", attendance != null
                    ? attendance.getTimestamp()
                    : null);

            data.put("memberName", user.getFullName());
            data.put("memberEmail", user.getEmail());
            data.put("jobTitle", user.getJobTitle());

            response.add(data);
        }

        return response;
    }

    // GET /api/attendance/history?days=30
    // Raw per-day records for every user across the trailing window.
    // The frontend derives streaks, points and the leaderboard from this;
    // kept intentionally dumb here so game rules can change without a
    // backend redeploy.
    @GetMapping("/history")
    public List<Map<String, Object>> getHistory(
            @RequestParam(defaultValue = "30") int days) {

        int window = Math.max(1, Math.min(days, 180));
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(window - 1L);

        List<Attendance> records = attendanceRepository.findByDateBetween(start, end);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Attendance record : records) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", record.getUserId());
            data.put("date", record.getDate());
            data.put("status", record.getStatus());
            data.put("timestamp", record.getTimestamp());
            response.add(data);
        }
        return response;
    }
}