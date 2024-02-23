package com.toomeet.user.user.dto;

import com.toomeet.user.image.dto.ImageResponseDto;
import com.toomeet.user.user.Gender;
import lombok.Data;

import java.util.Date;

@Data
public class ProfileResponseDto {
    private String description;
    private ImageResponseDto background;
    private ImageResponseDto avatar;
    private Gender gender;
    private Date dateOfBirth;
}
