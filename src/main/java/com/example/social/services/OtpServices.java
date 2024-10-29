package com.example.social.services;

import com.example.social.dto.request.authRequest.OtpLoginRequest;
import com.example.social.dto.request.userRequest.ChangePasswordRequest;
import com.example.social.dto.response.userResponse.UserSigninResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface OtpServices {
    String otpGenerate();

    UserSigninResponse otpSender(String userEmail);


    ResponseEntity<?> otpLogin(OtpLoginRequest otpLoginRequest);

    ResponseEntity<?> otpChangePasswordCheck(ChangePasswordRequest changePasswordRequest);
}
