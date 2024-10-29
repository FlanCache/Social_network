package com.example.social.dto;

import com.example.social.entity.Comment;
import com.example.social.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private int postId;
    private int postUserId;
    private String content;
    private List<Image> postImages;
    private List<Comment> postComments;
    private int like;
    private LocalDateTime postCreateTime;
    private int postDeleteFlag;

}
