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
}