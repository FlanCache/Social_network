package com.example.social.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Image_tb")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private int imageId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_delete_flag")
    private int imageDeleteFlag;

    @Column(name = "image_post_id")
    private int postId;

    @Column(name = "image_user_id")
    private int userId;
}
