package com.TooMeet.Post.response;


import com.TooMeet.Post.entity.CommentReaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentReactionResponse {

    private AuthorDto user = new AuthorDto();
    private Integer emoji;

}
