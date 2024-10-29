package com.example.test.social.controller;

import com.example.social.controller.CommentController;
import com.example.social.dto.request.comment.CommentRequest;
import com.example.social.repository.CommentRepository;
import com.example.social.services.CommentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)

public class TestCommentController {
    @InjectMocks
    CommentController commentController;
    @Mock
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;


    @Test
    public void testCreateCommentSuccess() {
        CommentRequest commentRequest = new CommentRequest();
        when(commentService.postComment(commentRequest)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        ResponseEntity<?> responseEntity = commentController.createComment(1,commentRequest);
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
    }



    @Test
    void testDeleteComment() {
        int commentIdToDelete = 1;

        when(commentService.deleteComment(commentIdToDelete)).thenReturn(new ResponseEntity<>( HttpStatus.OK));
        ResponseEntity<?> result = commentController.deleteComment(commentIdToDelete);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
