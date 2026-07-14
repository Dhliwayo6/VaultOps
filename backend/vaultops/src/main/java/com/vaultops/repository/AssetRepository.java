package com.vaultops.repository;

import com.vaultops.dtos.CategoryConditionStatDTO;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    long countByLocationId(Long locationId);
    Page<Asset> findByLocationId(Long locationId, Pageable pageable);
    List<Asset> findByLocationId(Long locationId);

    @Query("SELECT COUNT(a) FROM Asset a WHERE (:locationId IS NULL OR a.location.id = :locationId)")
    long count(@Param("locationId") Long locationId);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.usageStatus = :usage AND (:locationId IS NULL OR a.location.id = :locationId)")
    long countAssetsByUsageStatusAndLocationId(@Param("usage") Usage usage, @Param("locationId") Long locationId);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.conditionStatus = :condition AND (:locationId IS NULL OR a.location.id = :locationId)")
    long countAssetsByConditionStatusAndLocationId(@Param("condition") ConditionStatus condition, @Param("locationId") Long locationId);

    @Query("SELECT COALESCE(AVG(timestampdiff(DAY, a.createdAt, CURRENT_DATE)), 0.0) FROM Asset a WHERE a.usageStatus = :status AND (:locationId IS NULL OR a.location.id = :locationId)")
    Double getAverageDaysInStatusAndLocationId(@Param("status") Usage status, @Param("locationId") Long locationId);

    @Query("SELECT COALESCE(AVG(timestampdiff(DAY, a.createdAt, CURRENT_DATE)), 0.0) FROM Asset a WHERE a.usageStatus = :status")
    Double getAverageDaysInStatus(@Param("status") Usage status);

    Optional<Asset> findBySerialNumber(String serialNumber);
    List<Asset> findByType(String type);
    List<Asset> findByTypeAndConditionStatus(String type, ConditionStatus conditionStatus);

    @Query("SELECT new com.vaultops.dtos.CategoryConditionStatDTO(a.type, a.conditionStatus, COUNT(a)) " +
           "FROM Asset a WHERE (:locationId IS NULL OR a.location.id = :locationId) GROUP BY a.type, a.conditionStatus")
    List<CategoryConditionStatDTO> getAssetCountsGroupedByCategoryAndCondition(@Param("locationId") Long locationId);

    @Query("SELECT YEAR(a.createdAt), MONTH(a.createdAt), COUNT(a) " +
           "FROM Asset a " +
           "WHERE a.createdAt >= :startDate AND (:locationId IS NULL OR a.location.id = :locationId) " +
           "GROUP BY YEAR(a.createdAt), MONTH(a.createdAt)")
    List<Object[]> getAssetCreationTrends(@Param("startDate") LocalDateTime startDate, @Param("locationId") Long locationId);

    @Query("SELECT COALESCE(SUM(a.purchasePrice), 0) FROM Asset a WHERE (:locationId IS NULL OR a.location.id = :locationId)")
    BigDecimal getTotalAssetValuation(@Param("locationId") Long locationId);

    @Query("SELECT COALESCE(AVG(a.purchasePrice), 0) FROM Asset a WHERE (:locationId IS NULL OR a.location.id = :locationId)")
    BigDecimal getAverageAssetValue(@Param("locationId") Long locationId);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.usageStatus = com.vaultops.enums.Usage.SERVICE AND timestampdiff(DAY, a.createdAt, CURRENT_DATE) > :days AND (:locationId IS NULL OR a.location.id = :locationId)")
    long countOverdueRepairs(@Param("days") long days, @Param("locationId") Long locationId);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.warrantyExpiryDate IS NOT NULL AND a.warrantyExpiryDate >= CURRENT_DATE AND a.warrantyExpiryDate <= :thirtyDaysFromNow AND (:locationId IS NULL OR a.location.id = :locationId)")
    long countExpiringWarranties(@Param("thirtyDaysFromNow") java.time.LocalDate thirtyDaysFromNow, @Param("locationId") Long locationId);
}
