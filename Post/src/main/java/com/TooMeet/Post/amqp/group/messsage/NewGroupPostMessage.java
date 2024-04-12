package com.TooMeet.Post.amqp.group.messsage;

import com.TooMeet.Post.request.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewGroupPostMessage {

    private UUID groupId;
    private Long userId;
    private String content;
    private List<String> images;
    private Choice status;


}
