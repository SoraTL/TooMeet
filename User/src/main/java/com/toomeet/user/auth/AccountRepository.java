package com.toomeet.user.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUserId(Long userId);

}
