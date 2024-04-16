package com.TooMeet.Post.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AdminRequest {
    @JsonProperty("isAdmin")
    private Integer isAdmin;

    public void setIsAdmin(Integer isAdmin){
        this.isAdmin=isAdmin;
    }
}
