package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.TransactionService;
import com.example.bankmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable Long accountId, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        // Check if user owns the account or is admin/employee
        if (accountService.findById(accountId).isPresent()) {
            var account = accountService.findById(accountId).get();
            if (!account.getCustomer().getId().equals(user.getId()) &&
                !user.getRole().equals(User.Role.ADMIN) &&
                !user.getRole().equals(User.Role.EMPLOYEE)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(transactionService.findByAccountId(accountId));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Long accountId = Long.valueOf(request.get("accountId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());

        boolean success = transactionService.deposit(accountId, amount, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Deposit successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Deposit failed"));
        }
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Long accountId = Long.valueOf(request.get("accountId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());

        boolean success = transactionService.withdraw(accountId, amount, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Withdrawal successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Withdrawal failed"));
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
        Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());

        boolean success = transactionService.transfer(fromAccountId, toAccountId, amount, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Transfer successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Transfer failed"));
        }
    }
}