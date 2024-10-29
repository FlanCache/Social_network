package com.example.test.social.service;

import com.example.social.common.CommonMessage;
import com.example.social.dto.request.comment.CommentRequest;
import com.example.social.dto.response.CommentResponse.CommentResponse;
import com.example.social.dto.response.CommentResponse.PostCommentResponse;
import com.example.social.entity.Comment;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.repository.CommentRepository;
import com.example.social.repository.FriendShipRepository;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;
import com.example.social.services.FriendShipService;
import com.example.social.servicesImpl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)

public class TestCommentService {
    @InjectMocks
    CommentServiceImpl commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    FriendShipService friendShipService;
    @Mock
    FriendShipRepository friendShipRepository;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testPostComment() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(1);
        commentRequest.setContent("Test comment");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = "userTest";
        when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn(username);

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail(username);
        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(currentUser));
        Post post = new Post();
        post.setPostId(1);
        post.setPostUserId(2);
        post.setPostDeleteFlag(0);
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(commentRequest.getPostId(), 0)).thenReturn(Optional.of(post));
        when(friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId()) && currentUser.getUserId() != post.getPostUserId()).thenReturn(true);

        PostCommentResponse expectedResponse = new PostCommentResponse();
        expectedResponse.setContent(commentRequest.getContent());
        expectedResponse.setCommentDeleteFlag(0);

        ResponseEntity<?> result = commentService.postComment(commentRequest);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertInstanceOf(PostCommentResponse.class, result.getBody());
        PostCommentResponse actualResponse = (PostCommentResponse) result.getBody();
        assertEquals(expectedResponse.getContent(), actualResponse.getContent());
        assertEquals(expectedResponse.getCommentDeleteFlag(), actualResponse.getCommentDeleteFlag());
        assertNotNull(actualResponse.getCommentCreatedTime());
    }

    @Test
    void testPostCommentWhenNotUserDetails() {
        CommentRequest commentRequest = new CommentRequest();
        Authentication authentication = mock(Authentication.class);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<?> result = commentService.postComment(commentRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals(commentResponse, result.getBody());
    }

    @Test
    void testPostCommentWhenPostNotFound() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(1);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = "userTest";
        when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn(username);

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail(username);
        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(currentUser));

        when(postRepository.findPostsByPostIdAndPostDeleteFlag(commentRequest.getPostId(), 0)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> commentService.postComment(commentRequest));
        ;
        assertEquals(CommonMessage.POST_NOT_FOUND, ex.getMessage());
    }

    @Test
    void testPostCommentWhenPostDeleted() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(1);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = "userTest";
        when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn(username);

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail(username);
        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(currentUser));

        Post post = new Post();
        post.setPostId(1);
        post.setPostUserId(2);
        post.setPostDeleteFlag(1);
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(commentRequest.getPostId(), 0)).thenReturn(Optional.of(post));

        ResponseEntity<?> result = commentService.postComment(commentRequest);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setMessage(CommonMessage.POST_NOT_FOUND);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(commentResponse, result.getBody());
    }


    @Test
    void testDeleteCommentWhenCommentNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = "userTest";
        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));

        int commentId = 1;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> commentService.deleteComment(commentId));

        assertEquals(CommonMessage.COMMENT_NOT_FOUND, ex.getMessage());
    }

    @Test
    void testDeleteCommentWhenNoPermission() {
        int commentId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CommentResponse commentResponse = new CommentResponse();

        String username = "userTest";
        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        Comment existingComment = new Comment();
        existingComment.setCommentUserId(2);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        ResponseEntity<?> result = commentService.deleteComment(commentId);
        commentResponse.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals(commentResponse, result.getBody());
    }

    @Test
    void testDeleteCommentWhenCommentAlreadyDeleted() {
        int commentId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setMessage(CommonMessage.COMMENT_NOT_FOUND);
        String username = "userTest";
        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        Comment existingComment = new Comment();
        existingComment.setCommentUserId(1);
        existingComment.setCommentDeleteFlag(1);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        ResponseEntity<?> result = commentService.deleteComment(commentId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(commentResponse, result.getBody());
    }

    @Test
    void testDeleteCommentSuccess() {
        int commentId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = "userTest";
        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        Comment existingComment = new Comment();
        existingComment.setCommentUserId(1);
        existingComment.setCommentDeleteFlag(0);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        ResponseEntity<?> result = commentService.deleteComment(commentId);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setMessage(CommonMessage.SUCCESS);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(commentResponse, result.getBody());
    }
}
