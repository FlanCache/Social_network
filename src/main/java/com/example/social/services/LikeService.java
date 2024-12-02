package com.example.social.services;

import com.example.social.entity.Like;
import com.example.social.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {

    ResponseEntity<?> likePost(int postId);

    Like likeCheck(int postId, int userId);

    List<User> getUsersWhoLikedPost(int postId);

    boolean hasUserLikedPost(int postId, int userId);
}
