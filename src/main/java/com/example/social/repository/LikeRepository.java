package com.example.social.repository;

import com.example.social.common.LikeStatus;
import com.example.social.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    Like findByLikePostIdAndLikeUserId(int postId, int userId);

    Boolean existsLikeByLikePostIdAndLikeUserId(int postId, int userId);
    int countLikeByLikePostIdAndLikeStatus(int postId, LikeStatus likeStatus);
    List<Like> findByLikePostId(int postId);
    @Query("SELECT COUNT(p) FROM Like p WHERE (p.likeCreatedTime >= :oneWeekAgo) AND (p.likeUserId = :UserId)")
    int countLikesInLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo,@Param("UserId") int userId);
}
