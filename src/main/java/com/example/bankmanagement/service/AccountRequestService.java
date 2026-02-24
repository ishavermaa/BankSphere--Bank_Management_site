package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.AccountRequest;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.model.AuditLog;
import com.example.bankmanagement.repository.AccountRequestRepository;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountRequestService {

    @Autowired
    private AccountRequestRepository accountRequestRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    public AccountRequest save(AccountRequest accountRequest) {
        return accountRequestRepository.save(accountRequest);
    }

    public Optional<AccountRequest> findById(Long id) {
        return accountRequestRepository.findById(id);
    }

    public List<AccountRequest> findByUserId(Long userId) {
        return accountRequestRepository.findByUserId(userId);
    }

    public List<AccountRequest> findByStatus(AccountRequest.RequestStatus status) {
        return accountRequestRepository.findByStatus(status);
    }

    public List<AccountRequest> findAll() {
        return accountRequestRepository.findAll();
    }

    public void deleteById(Long id) {
        accountRequestRepository.deleteById(id);
    }

    public List<AccountRequest> findPendingRequests() {
        return accountRequestRepository.findByStatus(AccountRequest.RequestStatus.PENDING);
    }

    public boolean approveRequest(Long requestId, User processedBy) {
        Optional<AccountRequest> requestOpt = findById(requestId);
        if (requestOpt.isPresent()) {
            AccountRequest request = requestOpt.get();
            if (request.getStatus() == AccountRequest.RequestStatus.PENDING) {
                request.setStatus(AccountRequest.RequestStatus.APPROVED);
                request.setProcessedBy(processedBy);
                save(request);

                // Create account
                String accountNumber = generateAccountNumber();
                Account account = new Account(accountNumber, request.getUser(), request.getAccountType(), "Main Branch");
                accountRepository.save(account);

                // Audit log
                AuditLog auditLog = new AuditLog("ACCOUNT_CREATED", "Account " + accountNumber, processedBy);
                auditLogRepository.save(auditLog);
                return true;
            }
        }
        return false;
    }

    public boolean rejectRequest(Long requestId, User processedBy) {
        Optional<AccountRequest> requestOpt = findById(requestId);
        if (requestOpt.isPresent()) {
            AccountRequest request = requestOpt.get();
            if (request.getStatus() == AccountRequest.RequestStatus.PENDING) {
                request.setStatus(AccountRequest.RequestStatus.REJECTED);
                request.setProcessedBy(processedBy);
                save(request);

                // Audit log
                AuditLog auditLog = new AuditLog("ACCOUNT_REQUEST_REJECTED", "Request ID " + requestId, processedBy);
                auditLogRepository.save(auditLog);
                return true;
            }
        }
        return false;
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}