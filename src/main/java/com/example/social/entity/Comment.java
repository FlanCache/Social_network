package com.example.social.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Comment_tb")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;
    @Column(name = "comment_content")
    private String commentContent;
    @Column(name = "comment_post_id")
    private int commentPostId;
    @Column(name = "comment_user_id")
    private int commentUserId;
    @Column(name = "comment_delete_flag")
    private int commentDeleteFlag;
    @Column(name = "comment_created_time")
    private LocalDateTime commentCreatedTime;
}
