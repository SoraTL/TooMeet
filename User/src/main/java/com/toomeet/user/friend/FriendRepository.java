package com.toomeet.user.friend;

import com.toomeet.user.user.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {


    @Query(value = "select f from Friend f where (f.user1.id = ?1 and f.user2.status = ?2) or (f.user2.id = ?1 and f.user1.status = ?2)")
    Page<Friend> getAllByStatus(Long userId, Status status, Pageable pageable);

    @Query(value = "select f from Friend f where f.user1.id = ?1 or f.user2.id = ?1")
    Page<Friend> getAllByUserId(Long userId, Pageable pageable);

    @Query(value = "select f from Friend f where " +
            "(f.user1.id = :userId and f.user2.name ilike %:keyword%) or " +
            "(f.user2.id = :userId and f.user1.name ilike %:keyword%)")
    Page<Friend> searchByName(Long userId, String keyword, Pageable pageable);

    @Query(value = "select case when count(f) > 0 then true else false end from Friend  f where (f.user1.id = ?1 and f.user2.id = ?2) or (f.user2.id = ?1 and f.user1.id = ?2)")
    boolean existsFriendByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Transactional
    @Modifying
    @Query(value = "delete from Friend  f where (f.user1.id = ?1 and f.user2.id = ?2) or (f.user1.id = ?2 and f.user2.id = ?1)")
    void deleteByUser1IdAndUser2Id(Long user1Id, Long user2Id);


}
