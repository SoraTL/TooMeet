package com.toomet.chat.reaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.toomet.chat.member.Member;
import com.toomet.chat.message.Message;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(ReactionId.class)
public class Reaction {

    @Id
    @ManyToOne
    @JsonBackReference
    private Message message;

    @Id
    @ManyToOne
    private Member member;

    @Builder.Default
    private Integer count = 0;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReactionType type = ReactionType.LIKE;


    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    public enum ReactionType {
        LIKE,
        HAHA,
        LOVE,
        WOW,
        SAD,
        ANGRY
    }
}
