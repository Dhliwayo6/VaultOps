package com.vaultops.repository;

import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    List<Asset> findByNameContaining(String name);

    @Query("SELECT a FROM Asset a WHERE a.name LIKE %:keyword% OR a.type LIKE %:keyword%")
    List<Asset> findByNameOrTypeContaining(@Param("keyword") String name);

    List<Asset> findAssetsByUsageStatus(Usage usageStatus);
    List<Asset> findAssetsByConditionStatus(ConditionStatus condition);
    Long countAssetsByUsageStatus(Usage usage);
    Long countAssetsByConditionStatus(ConditionStatus condition);

    Optional<Asset> findBySerialNumber(String serialNumber);
    List<Asset> findByType(String type);
    List<Asset> findByTypeAndConditionStatus(String type, ConditionStatus conditionStatus);

}
