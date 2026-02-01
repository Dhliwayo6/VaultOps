package com.vaultops.repository;

import com.vaultops.model.ImportError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportErrorRepository extends JpaRepository<ImportError, Long> {

    List<ImportError> findByImportLogId(Long importLogId);
    Long countByImportLogId(Long importLogId);
}
