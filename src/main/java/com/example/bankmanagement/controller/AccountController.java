package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Account>> getMyAccounts(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(accountService.findByCustomerId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Account account = accountService.findById(id).orElse(null);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns the account or is admin/employee
        if (!account.getCustomer().getId().equals(user.getId()) &&
            !user.getRole().equals(User.Role.ADMIN) &&
            !user.getRole().equals(User.Role.EMPLOYEE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(account);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.save(account));
    }

    @PutMapping("/{id}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> freezeAccount(@PathVariable Long id) {
        Account account = accountService.findById(id).orElse(null);
        if (account != null) {
            account.setStatus(Account.AccountStatus.FROZEN);
            return ResponseEntity.ok(accountService.save(account));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> unfreezeAccount(@PathVariable Long id) {
        Account account = accountService.findById(id).orElse(null);
        if (account != null) {
            account.setStatus(Account.AccountStatus.ACTIVE);
            return ResponseEntity.ok(accountService.save(account));
        }
        return ResponseEntity.notFound().build();
    }

    // Admin endpoints
    @PutMapping("/admin/accounts/{id}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> adminFreezeAccount(@PathVariable Long id) {
        Account account = accountService.findById(id).orElse(null);
        if (account != null) {
            account.setStatus(Account.AccountStatus.FROZEN);
            return ResponseEntity.ok(accountService.save(account));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/accounts/{id}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> adminUnfreezeAccount(@PathVariable Long id) {
        Account account = accountService.findById(id).orElse(null);
        if (account != null) {
            account.setStatus(Account.AccountStatus.ACTIVE);
            return ResponseEntity.ok(accountService.save(account));
        }
        return ResponseEntity.notFound().build();
    }
}