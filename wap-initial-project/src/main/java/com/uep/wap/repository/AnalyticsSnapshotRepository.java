package com.uep.wap.repository;

import com.uep.wap.model.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, Long> {
    Optional<AnalyticsSnapshot> findBySnapshotDate(LocalDate snapshotDate);
}
