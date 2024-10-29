package com.example.social.services;

import com.example.social.dto.request.comment.CommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<?> postComment(CommentRequest commentRequest);
    ResponseEntity<?> deleteComment(int commentId);
}
