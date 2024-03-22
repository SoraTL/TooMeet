package com.TooMeet.Post.resposn;


import com.TooMeet.Post.request.Image;
import com.TooMeet.Post.request.User;
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

    public AuthorDto convertToAuthor(User user){
        AuthorDto authorDto= new AuthorDto();

        authorDto.setId(user.getId());
        authorDto.setName(user.getName());
        authorDto.setAvatar(user.getAvatar());

        return authorDto;
    }


}
