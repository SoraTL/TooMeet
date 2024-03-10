package com.TooMeet.Post.request;

import lombok.Getter;

import java.util.Date;


@Getter
public class Image {
    private String url;
    private Format format;
    private Date createdAt;
    private Date updatedAt;
}
