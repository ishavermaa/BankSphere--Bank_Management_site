package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long customerId);
    List<Loan> findByStatus(Loan.LoanStatus status);
    long countByStatus(Loan.LoanStatus status);
}