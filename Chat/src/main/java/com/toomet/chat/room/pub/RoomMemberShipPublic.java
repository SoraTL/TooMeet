package com.toomet.chat.room.pub;

import com.toomet.chat.client.dto.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RoomMemberShipPublic {
    private Long roomId;
    private User member;
    private MemberShipType type;
    private Date timestamp;

    public enum MemberShipType {
        JOIN,
        LEAVE
    }
}
