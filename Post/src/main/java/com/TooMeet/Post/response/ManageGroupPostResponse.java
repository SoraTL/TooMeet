package com.TooMeet.Post.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageGroupPostResponse {

    private UUID postId;
    private AuthorDto author;
    private List<String> images;
    private String content;
    private String status;
    private Date createdAt;

}
