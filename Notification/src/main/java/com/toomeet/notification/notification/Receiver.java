package com.toomeet.notification.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Receiver {
    @Column(name = "receiver_id")
    private Long id;

    @Column(name = "receiver_avatar")
    private String avatar;

    @Column(name = "receiver_name")
    private String name;
}
