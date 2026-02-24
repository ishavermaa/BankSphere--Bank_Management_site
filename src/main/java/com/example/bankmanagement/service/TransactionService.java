package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.model.AuditLog;
import com.example.bankmanagement.repository.TransactionRepository;
import com.example.bankmanagement.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> findByFromAccountId(Long accountId) {
        return transactionRepository.findByFromAccountId(accountId);
    }

    public List<Transaction> findByToAccountId(Long accountId) {
        return transactionRepository.findByToAccountId(accountId);
    }

    public List<Transaction> findByAccountId(Long accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    public long getTotalTransactions() {
        return transactionRepository.count();
    }

    @Transactional
    public boolean deposit(Long accountId, Double amount, User performedBy) {
        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getStatus() == Account.AccountStatus.ACTIVE) {
                account.setBalance(account.getBalance() + amount);
                accountService.save(account);
                Transaction transaction = new Transaction(null, account, amount, Transaction.TransactionType.DEPOSIT);
                save(transaction);

                // Audit log
                AuditLog auditLog = new AuditLog("DEPOSIT", "Account " + account.getAccountNumber(), performedBy);
                auditLogRepository.save(auditLog);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean withdraw(Long accountId, Double amount, User performedBy) {
        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getStatus() == Account.AccountStatus.ACTIVE && account.getBalance() >= amount) {
                account.setBalance(account.getBalance() - amount);
                accountService.save(account);
                Transaction transaction = new Transaction(account, null, amount, Transaction.TransactionType.WITHDRAW);
                save(transaction);

                // Audit log
                AuditLog auditLog = new AuditLog("WITHDRAW", "Account " + account.getAccountNumber(), performedBy);
                auditLogRepository.save(auditLog);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean transfer(Long fromAccountId, Long toAccountId, Double amount, User performedBy) {
        Optional<Account> fromAccountOpt = accountService.findById(fromAccountId);
        Optional<Account> toAccountOpt = accountService.findById(toAccountId);
        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();
            if (fromAccount.getStatus() == Account.AccountStatus.ACTIVE &&
                toAccount.getStatus() == Account.AccountStatus.ACTIVE &&
                fromAccount.getBalance() >= amount) {

                fromAccount.setBalance(fromAccount.getBalance() - amount);
                toAccount.setBalance(toAccount.getBalance() + amount);
                accountService.save(fromAccount);
                accountService.save(toAccount);
                Transaction transaction = new Transaction(fromAccount, toAccount, amount, Transaction.TransactionType.TRANSFER);
                save(transaction);

                // Audit log
                AuditLog auditLog = new AuditLog("TRANSFER", "From " + fromAccount.getAccountNumber() + " to " + toAccount.getAccountNumber(), performedBy);
                auditLogRepository.save(auditLog);
                return true;
            }
        }
        return false;
    }
}