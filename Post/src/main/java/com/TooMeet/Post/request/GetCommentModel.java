package com.TooMeet.Post.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Getter
@Setter
public class GetCommentModel {
    private UUID parentId;
    private int page;
    private int limit;
}
