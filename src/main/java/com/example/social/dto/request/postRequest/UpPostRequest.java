package com.example.social.dto.request.postRequest;

import com.example.social.entity.Comment;
import com.example.social.entity.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(type = "object" ,example = "{\n" +
        "  \"postContent\": \"string\"\n" +
        "}")
public class UpPostRequest {
    private int postId;
    private String postContent;
    private int postIdUser;
    private LocalDateTime postCreateDate;

}
