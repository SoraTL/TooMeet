package com.toomeet.user.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query(value =
            "select u from User u " +
                    "where u.id != ?1 " +
                    "and not exists ( " +
                    "select 1 from Friend f " +
                    "where (f.user1.id = ?1 and f.user2.id = u.id) or (f.user2.id = ?1 and f.user1.id = u.id)" +
                    ") and not exists ( " +
                    "select 1 from FriendRequest  r " +
                    "where (r.sender.id = ?1 and r.receiver.id = u.id) or (r.sender.id = u.id and r.receiver.id = ?1)" +
                    ")"
    )
    Page<User> getSuggestions(Long userId, Pageable pageable);

}