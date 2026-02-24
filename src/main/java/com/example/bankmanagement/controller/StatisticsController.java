package com.example.bankmanagement.controller;

import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.LoanService;
import com.example.bankmanagement.service.TransactionService;
import com.example.bankmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatisticsController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LoanService loanService;

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUsers());
        stats.put("totalAccounts", accountService.getTotalAccounts());
        stats.put("totalTransactions", transactionService.getTotalTransactions());
        stats.put("totalLoans", loanService.getTotalLoans());
        stats.put("pendingLoans", loanService.getPendingLoans());
        stats.put("pendingAccountRequests", accountService.getPendingAccountRequests());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/employee/stats")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getEmployeeStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAccounts", accountService.getTotalAccounts());
        stats.put("totalTransactions", transactionService.getTotalTransactions());
        stats.put("pendingLoans", loanService.getPendingLoans());
        stats.put("pendingAccountRequests", accountService.getPendingAccountRequests());
        return ResponseEntity.ok(stats);
    }
}