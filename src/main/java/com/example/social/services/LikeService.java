package com.example.social.services;

import com.example.social.entity.Like;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    ResponseEntity<?> likePost(int postId);

    Like likeCheck(int postId, int userId);
}
