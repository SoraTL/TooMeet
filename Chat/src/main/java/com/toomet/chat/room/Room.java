package com.toomet.chat.room;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.toomet.chat.image.Image;
import com.toomet.chat.member.Member;
import com.toomet.chat.message.Message;
import com.toomet.chat.message.MessageImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Collection;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Room {

    @UpdateTimestamp
    protected Date updatedAt;

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;


    @Embedded
    @Builder.Default
    private RoomSetting setting = new RoomSetting();

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Image avatar;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<Member> members;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private Collection<Message> messages;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @JsonBackReference
    private Collection<MessageImage> images;
    
    @CreationTimestamp
    private Date createdAt;

    public enum RoomType {
        GROUP,
        SINGLE
    }

}
