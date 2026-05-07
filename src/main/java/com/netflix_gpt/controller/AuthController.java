package com.netflix_gpt.controller;

import com.netflix_gpt.dto.AuthResponse;
import com.netflix_gpt.dto.LoginRequest;
import com.netflix_gpt.dto.SignUpRequest;
import com.netflix_gpt.entity.User;
import com.netflix_gpt.repository.UserRepository;
import com.netflix_gpt.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("User Already Exist");
        }
        User user=new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest authRequest) {

        User existing = userRepo.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), existing.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token=jwtUtil.generateAccessToken(existing.getEmail());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/refresh")
    public String refresh(@RequestBody String refreshToken) {

        String email = jwtUtil.extractEmail(refreshToken);

        return jwtUtil.generateAccessToken(email);
    }
}

