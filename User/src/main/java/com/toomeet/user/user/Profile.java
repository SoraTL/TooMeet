package com.toomeet.user.user;

import com.toomeet.user.image.Image;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private Date dateOfBirth;
    private String description;
    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    private Image background;
    @OneToOne(cascade = CascadeType.ALL)
    private Image avatar;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
}
