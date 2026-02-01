package com.vaultops.repository;

import com.vaultops.enums.ImportStatus;
import com.vaultops.model.ImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportLogRepository extends JpaRepository<ImportLog, Long> {

    List<ImportLog> findByUserIdOrderByStartedAtDesc(String userId);
    List<ImportLog> findByStatusOrderByStartedAtDesc(ImportStatus status);
    List<ImportLog> findByUserIdAndStatus(String userId, ImportStatus status);
}