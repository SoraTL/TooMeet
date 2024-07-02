package com.TooMeet.Post.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ShareModel {

    private UUID postId;
    private String content;
    private Integer privacy;

}
