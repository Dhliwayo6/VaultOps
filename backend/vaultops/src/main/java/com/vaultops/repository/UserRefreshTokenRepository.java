package com.vaultops.repository;

import com.vaultops.model.User;
import com.vaultops.model.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    Optional<UserRefreshToken> findByTokenHash(String tokenHash);
    List<UserRefreshToken> findByUser(User user);
    void deleteByUser(User user);
}
