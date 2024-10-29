package com.example.test.social.service;

import com.example.social.common.CommonMessage;
import com.example.social.common.LikeStatus;
import com.example.social.dto.response.likeResponse.LikeResponse;
import com.example.social.entity.Like;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.repository.LikeRepository;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;
import com.example.social.services.FriendShipService;
import com.example.social.servicesImpl.LikeServiceImpl;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestLikeService {
    @InjectMocks
    LikeServiceImpl likeService;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    LikeRepository likeRepository;
    @Mock
    FriendShipService friendShipService;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testLikePostUserNotFound() {

        int postId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User mockUser = new User();
        mockUser.setUserEmail("user@example.com");
        mockUser.setUserPassword("12345");
        mockUser.setUserFullName("userTest");
        mockUser.setUserId(1);
        mockUser.setUserRoles("user");
        mockUser.setUserAvatar("avatar.png");
        mockUser.setUserBirthday("01/01/2000");
        mockUser.setUserAddress("ha noi");
        mockUser.setUserPhone("01112233");
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        try {
            ResponseEntity<?> responseEntity = likeService.likePost(postId);
            Assertions.fail("Should have thrown UsernameNotFoundException");
        } catch (NoSuchElementException e) {
            assertNotNull(e);
        }
    }
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testLikePostPostNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        int postId = 1;
        User mockUser = new User();
        mockUser.setUserEmail("user@example.com");
        mockUser.setUserPassword("12345");
        mockUser.setUserFullName("userTest");
        mockUser.setUserId(1);
        mockUser.setUserRoles("user");
        mockUser.setUserAvatar("avatar.png");
        mockUser.setUserBirthday("01/01/2000");
        mockUser.setUserAddress("ha noi");
        mockUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(postId, 0)).thenReturn(Optional.empty());

        try {
            ResponseEntity<?> responseEntity = likeService.likePost(postId);
            Assertions.fail("Should have thrown UsernameNotFoundException");
        } catch (NoSuchElementException e) {
            assertNotNull(e);
        }

    }
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testLikePostNotEnoughPermission() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        int postId = 1;
        String username = "user";

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

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("user");
        Post post = new Post();
        post.setPostUserId(2);

        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(currentUser));
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(postId, 0)).thenReturn(Optional.of(post));
        when(friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId())).thenReturn(false);

        ResponseEntity<?> responseEntity = likeService.likePost(postId);
        LikeResponse response = new LikeResponse();
        response.setMessage(CommonMessage.LIKE_FRIEND_REQUEST);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testLikePostAlreadyLiked() {
        int postId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String username = "user";

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

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("user");

        Post post = new Post();
        post.setPostUserId(2);

        Like likedLike = new Like();
        likedLike.setLikeStatus(LikeStatus.LIKE);

        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(currentUser));
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(postId, 0)).thenReturn(Optional.of(post));
        when(friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId())).thenReturn(true);
        when(likeRepository.existsLikeByLikePostIdAndLikeUserId(anyInt(),anyInt())).thenReturn(true);
        when(likeService.likeCheck(postId, currentUser.getUserId())).thenReturn(likedLike);

        ResponseEntity<?> responseEntity = likeService.likePost(postId);
        LikeResponse response = new LikeResponse();
        response.setMessage(CommonMessage.UNLIKE);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testLikePostSuccess() {
        int postId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String username = "user";

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

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("user");

        Post post = new Post();
        post.setPostUserId(2);

        Like likedLike = new Like();
        likedLike.setLikeStatus(LikeStatus.UNLIKE);

        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(currentUser));
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(postId, 0)).thenReturn(Optional.of(post));
        when(friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId())).thenReturn(true);
        when(likeRepository.existsLikeByLikePostIdAndLikeUserId(anyInt(),anyInt())).thenReturn(true);
        when(likeService.likeCheck(postId, currentUser.getUserId())).thenReturn(likedLike);

        ResponseEntity<?> responseEntity = likeService.likePost(postId);
        LikeResponse response = new LikeResponse();
        response.setMessage(CommonMessage.LIKE);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(likeRepository, times(1)).save(any(Like.class));
    }
}
