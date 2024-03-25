package com.toomeet.user.auth;

import com.toomeet.user.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account getAccountById(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("account not found"));
    }

}
