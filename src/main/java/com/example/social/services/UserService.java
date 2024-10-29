package com.example.social.services;


import com.example.social.dto.UserDto;
import com.example.social.dto.request.authRequest.SigninRequest;
import com.example.social.dto.request.authRequest.SigupRequest;
import com.example.social.dto.request.userRequest.UserUpdateRequest;
import com.example.social.dto.response.userResponse.UserUpdateResponse;
import com.example.social.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface UserService{


    ResponseEntity<?> signUp(SigupRequest sigupRequest);

    ResponseEntity<?> signIn(SigninRequest signinRequest);

    User signUpResponseToUser(SigupRequest sigupRequest);

    ResponseEntity<?> updateUser(UserUpdateRequest userUpdateRequest);


    ResponseEntity<?> avatarUpdate(MultipartFile file);

    ResponseEntity<?> forgotPassword(String userEmail);


    ResponseEntity<?> exportReport() throws IOException;


    ResponseEntity<?> userInfo();
}
