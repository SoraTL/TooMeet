package com.toomeet.notification.friend;

import com.toomeet.notification.client.UserClient;
import com.toomeet.notification.client.dto.UserInfoDto;
import com.toomeet.notification.friend.event.FriendRequestEvent;
import com.toomeet.notification.friend.event.ReplyFriendRequestEvent;
import com.toomeet.notification.friend.pub.FriendNotificationPublic;
import com.toomeet.notification.notification.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendListener {
    private final NotificationService notificationService;
    private final RabbitTemplate rabbitTemplate;
    private final UserClient userClient;
    private final ModelMapper mapper;

    @Value("${spring.rabbitmq.exchange.socket_exchange}")
    private String socketExchange;

    @Value("${spring.rabbitmq.routing.socket_notify_friend_routing}")
    private String socketNotifyRoutingKey;

    @RabbitListener(queues = {"${spring.rabbitmq.queue.subscribe.notify_friend_request_queue}"})
    public void friendRequestListener(FriendRequestEvent event) {

        UserInfoDto sender = userClient.getUserInfo(event.getSenderId().toString());
        UserInfoDto receiver = userClient.getUserInfo(event.getReceiverId().toString());

//        rabbitTemplate.convertAndSend(socketFriendExchange, socketNotifyFriendRouting, event);
        Notification notification = notificationService.saveNotify(Notification.builder()
                .content(sender.getName() + " Đã gửi lời mời kết bạn. " + "\"" + event.getMessage() + "\"")
                .sender(Sender.builder()
                        .avatar(sender.getProfile().getAvatar().getUrl())
                        .name(sender.getName())
                        .id(sender.getId())
                        .build())
                .receiver(Receiver.builder()
                        .avatar(receiver.getProfile().getAvatar().getUrl())
                        .name(receiver.getName())
                        .id(receiver.getId())
                        .build())
                .type(NotificationType.FRIEND)
                .build());

        rabbitTemplate.convertAndSend(
                socketExchange,
                socketNotifyRoutingKey,
                mapper.map(notification, FriendNotificationPublic.class)
        );
    }

    @RabbitListener(queues = {"${spring.rabbitmq.queue.subscribe.notify_reply_friend_request_queue}"})
    public void replyFriendRequestListener(ReplyFriendRequestEvent event) {

        if (event.getType().equals(ReplyFriendRequestEvent.Type.REJECTED)) return;

        UserInfoDto sender = userClient.getUserInfo(event.getSenderId().toString());
        UserInfoDto receiver = userClient.getUserInfo(event.getReceiverId().toString());

        Notification notification = notificationService.saveNotify(Notification.builder()
                .content(sender.getName() + "Đã chấp nhận lời mời kết bạn. Bạn và " + sender.getName() + " đã là bạn bè.")
                .sender(Sender.builder()
                        .avatar(sender.getProfile().getAvatar().getUrl())
                        .name(sender.getName())
                        .id(sender.getId())
                        .build())
                .receiver(Receiver.builder()
                        .avatar(receiver.getProfile().getAvatar().getUrl())
                        .name(receiver.getName())
                        .id(receiver.getId())
                        .build())
                .type(NotificationType.FRIEND)
                .build());

        rabbitTemplate.convertAndSend(
                socketExchange,
                socketNotifyRoutingKey,
                mapper.map(notification, FriendNotificationPublic.class)
        );
    }


}
