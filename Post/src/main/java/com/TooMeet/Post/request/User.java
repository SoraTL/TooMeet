package com.TooMeet.Post.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private profile profile;

    @Setter
    @Getter
    @NoArgsConstructor
    private static class profile{
        private Image avatar;
        private String description;
        private Format format;
    }

    public String getAvatar(){
        return this.getProfile().getAvatar().getUrl();
    }

}
