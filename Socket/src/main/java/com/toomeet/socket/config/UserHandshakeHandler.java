package com.toomeet.socket.config;

import com.sun.security.auth.UserPrincipal;
import com.toomeet.socket.exceptions.ForbiddenException;
import lombok.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(
            @NonNull ServerHttpRequest request,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) {

        String userId = request.getHeaders().getFirst("x-user-id");
        if (userId == null) throw new ForbiddenException("Kết nối thất bại:::>>>x<<<:::Không có quyền truy cập");
        return new UserPrincipal(userId);
    }
}
