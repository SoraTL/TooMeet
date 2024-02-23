package com.toomeet.user.user;

import com.toomeet.user.image.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private Date dateOfBirth;
    private String description;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image background;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image avatar;
    @Enumerated(EnumType.STRING)
    private Gender gender;

}
