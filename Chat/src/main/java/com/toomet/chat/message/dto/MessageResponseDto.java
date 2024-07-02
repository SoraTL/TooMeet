package com.toomet.chat.message.dto;

import com.toomet.chat.client.dto.User;
import com.toomet.chat.message.Message;
import com.toomet.chat.message.MessageImage;
import com.toomet.chat.reaction.Reaction;
import com.toomet.chat.reaction.dto.ReactionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponseDto {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private Message.Icon icon;
    private String text;
    private MessageReply reply;
    private String image;
    @Builder.Default
    private List<ReactionResponseDto> reactions = new ArrayList<>();
    private Reaction.ReactionType reaction;
    private boolean isRecall;
    @Builder.Default
    private List<Long> viewedMembers = new ArrayList<>();
    private Message.Status status;
    private Date timestamp;

    public static MessageResponseDto convertFromMessage(Message message, Long userId) {


        MessageResponseDto messageResponse = MessageResponseDto.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .roomId(message.getRoom().getId())
                .icon(message.getIcon())
                .text(message.getText())
                .senderName("unknown")
//                .viewedMembers((message.getViewedMembers().stream().map(Member::getId).toList()))
                .status(message.getStatus())
                .isRecall(message.getIsRecall())
                .timestamp(message.getTimestamp())
                .build();


        MessageImage messageImage = message.getImage();
        if (messageImage != null) {
            messageResponse.setImage(message.getImage().getImage().getUrl());
        }

        List<Reaction> reactions = (List<Reaction>) message.getReactions();
        if (reactions != null) {
            List<ReactionResponseDto> memberReaction = reactions.stream().map(reaction -> {
                if (reaction.getMember().getId().equals(userId)) messageResponse.setReaction(reaction.getType());
                return ReactionResponseDto.convertFromReaction(reaction);
            }).toList();
            if (memberReaction.size() > 5) memberReaction = memberReaction.subList(0, 5);

            messageResponse.setReactions(memberReaction);
        }
        Message reply = message.getReply();
        if (reply != null) {

            MessageReply messageReply = MessageReply.builder()
                    .id(reply.getId())
                    .text(reply.getText())
                    .icon(reply.getIcon())
                    .senderId(reply.getSender().getId())
                    .timestamp(reply.getTimestamp())
                    .build();

            if (reply.getImage() != null) {
                messageReply.setImage(reply.getImage().getImage().getUrl());
            }
            messageResponse.setReply(messageReply);

        }
        return messageResponse;
    }

    public static MessageResponseDto convertFromMessage(Message message, User sender) {
        MessageResponseDto messageResponseDto = convertFromMessage(message, sender.getId());
        messageResponseDto.setSenderName(sender.getName());
        return messageResponseDto;
    }

    @Data
    @Builder
    public static class MessageReply {
        private Long id;
        private Long senderId;
        private Message.Icon icon;
        private String text;
        private String image;
        private Date timestamp;
    }


}
