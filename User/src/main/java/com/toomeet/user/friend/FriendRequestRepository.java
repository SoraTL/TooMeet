package com.toomeet.user.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<FriendRequest> getAllBySenderId(Long senderId);

    List<FriendRequest> getAllByReceiverId(Long receiverId);
}
