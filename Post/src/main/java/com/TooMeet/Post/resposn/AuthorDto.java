package com.TooMeet.Post.resposn;


import com.TooMeet.Post.request.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AuthorDto {
    private String name;
    private long id;
    private String avatar;

}
