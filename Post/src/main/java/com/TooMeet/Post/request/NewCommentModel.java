package com.TooMeet.Post.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class NewCommentModel {
    private UUID parentId;
    private String content;
    private int level;
}

