package com.example.social.controller;

import com.example.social.dto.request.postRequest.EditPostRequest;
import com.example.social.dto.request.postRequest.UpPostRequest;
import com.example.social.services.LikeService;
import com.example.social.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/post")
@Validated
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    LikeService likeService;

    @PostMapping(value = "/upPost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upPost(@RequestPart(value = "file", required = false) @Valid MultipartFile[] multipartFiles,
                                     UpPostRequest upPostRequest,
                                     HttpServletRequest request
                                    )  {
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Content-Type: " + request.getContentType());

        // Log file và nội dung
        System.out.println("Files received: " + (multipartFiles != null ? multipartFiles.length : 0));
        System.out.println("Post Content: " + upPostRequest.getPostContent());
        return postService.upPost(upPostRequest, multipartFiles);
    }

    @PutMapping(value = "/editPost/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editPost(@RequestPart(value = "file", required = false) @Valid MultipartFile[] multipartFiles, EditPostRequest editPostRequest, @PathVariable int postId)  {
        editPostRequest.setPostId(postId);
        return postService.editPost(editPostRequest, multipartFiles);
    }
    @DeleteMapping(value ="/deletePost/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable int postId) {
        return postService.deletePost(postId);
    }


    @GetMapping(value = "/{postId}")
    public ResponseEntity<?> getPost(@PathVariable int postId) {
        return postService.showPostById(postId);
    }

    @GetMapping(value = "/timeLine")
    public ResponseEntity<?> timeLine(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size ) {
        return postService.timeLineByPage(page, size);
    }

    @GetMapping(value = "/selfTimeLine/{userId}")
    public ResponseEntity<?> selfTimeLine(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size,@PathVariable int userId ) {
        return postService.getPostByUserId(page, size,userId);
    }
}
