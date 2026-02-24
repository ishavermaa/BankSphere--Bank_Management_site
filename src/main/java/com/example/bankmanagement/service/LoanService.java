package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.model.AuditLog;
import com.example.bankmanagement.repository.LoanRepository;
import com.example.bankmanagement.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    public Loan save(Loan loan) {
        return loanRepository.save(loan);
    }

    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> findByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> findByStatus(Loan.LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }

    public long getTotalLoans() {
        return loanRepository.count();
    }

    public long getPendingLoans() {
        return loanRepository.countByStatus(Loan.LoanStatus.PENDING);
    }

    public boolean approveLoan(Long loanId, User approvedBy) {
        Optional<Loan> loanOpt = findById(loanId);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            if (loan.getStatus() == Loan.LoanStatus.PENDING) {
                loan.setStatus(Loan.LoanStatus.APPROVED);
                loan.setApprovedBy(approvedBy);
                save(loan);

                // Audit log
                AuditLog auditLog = new AuditLog("LOAN_APPROVED", "Loan ID " + loanId, approvedBy);
                auditLogRepository.save(auditLog);
                return true;
            }
        }
        return false;
    }

    public boolean rejectLoan(Long loanId, User approvedBy) {
        Optional<Loan> loanOpt = findById(loanId);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            if (loan.getStatus() == Loan.LoanStatus.PENDING) {
                loan.setStatus(Loan.LoanStatus.REJECTED);
                loan.setApprovedBy(approvedBy);
                save(loan);

                // Audit log
                AuditLog auditLog = new AuditLog("LOAN_REJECTED", "Loan ID " + loanId, approvedBy);
                auditLogRepository.save(auditLog);
                return true;
            }
        }
        return false;
    }

    public List<Loan> findPendingLoans() {
        return loanRepository.findByStatus(Loan.LoanStatus.PENDING);
    }
}