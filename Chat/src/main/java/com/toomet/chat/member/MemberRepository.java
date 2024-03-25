package com.toomet.chat.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, MemberId> {

    @Query(value = "select m.id from Member m where m.room.id = :roomId and m.state != 'LEAVED'")
    List<Long> getAllByRoomId(Long roomId);

    Optional<Member> findByIdAndRoomId(Long memberId, Long roomId);
}
