package com.netflix_gpt.repository;

import com.netflix_gpt.dto.AuthRequest;
import com.netflix_gpt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AuthRequest,Long> {

    Optional<AuthRequest> findByEmailId(String emailId);

}
