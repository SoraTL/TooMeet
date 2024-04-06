package com.TooMeet.Post.request;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private profile profile = new profile();

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class profile{
        private Image avatar = new Image();
        private String description;
        private Format format;
    }

    public String getAvatar(){
        if(profile==null) return null;
        return profile.getAvatar().getUrl();
    }

}
