package com.vaultops.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@SpringBootTest
public class MailIntegrationTest {

    @Autowired
    private MailService mailService;

    // Use MockBean to satisfy JavaMailSender in test environment without actual SMTP binding during default tests
    @MockBean
    private JavaMailSender mockMailSender;

    @Test
    public void contextLoads() {
        assertThat(mailService).isNotNull();
    }

    @Test
    public void testSendPlainTextEmailCallsSender() {
        mailService.sendPlainTextEmail("test@example.com", "Subject", "Body");
        verify(mockMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
    }
}
