package com.toomet.chat.room;

import com.toomet.chat.room.dto.NewRoomRequestDto;
import com.toomet.chat.room.event.CreateRoomEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoomListener {
    private final RoomService roomService;

    @RabbitListener(queues = {"${spring.rabbitmq.queue.subscribe.chat_create_room}"})
    public void createRoomListener(CreateRoomEvent event) throws IOException {
        Set<Long> members = Set.of(event.getUser1Id(), event.getUser2Id());
        NewRoomRequestDto dto = NewRoomRequestDto.builder()
                .member(members)
                .type(Room.RoomType.SINGLE)
                .name(event.getUser1Id() + "@" + event.getUser2Id())
                .build();
        roomService.createNewRoom(dto);
    }

}
