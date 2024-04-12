package com.toomet.chat.reaction;

import com.toomet.chat.exceptions.ForbiddenException;
import com.toomet.chat.exceptions.NotFoundException;
import com.toomet.chat.member.Member;
import com.toomet.chat.member.MemberService;
import com.toomet.chat.message.Message;
import com.toomet.chat.message.MessageService;
import com.toomet.chat.reaction.dto.ReactionMessageDto;
import com.toomet.chat.reaction.dto.ReactionResponseDto;
import com.toomet.chat.reaction.pub.MessageReactionPublic;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final MessageService messageService;
    private final MemberService memberService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.socket_exchange}")
    private String socketExchange;

    @Value("${spring.rabbitmq.routing.socket_chat_message_reaction}")
    private String socketChatMessageReactionRoutingKey;

    public ReactionResponseDto reactionMessage(Long roomId, Long messageId, Long userId, ReactionMessageDto dto) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Phòng không tồn tại hoặc bạn không có quyền truy cập phòng này");
        }
        Message message = messageService.getMessageById(messageId);
        Member member = memberService.getMemberById(userId, roomId);


        Optional<Reaction> reactionOptional = reactionRepository.findByMessageIdAndMemberId(messageId, userId);
        Reaction reaction;
        if (reactionOptional.isPresent()) {
            reaction = reactionOptional.get();
            reaction.setType(dto.getType());
        } else {
            reaction = Reaction.builder()
                    .member(member)
                    .message(message)
                    .type(dto.getType())
                    .count(1)
                    .build();
        }
        reaction = reactionRepository.save(reaction);

        // send to socket
        MessageReactionPublic messageReactionPublic = MessageReactionPublic.builder()
                .roomId(roomId)
                .memberId(userId)
                .messageId(messageId)
                .reactionType(reaction.getType())
                .type(MessageReactionPublic.Type.CREATE)
                .timestamp(new Date())
                .build();

        rabbitTemplate.convertAndSend(socketExchange, socketChatMessageReactionRoutingKey, messageReactionPublic);
        return ReactionResponseDto.convertFromReaction(reaction);
    }

    public ReactionResponseDto removeReaction(Long roomId, Long messageId, Long userId) {
        Optional<Reaction> reactionOptional = reactionRepository.findByMessageIdAndMemberId(messageId, userId);
        if (reactionOptional.isEmpty()) {
            throw new NotFoundException("Bạn chưa tương tác tin nhắn này");
        }

        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Phòng không tồn tại hoặc bạn không có quyền truy cập phòng này");
        }

        Reaction reaction = reactionOptional.get();
        reactionRepository.delete(reaction);

        // send to socket
        MessageReactionPublic messageReactionPublic = MessageReactionPublic.builder()
                .roomId(roomId)
                .memberId(userId)
                .messageId(messageId)
                .reactionType(reaction.getType())
                .type(MessageReactionPublic.Type.REMOVE)
                .timestamp(new Date())
                .build();

        rabbitTemplate.convertAndSend(socketExchange, socketChatMessageReactionRoutingKey, messageReactionPublic);

        return ReactionResponseDto.convertFromReaction(reaction);
    }


}
