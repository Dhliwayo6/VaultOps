package com.vaultops.repository;

import com.vaultops.model.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    Optional<UserOtp> findByUserId(Long userId);
    Optional<UserOtp> findByUserEmail(String email);
    void deleteByUserId(Long userId);
}
