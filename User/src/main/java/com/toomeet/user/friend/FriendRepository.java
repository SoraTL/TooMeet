package com.toomeet.user.friend;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> getAllByUser1IdOrUser2Id(Long user1Id, Long user2Id);

    Optional<Friend> getByUser1IdAndUser2IdOrUser2IdAndUser1Id(Long user1_id, Long user2_id, Long user2_id2, Long user1_id2);

    @Query(value = "select case when count(f) > 0 then true else false end from Friend  f where (f.user1.id = ?1 and f.user2.id = ?2) or (f.user2.id = ?1 and f.user1.id = ?2)")
    boolean existsFriendByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Transactional
    @Modifying
    @Query(value = "delete from Friend  f where (f.user1.id = ?1 and f.user2.id = ?2) or (f.user1.id = ?2 and f.user2.id = ?1)")
    void deleteByUser1IdAndUser2Id(Long user1Id, Long user2Id);

}
