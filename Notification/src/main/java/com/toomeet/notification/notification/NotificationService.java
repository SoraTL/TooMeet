package com.toomeet.notification.notification;

import com.toomeet.notification.notification.dto.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper mapper;

    public Page<NotificationResponseDto> getAllNotify(Long userId, int page, int limit) {
        Page<Notification> notifications = notificationRepository
                .getAllByReceiverId(
                        userId,
                        PageRequest.of(page, limit, Sort.by(Sort.Order.desc("timestamp"))
                        ));

        return notifications.map(notification -> mapper.map(notification, NotificationResponseDto.class));
    }

    public Notification saveNotify(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void deleteNotify() {

    }
}
