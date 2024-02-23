package com.toomeet.user.friend.dto;

import com.toomeet.user.validator.EnumValidator;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReplyAddFriendDto {

    @NotNull
    private Long requestId;

    @NotNull
    @EnumValidator(message = "Invalid reply type", enumClass = Type.class)
    private Type type;

    public enum Type {
        ACCEPTED,
        REJECTED
    }
}
