package com.toomeet.user.friend.pub;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomPublic {
    private Long user1Id;
    private Long user2Id;
}
