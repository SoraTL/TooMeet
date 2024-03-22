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
    @RequiredArgsConstructor
    private static class profile{
        private Image avatar = new Image();
        private String description;
        private Format format;
    }

    public String getAvatar(){
        return this.getProfile().getAvatar().getUrl();
    }

}
