package com.example.social.dto.response.postResponse;

import com.example.social.dto.PostDto;
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
public class UpPostResponse {
    private int Id;
    private int userId;
    private String content;
    private List<Image> images;
    private List<Comment> comments;
    private int like;
    private LocalDateTime createTime;
    private int deleteFlag;
}
