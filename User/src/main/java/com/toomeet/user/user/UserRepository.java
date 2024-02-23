package com.toomeet.user.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value =
            "select u from User u where " +
                    "u.id != ?1 and u.id not in " +
                    "(select " +
                    "case when f.user1.id = ?1 then f.user2.id else f.user1.id end " +
                    "from Friend f where " +
                    "f.user1.id = ?1 or f.user2.id = ?1) "
    )
    Page<User> getSuggestions(Long userId, Pageable pageable);

}
