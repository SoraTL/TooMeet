package com.toomet.chat.reaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {

    boolean existsByMessageIdAndMemberId(Long messageId, Long memberId);

    Optional<Reaction> findByMessageIdAndMemberId(Long messageId, Long memberId);
}
