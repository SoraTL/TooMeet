package com.toomeet.user.user.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class UserSuggestionResponseDto {
    Page<UserOverviewDto> users;

}
