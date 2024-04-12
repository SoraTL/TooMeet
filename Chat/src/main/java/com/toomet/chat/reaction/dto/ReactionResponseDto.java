package com.toomet.chat.reaction.dto;

import com.toomet.chat.reaction.Reaction;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ReactionResponseDto {
    private Long id;
    private Integer count;
    private Date timestamp;
    private Long memberId;
    private Reaction.ReactionType type;

    public static ReactionResponseDto convertFromReaction(Reaction reaction) {
        return ReactionResponseDto
                .builder()
                .count(reaction.getCount())
                .memberId(reaction.getMember().getId())
                .timestamp(reaction.getUpdatedAt())
                .type(reaction.getType())
                .build();
    }
}
