package com.example.social.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="Post_tb")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    @Column(name = "post_user_id")
    private int postUserId;

    @Column(name = "post_content")
    private String content;

    @Column(name = "post_create_time")
    private LocalDateTime postCreateTime;

    @Column(name = "post_delete_flag")
    private int postDeleteFlag;

}