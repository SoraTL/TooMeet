package com.TooMeet.Post.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class Image {
    private String url;
    private Format format;
    private Date createdAt;
    private Date updatedAt;
}
