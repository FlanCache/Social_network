package com.example.social.controller;

import com.example.social.dto.request.postRequest.UpPostRequest;
import com.example.social.fileProcess.FilesStorageService;
import com.example.social.dto.request.userRequest.UserUpdateRequest;
import com.example.social.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    FilesStorageService storageService;
    @Autowired
    UserService userService;

    @Valid
    @PatchMapping(value = "/updateInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@RequestPart(value = "file", required = false) @Valid MultipartFile multipartFile, UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(userUpdateRequest);
    }

    @PutMapping(value = "/avatarUpdate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> avatarUpdate(@RequestParam("file") MultipartFile file) {
        return userService.avatarUpdate(file);
    }

    @GetMapping(value = "/log")
    public ResponseEntity<?> exportLog() throws IOException {
        return userService.exportReport();
    }

    @GetMapping(value = "/info")
    public ResponseEntity<?> userInfo() {
        return userService.userInfo();
    }

    @GetMapping(value = "/findUserById/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        return userService.getUserInfoById(userId);
    }
    @GetMapping(value = "/findUserByEmail/{userEmail}")
    public ResponseEntity<?> getUserById(@PathVariable String userEmail) {
        return userService.getUserInfoByEmail(userEmail);
    }

}
