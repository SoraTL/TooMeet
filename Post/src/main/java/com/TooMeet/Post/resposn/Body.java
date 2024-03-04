package com.TooMeet.Post.resposn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@RequiredArgsConstructor
public class Body {
    private UUID postId;
    private String userName;
    private Long userId;
    private String content;
    private String imageUrl;
    private int privacy;
    private int emoji;
    private Timestamp createAt;
    private Timestamp updateAt;
}
