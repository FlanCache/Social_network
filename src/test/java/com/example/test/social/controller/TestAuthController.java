package com.example.test.social.controller;

import com.example.social.controller.AuthController;
import com.example.social.dto.request.authRequest.OtpLoginRequest;
import com.example.social.dto.request.authRequest.SigninRequest;
import com.example.social.dto.request.authRequest.SigupRequest;
import com.example.social.dto.request.userRequest.ChangePasswordRequest;
import com.example.social.services.UserService;
import com.example.social.servicesImpl.OtpServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestAuthController {
    @Mock
    UserService userService;

    @InjectMocks
    AuthController authController;
    @Mock
    OtpServicesImpl otpServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testSignup() {
        SigupRequest sigupRequest = new SigupRequest();
        sigupRequest.setFullName("testUser");
        sigupRequest.setPassword("testPassword");

        when(userService.signUp(sigupRequest)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        ResponseEntity<?> responseEntity = authController.signup(sigupRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        verify(userService).signUp(sigupRequest);
    }

    @Test
    void testSigninSuccess() {
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("testUser");
        signinRequest.setPassword("testPassword");

        when(userService.signIn(signinRequest)).thenReturn(new ResponseEntity<>( HttpStatus.OK));

        ResponseEntity<?> responseEntity = authController.signin(signinRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testSigninOtpSuccess() {
        OtpLoginRequest otpRequest = new OtpLoginRequest();
        otpRequest.setOtp("123456");
        otpRequest.setEmail("test@example.com");

        when(otpServices.otpLogin(otpRequest))
                .thenReturn(new ResponseEntity<>( HttpStatus.OK));

        ResponseEntity<?> responseEntity = authController.signinOtp(otpRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testForgotPasswordSuccess() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setEmail("test@example.com");

        when(userService.forgotPassword(changePasswordRequest.getEmail())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> responseEntity = authController.forgotPassword(changePasswordRequest.getEmail());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testChangePasswordSuccess() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOtp("123456");
        changePasswordRequest.setNewPassword("newPassword");

        when(otpServices.otpChangePasswordCheck(Mockito.any())).thenReturn(new ResponseEntity<>( HttpStatus.OK));

        ResponseEntity<?> responseEntity = authController.changePassword(changePasswordRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

}
