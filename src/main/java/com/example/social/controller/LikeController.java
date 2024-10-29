package com.example.social.controller;

import com.example.social.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/like")
@Validated
public class LikeController {
    @Autowired
    LikeService likeService;
    @PostMapping(value ="/{postId}")
    public ResponseEntity<?> likePost(@PathVariable int postId) {
        return likeService.likePost(postId);
    }
}
