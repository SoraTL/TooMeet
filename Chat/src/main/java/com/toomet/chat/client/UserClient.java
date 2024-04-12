package com.toomet.chat.client;

import com.toomet.chat.client.dto.UserClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user", url = "http://localhost:8082/users")
public interface UserClient {
    @GetMapping("/info/{id}")
    UserClientResponseDto getUserInfo(@PathVariable("id") Long id);
}
