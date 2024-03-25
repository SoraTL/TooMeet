package com.toomet.chat.message;

import com.toomet.chat.client.UserClient;
import com.toomet.chat.client.dto.User;
import com.toomet.chat.client.dto.UserClientResponseDto;
import com.toomet.chat.exceptions.BadRequestException;
import com.toomet.chat.exceptions.ForbiddenException;
import com.toomet.chat.exceptions.NotFoundException;
import com.toomet.chat.image.Image;
import com.toomet.chat.image.ImageService;
import com.toomet.chat.image.ImageUploaderResponse;
import com.toomet.chat.member.Member;
import com.toomet.chat.member.MemberId;
import com.toomet.chat.member.MemberService;
import com.toomet.chat.message.dto.CreateMessageDto;
import com.toomet.chat.message.dto.MessageResponseDto;
import com.toomet.chat.message.pub.NewMessagePublic;
import com.toomet.chat.room.Room;
import com.toomet.chat.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final RoomService roomService;
    private final MemberService memberService;
    private final ModelMapper mapper;
    private final ImageService imageService;
    private final RabbitTemplate rabbitTemplate;
    private final UserClient userClient;

    @Value("${spring.rabbitmq.exchange.socket_exchange}")
    private String socketExchange;

    @Value("${spring.rabbitmq.routing.socket_new_chat_message_routing}")
    private String socketNewChatMessageRoutingKey;


    public MessageResponseDto createMessage(Long senderId, Long roomId, CreateMessageDto dto) {
        Message newMessage = newMessage(senderId, roomId, dto);
        MessageResponseDto messageResponse = MessageResponseDto.convertFromMessage(newMessage, senderId);
        NewMessagePublic newMessagePublic = mapper.map(messageResponse, NewMessagePublic.class);
        rabbitTemplate.convertAndSend(socketExchange, socketNewChatMessageRoutingKey, newMessagePublic);
        return messageResponse;
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy tin nhắn id:" + messageId)
        );
    }

    public Page<MessageResponseDto> getAllMessageInRoom(Long userId, Long roomId, int page, int limit) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Bạn không có quyền truy cập phòng này");
        }


        Member member = memberService.getMemberById(userId, roomId);


        Pageable request = PageRequest.of(page, limit).withSort(Sort.Direction.DESC, "timestamp");
        Page<Message> messages = messageRepository.getAllByRoomId(roomId, member.getDeletedMessageTime(), request);


        return messages.map(message -> MessageResponseDto.convertFromMessage(message, userId));
    }


    public MessageResponseDto replyMessage(Long senderId, Long reply, Long roomId, CreateMessageDto dto) {
        Message message = getMessageById(reply);
        if (!message.getRoom().getId().equals(roomId)) {
            throw new ForbiddenException("Tin nhắn không tồn tại");
        }
        Message replyMessage = newMessage(senderId, roomId, dto);
        replyMessage.setReply(message);
        Message newMessage = messageRepository.save(replyMessage);
        MessageResponseDto messageResponseDto = MessageResponseDto.convertFromMessage(newMessage, senderId);
        NewMessagePublic messagePublic = mapper.map(messageResponseDto, NewMessagePublic.class);
        rabbitTemplate.convertAndSend(socketExchange, socketNewChatMessageRoutingKey, messagePublic);
        return messageResponseDto;
    }

    public MessageResponseDto recallMessage(Long userId, Long roomId, Long messageId) {
        Message message = getMessageById(messageId);
        if (!message.getRoom().getId().equals(roomId)) {
            throw new ForbiddenException("Tin nhắn không tồn tại");
        }

        if (!message.getSender().getId().equals(userId)) {
            throw new ForbiddenException("Không thể thu hồi tin nhắn này");
        }

        message.setIsRecall(true);
        message.setImage(null);
        message.setIcon(null);
        message.setText("Tin nhắn đã được thu hồi");

        messageRepository.save(message);

        return MessageResponseDto.convertFromMessage(message, userId);
    }

    private Message newMessage(Long senderId, Long roomId, CreateMessageDto dto) {

        if (memberService.isNotRoomMember(senderId, roomId)) {
            throw new ForbiddenException("Bạn không có quyền gửi tin nhắn cho phòng này");
        }
        Room room = roomService.getRoomById(roomId);
        MemberId memberId = new MemberId(senderId, room);

        Message message = Message.builder()
                .sender(memberService.getMemberById(memberId))
                .room(room)
                .build();

        if (dto.getText() != null) {
            message.setText(dto.getText());
        }

        if (dto.getIcon() != null) {
            message.setIcon(dto.getIcon());
        }

        return messageRepository.save(message);
    }

    public MessageResponseDto createMessageImage(Long roomId, Long senderId, MultipartFile imageFile) {
        if (memberService.isNotRoomMember(senderId, roomId)) {
            throw new ForbiddenException("Bạn không có quyền gửi tin nhắn cho phòng này");
        }

        Room room = roomService.getRoomById(roomId);
        MemberId memberId = new MemberId(senderId, room);
        Member member = memberService.getMemberById(memberId);
        Message message = Message.builder()
                .sender(member)
                .room(room)
                .build();

        String cloudPath = "message/" + UUID.randomUUID();
        ImageUploaderResponse imageUploaderResponse = imageService.upsertImage(cloudPath, imageFile);

        Image image = Image.builder()
                .cloudPublicId(imageUploaderResponse.getPublicId())
                .format(imageUploaderResponse.getFormat())
                .url(imageUploaderResponse.getUrl())
                .cloudPath(cloudPath)
                .build();

        MessageImage messageImage = MessageImage.builder()
                .image(image)
                .member(member)
                .room(room)
                .message(message)
                .build();

        message.setImage(messageImage);

        messageRepository.save(message);
        MessageResponseDto messageResponseDto = MessageResponseDto.convertFromMessage(message, senderId);
        NewMessagePublic messagePublic = mapper.map(messageResponseDto, NewMessagePublic.class);
        rabbitTemplate.convertAndSend(socketExchange, socketNewChatMessageRoutingKey, messagePublic);
        return messageResponseDto;
    }

    public MessageResponseDto getLatestMessage(Long roomId, Long userId) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Bạn không có quyền gửi tin nhắn cho phòng này");
        }

        Optional<Message> messageOptional = messageRepository.getLatestMessageByRoomId(roomId);

        if (messageOptional.isEmpty()) return null;
        Message message = messageOptional.get();
        Long senderId = message.getSender().getId();
        try {
            UserClientResponseDto response = userClient.getUserInfo(senderId);
            User user = User.convertFromUserClientResponse(response);
            return MessageResponseDto.convertFromMessage(message, user);
        } catch (Exception e) {
            throw new BadRequestException("Không tìm thấy người dùng " + senderId);
        }
    }
}
