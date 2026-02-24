package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
    List<AccountRequest> findByUserId(Long userId);
    List<AccountRequest> findByStatus(AccountRequest.RequestStatus status);
}