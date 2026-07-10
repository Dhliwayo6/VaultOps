package com.vaultops.repository;

import com.vaultops.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    @Query("SELECT YEAR(m.date), MONTH(m.date), COUNT(m) " +
           "FROM Maintenance m " +
           "WHERE m.date >= :startDate " +
           "GROUP BY YEAR(m.date), MONTH(m.date)")
    List<Object[]> getMaintenanceTrends(@Param("startDate") LocalDate startDate);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM Maintenance m")
    BigDecimal getTotalMaintenanceExpenditure();

}
