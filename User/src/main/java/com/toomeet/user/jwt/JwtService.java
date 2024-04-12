package com.toomeet.user.jwt;

import com.toomeet.user.auth.Account;

public interface JwtService {
    String generateToken(Account account);

    Long getTokenExpiredTime();

}
