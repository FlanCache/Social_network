package com.example.social.dto.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(type = "object" ,example = "{\n" +
        "  \"content\": \"string\"\n" +
        "}")
public class CommentRequest {
    private int postId;
    private String content;
}
