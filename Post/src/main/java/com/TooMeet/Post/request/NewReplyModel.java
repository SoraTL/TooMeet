package com.TooMeet.Post.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class NewReplyModel {
    private UUID parentId;
    private String content;
}
