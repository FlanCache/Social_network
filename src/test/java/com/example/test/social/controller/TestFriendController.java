package com.example.test.social.controller;

import com.example.social.controller.FriendController;
import com.example.social.dto.request.comment.CommentRequest;
import com.example.social.entity.FriendShip;
import com.example.social.repository.FriendShipRepository;
import com.example.social.services.FriendShipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestFriendController {
    @InjectMocks
    FriendController friendController;
    @Mock
    FriendShipService friendShipService;
    @Mock
    FriendShipRepository friendShipRepository;

    @Test
    void testAddFriendSuccess() {
        int userReceiverId = 123;

        when(friendShipService.addFriend(userReceiverId)).thenReturn(new ResponseEntity<>( HttpStatus.OK));
        ResponseEntity<?> result = friendController.addFriend(userReceiverId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    @Test
    void testAcceptFriendRequestSuccess() {
        int userSenderId = 456;
        when(friendShipService.acceptFriendRequest(userSenderId)).thenReturn(new ResponseEntity<>( HttpStatus.OK));

        ResponseEntity<?> result = friendController.acceptFriendRequest(userSenderId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    @Test
    void testDeclineFriendRequestSuccess() {
        int userSenderId = 123;
        when(friendShipService.declineFriendRequest(userSenderId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> result = friendController.declineFriend(userSenderId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    @Test
    void testGetFriendListId() {
        int userId = 123;
        List<Integer> friendIds = Arrays.asList(1, 2, 3);
        when(friendShipService.getListFriendId(userId)).thenReturn(friendIds);
        List<Integer> result = friendController.getFriendListId(userId);
        assertEquals(friendIds, result);
    }

    @Test
    public void testGetFriendship_LoggedInUserWithFriends_ReturnsFriendList() {
        List<FriendShip> friendShipList = new ArrayList<>();
        when(friendShipService.getAllFriendList()).thenReturn(new ResponseEntity<>( HttpStatus.OK));
        ResponseEntity<?> responseEntity = friendController.getFriendship();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
