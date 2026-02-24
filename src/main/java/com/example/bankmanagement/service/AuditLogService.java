package com.example.bankmanagement.service;

import com.example.bankmanagement.model.AuditLog;
import com.example.bankmanagement.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> findByPerformedById(Long userId) {
        return auditLogRepository.findByPerformedById(userId);
    }

    public List<AuditLog> findByEntityAffected(String entityAffected) {
        return auditLogRepository.findByEntityAffected(entityAffected);
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }
}