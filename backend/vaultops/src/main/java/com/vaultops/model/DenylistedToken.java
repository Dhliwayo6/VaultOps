package com.vaultops.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "denylisted_tokens")
@Data
@NoArgsConstructor
public class DenylistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    public DenylistedToken(String tokenHash, LocalDateTime expiryTime) {
        this.tokenHash = tokenHash;
        this.expiryTime = expiryTime;
    }
}
