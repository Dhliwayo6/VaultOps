package com.vaultops.repository;

import com.vaultops.model.Migration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationRepository extends JpaRepository<Migration, Long> {
}
