package com.toomeet.user.filter;

import com.toomeet.user.auth.Account;
import com.toomeet.user.auth.AccountService;
import com.toomeet.user.user.User;
import com.toomeet.user.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final AccountService accountService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String userIdHeader = request.getHeader("x-user-id");
        String accountIdHeader = request.getHeader("x-account-id");
        String userEmailHeader = request.getHeader("x-user-email");

        if (userIdHeader == null || accountIdHeader == null || userEmailHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Long userId = Long.parseLong(userIdHeader);

                User user = userService.getUserById(userId);

                Account account = accountService.getAccountById(accountIdHeader);
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, account.getAuthorities());

                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);

            }

        } catch (NumberFormatException e) {
            filterChain.doFilter(request, response);
            return;
        }


        filterChain.doFilter(request, response);
    }
}
