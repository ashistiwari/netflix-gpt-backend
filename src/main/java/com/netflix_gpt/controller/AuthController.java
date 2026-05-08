package com.netflix_gpt.controller;

import com.netflix_gpt.dto.AuthResponse;
import com.netflix_gpt.dto.LoginRequest;
import com.netflix_gpt.dto.SignUpRequest;
import com.netflix_gpt.entity.User;
import com.netflix_gpt.repository.UserRepository;
import com.netflix_gpt.security.CustomUserDetails;
import com.netflix_gpt.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private AuthenticationManager authenticationManager;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String token = jwtUtil.generateAccessToken(request.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @PostMapping("/refresh")
    public String refresh(@RequestBody String refreshToken) {

        String email = jwtUtil.extractEmail(refreshToken);

        return jwtUtil.generateAccessToken(email);
    }
    @GetMapping("/auth/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){
        UserDetails user=(UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }
}

