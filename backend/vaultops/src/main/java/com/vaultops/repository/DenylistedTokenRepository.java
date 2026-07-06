package com.vaultops.repository;

import com.vaultops.model.DenylistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DenylistedTokenRepository extends JpaRepository<DenylistedToken, Long> {
    Optional<DenylistedToken> findByTokenHash(String tokenHash);
    boolean existsByTokenHash(String tokenHash);
}
