package com.example.social.controller;

import com.example.social.dto.request.comment.CommentRequest;
import com.example.social.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/v1/comment")

public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping("/Comments/{postId}")
    public ResponseEntity<?> createComment(@PathVariable int postId,@RequestBody CommentRequest commentRequest) {
        commentRequest.setPostId(postId);
        return commentService.postComment(commentRequest);
    }

    @DeleteMapping("/Comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int commentId) {
        return commentService.deleteComment(commentId);
    }

}
