package com.example.test.social.controller;

import com.example.social.controller.PostController;
import com.example.social.dto.request.postRequest.EditPostRequest;
import com.example.social.dto.request.postRequest.UpPostRequest;
import com.example.social.services.LikeService;
import com.example.social.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestPostController {
    @InjectMocks
    PostController postController;
    @Mock
    PostService postService;
    @Mock
    LikeService likeService;



    @Test
    void testUpPostSuccess() throws Exception {
        UpPostRequest upPostRequest = new UpPostRequest();

        MockMultipartFile[] mockFiles = new MockMultipartFile[2];
        mockFiles[0] = new MockMultipartFile("file", "file1.png", "text/plain", "Lubia".getBytes());
        mockFiles[1] = new MockMultipartFile("file", "file2.png", "text/plain", ".1234".getBytes());

        when(postService.upPost(upPostRequest, mockFiles)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        //ResponseEntity<?> result = postController.upPost(mockFiles, upPostRequest);

        //assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testEditPostSuccess() throws Exception {
        EditPostRequest upPostRequest = new EditPostRequest();

        MockMultipartFile[] mockFiles = new MockMultipartFile[2];
        mockFiles[0] = new MockMultipartFile("file", "file1.png", "text/plain", "Lubia".getBytes());
        mockFiles[1] = new MockMultipartFile("file", "file2.png", "text/plain", ".1234".getBytes());

        when(postService.editPost(upPostRequest, mockFiles)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> result = postController.editPost(mockFiles, upPostRequest,1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testDeletePostSuccess() {
        int postId = 1;

        when(postService.deletePost(postId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> result = postController.deletePost(postId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    void testLikePostSuccess() {
        int postId = 1;

        when(likeService.likePost(postId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> result = likeService.likePost(postId);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(likeService, times(1)).likePost(postId);
    }

    @Test
    void testGetPostSuccess() {
        int postId = 1;

        when(postService.showPostById(postId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> result = postController.getPost(postId);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(postService, times(1)).showPostById(postId);
    }

    @Test
    void testTimeLineSuccess() {
        when(postService.timeLineByPage(1,1)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> result = postController.timeLine(1,1);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(postService, times(1)).timeLineByPage(1,1);
    }

}
