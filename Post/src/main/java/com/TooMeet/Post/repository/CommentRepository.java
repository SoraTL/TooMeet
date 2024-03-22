package com.TooMeet.Post.repository;

import com.TooMeet.Post.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Comment findByParentId(UUID parentId);
    @Override
    Page<Comment> findAll(Pageable pageable);
    Page<Comment> findByParentId(UUID parentId, Pageable pageable);

    Page<Comment> findByParentIdAndLevel(UUID parentId, int level, Pageable pageable);

    int countByParentId(UUID parentId);

    List<Long> findUserIdById(UUID id);

    @Query("SELECT c FROM Comment c WHERE c.parentId = :commentId AND c.id IN (SELECT cr.parentId FROM Comment cr GROUP BY cr.parentId HAVING COUNT(cr.parentId) > 1)")
    List<Comment> findRepliesWithMultipleRepliesByCommentId(@Param("commentId") UUID commentId);
}
