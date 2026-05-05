package com.netflix_gpt.controller;

import com.netflix_gpt.dto.AuthRequest;
import com.netflix_gpt.dto.AuthResponse;
import com.netflix_gpt.entity.User;
import com.netflix_gpt.repository.UserRepository;
import com.netflix_gpt.security.JwtUtil;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest request) {
        if(userRepo.findByEmailId(request.getEmailId()).isPresent()){
            return ResponseEntity.badRequest().body("User Already Exist");
        }
        User user=new User();
        user.setEmail(request.getEmailId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {

        AuthRequest existing = userRepo.findByEmailId(authRequest.getEmailId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), existing.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token=jwtUtil.generateAccessToken(existing.getEmailId(),"15");

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/refresh")
    public String refresh(@RequestBody String refreshToken) {

        String email = jwtUtil.extractEmail(refreshToken);

        return jwtUtil.generateAccessToken(email,"15");
    }
}

