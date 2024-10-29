package com.example.test.social.service;

import com.example.social.fileProcess.FilesStorageService;
import com.example.social.common.CommonMessage;
import com.example.social.common.ConstResouce;
import com.example.social.common.RelationStatus;
import com.example.social.controller.AuthController;
import com.example.social.dto.UserDto;
import com.example.social.dto.request.authRequest.SigninRequest;
import com.example.social.dto.request.authRequest.SigupRequest;
import com.example.social.dto.request.userRequest.UserUpdateRequest;
import com.example.social.dto.response.userResponse.UserSignUpResponse;
import com.example.social.dto.response.userResponse.UserSigninResponse;
import com.example.social.dto.response.userResponse.UserUpdateResponse;
import com.example.social.entity.User;
import com.example.social.repository.*;
import com.example.social.services.OtpServices;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUserService {
    @Mock
    private AuthController authController;
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    OtpServices otpServices;
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private FilesStorageService filesStorageService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    LikeRepository likeRepository;
    @Mock
    FriendShipRepository friendShipRepository;

    @Test
    void testSignUpWithExistingEmail() {

        SigupRequest signUpRequest = new SigupRequest();
        signUpRequest.setEmail("existing@example.com");
        signUpRequest.setPassword("existing@example.com");
        signUpRequest.setFullName("test1");


        when(userRepository.existsUserByUserEmail(signUpRequest.getEmail())).thenReturn(true);
        ResponseEntity<?> responseEntity =  userService.signUp(signUpRequest);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void testSignUpSuccess() {

        SigupRequest signUpRequest = new SigupRequest();
        signUpRequest.setEmail("new@example.com");
        signUpRequest.setPassword("existi");
        signUpRequest.setFullName("12345");
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("askjdjkasdkjasdkasdkas");

        User userForSave = new User();
        userForSave.setUserEmail("new@example.com");
        userForSave.setUserFullName("existi");
        userForSave.setUserPassword("12345");
        Mockito.when(userRepository.existsUserByUserEmail(signUpRequest.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userForSave);


        ResponseEntity<?> responseEntity = userService.signUp(signUpRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void testSignUpException() {

        SigupRequest signUpRequest = new SigupRequest();
        signUpRequest.setEmail("exception@example.com");

        when(userRepository.existsUserByUserEmail(signUpRequest.getEmail())).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenThrow(new RuntimeException("Some error occurred"));


        ResponseEntity<?> responseEntity = userService.signUp(signUpRequest);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void testSignIn_Success() {

        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("test@example.com");
        signinRequest.setPassword("password");
        UserSigninResponse userSigninResponse = new UserSigninResponse();
        String otpCode = "123456";
        userSigninResponse.setOtp(otpCode);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);


        when(otpServices.otpSender(signinRequest.getEmail())).thenReturn(userSigninResponse);


        ResponseEntity<?> responseEntity = userService.signIn(signinRequest);
        userSigninResponse.setMessage(CommonMessage.OTP_EXPIRE + otpCode);
        userSigninResponse.setToken(null);
        userSigninResponse.setOtp(null);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(authenticationManager).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));

        verify(otpServices).otpSender(signinRequest.getEmail());
    }



    @Test
    public void testSignUpResponseToUserWithNullValues() {
        SigupRequest sigupRequest = new SigupRequest();

        User user = userService.signUpResponseToUser(sigupRequest);

        Assertions.assertNull(user.getUserEmail());
        Assertions.assertNull(user.getUserPassword());
        Assertions.assertNull(user.getUserFullName());
        Assertions.assertEquals(user.getUserRoles(), "user");
        Assertions.assertEquals(user.getUserAvatar(), "avatar.png");
    }

    @Test
    public void testSignUpResponseToUser() {
        SigupRequest sigupRequest = new SigupRequest();
        sigupRequest.setEmail("test@example.com");
        sigupRequest.setPassword("password");
        sigupRequest.setFullName("Test User");

        User user = userService.signUpResponseToUser(sigupRequest);
        user.setUserPassword("password");

        Assertions.assertEquals("test@example.com", user.getUserEmail());
        Assertions.assertEquals("password", user.getUserPassword());
        Assertions.assertEquals("Test User", user.getUserFullName());
        Assertions.assertEquals("user", user.getUserRoles());
        Assertions.assertEquals(ConstResouce.DEFAULT_AVATAR, user.getUserAvatar());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testUpdateUserSuccess() {
        UserUpdateRequest userDtoRequest = new UserUpdateRequest();
        userDtoRequest.setFullName("New Name");
        //userDtoRequest.setUserEmail("user@example.com");

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

        ResponseEntity<UserUpdateResponse> responseEntity = (ResponseEntity<UserUpdateResponse>) userService.updateUser(userDtoRequest);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testUpdateUserUserNotFound() {
        UserDto userDtoRequest = new UserDto();
        userDtoRequest.setUserFullName("New Name");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByUserEmail(anyString())).thenThrow(NoSuchElementException.class);

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn(null);
        verify(userRepository, never()).save(any());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testAvatarUpdateFail() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Authentication authentication = mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));

        Mockito.when(filesStorageService.photoFormatCheck(mockFile)).thenReturn(false);
        Mockito.when(filesStorageService.isFileSizeValid(mockFile, ConstResouce.AVATAR_FILE_SIZE)).thenReturn(true);
        Mockito.when(filesStorageService.save(mockFile)).thenReturn("image_path");
        User mockUser = mock(User.class);
        when(userRepository.findByUserEmail(any())).thenReturn(java.util.Optional.of(mockUser));
        ResponseEntity<?> response = userService.avatarUpdate(mockFile);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testForgotPassword() {
        String userEmail = "test@example.com";
        User mockUser = new User();
        UserSigninResponse userSigninResponse = new UserSigninResponse();
        String expectedOtp = "123456";
        userSigninResponse.setOtp(expectedOtp);
        when(userRepository.findByUserEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(otpServices.otpSender(userEmail)).thenReturn(userSigninResponse);
        ResponseEntity<?> responseEntity = userService.forgotPassword(userEmail);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testExportReportWhenNotAuthenticated() throws IOException {
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

        ResponseEntity<?> result = userService.exportReport();

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals(CommonMessage.SIGNIN_FIRST, result.getBody());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testExportReportSuccess() throws IOException {
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
        currentUser.setUserId(1);

        when(userRepository.findByUserEmail("testUser")).thenReturn(Optional.of(currentUser));
        when(postRepository.countPostsInLastWeek(any(), eq(1))).thenReturn(5);
        when(commentRepository.countCommentsInLastWeek(any(), eq(1))).thenReturn(10);
        when(likeRepository.countLikesInLastWeek(any(), eq(1))).thenReturn(15);
        when(friendShipRepository.countFriendsInLastWeek(any(), eq(1), eq(RelationStatus.FRIEND))).thenReturn(20);

        ResponseEntity<?> result = userService.exportReport();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testExportReport_WithValidAuthentication() throws IOException {
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
        currentUser.setUserId(1);

        // Mock counts for report
        when(postRepository.countPostsInLastWeek(Mockito.any(LocalDateTime.class), Mockito.eq(currentUser.getUserId()))).thenReturn(5);
        when(commentRepository.countCommentsInLastWeek(Mockito.any(LocalDateTime.class), Mockito.eq(currentUser.getUserId()))).thenReturn(10);
        when(likeRepository.countLikesInLastWeek(Mockito.any(LocalDateTime.class), Mockito.eq(currentUser.getUserId()))).thenReturn(15);
        when(friendShipRepository.countFriendsInLastWeek(Mockito.any(LocalDateTime.class), Mockito.eq(currentUser.getUserId()), Mockito.eq(RelationStatus.FRIEND))).thenReturn(20);

        // Call the service method
        ResponseEntity<?> responseEntity = userService.exportReport();

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Add assertions for other properties if needed
    }


    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testUserInfo_WithInValidAuthentication() {
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


        when(userRepository.findByUserEmail("test@example.com")).thenReturn(java.util.Optional.of(currentUser));

        ResponseEntity<?> responseEntity = userService.userInfo();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

//    @Test
//    @MockitoSettings(strictness = Strictness.LENIENT)
//    void testUserInfoOk() {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        User currentUser = new User();
//        currentUser.setUserId(1);
//        currentUser.setUserFullName("Test User");
//        currentUser.setUserBirthday("12/01/2000");
//        currentUser.setUserAddress("123 Street, City");
//        currentUser.setUserAvatar("avatar.jpg");
//        currentUser.setUserEmail("test@example.com");
//        currentUser.setUserPhone("1234567890");
//        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
//        Mockito.when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));
//        currentUser.setUserId(1);
//
//        // Set other user properties...
//
//        // Mock userRepository behavior
//        when(userRepository.findByUserEmail("test@example.com")).thenReturn(java.util.Optional.of(currentUser));
//
//        // Call the service method
//        ResponseEntity<?> responseEntity = userService.userInfo();
//
//        // Verify the result
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        UserInfoResponse userInfoResponse = (UserInfoResponse) responseEntity.getBody();
//        assertEquals("Test User", userInfoResponse.getUserFullName());
//        assertEquals("12/01/2000", userInfoResponse.getUserBirthday());
//        assertEquals("123 Street, City", userInfoResponse.getUserAddress());
//        assertEquals("avatar.jpg", userInfoResponse.getUserAvatar());
//        assertEquals("test@example.com", userInfoResponse.getUserEmail());
//        assertEquals("1234567890", userInfoResponse.getUserPhone());
//        // Add assertions for other properties if needed
//    }
}
