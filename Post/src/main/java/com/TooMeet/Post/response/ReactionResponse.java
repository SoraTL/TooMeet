package com.TooMeet.Post.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReactionResponse {
    private String massage;
    private int reactionCount;
}
