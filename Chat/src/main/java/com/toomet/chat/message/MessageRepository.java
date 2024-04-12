package com.toomet.chat.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "select m from Message m where m.room.id = :roomId and m.timestamp > :timestamp")
    Page<Message> getAllByRoomId(Long roomId, Date timestamp, Pageable pageable);

    @Query(value = "select m from Message m where m.room.id = :roomId order by m.timestamp desc limit 1")
    Optional<Message> getLatestMessageByRoomId(Long roomId);
}
