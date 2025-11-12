package com.vaultops.repository;

import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByNameContaining(String name);

    @Query("SELECT a FROM Asset a WHERE a.name LIKE %:keyword% OR a.type LIKE %:keyword%")
    List<Asset> findByNameOrTypeContaining(@Param("keyword") String name);

    List<Asset> findAssetsByUsageStatus(Usage usageStatus);
    List<Asset> findAssetsByConditionStatus(ConditionStatus condition);
    Integer countAssetsByUsageStatus(Usage usage);
    Integer countAssetsByConditionStatus(ConditionStatus condition);
}
