package com.toomeet.socket.chat.event;

import lombok.Data;

import java.util.Date;

@Data
public class MemberShipEvent {
    private Long roomId;
    private MemberShipType type;
    private Date timestamp;
    private User member;

    public enum MemberShipType {
        JOIN,
        LEAVE
    }

    @Data
    private static class User {
        private Long id;
        private String name;
        private String avatar;

    }
}
