package com.toomet.chat.room.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class AddMemberDto {
    @NotNull
    private Set<Long> members;
}
