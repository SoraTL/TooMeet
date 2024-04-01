package com.toomet.chat.message;

import com.toomet.chat.member.Member;
import com.toomet.chat.reaction.Reaction;
import com.toomet.chat.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(indexes = {@Index(columnList = "room_id"), @Index(columnList = "timestamp")})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Member sender;

    @ManyToOne
    private Room room;

    @Enumerated(EnumType.STRING)
    private Icon icon;

    @Column(length = 50000)
    private String text;

    @ManyToOne
    private Message reply;

    @Builder.Default
    private Boolean isRecall = false;

    @OneToMany(mappedBy = "reply", fetch = FetchType.LAZY)
    private Collection<Message> replies;

    @OneToOne(cascade = CascadeType.ALL)
    private MessageImage image;

    @OneToMany
    @Builder.Default
    private Collection<Member> viewedMembers = new ArrayList<>();

    @OneToMany(mappedBy = "message")
    private Collection<Reaction> reactions;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.SENT;

    @CreationTimestamp
    private Date timestamp;

    public enum Icon {
        LIKE,
        HAHA,
        LOVE
    }

    public enum Status {
        SENT,
        RECEIVED,
        VIEWED
    }


}
