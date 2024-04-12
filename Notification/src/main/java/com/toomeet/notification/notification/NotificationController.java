package com.toomeet.notification.notification;

import com.toomeet.notification.notification.dto.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> getAllNotifications(
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(name = "p", required = false, defaultValue = "0") int page,
            @RequestParam(name = "l", required = false, defaultValue = "10") int limit
    ) {
        Page<NotificationResponseDto> notificationResponses = notificationService.getAllNotify(userId, page, limit);
        return new ResponseEntity<>(notificationResponses, HttpStatus.OK);
    }


}
