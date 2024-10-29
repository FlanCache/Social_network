package com.example.social.repository;

import com.example.social.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    Optional<Comment> findCommentByCommentPostId(int commentPostID);

    List<Comment> findCommentsByCommentPostIdAndCommentDeleteFlag(int commentPostID,int deleteFlag);

    @Query("SELECT COUNT(p) FROM Comment p WHERE (p.commentCreatedTime >= :oneWeekAgo) AND (p.commentUserId = :UserId)")
    int countCommentsInLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo,@Param("UserId") int userId);
}
