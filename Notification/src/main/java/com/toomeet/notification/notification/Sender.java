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
public class Sender {
    @Column(name = "sender_id")
    private Long id;
    @Column(name = "sender_avatar")
    private String avatar;
    @Column(name = "sender_name")
    private String name;

}
