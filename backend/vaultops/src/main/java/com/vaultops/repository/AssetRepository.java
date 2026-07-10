package com.vaultops.repository;

import com.vaultops.dtos.CategoryConditionStatDTO;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    List<Asset> findByNameContaining(String name);

    @Query("SELECT a FROM Asset a WHERE a.name LIKE %:keyword% OR a.type LIKE %:keyword%")
    List<Asset> findByNameOrTypeContaining(@Param("keyword") String name);

    List<Asset> findAssetsByUsageStatus(Usage usageStatus);
    List<Asset> findTop4ByUsageStatusOrderByCreatedAtDesc(Usage usageStatus);
    List<Asset> findAssetsByConditionStatus(ConditionStatus condition);
    Long countAssetsByUsageStatus(Usage usage);
    Long countAssetsByConditionStatus(ConditionStatus condition);

    @Query("SELECT COALESCE(AVG(timestampdiff(DAY, a.createdAt, CURRENT_DATE)), 0.0) FROM Asset a WHERE a.usageStatus = :status")
    Double getAverageDaysInStatus(@Param("status") Usage status);

    Optional<Asset> findBySerialNumber(String serialNumber);
    List<Asset> findByType(String type);
    List<Asset> findByTypeAndConditionStatus(String type, ConditionStatus conditionStatus);

    @Query("SELECT new com.vaultops.dtos.CategoryConditionStatDTO(a.type, a.conditionStatus, COUNT(a)) " +
           "FROM Asset a GROUP BY a.type, a.conditionStatus")
    List<CategoryConditionStatDTO> getAssetCountsGroupedByCategoryAndCondition();

    @Query("SELECT YEAR(a.createdAt), MONTH(a.createdAt), COUNT(a) " +
           "FROM Asset a " +
           "WHERE a.createdAt >= :startDate " +
           "GROUP BY YEAR(a.createdAt), MONTH(a.createdAt)")
    List<Object[]> getAssetCreationTrends(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(a.purchasePrice), 0) FROM Asset a")
    BigDecimal getTotalAssetValuation();

    @Query("SELECT COALESCE(AVG(a.purchasePrice), 0) FROM Asset a")
    BigDecimal getAverageAssetValue();

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.usageStatus = com.vaultops.enums.Usage.SERVICE AND timestampdiff(DAY, a.createdAt, CURRENT_DATE) > :days")
    long countOverdueRepairs(@Param("days") long days);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.warrantyExpiryDate IS NOT NULL AND a.warrantyExpiryDate >= CURRENT_DATE AND a.warrantyExpiryDate <= :thirtyDaysFromNow")
    long countExpiringWarranties(@Param("thirtyDaysFromNow") java.time.LocalDate thirtyDaysFromNow);
}
