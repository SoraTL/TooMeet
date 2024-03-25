package com.toomeet.notification.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfoDto {
    private Long id;
    private String name;
    private Profile profile;

    @Data
    public static class Profile {
        private String description;
        private Image avatar;

        @Data
        public static class Image {
            private String url;
            private Format format;
            private Date createdAt;
            private Date updatedAt;


            private enum Format {
                PNG,
                JPEG,
                SVG,
                GIF,
                WEBP,
                JPG
            }
        }
    }
}
