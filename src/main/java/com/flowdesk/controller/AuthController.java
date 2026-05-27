package com.flowdesk.controller;
import com.flowdesk.dto.*;
import com.flowdesk.entity.User;
import com.flowdesk.repository.UserRepository;
import com.flowdesk.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    @PostMapping("/login") public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userRepo.findByEmail(req.getEmail())
            .filter(u->encoder.matches(req.getPassword(),u.getPassword()))
            .filter(User::isActive)
            .map(u->ResponseEntity.ok(AuthResponse.builder().token(jwt.generateToken(u.getEmail(),u.getRole(),u.getId())).role(u.getRole()).fullName(u.getFullName()).userId(u.getId()).email(u.getEmail()).jobTitle(u.getJobTitle()).build()))
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
    @PostMapping("/register") public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) return ResponseEntity.badRequest().body("Email taken");
        User u = userRepo.save(User.builder().email(req.getEmail()).password(encoder.encode(req.getPassword())).fullName(req.getFullName()).role(req.getRole()!=null?req.getRole().toUpperCase():"MEMBER").jobTitle(req.getJobTitle()).build());
        return ResponseEntity.ok(AuthResponse.builder().token(jwt.generateToken(u.getEmail(),u.getRole(),u.getId())).role(u.getRole()).fullName(u.getFullName()).userId(u.getId()).email(u.getEmail()).jobTitle(u.getJobTitle()).build());
    }
}
