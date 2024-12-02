package com.example.social.controller;

import com.example.social.entity.User;
import com.example.social.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/like")
@CrossOrigin(origins = "*")
@Validated
public class LikeController {
    @Autowired
    LikeService likeService;
    @PostMapping(value ="/{postId}")
    public ResponseEntity<?> likePost(@PathVariable int postId) {
        return likeService.likePost(postId);
    }

    @GetMapping("/liked/{postId}/{userId}")
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable int postId, @PathVariable int userId) {
        boolean liked = likeService.hasUserLikedPost(postId, userId);
        return new ResponseEntity<>(liked, HttpStatus.OK);
    }

}
