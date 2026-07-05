package com.vaultops.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    public void sendOtp(String email, String otpCode) {
        log.info("--------------------------------------------------");
        log.info("TRANSACTIONAL EMAIL SIMULATION:");
        log.info("Sending OTP [{}] to email [{}]", otpCode, email);
        log.info("--------------------------------------------------");
    }
}
