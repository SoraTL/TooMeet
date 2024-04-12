package com.toomet.chat.image;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.toomet.chat.message.MessageImage;
import com.toomet.chat.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Image {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String cloudPath;

    @Column(nullable = false)
    @JsonIgnore
    private String cloudPublicId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Format format;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_avatar")
    @JsonIgnore
    private Room room;

    @OneToOne(fetch = FetchType.LAZY)
    private MessageImage messageImage;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;


    public enum Format {
        PNG,
        JPEG,
        SVG,
        GIF,
        WEBP,
        JPG
    }
}
