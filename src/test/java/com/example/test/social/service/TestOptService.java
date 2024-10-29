package com.example.test.social.service;

import com.example.social.common.CommonMessage;
import com.example.social.dto.request.authRequest.OtpLoginRequest;
import com.example.social.dto.request.userRequest.ChangePasswordRequest;
import com.example.social.dto.response.Response;
import com.example.social.dto.response.userResponse.UserSigninResponse;
import com.example.social.entity.User;
import com.example.social.jwt.JwtUltils;
import com.example.social.repository.UserRepository;
import com.example.social.servicesImpl.OtpServicesImpl;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class TestOptService {
    @InjectMocks
    OtpServicesImpl otpServices;
    @Mock
    UserRepository userRepository;
    @Mock
    JwtUltils jwtUltils;
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testOtpGenerate() {

        String generatedOtp = otpServices.otpGenerate();

        assertNotNull(generatedOtp);
        assertEquals(5, generatedOtp.length());

        assertTrue(generatedOtp.chars().allMatch(Character::isDigit));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testOtpSender() {
        User mockUser = new User();
        mockUser.setUserEmail("test@example.com");
        when(userRepository.findByUserEmail(anyString())).thenReturn(java.util.Optional.of(mockUser));
        UserSigninResponse generatedOtp = otpServices.otpSender("test@example.com");

        Mockito.when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(java.util.Optional.of(mockUser));
        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void testOtpLogin_UserNotFound() {
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());
        OtpLoginRequest otpLoginRequest = new OtpLoginRequest();
        otpLoginRequest.setEmail("nonexistent@example.com");
        otpLoginRequest.setOtp("123456");
        try {
            ResponseEntity<?> result = otpServices.otpLogin(otpLoginRequest);
            Assertions.fail("Should have thrown UsernameNotFoundException");
        } catch (NoSuchElementException e) {
            assertNotNull(e);
        }

    }

    @Test
    public void testOtpLogin_OtpDoesNotExist() {

        User mockUser = new User();
        mockUser.setUserOtpCreatedTime(LocalDateTime.now());
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(mockUser));
        OtpLoginRequest otpLoginRequest = new OtpLoginRequest();
        otpLoginRequest.setEmail("nonexistent@example.com");
        otpLoginRequest.setOtp("123456");
        ResponseEntity<?> result = otpServices.otpLogin(otpLoginRequest);
        Response response = new Response();
        response.setMessage(CommonMessage.OTP_NOT_EXIST);
        assertEquals(response, result.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void testOtpLogin_OtpDoesNotMatch() {

        User mockUser = new User();
        mockUser.setUserOtp("123456");
        mockUser.setUserOtpCreatedTime(LocalDateTime.now());
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(mockUser));
        OtpLoginRequest otpLoginRequest = new OtpLoginRequest();
        otpLoginRequest.setEmail("nonexistent@example.com");
        otpLoginRequest.setOtp("123453");

        ResponseEntity<?> result = otpServices.otpLogin(otpLoginRequest);
        Response userSigninResponse = new Response();
        userSigninResponse.setMessage(CommonMessage.OTP_NOT_MATCH);
        assertEquals(userSigninResponse, result.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void testOtpLogin_OtpExpired() {

        User existingUser = new User();
        existingUser.setUserOtp("123456");
        existingUser.setUserOtpCreatedTime(LocalDateTime.now().minusMinutes(10));
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(existingUser));
        OtpLoginRequest otpLoginRequest = new OtpLoginRequest();
        otpLoginRequest.setEmail("nonexistent@example.com");
        otpLoginRequest.setOtp("123456");
        UserSigninResponse userSigninResponse = new UserSigninResponse();
        userSigninResponse.setToken(null);
        userSigninResponse.setOtp(null);
        userSigninResponse.setMessage(CommonMessage.OTP_EXPIRED);

        ResponseEntity<UserSigninResponse> result = (ResponseEntity<UserSigninResponse>) otpServices.otpLogin(otpLoginRequest);

        assertEquals(CommonMessage.OTP_EXPIRED, Objects.requireNonNull(result.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testOtpLogin_Otp() {

        User existingUser = new User();
        existingUser.setUserOtp("123456");
        existingUser.setUserOtpCreatedTime(LocalDateTime.now().minusMinutes(4));
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(existingUser));
        OtpLoginRequest otpLoginRequest = new OtpLoginRequest();
        otpLoginRequest.setEmail("nonexistent@example.com");
        otpLoginRequest.setOtp("123456");


        ResponseEntity<?> result = otpServices.otpLogin(otpLoginRequest);


        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(existingUser.getUserOtp());
    }

    @Test
    public void testOtpChangePasswordCheckSuccess() {

        String userEmail = "test@example.com";
        String otp = "12345";
        String newPassword = "newPassword";

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setEmail(userEmail);
        changePasswordRequest.setOtp(otp);
        changePasswordRequest.setNewPassword(newPassword);

        User currentUser = new User();
        currentUser.setUserEmail(userEmail);
        currentUser.setUserOtp(otp);
        currentUser.setUserOtpCreatedTime(LocalDateTime.now().minusMinutes(4));

        when(userRepository.findByUserEmail(userEmail)).thenReturn(Optional.of(currentUser));

        ResponseEntity<?> responseEntity = otpServices.otpChangePasswordCheck(changePasswordRequest);
        UserSigninResponse response = new UserSigninResponse();
        response.setMessage(CommonMessage.SUCCESS);
        response.setOtp(null);
        response.setToken(null);

        verify(userRepository, times(1)).save(currentUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @Test
    public void testOtpChangePasswordCheckUserNotFound() {

        String userEmail = "nonexistent@example.com";
        String otp = "12345";
        String newPassword = "newPassword";

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setEmail(userEmail);
        changePasswordRequest.setOtp(otp);
        changePasswordRequest.setNewPassword(newPassword);

        when(userRepository.findByUserEmail(userEmail)).thenReturn(Optional.empty());


        try {
            ResponseEntity<?> responseEntity = otpServices.otpChangePasswordCheck(changePasswordRequest);
            Assertions.fail("Should have thrown UsernameNotFoundException");
        } catch (NoSuchElementException e) {
            assertNotNull(e);
        }
    }
}
