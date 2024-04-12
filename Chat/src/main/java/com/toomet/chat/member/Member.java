package com.toomet.chat.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toomet.chat.message.Message;
import com.toomet.chat.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(MemberId.class)
public class Member {

    @Id
    private Long id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JsonIgnore
    private Room room;

    @CreationTimestamp
    private Date participationTime;

    @Builder.Default
    private Date deletedMessageTime = new Date(0, Calendar.JANUARY, 0);

    @OneToMany(fetch = FetchType.LAZY)
    private Collection<Message> messages;

    @Builder.Default
    private boolean notification = true;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RoomState state = RoomState.MEMBER;

    public enum RoomState {
        MEMBER,
        LEAVED
    }

}
