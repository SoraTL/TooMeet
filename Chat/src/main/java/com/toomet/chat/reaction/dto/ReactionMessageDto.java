package com.toomet.chat.reaction.dto;

import com.toomet.chat.reaction.Reaction;
import com.toomet.chat.validator.EnumValidator;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionMessageDto {
    @NotNull
    @EnumValidator(enumClass = Reaction.ReactionType.class, message = "Kiểu tương tác không hợp lệ")
    private Reaction.ReactionType type;
}
