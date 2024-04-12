package com.toomeet.user.friend.pub;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyFriendRequestPublic {
    private Long senderId;
    private Long receiverId;

    @NotNull
    private Type type;

    public enum Type {
        REJECTED,
        ACCEPTED
    }
}
