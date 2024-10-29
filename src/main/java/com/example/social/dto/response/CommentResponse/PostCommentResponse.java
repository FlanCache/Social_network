package com.example.social.dto.response.CommentResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResponse {
    private String content;
    private LocalDateTime commentCreatedTime;
    private int commentDeleteFlag;

}
