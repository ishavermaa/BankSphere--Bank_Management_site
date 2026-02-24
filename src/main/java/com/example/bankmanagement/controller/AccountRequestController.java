package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.AccountRequest;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.AccountRequestService;
import com.example.bankmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account-requests")
@CrossOrigin
public class AccountRequestController {

    @Autowired
    private AccountRequestService accountRequestService;

    @Autowired
    private UserService userService;

    @PostMapping
    // @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AccountRequest> createAccountRequest(@RequestBody Map<String, String> request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Account.AccountType accountType = Account.AccountType.valueOf(request.get("accountType"));
        AccountRequest accountRequest = new AccountRequest(user, accountType);
        return ResponseEntity.ok(accountRequestService.save(accountRequest));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AccountRequest>> getMyRequests(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(accountRequestService.findByUserId(user.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<List<AccountRequest>> getAllRequests(@RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(accountRequestService.findByStatus(AccountRequest.RequestStatus.valueOf(status)));
        }
        return ResponseEntity.ok(accountRequestService.findAll());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        boolean success = accountRequestService.approveRequest(id, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Account request approved"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Account request approval failed"));
        }
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        boolean success = accountRequestService.rejectRequest(id, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Account request rejected"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Account request rejection failed"));
        }
    }

    // Admin endpoints
    @GetMapping("/admin/account-requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountRequest>> getPendingRequests() {
        return ResponseEntity.ok(accountRequestService.findPendingRequests());
    }

    @PutMapping("/admin/account-requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminApproveRequest(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        boolean success = accountRequestService.approveRequest(id, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Account request approved"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Account request approval failed"));
        }
    }

    @PutMapping("/admin/account-requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminRejectRequest(@PathVariable Long id, @RequestBody Map<String, String> request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        String reason = request.get("reason");

        boolean success = accountRequestService.rejectRequest(id, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Account request rejected"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Account request rejection failed"));
        }
    }
}