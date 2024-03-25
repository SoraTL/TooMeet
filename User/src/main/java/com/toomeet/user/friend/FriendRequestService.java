package com.toomeet.user.friend;

import com.toomeet.user.exceptions.BadRequestException;
import com.toomeet.user.exceptions.ConflictException;
import com.toomeet.user.exceptions.ForbiddenException;
import com.toomeet.user.exceptions.NotFoundException;
import com.toomeet.user.friend.dto.*;
import com.toomeet.user.friend.pub.CreateChatRoomPublic;
import com.toomeet.user.friend.pub.FriendRequestPublic;
import com.toomeet.user.friend.pub.ReplyFriendRequestPublic;
import com.toomeet.user.user.User;
import com.toomeet.user.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendService friendService;
    private final FriendRequestRepository requestRepository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.notify_exchange}")
    private String notifyExchange;

    @Value("${spring.rabbitmq.exchange.chat_exchange}")
    private String chatExchange;

    @Value("${spring.rabbitmq.routing.notify_friend_request}")
    private String notifyFriendRequestRoutingKey;


    @Value("${spring.rabbitmq.routing.notify_reply_friend_request}")
    private String notifyReplyFriendRequestRoutingKey;

    @Value("${spring.rabbitmq.routing.chat_create_room}")
    private String createChatRoomRoutingKey;


    public AddFriendResponseDto addFriend(User sender, AddFriendRequestDto dto) {

        Long receiverId = dto.getReceiverId();

        if (sender.getId().equals(dto.getReceiverId())) {
            throw new BadRequestException("Bạn không thế kết bạn với chính mình");
        }

        User receiver = userService.getUserById(receiverId);

        if (friendService.isExistingFriend(sender.getId(), receiverId)) {
            throw new ConflictException("Người dùng đã là bạn bè");
        }

        if (isExistingRequest(sender.getId(), receiverId)) {
            throw new ConflictException("Yêu cầu kết bạn đã được gửi");
        }


        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .message(dto.getMessage())
                .build();

        FriendRequest newFriendRequest = requestRepository.save(friendRequest);

        // send to notification service
        FriendRequestPublic friendRequestPublic = FriendRequestPublic
                .builder()
                .message(newFriendRequest.getMessage())
                .receiverId(newFriendRequest.getReceiver().getId())
                .senderId(newFriendRequest.getSender().getId())
                .timestamp(newFriendRequest.getTimeStamp())
                .build();

        rabbitTemplate.convertAndSend(
                notifyExchange,
                notifyFriendRequestRoutingKey,
                friendRequestPublic
        );

        return AddFriendResponseDto.builder()
                .requestId(newFriendRequest.getId())
                .message("Yêu cầu kết bạn đã được gửi")
                .build();

    }

    public String acceptFriend(User sender, ReplyAddFriendDto dto) {
        FriendRequest request = isValidReplyRequest(sender, dto);
        friendService.saveFriend(request.getSender(), request.getReceiver());
        requestRepository.deleteById(dto.getRequestId());

        // send to notification service
        ReplyFriendRequestPublic replyFriendRequestPublic = ReplyFriendRequestPublic
                .builder()
                .receiverId(request.getSender().getId())
                .senderId(request.getReceiver().getId())
                .type(ReplyFriendRequestPublic.Type.ACCEPTED)
                .build();
        rabbitTemplate.convertAndSend(
                notifyExchange,
                notifyReplyFriendRequestRoutingKey,
                replyFriendRequestPublic
        );

        // send to chat service
        CreateChatRoomPublic createChatRoomPublic = CreateChatRoomPublic.builder()
                .user1Id(request.getSender().getId())
                .user2Id(request.getReceiver().getId())
                .build();

        rabbitTemplate.convertAndSend(
                chatExchange,
                createChatRoomRoutingKey,
                createChatRoomPublic
        );

        return "Bạn và " + request.getSender().getName() + " đã là bạn bè";
    }

    public String rejectFriend(User sender, ReplyAddFriendDto dto) {
        FriendRequest request = isValidReplyRequest(sender, dto);
        requestRepository.deleteById(dto.getRequestId());


        // send to notification service
        ReplyFriendRequestPublic replyFriendRequestPublic = ReplyFriendRequestPublic
                .builder()
                .receiverId(request.getSender().getId())
                .senderId(request.getReceiver().getId())
                .type(ReplyFriendRequestPublic.Type.REJECTED)
                .build();

        rabbitTemplate.convertAndSend(
                notifyExchange,
                notifyReplyFriendRequestRoutingKey,
                replyFriendRequestPublic
        );

        return "Từ chối thành công";
    }

    public List<FriendRequestSentDto> getSentFriendRequests(User user) {
        List<FriendRequest> requests = requestRepository.getAllBySenderId(user.getId());
        return requests.stream().map(request -> mapper.map(request, FriendRequestSentDto.class)).toList();
    }

    public List<FriendRequestReceivedDto> getReceivedFriendRequests(User user) {
        List<FriendRequest> requests = requestRepository.getAllByReceiverId(user.getId());
        return requests.stream().map(request -> mapper.map(request, FriendRequestReceivedDto.class)).toList();
    }

    public String cancelFriendRequest(Long requestId, User user) {


        FriendRequest request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu kết bạn này")
                );

        if (!request.getSender().getId().equals(user.getId())) {
            throw new ForbiddenException("Bạn không có quyền xóa yêu cầu này");
        }

        requestRepository.deleteById(request.getId());
        return "Hủy lời mời thành công";
    }


    private boolean isExistingRequest(Long senderId, Long receiverId) {
        return requestRepository.existsBySenderIdAndReceiverId(senderId, receiverId);
    }

    private FriendRequest isValidReplyRequest(User sender, ReplyAddFriendDto dto) {
        Long requestId = dto.getRequestId();

        FriendRequest friendRequest = requestRepository
                .findById(requestId).orElseThrow(
                        () -> new NotFoundException("Không tìm thấy yêu cầu kết bạn này")
                );

        if (!friendRequest.getReceiver().getId().equals(sender.getId())) {
            throw new ForbiddenException("Bạn không có quyền phản hồi yêu cầu này");
        }
        return friendRequest;
    }


//    private List<FriendRequestSentDto> convertToResponseDto(List<FriendRequest> requests) {
//        return requests
//                .stream()
//                .map(request -> mapper.map(request, FriendRequestSentDto.class))
//                .toList();
//    }
}
