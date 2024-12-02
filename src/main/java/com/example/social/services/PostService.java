package com.example.social.services;

import com.example.social.dto.request.postRequest.EditPostRequest;
import com.example.social.dto.request.postRequest.UpPostRequest;
import com.example.social.dto.response.postResponse.UpPostResponse;
import com.example.social.entity.Image;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface PostService {
    ResponseEntity<?> upPost(UpPostRequest upPostRequest, MultipartFile[] multipartFiles);

    UpPostResponse postRequestToResponse(UpPostRequest upPostRequest, List<Image> imageList, int updateFlag);

    ResponseEntity<?> editPost(EditPostRequest editPostRequest, MultipartFile[] multipartFiles);

    ResponseEntity<?> deletePost(int postId);

    ResponseEntity<?> showPostById(int postId);



    ResponseEntity<?> getPostByUserId(int page, int size, int userId);

    ResponseEntity<?> timeLineByPage(int page, int size);
}
