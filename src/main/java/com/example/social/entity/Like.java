package com.example.social.entity;

import com.example.social.common.LikeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "like_tb")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likeId;

    @Column(name = "like_post_id")
    private int likePostId;

    @Column(name = "like_user_id")
    private int likeUserId;

    @Column(name = "like_status")
    private LikeStatus likeStatus;

    @Column(name = "like_created_time")
    private LocalDateTime likeCreatedTime;
}
