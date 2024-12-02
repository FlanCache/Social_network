package com.example.test.social.controller;

import com.example.social.fileProcess.FilesStorageService;
import com.example.social.controller.UserController;
import com.example.social.dto.UserDto;
import com.example.social.dto.request.userRequest.UserUpdateRequest;
import com.example.social.dto.response.userResponse.UserInfoResponse;
import com.example.social.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUserController {
    @InjectMocks
    UserController userController;
    @Mock
    FilesStorageService storageService;
    @Mock
    UserService userService;

   // @Test
//    void testUpdateUser() {
//        UserDto userDto = new UserDto();
//        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
//        when(userService.updateUser(userUpdateRequest)).thenReturn(new ResponseEntity<>( HttpStatus.OK));
//
//       // ResponseEntity<?> result = userController.updateUser(userUpdateRequest);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//
//        verify(userService, times(1)).updateUser(userUpdateRequest);
//    }

//    @Test
//    void testAvatarUpdate() {
//        MockMultipartFile mockFiles ;
//
//        mockFiles = new MockMultipartFile("file", "file1.png", "text/plain", "Lubia".getBytes());
//
//        when(userService.avatarUpdate(mockFiles)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        ResponseEntity<?> result = userController.avatarUpdate(mockFiles);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//
//        verify(userService, times(1)).avatarUpdate(mockFiles);
//    }

    @Test
    void testExportLog() throws IOException {
        when(userService.exportReport()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> result = userController.exportLog();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService, times(1)).exportReport();
    }
    @Test
    void testUserInfo() {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        when(userService.userInfo()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> result = userController.userInfo();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
