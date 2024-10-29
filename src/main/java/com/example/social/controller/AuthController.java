package com.example.social.controller;

import com.example.social.dto.request.authRequest.OtpLoginRequest;
import com.example.social.dto.request.authRequest.SigninRequest;
import com.example.social.dto.request.authRequest.SigupRequest;
import com.example.social.dto.request.userRequest.ChangePasswordRequest;
import com.example.social.services.OtpServices;
import com.example.social.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    OtpServices otpServices;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SigupRequest sigupRequest) {
        return userService.signUp(sigupRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest signinRequest) {
        return userService.signIn(signinRequest);
    }

    @PostMapping("/otp")
    public ResponseEntity<?> signinOtp(@Valid @RequestBody OtpLoginRequest otpRequest) {
        return otpServices.otpLogin(otpRequest);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return userService.forgotPassword(email);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        return otpServices.otpChangePasswordCheck(changePasswordRequest);
    }

}
