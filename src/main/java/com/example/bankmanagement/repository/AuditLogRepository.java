package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPerformedById(Long userId);
    List<AuditLog> findByEntityAffected(String entityAffected);
}