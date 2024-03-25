package com.toomeet.notification.client;

import com.toomeet.notification.client.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user", url = "http://localhost:8082/users")
public interface UserClient {
    @GetMapping("/info/{id}")
    UserInfoDto getUserInfo(@PathVariable("id") String id);
}
