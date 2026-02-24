package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.LoanService;
import com.example.bankmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private UserService userService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Loan> applyForLoan(@RequestBody Loan loan, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        loan.setCustomer(user);
        return ResponseEntity.ok(loanService.save(loan));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Loan>> getMyLoans(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(loanService.findByCustomerId(user.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<List<Loan>> getAllLoans(@RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(loanService.findByStatus(Loan.LoanStatus.valueOf(status)));
        }
        return ResponseEntity.ok(loanService.findAll());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<?> approveLoan(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        boolean success = loanService.approveLoan(id, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Loan approved"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Loan approval failed"));
        }
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<?> rejectLoan(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        boolean success = loanService.rejectLoan(id, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Loan rejected"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Loan rejection failed"));
        }
    }

    // Admin endpoints
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Loan>> getPendingLoans() {
        return ResponseEntity.ok(loanService.findPendingLoans());
    }

    @PutMapping("/admin/loans/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminRejectLoan(@PathVariable Long id, @RequestBody Map<String, String> request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        String reason = request.get("reason");

        boolean success = loanService.rejectLoan(id, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Loan rejected"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Loan rejection failed"));
        }
    }
}