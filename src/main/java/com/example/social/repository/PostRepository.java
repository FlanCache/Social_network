package com.example.social.repository;

import com.example.social.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findPostsByPostId(int postId);
    List<Post> findPostsByPostUserIdAndPostDeleteFlag(int postUserId,int postDeleteFlag);

    Optional<Post> findPostsByPostIdAndPostDeleteFlag(int postId, int postDeleteFlag);

    List<Post> findPostByPostUserIdAndPostDeleteFlagAndPostCreateTimeAfterOrderByPostCreateTimeDesc(int postUser, int deleteFlg,LocalDateTime timeCreated);

    List<Post> findByPostUserIdInAndPostDeleteFlagOrderByPostCreateTimeDesc(List<Integer> listFriendId,int postDeleteFlag,Pageable pageable);

    int countPostsByPostUserIdAndPostDeleteFlag(int postUserId,int postDelete);

    @Query("SELECT COUNT(p) FROM Post p WHERE (p.postCreateTime >= :oneWeekAgo) AND (p.postUserId = :postUserId) ORDER BY p.postCreateTime DESC")
    int countPostsInLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo,@Param("postUserId") int postUserId);


}
