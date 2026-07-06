package com.vaultops.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_refresh_tokens")
@Data
@NoArgsConstructor
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private boolean revoked = false;

    public UserRefreshToken(User user, String tokenHash, LocalDateTime expiryTime) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiryTime = expiryTime;
    }
}
