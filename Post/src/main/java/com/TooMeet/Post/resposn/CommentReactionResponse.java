package com.TooMeet.Post.resposn;


import com.TooMeet.Post.request.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentReactionResponse {

    List<Long> users;
    Integer emoji;

}
