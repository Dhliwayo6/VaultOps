package com.vaultops.assets.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Deployment Security Headers and Correlation ID Tests")
public class SecurityHeadersTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Verify secure HTTP headers and correlation ID are set on responses")
    public void testSecurityHeadersAndCorrelationId() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                // Expect 400 or any status since it's a GET, but headers should still be present
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Content-Security-Policy", "default-src 'self'; frame-ancestors 'none';"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().exists("X-Correlation-ID"))
                .andExpect(header().string("X-Correlation-ID", matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")));
    }

    @Test
    @DisplayName("Verify passed X-Correlation-ID is preserved in response header")
    public void testPreservesCorrelationId() throws Exception {
        String testCorrelationId = UUID.randomUUID().toString();
        mockMvc.perform(get("/api/auth/login")
                        .header("X-Correlation-ID", testCorrelationId))
                .andExpect(header().string("X-Correlation-ID", testCorrelationId));
    }
}
