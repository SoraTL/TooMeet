package com.toomeet.user.friend.dto;

import com.toomeet.user.image.Image;
import com.toomeet.user.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponseDto {
    private Long id;
    private String name;
    private Profile profile;

    @Data
    private static class Profile {
        private Image avatar;
        private Gender gender;
    }


}
