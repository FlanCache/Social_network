package com.example.social.dto.request.postRequest;

import com.example.social.entity.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(type = "object" ,example = "{\n" +
        "  \"postContent\": \"string\",\n" +
        "  \"postDeleteFlag\": \"0\"\n" +
        "}")
public class EditPostRequest {
    private int postId;
    private String postContent;
    private int postDeleteFlag;
}
