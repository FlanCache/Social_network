package com.example.social.dto.response.postResponse;

import com.example.social.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimelineResponse {
    private  int totalRecords;
    private List<PostDto> listPosts;
}
