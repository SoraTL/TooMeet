package com.toomet.chat.room;

import com.toomet.chat.message.MessageImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "select r from Room r join Member m on r.id = m.room.id where m.id = ?1 and m.state != 'LEAVED'")
    Page<Room> getAllByMemberId(Long memberId, Pageable pageable);

    @Query(value = "SELECT r FROM Room r JOIN Member m ON r.id = m.room.id WHERE m.id = ?1 AND m.state != 'LEAVED' AND r.name ILIKE %?2%")
    List<Room> searchByMemberIdAndName(Long memberId, String name);


    @Query(value = "select r.images from Room r where r.id = :roomId")
    Page<MessageImage> getAllRoomImage(Long roomId, Pageable pageable);
    
}
