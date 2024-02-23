package com.toomeet.user.auth;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUserId(Long userId);

    @Query(value = "SELECT a.email, a.password, a.roles, a.createdAt, a.updatedAt FROM Account a")
    Optional<Object> customFindById(@NonNull String id);
}
