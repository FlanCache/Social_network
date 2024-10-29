package com.example.test.social.service;

import com.example.social.fileProcess.FilesStorageService;
import com.example.social.common.CommonMessage;
import com.example.social.common.LikeStatus;
import com.example.social.dto.request.postRequest.EditPostRequest;
import com.example.social.dto.request.postRequest.UpPostRequest;
import com.example.social.dto.response.postResponse.PostResponse;
import com.example.social.dto.response.postResponse.UpPostResponse;
import com.example.social.entity.*;
import com.example.social.repository.*;
import com.example.social.services.FriendShipService;
import com.example.social.servicesImpl.PostServiceImpl;
import com.example.social.servicesImpl.UserServiceImpl;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestPostService {
    @InjectMocks
    PostServiceImpl postService;
    @Mock
    FriendShipService friendShipService;
    @Mock
    UserServiceImpl userService;
    @Mock
    FilesStorageService filesStorageService;
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    CommentRepository commentRepository;

    @Mock
    LikeRepository likeRepository;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testUpPostWhenNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        Mockito.when(userRepository.findByUserEmail(Mockito.anyString())).thenThrow(UsernameNotFoundException.class);
        UpPostRequest upPostRequest = new UpPostRequest();
        try {
            ResponseEntity<UpPostResponse> result = (ResponseEntity<UpPostResponse>) postService.upPost(upPostRequest, null);
            Assertions.fail("Should have thrown UsernameNotFoundException");
        } catch (NoSuchElementException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testUpPostWhenAuthenticated() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByUserEmail("user@example.com")).thenReturn(Optional.of(new User()));

        when(filesStorageService.photoFormatCheck(Mockito.any())).thenReturn(false);
        when(filesStorageService.isFileSizeValid(Mockito.any(), Mockito.anyInt())).thenReturn(true);
        when(filesStorageService.save(Mockito.any())).thenReturn("image_path");

        when(imageRepository.save(Mockito.any())).thenReturn(new Image());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[10]);
        UpPostRequest upPostRequest = new UpPostRequest();
        ResponseEntity<?> result = postService.upPost(upPostRequest, new MultipartFile[]{mockMultipartFile});

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testUpPostNullContentAndMultipartFiles() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

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
        when(authentication.getName()).thenReturn("user@example.com");
        Mockito.when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));

        UpPostRequest upPostRequest = new UpPostRequest();
        MultipartFile[] multipartFiles = null;


        ResponseEntity<?> result = postService.upPost(upPostRequest, multipartFiles);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(CommonMessage.POST_VALID, result.getBody());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testUpPostOk() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

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
        when(authentication.getName()).thenReturn("user@example.com");
        Mockito.when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));

        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setPostContent("bilibili");
        MultipartFile[] multipartFiles = null;


        ResponseEntity<?> result = postService.upPost(upPostRequest, multipartFiles);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testPostRequestToResponse() {
        CommentRepository commentRepository = mock(CommentRepository.class);
        LikeRepository likeRepository = mock(LikeRepository.class);


        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setPostId(1);
        upPostRequest.setPostIdUser(123);
        upPostRequest.setPostContent("Test post content");
        upPostRequest.setPostCreateDate(LocalDateTime.now());

        List<Image> imageList = new ArrayList<>();

        when(commentRepository.findCommentsByCommentPostIdAndCommentDeleteFlag(1, 0)).thenReturn(null);
        when(likeRepository.countLikeByLikePostIdAndLikeStatus(1, LikeStatus.LIKE)).thenReturn(1);

        UpPostResponse upPostResponse = postService.postRequestToResponse(upPostRequest, imageList, 0);


        assertEquals(1, upPostResponse.getId());
        assertEquals(123, upPostResponse.getUserId());
        assertEquals("Test post content", upPostResponse.getContent());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testPostSave() {
        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setPostIdUser(123);
        upPostRequest.setPostContent("Test post content");

        Post savedPost = new Post();
        savedPost.setPostUserId(upPostRequest.getPostIdUser());
        savedPost.setPostCreateTime(LocalDateTime.now());
        savedPost.setContent(upPostRequest.getPostContent());
        savedPost.setPostDeleteFlag(0);

        when(postRepository.save(Mockito.any(Post.class))).thenReturn(savedPost);

        Post result = postService.postSave(upPostRequest);

        assertNotNull(result);
        assertEquals(0, result.getPostId());
        assertEquals(123, result.getPostUserId());
        assertEquals("Test post content", result.getContent());
        assertEquals(0, result.getPostDeleteFlag());
        Mockito.verify(postRepository, times(1)).save(Mockito.any(Post.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testEditPost() {
        EditPostRequest editPostRequest = new EditPostRequest();
        editPostRequest.setPostId(1);
        editPostRequest.setPostContent("Updated post content");
        editPostRequest.setPostDeleteFlag(0);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        User currentUser = new User();
        currentUser.setUserId(123);
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(java.util.Optional.of(currentUser));

        Post existingPost = new Post();
        existingPost.setPostId(1);
        existingPost.setPostUserId(123);
        existingPost.setContent("Original post content");
        existingPost.setPostDeleteFlag(0);
        existingPost.setPostCreateTime(LocalDateTime.now());
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(java.util.Optional.of(existingPost));

        MultipartFile[] multipartFiles = null;

        when(filesStorageService.photoFormatCheck(Mockito.any())).thenReturn(false);
        when(filesStorageService.isFileSizeValid(Mockito.any(), Mockito.anyInt())).thenReturn(true);

        List<Image> imagesFormPost = new ArrayList<>();
        List<Comment> commentList = new ArrayList<>();

        when(imageRepository.findByPostIdAndImageDeleteFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(imagesFormPost);
        when(commentRepository.findCommentsByCommentPostIdAndCommentDeleteFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(commentList);
        when(likeRepository.countLikeByLikePostIdAndLikeStatus(Mockito.anyInt(),Mockito.any(LikeStatus.class))).thenReturn(1);
        ResponseEntity<?> result = postService.editPost(editPostRequest, multipartFiles);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(postRepository, times(1)).findPostsByPostIdAndPostDeleteFlag(Mockito.anyInt(), eq(0));
        verify(userRepository, times(1)).findByUserEmail(Mockito.anyString());
        verify(imageRepository, times(1)).findByPostIdAndImageDeleteFlag(Mockito.anyInt(), eq(0));

    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testEditPostInAuthenticated() {
        EditPostRequest editPostRequest = new EditPostRequest();
        editPostRequest.setPostId(1);
        editPostRequest.setPostContent("Updated post content");
        editPostRequest.setPostDeleteFlag(0);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        User currentUser = new User();
        currentUser.setUserId(123);
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Post existingPost = new Post();
        existingPost.setPostId(1);
        existingPost.setPostUserId(123);
        existingPost.setContent("Original post content");
        existingPost.setPostDeleteFlag(0);
        existingPost.setPostCreateTime(LocalDateTime.now());
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(java.util.Optional.of(existingPost));

        MultipartFile[] multipartFiles = null;

        when(filesStorageService.photoFormatCheck(Mockito.any())).thenReturn(false);
        when(filesStorageService.isFileSizeValid(Mockito.any(), Mockito.anyInt())).thenReturn(true);

        List<Image> imagesFormPost = new ArrayList<>();
        when(imageRepository.findByPostIdAndImageDeleteFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(imagesFormPost);

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> postService.editPost(editPostRequest, multipartFiles));
        ;

        assertEquals(CommonMessage.USER_NOT_FOUND, ex.getMessage());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testDeletePostWhenNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        ResponseEntity<?> result = postService.deletePost(1);
        PostResponse response = new PostResponse();
        response.setMessage(CommonMessage.SIGNIN_FIRST);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals(response,result.getBody());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testDeletePost() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));
        Post existingPost = new Post();
        existingPost.setPostId(1);
        existingPost.setPostUserId(1);
        existingPost.setContent("Original post content");
        existingPost.setPostDeleteFlag(0);
        existingPost.setPostCreateTime(LocalDateTime.now());
        when(postRepository.findPostsByPostIdAndPostDeleteFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(java.util.Optional.of(existingPost));

        ResponseEntity<?> result = postService.deletePost(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testShowPostByIdSuccess() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));

        Post post = new Post();
        post.setPostId(1);
        post.setPostUserId(2);

        Mockito.when(postRepository.findPostsByPostIdAndPostDeleteFlag(post.getPostId(), 0)).thenReturn(Optional.of(post));
        Mockito.when(friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId())).thenReturn(true);

        ResponseEntity<?> responseEntity = postService.showPostById(post.getPostId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testShowPostByIdUnauthorized() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));

        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");

        ResponseEntity<?> responseEntity = postService.showPostById(1);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testShowPostByIdPostNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));

        Mockito.when(postRepository.findPostsByPostIdAndPostDeleteFlag(1, 0)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> postService.showPostById(1));

        assertEquals(CommonMessage.POST_NOT_FOUND, ex.getMessage());
    }

    @Test
    void testShowPostByIdFriendRequestRequired() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));

        Post post = new Post();
        post.setPostId(1);
        post.setPostUserId(2);

        Mockito.when(postRepository.findPostsByPostIdAndPostDeleteFlag(post.getPostId(), 0)).thenReturn(Optional.of(post));
        Mockito.when(friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId())).thenReturn(false);

        ResponseEntity<?> responseEntity = postService.showPostById(post.getPostId());

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testTimeLineSuccess() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));

        List<Integer> listFriendsId = Arrays.asList(2, 3, 4);
        when(friendShipService.getListFriendId(currentUser.getUserId())).thenReturn(listFriendsId);

        List<Post> postsList = new ArrayList<>();
        Post friendPost1 = new Post();
        friendPost1.setPostId(10);
        friendPost1.setPostUserId(2);
        friendPost1.setPostCreateTime(LocalDateTime.now().minusHours(3));
        postsList.add(friendPost1);

        Post friendPost2 = new Post();
        friendPost2.setPostId(11);
        friendPost2.setPostUserId(3);
        friendPost2.setPostCreateTime(LocalDateTime.now().minusHours(4));
        postsList.add(friendPost2);

        Post currentUserPost = new Post();
        currentUserPost.setPostId(12);
        currentUserPost.setPostUserId(1);
        currentUserPost.setPostCreateTime(LocalDateTime.now().minusHours(2));
        postsList.add(currentUserPost);

        when(postRepository.findPostByPostUserIdAndPostDeleteFlagAndPostCreateTimeAfterOrderByPostCreateTimeDesc(2, 0, currentUserPost.getPostCreateTime())).thenReturn(postsList);

        ResponseEntity<?> responseEntity = postService.timeLineByPage(0, 1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testTimeLineUnauthorized() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserId(1);
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        ResponseEntity<?> responseEntity = postService.timeLineByPage(0, 1);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
}
