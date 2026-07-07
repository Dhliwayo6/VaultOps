package com.vaultops.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("manual-mail")
public class RealMailSenderManualVerification {

    @Autowired
    private MailService mailService;

    @Test
    public void testSendRealEmail() {
        System.out.println("Executing manual real email delivery verification...");
        mailService.sendPlainTextEmail(
            "b0f811001@smtp-brevo.com",
            "VaultOps Live SMTP Verification",
            "Hello! This is a real test email sent to verify the Brevo SMTP configuration works correctly."
        );
        System.out.println("Email sent successfully!");
    }
}

